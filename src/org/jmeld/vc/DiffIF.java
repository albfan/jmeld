package org.jmeld.vc;

import org.jmeld.diff.*;

import java.util.*;

public interface DiffIF
{
  public List<? extends TargetIF> getTargetList();

  public interface TargetIF
  {
    public String getPath();

    public JMRevision getRevision();
  }
}
