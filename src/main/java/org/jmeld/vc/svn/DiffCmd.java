package org.jmeld.vc.svn;

import org.jmeld.diff.*;
import org.jmeld.util.*;
import org.jmeld.vc.*;
import org.jmeld.vc.util.*;

import java.io.*;
import java.util.regex.*;

public class DiffCmd
    extends VcCmd<DiffData>
{
  // Instance variables:
  private File file;
  private boolean recursive;
  private BufferedReader reader;
  private String unreadLine;

  public DiffCmd(File file, boolean recursive)
  {
    this.file = file;
    this.recursive = recursive;
  }

  public Result execute()
  {
    super.execute("svn", "diff", "--non-interactive", "--no-diff-deleted",
      recursive ? "" : "-N", file.getPath());

    return getResult();
  }

  protected void build(byte[] data)
  {
    String path;
    JMRevision revision;
    JMDelta delta;
    DiffData diffData;

    diffData = new DiffData();

    reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
        data)));

    try
    {
      for (;;)
      {
        path = readIndex();
        if (path == null)
        {
          break;
        }

        System.out.println("path = " + path);

        revision = new JMRevision(null, null);
        diffData.addTarget(path, revision);

        readLine(); // =====================================
        readLine(); // --- <Path>   (revision ...)
        readLine(); // +++ <Path>   (working copy)
        for (;;)
        {
          delta = readDelta();
          if (delta == null)
          {
            break;
          }
          revision.add(delta);
        }
      }
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
      setResult(Result.FALSE("Parse failed"));
    }

    setResultData(diffData);
  }

  private String readIndex()
      throws IOException
  {
    final String indexMarker = "Index: ";
    String line;

    line = readLine();
    if (line == null || !line.startsWith(indexMarker))
    {
      return null;
    }

    return line.substring(indexMarker.length());
  }

  private JMDelta readDelta()
      throws IOException
  {
    final Pattern deltaPattern = Pattern
        .compile("@@ -(\\d*),(\\d*) \\+(\\d*),(\\d*) @@");

    String line;
    Matcher m;
    JMDelta delta;
    JMChunk originalChunk;
    JMChunk revisedChunk;

    // @@ <LineNumberRevision>,<NumberOfLines> <lineNumberWorkingCopy>,<NumberOfLines> @@
    line = readLine();
    if (line == null)
    {
      return null;
    }

    m = deltaPattern.matcher(line);
    if (!m.matches())
    {
      unreadLine(line);
      return null;
    }

    originalChunk = new JMChunk(Integer.valueOf(m.group(1)), Integer.valueOf(m
        .group(2)));
    revisedChunk = new JMChunk(Integer.valueOf(m.group(3)), Integer.valueOf(m
        .group(4)));

    delta = new JMDelta(originalChunk, revisedChunk);

    while ((line = readLine()) != null)
    {
      if (line.startsWith(" "))
      {
        continue;
      }

      if (line.startsWith("+"))
      {
        continue;
      }

      if (line.startsWith("-"))
      {
        continue;
      }

      unreadLine(line);
      break;
    }

    System.out.println("delta = " + delta);

    return delta;
  }

  private void unreadLine(String unreadLine)
  {
    this.unreadLine = unreadLine;
  }

  private String readLine()
      throws IOException
  {
    String line;

    if (unreadLine != null)
    {
      line = unreadLine;
      unreadLine = null;
      return line;
    }

    return reader.readLine();
  }

  public static void main(String[] args)
  {
    DiffCmd cmd;
    DiffIF result;

    File file = parseFile(args);
    if (file == null) {
      return;
    }

    result = new SubversionVersionControl()
        .executeDiff(file, true);
    if (result != null)
    {
      for (DiffIF.TargetIF target : result.getTargetList())
      {
        System.out.println(target.getPath() + " " + target.getRevision());
      }
    }
  }
}
