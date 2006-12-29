package org.jmeld.diff;

import org.jmeld.util.*;

import java.util.*;

public class JMDelta
{
  // Class variables:
  private static int     ADD = 1;
  private static int     DELETE = 2;
  private static int     CHANGE = 3;
  private static boolean debug = true;

  // Instance variables:
  private JMChunk    original;
  private JMChunk    revised;
  private int        type;
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
    JMDiff       diff;
    char[]       original1;
    Character[]  original2;
    char[]       revised1;
    Character[]  revised2;
    List<String> o2;
    List<String> r2;
    JMRevision   rev;
    JMChunk      o;
    JMChunk      r;
    int          anchor;
    int          size;

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

    /*
       o2 = new WordTokenizer(revision.getOriginalString(original)).getTokens();
       r2 = new WordTokenizer(revision.getRevisedString(revised)).getTokens();
       int[] start_o2;
       int[] start_r2;
       start_o2 = new int[o2.size()];
       for (int i = 0; i < o2.size(); i++)
       {
         start_o2[i] = start_o2[i - 1] + o2.get(i).length();
       }
       start_r2 = new int[r2.size()];
       for (int i = 0; i < r2.size(); i++)
       {
         start_r2[i] = start_r2[i - 1] + r2.get(i).length();
       }
       if (debug)
       {
         size = o2.size() > r2.size() ? o2.size() : r2.size();
         System.out.println("START O2");
         for (int i = 0; i < o2.size(); i++)
         {
           System.out.println(o2.get(i));
         }
         System.out.println("END O2");
         System.out.println("START R2");
         for (int i = 0; i < r2.size(); i++)
         {
           System.out.println(r2.get(i));
         }
         System.out.println("END r2");
         size = o2.size() > r2.size() ? o2.size() : r2.size();
         for (int i = 0; i < size; i++)
         {
           System.out.printf("[%03d] \"%s\" \"%s\"\n", i,
             (i < o2.size() ? o2.get(i) : ""), (i < r2.size() ? r2.get(i) : ""));
         }
       }
     */
    try
    {
      rev = new JMDiff().diff(original2, revised2);
      /*
         rev = new JMDiff().diff(o2, r2);
         for (JMDelta d : rev.getDeltas())
         {
           o = d.getOriginal();
           r = d.getRevised();
           if (debug)
           {
             System.out.printf(
               "o[%03d,%03d] -> r[%03d,%03d]\n",
               o.getAnchor(),
               o.getSize(),
               r.getAnchor(),
               r.getSize());
           }
         }
         for (JMDelta d : rev.getDeltas())
         {
           o = d.getOriginal();
           anchor = o.getAnchor();
           size = o.getSize();
           o.setAnchor(start_o2[anchor]);
           o.setSize(start_o2[anchor + size - 1] - start_o2[anchor]
             + o2.get(anchor + size).length());
           r = d.getRevised();
           anchor = r.getAnchor();
           size = r.getSize();
           r.setAnchor(start_r2[anchor]);
           r.setSize(start_r2[anchor + size - 1] - start_r2[anchor]
             + r2.get(anchor + size).length());
         }
         if (debug)
         {
           System.out.println("after");
         }
         for (JMDelta d : rev.getDeltas())
         {
           o = d.getOriginal();
           r = d.getRevised();
           if (debug)
           {
             System.out.printf(
               "o[%03d,%03d] -> r[%03d,%03d]\n",
               o.getAnchor(),
               o.getSize(),
               r.getAnchor(),
               r.getSize());
           }
         }
         debug = false;
       */
      return rev;
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
}
