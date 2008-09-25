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

import org.jmeld.util.*;
import org.jmeld.util.node.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class CompareUtil
{
  private static final int MAX_LINE_NUMBER = 1000;
  private static char[]    leftLine = new char[MAX_LINE_NUMBER];
  private static char[]    rightLine = new char[MAX_LINE_NUMBER];

  private CompareUtil()
  {
  }

  public static boolean contentEquals(
    BufferNode nodeLeft,
    BufferNode nodeRight,
    Ignore     ignore)
  {
    if (nodeLeft instanceof FileNode && nodeRight instanceof FileNode)
    {
      return contentEquals((FileNode) nodeLeft, (FileNode) nodeRight, ignore);
    }

    return false;
  }

  private static boolean contentEquals(
    FileNode nodeLeft,
    FileNode nodeRight,
    Ignore   ignore)
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

    fileLeft = nodeLeft.getFile();
    fileRight = nodeRight.getFile();

    fLeft = null;
    fRight = null;

    try
    {
      if (fileLeft.isDirectory() || fileRight.isDirectory())
      {
        return true;
      }

      if (!ignore.ignore && fileLeft.length() != fileRight.length())
      {
        return false;
      }

      // In practice most files that have the same length will
      //   be equal. So eventhough some ignore feature is activated
      //   we will examine if the files are equal. If they are
      //   equal we won't have to execute the expensive 
      //   contentEquals method below. This should speed up directory
      //   comparisons quite a bit.
      if (!ignore.ignore ||
          fileLeft.length() == fileRight.length())
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
        if(!ignore.ignore || equals)
        {
          return equals;
        }
      }

      equals = contentEquals(
                nodeLeft.getDocument().getReader(),
                nodeRight.getDocument().getReader(),
                ignore);

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
    char[] left,
    char[] right,
    Ignore ignore)
  {
    try
    {
      return contentEquals(
        new CharArrayReader(left),
        new CharArrayReader(right),
        ignore);
    }
    catch (IOException ex)
    {
      // IOException will never happen!
      return false;
    }
  }

  /** Test if 2 readers are equals (with ignore possibilities).
   *  Synchronized because leftLine and rightLine are static variables for
   *    performance reasons.
   */
  private static synchronized boolean contentEquals(
    Reader readerLeft,
    Reader readerRight,
    Ignore ignore)
    throws IOException
  {
    boolean equals;
    boolean leftFound;
    boolean rightFound;
    int     leftChar;
    int     rightChar;
    int     nextChar;
    boolean eol;
    boolean previousEolLeft;
    boolean previousEolRight;
    int     leftLineIndex;
    int     rightLineIndex;
    boolean whitespaceAtBegin;
    int     whitespaceIndex;

    leftLineIndex = 0;
    rightLineIndex = 0;

    equals = false;
    leftChar = 0;
    rightChar = 0;
    previousEolLeft = true;
    previousEolRight = true;

    try
    {
      for (;;)
      {
        leftFound = false;
        whitespaceAtBegin = true;
        whitespaceIndex = -1;
        while ((leftChar = readerLeft.read()) != -1)
        {
          eol = isEOL(leftChar);

          if ((ignore.ignoreEOL || ignore.ignoreBlankLines
            || ignore.ignoreWhitespace) && eol)
          {
            readerLeft.mark(1);
            nextChar = readerLeft.read();

            // Try to read the following combinations as 1 newline:
            // 1. '\n'
            // 2. '\r'
            // 3. '\r\n'
            // 4. '\n\r'
            if (!((leftChar == '\n' && nextChar == '\r')
              || (leftChar == '\r' && nextChar == '\n')))
            {
              // The next character doesn't belong to a newline -> unread it!
              readerLeft.reset();
            }

            // Replace all combinations of a newline with '\n'. The compare is now easy.
            leftChar = '\n';
          }

          if (ignore.ignoreBlankLines && previousEolLeft && eol)
          {
            continue;
          }

          if (!eol)
          {
            if (ignore.ignoreWhitespace)
            {
              if (Character.isWhitespace(leftChar))
              {
                if (whitespaceIndex == -1)
                {
                  whitespaceIndex = leftLineIndex;
                }
              }
              else
              {
                if (whitespaceIndex != -1)
                {
                  if (whitespaceAtBegin)
                  {
                    whitespaceAtBegin = false;
                    if (ignore.ignoreWhitespaceAtBegin)
                    {
                      leftLineIndex = whitespaceIndex;
                    }
                  }
                  else
                  {
                    if (ignore.ignoreWhitespaceInBetween)
                    {
                      leftLineIndex = whitespaceIndex;
                    }
                  }
                  whitespaceIndex = -1;
                }
              }
            }

            if (ignore.ignoreCase)
            {
              leftChar = Character.toLowerCase(leftChar);
            }
          }

          previousEolLeft = eol;

          leftLine[leftLineIndex] = (char) leftChar;
          leftLineIndex++;

          if (eol || leftLineIndex >= MAX_LINE_NUMBER)
          {
            if (whitespaceIndex != -1 && ignore.ignoreWhitespaceAtEnd)
            {
              leftLineIndex = whitespaceIndex;
            }

            break;
          }
        }

        rightFound = false;
        whitespaceAtBegin = true;
        whitespaceIndex = -1;
        while ((rightChar = readerRight.read()) != -1)
        {
          eol = isEOL(rightChar);

          if ((ignore.ignoreEOL || ignore.ignoreBlankLines
            || ignore.ignoreWhitespace) && eol)
          {
            readerRight.mark(1);
            nextChar = readerRight.read();

            // Try to read the following combinations as 1 newline:
            // 1. '\n'
            // 2. '\r'
            // 3. '\r\n'
            // 4. '\n\r'
            if (!((rightChar == '\n' && nextChar == '\r')
              || (rightChar == '\r' && nextChar == '\n')))
            {
              // The next character doesn't belong to a newline -> unread it!
              readerRight.reset();
            }

            rightChar = '\n';
          }

          if (ignore.ignoreBlankLines && previousEolRight && eol)
          {
            continue;
          }

          if (!eol)
          {
            if (ignore.ignoreWhitespace)
            {
              if (Character.isWhitespace(rightChar))
              {
                if (whitespaceIndex == -1)
                {
                  whitespaceIndex = rightLineIndex;
                }
              }
              else
              {
                if (whitespaceIndex != -1)
                {
                  if (whitespaceAtBegin)
                  {
                    whitespaceAtBegin = false;
                    if (ignore.ignoreWhitespaceAtBegin)
                    {
                      rightLineIndex = whitespaceIndex;
                    }
                  }
                  else
                  {
                    if (ignore.ignoreWhitespaceInBetween)
                    {
                      rightLineIndex = whitespaceIndex;
                    }
                  }
                  whitespaceIndex = -1;
                }
              }
            }
            if (ignore.ignoreCase)
            {
              rightChar = Character.toLowerCase(rightChar);
            }
          }

          previousEolRight = eol;

          rightLine[rightLineIndex] = (char) rightChar;
          rightLineIndex++;

          if (eol || rightLineIndex >= MAX_LINE_NUMBER)
          {
            if (whitespaceIndex != -1 && ignore.ignoreWhitespaceAtEnd)
            {
              rightLineIndex = whitespaceIndex;
            }

            break;
          }
        }

        if (leftLineIndex > 0 && leftLineIndex == rightLineIndex)
        {
          if (equals(leftLine, rightLine, leftLineIndex))
          {
            leftLineIndex = 0;
            rightLineIndex = 0;
            continue;
          }

          equals = false;
          break;
        }

        if (leftLineIndex != rightLineIndex)
        {
          equals = false;
          break;
        }

        if (leftLineIndex == 0 && rightLineIndex == 0)
        {
          equals = true;
          break;
        }
      }
    }
    finally
    {
      readerLeft.close();
      readerRight.close();
    }

    return equals;
  }

  private static boolean equals(
    char[] a1,
    char[] a2,
    int    size)
  {
    for (int i = 0; i < size; i++)
    {
      if (a1[i] != a2[i])
      {
        return false;
      }
    }

    return true;
  }

  public static boolean isEOL(int character)
  {
    return character == '\n' || character == '\r';
  }
}
