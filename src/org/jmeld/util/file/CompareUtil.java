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
import java.nio.charset.*;
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

/*
  private static boolean contentEquals2(
    FileNode nodeLeft,
    FileNode nodeRight,
    boolean  ignoreWhitespace)
  {
    try
    {
      return IOUtils.contentEquals(
        new BufferedReader(new FileReader(nodeLeft.getFile())),
        new BufferedReader(new FileReader(nodeRight.getFile())));
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return false;
    }
  }
  */

  private static boolean contentEquals(
    FileNode nodeLeft,
    FileNode nodeRight,
    boolean  ignoreWhitespace)
  {
    return contentEquals1(nodeLeft, nodeRight, ignoreWhitespace);
  }

  private static boolean contentEquals1(
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
    byte             leftByte;
    byte             rightByte;

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

      fLeft = new RandomAccessFile(fileLeft, "r");
      fRight = new RandomAccessFile(fileRight, "r");
      fcLeft = fLeft.getChannel();
      fcRight = fRight.getChannel();

      bbLeft = fcLeft.map(FileChannel.MapMode.READ_ONLY, 0, (int) fcLeft.size());
      bbRight = fcRight.map(FileChannel.MapMode.READ_ONLY, 0,
          (int) fcRight.size());

      if (!ignoreWhitespace)
      {
        equals = bbLeft.equals(bbRight);
      }
      else
      {
        // I should do some charset decoding here!
        equals = false;
        leftByte = 0;
        rightByte = 0;
        for (;;)
        {
          leftFound = false;
          while (bbLeft.hasRemaining())
          {
            leftByte = bbLeft.get();
            // 0x0A = LINE FEED
            // 0x0C = FORM FEED
            // 0x0D = CARRIAGE RETURN
            // 0x20 = SPACE
            if (leftByte == 0x0A || leftByte == 0x0C || leftByte == 0x0D
              || leftByte == 0x20)
            {
              continue;
            }

            leftFound = true;
            break;
          }

          rightFound = false;
          while (bbRight.hasRemaining())
          {
            rightByte = bbRight.get();
            if (rightByte == 0x0A || rightByte == 0x0C || rightByte == 0x0D
              || rightByte == 0x20)
            {
              continue;
            }

            rightFound = true;
            break;
          }

          if (leftFound && rightFound)
          {
            if (leftByte == rightByte)
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

  private static boolean contentEquals2(
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
    Charset          charset;
    CharBuffer       cbLeft;
    CharBuffer       cbRight;
    boolean          equals;
    boolean          leftFound;
    boolean          rightFound;
    char             leftChar;
    char             rightChar;

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

      fLeft = new RandomAccessFile(fileLeft, "r");
      fRight = new RandomAccessFile(fileRight, "r");
      fcLeft = fLeft.getChannel();
      fcRight = fRight.getChannel();

      bbLeft = fcLeft.map(FileChannel.MapMode.READ_ONLY, 0, (int) fcLeft.size());
      bbRight = fcRight.map(FileChannel.MapMode.READ_ONLY, 0,
          (int) fcRight.size());

      if (!ignoreWhitespace)
      {
        equals = bbLeft.equals(bbRight);
      }
      else
      {
        charset = Charset.defaultCharset();
        cbLeft = charset.decode(bbLeft);
        cbRight = charset.decode(bbRight);

        equals = false;
        leftChar = 0;
        rightChar = 0;
        for (;;)
        {
          leftFound = false;
          while (cbLeft.hasRemaining())
          {
            leftChar = cbLeft.get();
            if (Character.isWhitespace(leftChar))
            {
              continue;
            }

            leftFound = true;
            break;
          }

          rightFound = false;
          while (cbRight.hasRemaining())
          {
            rightChar = cbRight.get();
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
