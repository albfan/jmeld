package org.jmeld.diff;

import org.apache.commons.jrcs.diff.*;
import org.gnu.diff.*;
import org.jmeld.*;

public class MyersDiff
       implements JMDiffAlgorithmIF
{
  public MyersDiff()
  {
  }

  public JMRevision diff(
    Object[] orig,
    Object[] rev)
    throws JMeldException
  {
    org.apache.commons.jrcs.diff.myers.MyersDiff diff;
    Revision                                     revision;

    try
    {
      diff = new org.apache.commons.jrcs.diff.myers.MyersDiff();
      revision = diff.diff(orig, rev);
    }
    catch (Exception ex)
    {
      throw new JMeldException("MyersDiff failed", ex);
    }

    return buildRevision(revision, orig, rev);
  }

  private JMRevision buildRevision(
    Revision revision,
    Object[] orig,
    Object[] rev)
  {
    JMRevision result;
    Delta      delta;
    Chunk      original;
    Chunk      revised;

    if (orig == null)
    {
      throw new IllegalArgumentException("original sequence is null");
    }

    if (rev == null)
    {
      throw new IllegalArgumentException("revised sequence is null");
    }

    result = new JMRevision(orig, rev);
    for (int i = 0; i < revision.size(); i++)
    {
      delta = revision.getDelta(i);
      original = delta.getOriginal();
      revised = delta.getRevised();

      result.add(
        new JMDelta(
          new JMChunk(
            original.anchor(),
            original.size()),
          new JMChunk(
            revised.anchor(),
            revised.size())));
    }

    return result;
  }
}
