package org.jmeld.diff;

import org.jmeld.util.*;

import java.util.*;

public class JMDelta
{
  // Class variables:
  private static char    ADD = 'A';
  private static char    DELETE = 'D';
  private static char    CHANGE = 'C';
  private static boolean debug = false;

  // Instance variables:
  private JMChunk    original;
  private JMChunk    revised;
  private char       type;
  private JMRevision revision;
  private JMRevision changeRevision;

  public JMDelta(
    JMChunk original,
    JMChunk revised)
  {
    this.original = original;
    this.revised = revised;

    initType();
  }

  void setRevision(JMRevision revision)
  {
    this.revision = revision;
  }

  public JMChunk getOriginal()
  {
    return original;
  }

  public JMChunk getRevised()
  {
    return revised;
  }

  public boolean isAdd()
  {
    return type == ADD;
  }

  public boolean isDelete()
  {
    return type == DELETE;
  }

  public boolean isChange()
  {
    return type == CHANGE;
  }

  public JMRevision getChangeRevision()
  {
    if (changeRevision == null)
    {
      changeRevision = createChangeRevision();
    }

    return changeRevision;
  }

  private JMRevision createChangeRevision()
  {
    JMDiff        diff;
    char[]        original1;
    Character[]   original2;
    char[]        revised1;
    Character[]   revised2;
    List<String>  o2;
    List<String>  r2;
    JMRevision    rev;
    JMRevision    rev2;
    JMChunk       o;
    JMChunk       r;
    int           anchor;
    int           size;
    WordTokenizer wt;
    int[]         oIndex;
    int[]         rIndex;
    int           oAnchor;
    int           oLength;
    int           rAnchor;
    int           rLength;

    original1 = revision.getOriginalString(original).toCharArray();
    original2 = new Character[original1.length];
    for (int j = 0; j < original1.length; j++)
    {
      original2[j] = new Character(original1[j]);
    }
    revised1 = revision.getRevisedString(revised).toCharArray();
    revised2 = new Character[revised1.length];
    for (int j = 0; j < revised1.length; j++)
    {
      revised2[j] = new Character(revised1[j]);
    }

    try
    {
      //rev = new JMDiff().diff(original2, revised2);
      wt = TokenizerFactory.getInnerDiffTokenizer();
      o2 = wt.getTokens(revision.getOriginalString(original));
      r2 = wt.getTokens(revision.getRevisedString(revised));
      rev = new JMDiff().diff(o2, r2);

      oIndex = new int[o2.size()];
      for (int i = 0; i < o2.size(); i++)
      {
        oIndex[i] = o2.get(i).length();
        if (i > 0)
        {
          oIndex[i] += oIndex[i - 1];
        }
        debug("oIndex[" + i + "] = " + oIndex[i] + " \"" + o2.get(i) + "\"");
      }

      rIndex = new int[r2.size()];
      for (int i = 0; i < r2.size(); i++)
      {
        rIndex[i] = r2.get(i).length();
        if (i > 0)
        {
          rIndex[i] += rIndex[i - 1];
        }
        debug("rIndex[" + i + "] = " + rIndex[i] + " \"" + r2.get(i) + "\"");
      }

      rev2 = new JMRevision(original2, revised2);
      for (JMDelta d : rev.getDeltas())
      {
        o = d.getOriginal();
        r = d.getRevised();

        int maxSize = Math.max(
            o.getSize(),
            r.getSize());

        anchor = o.getAnchor();
        size = o.getSize();
        oAnchor = anchor == 0 ? 0 : oIndex[anchor - 1];
        oLength = oIndex[anchor + size - 1] - oAnchor;

        anchor = r.getAnchor();
        size = r.getSize();
        rAnchor = anchor == 0 ? 0 : rIndex[anchor - 1];
        rLength = rIndex[anchor + size - 1] - rAnchor;

        JMDelta d2;
        d2 = new JMDelta(
            new JMChunk(oAnchor, oLength),
            new JMChunk(rAnchor, rLength));
        rev2.add(d2);

        debug("delta = " + d + " -> " + d2);
      }

      return rev2;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    return null;
  }

  void initType()
  {
    if (original.getSize() > 0 && revised.getSize() == 0)
    {
      type = DELETE;
    }
    else if (original.getSize() == 0 && revised.getSize() > 0)
    {
      type = ADD;
    }
    else
    {
      type = CHANGE;
    }
  }

  public boolean equals(Object o)
  {
    JMDelta d;

    d = (JMDelta) o;
    if (revision != d.revision)
    {
      return false;
    }

    if (!original.equals(d.original) || !revised.equals(d.revised))
    {
      return false;
    }

    return true;
  }

  private void debug(String s)
  {
    if (debug)
    {
      System.out.println(s);
    }
  }

  public String toString()
  {
    return type + ": org[" + original + "] rev[" + revised + "]";
  }
}
