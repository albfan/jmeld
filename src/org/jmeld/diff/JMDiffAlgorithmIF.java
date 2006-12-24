package org.jmeld.diff;

import org.jmeld.*;

public interface JMDiffAlgorithmIF
{
  public JMRevision diff(
    Object[] orig,
    Object[] rev)
    throws JMeldException;
}
