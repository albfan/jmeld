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
package org.jmeld.util;

import org.jmeld.diff.*;
import org.jmeld.ui.*;

import java.io.*;

public class DiffUtil
{
  public static boolean debug = false;

  public static int getRevisedLine(JMRevision revision, int originalLine)
  {
    JMDelta delta;
    int originalAnchor;
    int originalSize;
    int revisedAnchor;
    int revisedSize;
    int revisedLine;

    if (revision == null)
    {
      return 0;
    }

    revisedLine = originalLine;

    delta = findOriginalDelta(revision, originalLine);
    if (delta != null)
    {
      originalAnchor = delta.getOriginal().getAnchor();
      originalSize = delta.getOriginal().getSize();
      revisedAnchor = delta.getRevised().getAnchor();
      revisedSize = delta.getRevised().getSize();

      if (originalLine - originalAnchor < originalSize)
      {
        revisedLine = revisedAnchor;
      }
      else
      {
        revisedLine = revisedAnchor + revisedSize - originalSize
                      + (originalLine - originalAnchor);
      }
    }
    else
    {
      originalAnchor = 0;
      originalSize = 0;
      revisedAnchor = 0;
      revisedSize = 0;
    }

    if (debug)
    {
      System.out.printf("%03d-%02d, %03d-%02d == ", originalAnchor,
        originalSize, revisedAnchor, revisedSize);
    }

    return revisedLine;
  }

  public static int getOriginalLine(JMRevision revision, int revisedLine)
  {
    JMDelta delta;
    int originalAnchor;
    int originalSize;
    int revisedAnchor;
    int revisedSize;
    int originalLine;

    originalLine = revisedLine;

    delta = findRevisedDelta(revision, revisedLine);
    if (delta != null)
    {
      originalAnchor = delta.getOriginal().getAnchor();
      originalSize = delta.getOriginal().getSize();
      revisedAnchor = delta.getRevised().getAnchor();
      revisedSize = delta.getRevised().getSize();

      if (revisedLine - revisedAnchor < revisedSize)
      {
        originalLine = originalAnchor;
      }
      else
      {
        originalLine = originalAnchor + originalSize - revisedSize
                       + (revisedLine - revisedAnchor);
      }
    }
    else
    {
      originalAnchor = 0;
      originalSize = 0;
      revisedAnchor = 0;
      revisedSize = 0;
    }

    if (debug)
    {
      System.out.printf("%03d-%02d, %03d-%02d == ", originalAnchor,
        originalSize, revisedAnchor, revisedSize);
    }

    return originalLine;
  }

  private static JMDelta findOriginalDelta(JMRevision revision, int line)
  {
    return findDelta(revision, line, true);
  }

  private static JMDelta findRevisedDelta(JMRevision revision, int line)
  {
    return findDelta(revision, line, false);
  }

  private static JMDelta findDelta(JMRevision revision, int line,
      boolean originalDelta)
  {
    JMDelta previousDelta;
    int anchor;

    if (revision == null)
    {
      return null;
    }

    previousDelta = null;
    for (JMDelta delta : revision.getDeltas())
    {
      if (originalDelta)
      {
        anchor = delta.getOriginal().getAnchor();
      }
      else
      {
        anchor = delta.getRevised().getAnchor();
      }

      if (anchor > line)
      {
        break;
      }

      previousDelta = delta;
    }

    return previousDelta;
  }
}
