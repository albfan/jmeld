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

import org.eclipse.compare.rangedifferencer.*;
import org.jmeld.*;

public class EclipseDiff
    extends AbstractJMDiffAlgorithm
{
  public EclipseDiff()
  {
  }

  public JMRevision diff(Object[] orig, Object[] rev)
      throws JMeldException
  {
    RangeDifference[] differences;

    differences = RangeDifferencer.findDifferences(new RangeComparator(orig),
      new RangeComparator(rev));

    return buildRevision(differences, orig, rev);
  }

  private JMRevision buildRevision(RangeDifference[] differences,
      Object[] orig, Object[] rev)
  {
    JMRevision result;

    if (orig == null)
    {
      throw new IllegalArgumentException("original sequence is null");
    }

    if (rev == null)
    {
      throw new IllegalArgumentException("revised sequence is null");
    }

    result = new JMRevision(orig, rev);
    for (RangeDifference rd : differences)
    {
      result.add(new JMDelta(new JMChunk(rd.leftStart(), rd.leftLength()),
          new JMChunk(rd.rightStart(), rd.rightLength())));
    }

    return result;
  }

  private class RangeComparator
      implements IRangeComparator
  {
    private Object[] objectArray;

    RangeComparator(Object[] objectArray)
    {
      this.objectArray = objectArray;
    }

    public int getRangeCount()
    {
      return objectArray.length;
    }

    public boolean rangesEqual(int thisIndex, IRangeComparator other,
        int otherIndex)
    {
      Object o1;
      Object o2;

      o1 = objectArray[thisIndex];
      o2 = ((RangeComparator) other).objectArray[otherIndex];

      if (o1 == o2)
      {
        return true;
      }

      if (o1 == null && o2 != null)
      {
        return false;
      }

      if (o1 != null && o2 == null)
      {
        return false;
      }

      return o1.equals(o2);
    }

    public boolean skipRangeComparison(int length, int maxLength,
        IRangeComparator other)
    {
      return false;
    }
  }
}
