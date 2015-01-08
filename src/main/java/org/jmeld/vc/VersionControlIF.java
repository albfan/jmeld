package org.jmeld.vc;

import java.io.*;
import java.util.Vector;

public interface VersionControlIF
{
  public boolean isInstalled();

  public String getName();

  public boolean isEnabled(File file);

  //public BlameIF executeBlame(File file);

  //public DiffIF executeDiff(File dir, boolean recursive);

  public StatusResult executeStatus(File dir);

  public BaseFile getBaseFile(File dir);

  public Vector getRevisions(File file);
}
