package org.jmeld.diff;

import org.apache.commons.jrcs.diff.*;
import org.apache.commons.jrcs.diff.Diff;
import org.apache.commons.jrcs.diff.myers.*;
import org.gnu.diff.*;
import org.jmeld.util.*;

public class JMeldDiff
{
  private DiffAlgorithm algorithm;

  public JMeldDiff()
  {
    MyersDiff.checkMaxTime = true;

    // MyersDiff is the fastest but can be very slow when 2 files
    //   are very different.
    algorithm = new MyersDiff();
  }

  public Revision diff(Object[] a, Object[] b)
    throws DifferentiationFailedException
  {
    Diff     diff;
    Revision revision;
    Delta    delta;
    Chunk    original;
    Chunk    revised;
    int      line;

    // First try MyersAlgoritm and if that takes too much time : Then
    //   try GNUAlgoritm.
    for (;;)
    {
      try
      {
        diff = new Diff(a, algorithm);
        revision = diff.diff(b);
        System.out.println(revision);
        /*
        for (int i = 0; i < revision.size(); i++)
        {
          delta = revision.getDelta(i);
          original = delta.getOriginal();
          revised = delta.getRevised();

          System.out.println("org[" + original.anchor() + "-"
            + original.size() + "]rev[" + revised.anchor() + "-"
            + revised.size() + "]");

          for (int j = 0; j < original.size(); j++)
          {
            line = original.anchor() + j;
            System.out.println("org[" + line + "] = "
              + StringUtil.replaceNewLines(a[line].toString()));
          }
          for (int j = 0; j < revised.size(); j++)
          {
            line = revised.anchor() + j;
            System.out.println("rev[" + line + "] = "
              + StringUtil.replaceNewLines(b[line].toString()));
          }
        }
          */

        return revision;
      }
      catch (MaxTimeExceededException ex)
      {
        // GNUDiff is slower than MyersDiff but much faster if 2 files
        //   are very different.
        algorithm = new GNUDiff();
      }
    }
  }
}
