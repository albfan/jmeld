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
package org.jmeld.util.file;

import org.jmeld.util.node.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class CompareUtil
{
  private CompareUtil()
  {
  }

  public static boolean contentEquals(
    BufferNode nodeLeft,
    BufferNode nodeRight)
  {
    if (nodeLeft instanceof FileNode && nodeRight instanceof FileNode)
    {
      return contentEquals((FileNode) nodeLeft, (FileNode) nodeRight);
    }

    return false;
  }

  private static boolean contentEquals(
    FileNode nodeLeft,
    FileNode nodeRight)
  {
    File             fileLeft;
    File             fileRight;
    RandomAccessFile fLeft;
    RandomAccessFile fRight;
    FileChannel      fcLeft;
    FileChannel      fcRight;
    ByteBuffer       bbLeft;
    ByteBuffer       bbRight;
    boolean          equals;

    fLeft = null;
    fRight = null;

    try
    {
      fileLeft = nodeLeft.getFile();
      fileRight = nodeRight.getFile();

      if (fileLeft.isDirectory() || fileRight.isDirectory())
      {
        return true;
      }

      if (fileLeft.length() != fileRight.length())
      {
        return false;
      }

      fLeft = new RandomAccessFile(fileLeft, "r");
      fRight = new RandomAccessFile(fileRight, "r");
      fcLeft = fLeft.getChannel();
      fcRight = fRight.getChannel();

      bbLeft = fcLeft.map(FileChannel.MapMode.READ_ONLY, 0, (int) fcLeft.size());
      bbRight = fcRight.map(FileChannel.MapMode.READ_ONLY, 0,
          (int) fcRight.size());

      equals = bbLeft.equals(bbRight);

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
        if (fLeft != null)
        {
          fLeft.close();
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }

      try
      {
        if (fRight != null)
        {
          fRight.close();
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }
}
