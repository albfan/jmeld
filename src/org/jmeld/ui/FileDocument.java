package org.jmeld.ui;

import org.jmeld.*;
import org.jmeld.util.*;

import javax.swing.text.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;
import java.util.List;

public class FileDocument
{
  // class variables:
  public static String          ORIGINAL = "Original";
  public static String          REVISED = "Revised";
  private static Charset        charset = Charset.forName("UTF-8");
  private static CharsetDecoder decoder = charset.newDecoder();

  // instance variables:
  private File          file;
  private Line[]        lines;
  private PlainDocument document;
  private MyGapContent  content;

  public FileDocument(File file)
  {
    this.file = file;
  }

  public String getName()
  {
    return file.getName();
  }

  public File getFile()
  {
    return file;
  }

  public PlainDocument getDocument()
  {
    return document;
  }

  public Line[] getLines()
  {
    return lines;
  }

  public int getOffsetForLine(int lineNumber)
  {
    int lineCounter;

    if (lineNumber < 0)
    {
      return -1;
    }

    if (lineNumber == 0)
    {
      return 0;
    }

    if (lineNumber > lines.length)
    {
      lineNumber = lines.length;
    }

    return lines[lineNumber - 1].getOffset();
  }

  public int getLineForOffset(int offset)
  {
    int lineNumber;

    if (offset < 0)
    {
      return 0;
    }

    lineNumber = 0;
    for (Line line : lines)
    {
      if (line.getOffset() > offset)
      {
        return lineNumber;
      }

      lineNumber++;
    }

    return lines.length - 1;
  }

  public void read()
    throws JMeldException, FileNotFoundException, IOException,
      BadLocationException
  {
    Reader         reader;
    BufferedReader br;
    String         s;
    int            number;
    char[]         buffer;
    int            totalOffset;
    int            test = 3;

    if (!file.isFile() || !file.canRead())
    {
      throw new JMeldException("Could not open file: " + file);
    }

    StopWatch stopWatch = new StopWatch();

    stopWatch.start();
    System.out.println("before read");

    content = new MyGapContent((int) file.length() + 500);
    document = new PlainDocument(content);

    // Read the file 
    if (test == 1)
    {
      reader = new FileReader(file);

      buffer = new char[(int) file.length()];
      number = reader.read(buffer, 0, buffer.length);
      if (number == -1 || number != file.length())
      {
        throw new JMeldException("File: " + file + ", filesize(="
          + file.length() + ") not equal to number of chars(" + number
          + ") read");
      }

      reader.close();

      s = new String(buffer, 0, number);

      System.out.println("read took " + stopWatch.getElapsedTime());
      stopWatch.start();

      document.insertString(0, s, null);
    }
    else if (test == 2)
    {
      FileInputStream  fis;
      FileChannel      fc;
      int              size;
      MappedByteBuffer bb;
      CharBuffer       cb;

      fis = new FileInputStream(file);
      fc = fis.getChannel();
      size = (int) fc.size();
      bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, size);
      cb = decoder.decode(bb);
      s = cb.toString();

      fis.close();

      System.out.println("read took " + stopWatch.getElapsedTime());
      stopWatch.start();

      document.insertString(0, s, null);
    }
    else if (test == 3)
    {
      reader = new FileReader(file);

      new DefaultEditorKit().read(reader, document, 0);

      reader.close();
    }

    System.out.println("create document took " + stopWatch.getElapsedTime());

