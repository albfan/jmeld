package org.jmeld.util.vc;

import java.io.*;

public interface VersionControlIF
{
  public BlameIF getBlame(File file);

  public DiffIF getDiff(File dir, boolean recursive);
}
