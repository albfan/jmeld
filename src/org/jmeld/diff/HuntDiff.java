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
package org.jmeld.diff;

import org.jmeld.*;
import org.netbeans.api.diff.*;

public class HuntDiff
       implements JMDiffAlgorithmIF
{
  public HuntDiff()
  {
  }

  public JMRevision diff(
    Object[] orig,
    Object[] rev)
    throws JMeldException
  {
    Difference[] differences;

    try
    {
      differences = org.netbeans.modules.diff.builtin.provider.HuntDiff.diff(orig,
          rev);
    }
    catch (Exception ex)
    {
      throw new JMeldException("HuntDiff failed", ex);
    }

    return buildRevision(differences, orig, rev);
  }

  private JMRevision buildRevision(
    Difference[] differences,
    Object[]     orig,
    Object[]     rev)
  {
    JMRevision result;
    int        firstSize;
    int        firstStart;
    int        secondSize;
    int        secondStart;

    if (orig == null)
    {
      throw new IllegalArgumentException("original sequence is null");
    }

    if (rev == null)
    {
      throw new IllegalArgumentException("revised sequence is null");
    }

    result = new JMRevision(orig, rev);
    for (Difference difference : differences)
    {
      System.out.println("difference = " + difference);

      firstStart = difference.getFirstStart() - 1;
      if (firstStart < 0)
      {
        firstStart = 0;
      }

      secondStart = difference.getSecondStart() - 1;
      if (secondStart < 0)
      {
        secondStart = 0;
      }

      firstSize = difference.getFirstEnd() - difference.getFirstStart() + 1;
      if (firstSize < 0)
      {
        firstSize = 0;
      }

      secondSize = difference.getSecondEnd() - difference.getSecondStart() + 1;
      if (secondSize < 0)
      {
        secondSize = 0;
      }

      result.add(
        new JMDelta(
          new JMChunk(firstStart, firstSize),
          new JMChunk(secondStart, secondSize)));
    }

    return result;
  }
}