    initLines();
  }

  public void initLines()
  {
    Element   paragraph;
    Element   e;
    int       rangeStart;
    int       rangeEnd;
    String    line;
    int       size;
    Segment   segment;
    StopWatch stopWatch;

    System.out.println("initLines");
    stopWatch = new StopWatch().start();

    paragraph = document.getDefaultRootElement();
    size = paragraph.getElementCount();
    lines = new Line[size];
    for (int i = 0; i < size; i++)
    {
      e = paragraph.getElement(i);
      rangeStart = e.getStartOffset();
      rangeEnd = e.getEndOffset();
      lines[i] = new Line(e);
    }

    System.out.println("initLines took " + stopWatch.getElapsedTime());

    //print();
  }

  void doSave()
    throws IOException
  {
    Writer out;

    try
    {
      out = new FileWriter(file);
      new DefaultEditorKit().write(out, document, 0, document.getLength());
      out.flush();
      out.close();
    }
    catch (BadLocationException e)
    {
      System.err.println(e.getMessage());
    }
  }

  class MyGapContent
         extends GapContent
  {
    public MyGapContent(int length)
    {
      super(length);
    }

    char[] getCharArray()
    {
      return (char[]) getArray();
    }

    public char getChar(int offset)
    {
      int g0;
      int g1;

      g0 = getGapStart();
      g1 = getGapEnd();

      if (offset >= g0)
      {
        // Take into account the gap!
        // This offset is above the gap.
        offset = g1 + offset - g0;
      }

      return getCharArray()[offset];
    }

    public boolean equals(MyGapContent c2, int start1, int end1, int start2)
    {
      char[] array1;
      char[] array2;
      int    g1_0;
      int    g1_1;
      int    g2_0;
      int    g2_1;
      int    size;

      array1 = getCharArray();
      array2 = c2.getCharArray();

      g1_0 = getGapStart();
      g1_1 = getGapEnd();
      g2_0 = c2.getGapStart();
      g2_1 = c2.getGapEnd();

      size = end1 - start1;
      for (int o1 = start1, o2 = start2, i = 0; i < size; i++, o1++, o2++)
      {
        if (o1 == g1_0)
        {
          o1 += g1_1 - g1_0;
        }

        if (o2 == g2_0)
        {
          o2 += g2_1 - g2_0;
        }

        if (array1[o1] != array2[o2])
        {
          return false;
        }
      }

      return true;
    }

    public int hashCode(int start, int end)
    {
      char[] array;
      int    g0;
      int    g1;
      int    size;
      int    h;

      h = 0;

      array = getCharArray();

      g0 = getGapStart();
      g1 = getGapEnd();

      size = end - start;
      for (int o = start, i = 0; i < size; i++, o++)
      {
        if (o == g0)
        {
          o += g1 - g0;
        }

        h = 31 * h + array[o];
      }

      if(h == 0)
      {
        h = 1;
      }

      return h;
    }

  }

  public class Line
  {
    Element element;
    int     hash;

    Line(Element element)
    {
      this.element = element;
    }

    MyGapContent getContent()
    {
      return content;
    }

    public int getOffset()
    {
      return element.getEndOffset();
    }

    public void print()
    {
      System.out.printf("[%08d]: %s\n", getOffset(),
        StringUtil.replaceNewLines(toString()));
    }

    public boolean equals(Object o)
    {
      Element      element2;
      Line line2;
      int          start1, start2;
      int          end1, end2;

      line2 = ((Line) o);
      element2 = line2.element;

      start1 = element.getStartOffset();
      end1 = element.getEndOffset();
      start2 = element2.getStartOffset();
      end2 = element2.getEndOffset();

      // If the length is different the element is not equal!
      if ((end1 - start1) != (end2 - start2))
      {
        return false;
      }

      return content.equals(line2.getContent(), start1, end1, start2);
    }

    public int hashCode()
    {
      if (hash == 0)
      {
        hash = content.hashCode(element.getStartOffset(), element.getEndOffset());
      }

      return hash;
    }

    public String toString()
    {
      try
      {
        return content.getString(element.getStartOffset(),
          element.getEndOffset() - element.getStartOffset());
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
        return "";
      }
    }
  }

  public void print()
  {
    if (lines != null)
    {
      for (int lineNumber = 0; lineNumber < lines.length; lineNumber++)
      {
        System.out.printf("[%05d]", lineNumber);
        lines[lineNumber].print();
      }
    }
  }

  public static void main(String[] args)
  {
    FileDocument fd;

    try
    {
      fd = new FileDocument(new File(args[0]));
      fd.read();
      fd.print();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
