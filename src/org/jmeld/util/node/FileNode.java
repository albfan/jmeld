/*
   JMeld is a visual diff and merge tool.
   Copyright (C) 2007  Kees Kuip
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA  02110-1301  USA
 */
package org.jmeld.util.node;

import org.jmeld.ui.text.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class FileNode
       extends JMeldNode
       implements BufferNode
{
  private File         file;
  private FileDocument document;

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

  public FileDocument getDocument()
  {
    if (document == null)
    {
      document = new FileDocument(file);
    }

    return document;
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
      if(!(node instanceof FileNode))
      {
        return false;
      }

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
