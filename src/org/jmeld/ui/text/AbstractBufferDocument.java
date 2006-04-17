package org.jmeld.ui.text;

import org.jmeld.*;
import org.jmeld.ui.text.*;
import org.jmeld.util.*;

import javax.swing.event.*;
import javax.swing.text.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.List;

public abstract class AbstractBufferDocument
       implements BufferDocumentIF, DocumentListener
{
  // instance variables:
  private String                               name;
  private String                               shortName;
  private Line[]                               lines;
  private PlainDocument                        document;
  private MyGapContent                         content;
  private List<BufferDocumentChangeListenerIF> listeners;

  // Variables to detect if this document has been changed (and needs
  //   to be saved!)
  private boolean changed;
  private int     originalLength;
  private int     digest;

  public AbstractBufferDocument()
  {
    listeners = new ArrayList<BufferDocumentChangeListenerIF>();
  }

  public void addChangeListener(BufferDocumentChangeListenerIF listener)
  {
    listeners.add(listener);
  }

  public void removeChangeListener(BufferDocumentChangeListenerIF listener)
  {
    listeners.remove(listener);
  }

  abstract int getBufferSize();

  abstract Reader getReader()
    throws JMeldException;

  abstract Writer getWriter()
    throws JMeldException;

  protected void setName(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  protected void setShortName(String shortName)
  {
    this.shortName = shortName;
  }

  public String getShortName()
  {
    return shortName;
  }

  public PlainDocument getDocument()
  {
    return document;
  }

  public boolean isChanged()
  {
    return changed;
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
    throws JMeldException
  {
    try
    {
      Reader    reader;
      StopWatch stopWatch;

      if (document != null)
      {
        document.removeDocumentListener(this);
      }

      stopWatch = new StopWatch();
      stopWatch.start();
      System.out.println("before read");

      content = new MyGapContent(getBufferSize() + 500);
      document = new PlainDocument(content);

      reader = getReader();
      new DefaultEditorKit().read(reader, document, 0);
      reader.close();

      System.out.println("create document took " + stopWatch.getElapsedTime());
      document.addDocumentListener(this);

      initLines();
      initDigest();
    }
    catch (JMeldException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new JMeldException("Problem reading document (name=" + getName()
        + ") in buffer", ex);
    }
  }

  public void initLines()
  {
    Element   paragraph;
    Element   e;
    int       size;
    StopWatch stopWatch;

    System.out.println("initLines");
    stopWatch = new StopWatch().start();

    paragraph = document.getDefaultRootElement();
    size = paragraph.getElementCount();
    lines = new Line[size - 1];
    for (int i = 0; i < lines.length; i++)
    {
      e = paragraph.getElement(i);
      lines[i] = new Line(e);
    }

    System.out.println("initLines took " + stopWatch.getElapsedTime());

    //print();
  }

  public void write()
    throws JMeldException
  {
    Writer out;

    try
    {
      out = getWriter();
      new DefaultEditorKit().write(out, document, 0, document.getLength());
      out.flush();
      out.close();

      initDigest();
    }
    catch (JMeldException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new JMeldException("Problem writing document (name=" + getName()
        + ") from buffer", ex);
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
      int    o1;
      int    o2;

      array1 = getCharArray();
      array2 = c2.getCharArray();

      g1_0 = getGapStart();
      g1_1 = getGapEnd();
      g2_0 = c2.getGapStart();
      g2_1 = c2.getGapEnd();

      if (start1 >= g1_0)
      {
        o1 = start1 + g1_1 - g1_0;
      }
      else
      {
        o1 = start1;
      }

      if (start2 >= g2_0)
      {
        o2 = start2 + g2_1 - g2_0;
      }
      else
      {
        o2 = start2;
      }

      size = end1 - start1;
      for (int i = 0; i < size; i++, o1++, o2++)
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
      int    o;

      h = 0;

      array = getCharArray();

      g0 = getGapStart();
      g1 = getGapEnd();

      // Mind the gap!
      if (start >= g0)
      {
        o = start + g1 - g0;
      }
      else
      {
        o = start;
      }

      size = end - start;
      for (int i = 0; i < size; i++, o++)
      {
        // Mind the gap!
        if (o == g0)
        {
          o += g1 - g0;
        }

        h = 31 * h + array[o];
      }

      if (h == 0)
      {
        h = 1;
      }

      return h;
    }

    public int getDigest()
    {
      return hashCode(0, document.getLength());
    }
  }

  public class Line
  {
    Element element;

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
      Element element2;
      Line    line2;
      int     start1;
      int     start2;
      int     end1;
      int     end2;

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
      return content.hashCode(element.getStartOffset(), element.getEndOffset());
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

  public void changedUpdate(DocumentEvent de)
  {
    documentChanged();
  }

  public void insertUpdate(DocumentEvent de)
  {
    documentChanged();
  }

  public void removeUpdate(DocumentEvent de)
  {
    documentChanged();
  }

  private void initDigest()
  {
    originalLength = document != null ? document.getLength() : 0;
    digest = createDigest();
    changed = false;

    fireDocumentChanged();
  }

  public int createDigest()
  {
    return content.getDigest();
  }

  private void documentChanged()
  {
    boolean newChanged;
    int     newDigest;

    newChanged = false;
    if (document.getLength() != originalLength)
    {
      newChanged = true;
    }
    else
    {
      // Calculate the digest in order to see of a buffer has been
      //   changed (and should be saved)
      newDigest = createDigest();
      if (newDigest != digest)
      {
        newChanged = true;
      }
    }

    if (newChanged != changed)
    {
      changed = newChanged;
    }

    fireDocumentChanged();
  }

  private void fireDocumentChanged()
  {
    for (BufferDocumentChangeListenerIF listener : listeners)
    {
      listener.documentChanged();
    }
  }
}
