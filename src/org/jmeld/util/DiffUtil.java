package org.jmeld.util;

import org.apache.commons.jrcs.diff.*;
import org.jmeld.ui.*;

import java.io.*;

public class DiffUtil
{
  public static boolean debug = false;

  public static int getRevisedLine(
    Revision revision,
    int      originalLine)
  {
    Delta delta;
    int   originalAnchor;
    int   originalSize;
    int   revisedAnchor;
    int   revisedSize;
    int   revisedLine;

    if (revision == null)
    {
      return 0;
    }

    revisedLine = originalLine;

    delta = findOriginalDelta(revision, originalLine);
    if (delta != null)
    {
      originalAnchor = delta.getOriginal().anchor();
      originalSize = delta.getOriginal().size();
      revisedAnchor = delta.getRevised().anchor();
      revisedSize = delta.getRevised().size();

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
    Revision revision,
    int      revisedLine)
  {
    Delta delta;
    int   originalAnchor;
    int   originalSize;
    int   revisedAnchor;
    int   revisedSize;
    int   originalLine;

    originalLine = revisedLine;

    delta = findRevisedDelta(revision, revisedLine);
    if (delta != null)
    {
      originalAnchor = delta.getOriginal().anchor();
      originalSize = delta.getOriginal().size();
      revisedAnchor = delta.getRevised().anchor();
      revisedSize = delta.getRevised().size();

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

  private static Delta findOriginalDelta(
    Revision revision,
    int      line)
  {
    return findDelta(revision, line, true);
  }

  private static Delta findRevisedDelta(
    Revision revision,
    int      line)
  {
    return findDelta(revision, line, false);
  }

  private static Delta findDelta(
    Revision revision,
    int      line,
    boolean  originalDelta)
  {
    Delta delta;
    Delta previousDelta;
    int   anchor;

    if (revision == null)
    {
      return null;
    }

    previousDelta = null;
    for (int i = 0; i < revision.size(); i++)
    {
      delta = revision.getDelta(i);

      if (originalDelta)
      {
        anchor = delta.getOriginal().anchor();
      }
      else
      {
        anchor = delta.getRevised().anchor();
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
