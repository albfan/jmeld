package org.jmeld.util.node;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class FileNode
       extends JMeldNode
{
  private File file;

  public FileNode(
    String name,
    File   file)
  {
    super(name, !file.isDirectory());
    this.file = file;
  }

  public File getFile()
  {
    return file;
  }

  public long getSize()
  {
    return file.length();
  }

  public boolean contentEquals(JMeldNode node)
  {
    File             file2;
    RandomAccessFile f1;
    RandomAccessFile f2;
    FileChannel      fc1;
    FileChannel      fc2;
    ByteBuffer       bb1;
    ByteBuffer       bb2;
    boolean          equals;

    f1 = null;
    f2 = null;

    try
    {
      file2 = ((FileNode) node).getFile();

      if (file.isDirectory() || file2.isDirectory())
      {
        return true;
      }

      if (file.length() != file2.length())
      {
        return false;
      }

      f1 = new RandomAccessFile(file, "r");
      f2 = new RandomAccessFile(file2, "r");
      fc1 = f1.getChannel();
      fc2 = f2.getChannel();

      bb1 = fc1.map(FileChannel.MapMode.READ_ONLY, 0, (int) fc1.size());
      bb2 = fc2.map(FileChannel.MapMode.READ_ONLY, 0, (int) fc2.size());

      equals = bb1.equals(bb2);

      return equals;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return false;
    }
    finally
    {
      try
      {
        if (f1 != null)
        {
          f1.close();
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }

      try
      {
        if (f2 != null)
        {
          f2.close();
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }
}
