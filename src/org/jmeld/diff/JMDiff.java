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

import org.gnu.diff.*;
import org.jmeld.*;
import org.jmeld.util.*;

import java.util.*;

public class JMDiff
{
  private List<JMDiffAlgorithmIF> algorithms;

  public JMDiff()
  {
    MyersDiff myersDiff;

    myersDiff = new MyersDiff();
    //myersDiff.checkMaxTime(true);

    // MyersDiff is the fastest but can be very slow when 2 files
    //   are very different.
    algorithms = new ArrayList<JMDiffAlgorithmIF>();
    algorithms.add(new GNUDiff());
    algorithms.add(myersDiff);
    algorithms.add(new HuntDiff());
  }

  public JMRevision diff(
    List<String> a,
    List<String> b)
    throws JMeldException
  {
    if (a == null)
    {
      a = Collections.emptyList();
    }
    if (b == null)
    {
      b = Collections.emptyList();
    }
    return diff(
      a.toArray(),
      b.toArray());
  }

  public JMRevision diff(
    Object[] a,
    Object[] b)
    throws JMeldException
  {
    JMRevision revision;

    if (a == null)
    {
      a = new Object[] {  };
    }
    if (b == null)
    {
      b = new Object[] {  };
    }

    for (JMDiffAlgorithmIF algorithm : algorithms)
    {
      try
      {
        revision = algorithm.diff(a, b);
        revision.filter();

        return revision;
      }
      catch (MaxTimeExceededException ex)
      {
        System.out.println("Time exceeded: try next algorithm");
      }
    }

    return null;
  }
}
