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
  }

  public JMRevision diff(
    List<String> a,
    List<String> b)
    throws JMeldException
  {
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
