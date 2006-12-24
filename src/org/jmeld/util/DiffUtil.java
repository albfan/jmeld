package org.jmeld.util;

import org.jmeld.diff.*;
import org.jmeld.ui.*;

import java.io.*;

public class DiffUtil
{
  public static boolean debug = false;

  public static int getRevisedLine(
    JMRevision revision,
    int        originalLine)
  {
    JMDelta delta;
    int     originalAnchor;
    int     originalSize;
    int     revisedAnchor;
    int     revisedSize;
    int     revisedLine;

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

  public static int getOriginalLine(
    JMRevision revision,
    int        revisedLine)
  {
    JMDelta delta;
    int     originalAnchor;
    int     originalSize;
    int     revisedAnchor;
    int     revisedSize;
    int     originalLine;

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

  private static JMDelta findOriginalDelta(
    JMRevision revision,
    int        line)
  {
    return findDelta(revision, line, true);
  }

  private static JMDelta findRevisedDelta(
    JMRevision revision,
    int        line)
  {
    return findDelta(revision, line, false);
  }

  private static JMDelta findDelta(
    JMRevision revision,
    int        line,
    boolean    originalDelta)
  {
    JMDelta previousDelta;
    int   anchor;

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
