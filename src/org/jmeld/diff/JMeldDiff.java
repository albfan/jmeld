package org.jmeld.diff;

import org.apache.commons.jrcs.diff.*;
import org.apache.commons.jrcs.diff.Diff;
import org.apache.commons.jrcs.diff.myers.*;
import org.gnu.diff.*;
import org.jmeld.util.*;

import java.util.*;

public class JMeldDiff
{
  private List<DiffAlgorithm> algorithms;

  public JMeldDiff()
  {
    MyersDiff myersDiff;

    myersDiff = new MyersDiff();
    myersDiff.checkMaxTime(true);

    // MyersDiff is the fastest but can be very slow when 2 files
    //   are very different.
    algorithms = new ArrayList<DiffAlgorithm>();
    algorithms.add(myersDiff);
    algorithms.add(new GNUDiff());
  }

  public Revision diff(
    Object[] a,
    Object[] b)
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
    for (DiffAlgorithm algorithm : algorithms)
    {
      try
      {
        diff = new Diff(a, algorithm);
        revision = diff.diff(b);

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
