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
import org.jmeld.util.*;

import java.util.*;

public class JMDiff
{
  private List<JMDiffAlgorithmIF> algorithms;

  public JMDiff()
  {
    MyersDiff myersDiff;

    // Timing/Memory (msec/Mb):
    //                                             Myers  Eclipse GNU Hunt
    //  ================================================================================
    //  2 Totally different files  (116448 lines)  31317  1510    340 195
    //  2 Totally different files  (232896 lines)  170673 212     788 354
    //  2 Medium different files  (1778583 lines)  41     55      140 24679
    //  2 Medium different files (10673406 lines)  216    922     632 >300000
    //  2 Equal files             (1778583 lines)  32     55      133 24632
    //  2 Equal files            (10673406 lines)  121    227     581 >60000
    myersDiff = new MyersDiff();
    myersDiff.checkMaxTime(true);

    // MyersDiff is the fastest but can be very slow when 2 files
    //   are very different.
    algorithms = new ArrayList<JMDiffAlgorithmIF>();
    algorithms.add(myersDiff);

    // GNUDiff is a little bit slower than Myersdiff but performs way
    //   better if the files are very different.
    // Don't use it for now because of GPL
    //algorithms.add(new GNUDiff());

    // EclipseDiff looks like Myersdiff but is slower.
    // It performs much better if the files are totally different
    algorithms.add(new EclipseDiff());

    // HuntDiff (from netbeans) is very, very slow
    //algorithms.add(new HuntDiff());
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
    StopWatch  sp;

    if (a == null)
    {
      a = new Object[] {  };
    }
    if (b == null)
    {
      b = new Object[] {  };
    }

    sp = new StopWatch();
    sp.start();

    for (JMDiffAlgorithmIF algorithm : algorithms)
    {
      try
      {
        revision = algorithm.diff(a, b);
        revision.filter();

        if (a.length > 1000)
        {
          System.out.println("diff took " + sp.getElapsedTime() + " msec. ["
            + algorithm.getClass() + "]");
        }

        return revision;
      }
      catch (JMeldException ex)
      {
        if (ex.getCause() instanceof MaxTimeExceededException)
        {
          System.out.println("Time exceeded for " + algorithm.getClass()
            + ": try next algorithm");
        }
        else
        {
          throw ex;
        }
      }
    }

    return null;
  }
}
