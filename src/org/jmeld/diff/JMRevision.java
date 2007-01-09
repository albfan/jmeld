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

import java.util.*;

public class JMRevision
{
  private Object[]            org;
  private Object[]            rev;
  private LinkedList<JMDelta> deltas;
  private List<String>        regexes;

  public JMRevision(
    Object[] org,
    Object[] rev)
  {
    this.org = org;
    this.rev = rev;

    deltas = new LinkedList<JMDelta>();

    skip("Kees Kuip");
  }

  public void add(JMDelta delta)
  {
    deltas.add(delta);
    delta.setRevision(this);
  }

  public List<JMDelta> getDeltas()
  {
    return deltas;
  }

  public int getOrgSize()
  {
    return org.length;
  }

  public int getRevSize()
  {
    return rev.length;
  }

  public String getOriginalString(JMChunk chunk)
  {
    return getObjects(org, chunk);
  }

  public String getRevisedString(JMChunk chunk)
  {
    return getObjects(rev, chunk);
  }

  private String getObjects(
    Object[] objects,
    JMChunk  chunk)
  {
    Object[]     result;
    StringBuffer sb;
    int          end;

    if (chunk.getSize() <= 0)
    {
      return "";
    }

    sb = new StringBuffer();
    end = chunk.getAnchor() + chunk.getSize();
    for (int offset = chunk.getAnchor(); offset < end; offset++)
    {
      sb.append(objects[offset].toString());
    }

    return sb.toString();
  }

  public void skip(String regex)
  {
    if (regexes == null)
    {
      regexes = new ArrayList<String>();
    }

    regexes.add(regex);
  }

  public void filter()
  {
    JMChunk   original;
    JMChunk   revised;
    boolean[] originalSkip;
    boolean[] revisedSkip;
    int       end;
    Object    o;
    JMDelta   delta;

    // Skip changes in lines that match a regex.
    if (regexes != null)
    {
      for (ListIterator<JMDelta> it = deltas.listIterator(0); it.hasNext();)
      {
        delta = it.next();

        original = delta.getOriginal();
        revised = delta.getRevised();

        originalSkip = null;
        revisedSkip = null;

        end = original.getAnchor() + original.getSize();
        for (int index = original.getAnchor(); index < end; index++)
        {
          o = org[index];
          if (o == null)
          {
            continue;
          }

          for (String regex : regexes)
          {
            if (o.toString().contains(regex))
            {
              if (originalSkip == null)
              {
                originalSkip = new boolean[original.getSize()];
              }

              originalSkip[index - original.getAnchor()] = true;
            }
          }
        }

        end = revised.getAnchor() + revised.getSize();
        for (int index = revised.getAnchor(); index < end; index++)
        {
          o = rev[index];
          if (o == null)
          {
            continue;
          }

          for (String regex : regexes)
          {
            if (o.toString().contains(regex))
            {
              if (revisedSkip == null)
              {
                revisedSkip = new boolean[revised.getSize()];
              }

              revisedSkip[index - revised.getAnchor()] = true;
            }
          }
        }

        // There are some matches in the delta's that should be 
        //   skipped.
        if (originalSkip != null || revisedSkip != null)
        {
          // I must look at this. It seems that gnu's diff only
          //   can skip whole chunks. So only if ALL the lines in
          //   the original AND in the revised match a regular
          //   expression the chunk is skipped. (Is this useful?)

          // I was planning on creating new chunks here (hence the
          //   linkedlist). But I beter wait a bit.
        }
      }
    }
  }
}
