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
import org.jmeld.ui.text.*;
import org.jmeld.util.*;
import org.jmeld.util.file.*;

import java.util.*;
import java.nio.*;

public class JMDiff
{
  public static int BUFFER_SIZE=100000;
  // Class variables:
  // Allocate a charBuffer once for performance. The charbuffer is used to
  //   store a 'line' without it's ignored characters. 
  static final private CharBuffer inputLine = CharBuffer.allocate(BUFFER_SIZE);
  static final private CharBuffer outputLine = CharBuffer.allocate(BUFFER_SIZE);
  // Instance variables:
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
    // are very different.
    algorithms = new ArrayList<JMDiffAlgorithmIF>();
    //algorithms.add(myersDiff);

    // EclipseDiff looks like Myersdiff but is slower.
    // It performs much better if the files are totally different
    algorithms.add(new EclipseDiff());

    // HuntDiff (from netbeans) is very, very slow
    //algorithms.add(new HuntDiff());
  }

  public JMRevision diff(List<String> a, List<String> b, Ignore ignore)
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
    return diff(a.toArray(), b.toArray(), ignore);
  }

  public JMRevision diff(Object[] a, Object[] b, Ignore ignore)
      throws JMeldException
  {
    JMRevision revision;
    StopWatch sp;
    boolean filtered;
    Object[] org;
    Object[] rev;
    long filteredTime;

    org = a;
    rev = b;

    if (org == null)
    {
      org = new Object[] {};
    }
    if (rev == null)
    {
      rev = new Object[] {};
    }

      filtered = org instanceof AbstractBufferDocument.Line[]
              && rev instanceof AbstractBufferDocument.Line[];

    sp = new StopWatch();
    sp.start();

    if (filtered)
    {
      org = filter(ignore, org);
      rev = filter(ignore, rev);
    }

    filteredTime = sp.getElapsedTime();

    for (JMDiffAlgorithmIF algorithm : algorithms)
    {
      try
      {
        revision = algorithm.diff(org, rev);
        revision.setIgnore(ignore);
        revision.update(a, b);
//        revision.filter();
        if (filtered)
        {
          adjustRevision(revision, a, (JMString[]) org, b, (JMString[]) rev);
        }

        if (a.length > 1000)
        {
          System.out.println("diff took " + sp.getElapsedTime()
                             + " msec. [filter=" + filteredTime + " msec]["
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

  private void adjustRevision(JMRevision revision, Object[] orgArray,
      JMString[] orgArrayFiltered, Object[] revArray,
      JMString[] revArrayFiltered)
  {
    JMChunk chunk;
    int anchor;
    int size;
    int index;

    for (JMDelta delta : revision.getDeltas())
    {
      chunk = delta.getOriginal();
      //System.out.print("  original=" + chunk);
      index = chunk.getAnchor();
      if (index < orgArrayFiltered.length)
      {
        anchor = orgArrayFiltered[index].lineNumber;
      }
      else
      {
        anchor = orgArray.length;
      }

      size = chunk.getSize();
      if (size > 0)
      {
        index += chunk.getSize() - 1;
        if (index < orgArrayFiltered.length)
        {
          size = orgArrayFiltered[index].lineNumber - anchor + 1;
        }
        /*
           index += chunk.getSize();
           if (index < orgArrayFiltered.length)
           {
             size = orgArrayFiltered[index].lineNumber - anchor;
           }
         */
      }
      chunk.setAnchor(anchor);
      chunk.setSize(size);
      //System.out.println(" => " + chunk);

      chunk = delta.getRevised();
      //System.out.print("  revised=" + chunk);
      index = chunk.getAnchor();
      if (index < revArrayFiltered.length)
      {
        //System.out.print(" [index=" + index + ", text="
        //+ revArrayFiltered[index].s + "]");
        anchor = revArrayFiltered[index].lineNumber;
      }
      else
      {
        anchor = revArray.length;
      }
      size = chunk.getSize();
      if (size > 0)
      {
        index += chunk.getSize() - 1;
        if (index < revArrayFiltered.length)
        {
          size = revArrayFiltered[index].lineNumber - anchor + 1;
        }
        /*
           index += chunk.getSize();
           if (index < revArrayFiltered.length)
           {
             size = revArrayFiltered[index].lineNumber - anchor;
           }
         */
      }
      chunk.setAnchor(anchor);
      chunk.setSize(size);
      //System.out.println(" => " + chunk);
    }
  }

  private JMString[] filter(Ignore ignore, Object[] array)
  {
    List<JMString> result;
    JMString jms;
    int lineNumber;

    synchronized (inputLine)
    {
      //System.out.println("> start");
      result = new ArrayList<JMString>(array.length);
      lineNumber = -1;
      for (Object o : array)
      {
        lineNumber++;

        inputLine.clear();
        inputLine.put(o.toString());
        CompareUtil.removeIgnoredChars(inputLine, ignore, outputLine);
        if (outputLine.remaining() == 0)
        {
          continue;
        }

        jms = new JMString();
        jms.s = outputLine.toString();
        jms.lineNumber = lineNumber;
        result.add(jms);

        //System.out.println("  " + jms);
      }
    }

    return result.toArray(new JMString[result.size()]);
  }

  class JMString
  {
    String s;
    int lineNumber;

    @Override
    public int hashCode()
    {
      return s.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
      return s.equals(((JMString) o).s);
    }

    @Override
    public String toString()
    {
      return "[" + lineNumber + "] " + s;
    }
  }
}
