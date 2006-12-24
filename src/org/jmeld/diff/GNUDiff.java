package org.jmeld.diff;

import org.gnu.diff.*;
import org.jmeld.*;

public class GNUDiff
       implements JMDiffAlgorithmIF
{
  public GNUDiff()
  {
  }

  public JMRevision diff(
    Object[] orig,
    Object[] rev)
    throws JMeldException
  {
    Diff        diff;
    Diff.change change;

    try
    {
      diff = new Diff(orig, rev);
      change = diff.diff_2();
    }
    catch (Exception ex)
    {
      throw new JMeldException("MyersDiff failed", ex);
    }

    return buildRevision(change, orig, rev);
  }

  private JMRevision buildRevision(
    Diff.change change,
    Object[]    orig,
    Object[]    rev)
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
    while (change != null)
    {
      result.add(
        new JMDelta(
          new JMChunk(change.line0, change.deleted),
          new JMChunk(change.line1, change.inserted)));

      change = change.link;
    }

    return result;
  }
}
