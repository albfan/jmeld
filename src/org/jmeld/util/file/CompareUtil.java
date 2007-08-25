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

import org.apache.commons.io.*;
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
    BufferNode nodeRight,
    boolean    ignoreWhitespace)
  {
    if (nodeLeft instanceof FileNode && nodeRight instanceof FileNode)
    {
      return contentEquals((FileNode) nodeLeft, (FileNode) nodeRight,
        ignoreWhitespace);
    }

    return false;
  }

  private static boolean contentEquals(
    FileNode nodeLeft,
    FileNode nodeRight,
    boolean  ignoreWhitespace)
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
    boolean          leftFound;
    boolean          rightFound;
    int              leftChar;
    int              rightChar;
    BufferedReader   readerLeft;
    BufferedReader   readerRight;

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

      if (!ignoreWhitespace && fileLeft.length() != fileRight.length())
      {
        return false;
      }

      if (!ignoreWhitespace)
      {
        fLeft = new RandomAccessFile(fileLeft, "r");
        fRight = new RandomAccessFile(fileRight, "r");
        fcLeft = fLeft.getChannel();
        fcRight = fRight.getChannel();

        bbLeft = fcLeft.map(FileChannel.MapMode.READ_ONLY, 0,
            (int) fcLeft.size());
        bbRight = fcRight.map(FileChannel.MapMode.READ_ONLY, 0,
            (int) fcRight.size());

        equals = bbLeft.equals(bbRight);
      }
      else
      {
        readerLeft = new BufferedReader(new FileReader(fileLeft));
        readerRight = new BufferedReader(new FileReader(fileRight));

        equals = false;
        leftChar = 0;
        rightChar = 0;
        for (;;)
        {
          leftFound = false;
          while ((leftChar = readerLeft.read()) != -1)
          {
            if (Character.isWhitespace(leftChar))
            {
              continue;
            }

            leftFound = true;
            break;
          }

          rightFound = false;
          while ((rightChar = readerRight.read()) != -1)
          {
            if (Character.isWhitespace(rightChar))
            {
              continue;
            }

            rightFound = true;
            break;
          }

          if (leftFound && rightFound)
          {
            if (leftChar == rightChar)
            {
              continue;
            }

            equals = false;
            break;
          }

          if ((leftFound && !rightFound) || (!leftFound && rightFound))
          {
            equals = false;
            break;
          }

          if (!leftFound && !rightFound)
          {
            equals = true;
            break;
          }
        }
      }

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

  public static boolean contentEquals(
    char[]  left,
    char[]  right,
    boolean ignoreWhitespace)
  {
    boolean equals;
    boolean leftFound;
    boolean rightFound;
    char    leftChar;
    char    rightChar;
    int     leftIndex;
    int     rightIndex;

    leftIndex = 0;
    rightIndex = 0;

    if (!ignoreWhitespace)
    {
      equals = Arrays.equals(left, right);
    }
    else
    {
      equals = false;
      leftChar = 0;
      rightChar = 0;
      for (;;)
      {
        leftFound = false;
        while (leftIndex < left.length)
        {
          leftChar = left[leftIndex];
          leftIndex++;

          if (Character.isWhitespace(leftChar))
          {
            continue;
          }

          leftFound = true;
          break;
        }

        rightFound = false;
        while (rightIndex < right.length)
        {
          rightChar = right[rightIndex];
          rightIndex++;

          if (Character.isWhitespace(rightChar))
          {
            continue;
          }

          rightFound = true;
          break;
        }

        if (leftFound && rightFound)
        {
          if (leftChar == rightChar)
          {
            continue;
          }

          equals = false;
          break;
        }

        if ((leftFound && !rightFound) || (!leftFound && rightFound))
        {
          equals = false;
          break;
        }

        if (!leftFound && !rightFound)
        {
          equals = true;
          break;
        }
      }
    }

    return equals;
  }
}
