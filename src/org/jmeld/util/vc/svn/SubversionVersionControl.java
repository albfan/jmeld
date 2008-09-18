package org.jmeld.util.vc.svn;

import org.jmeld.util.vc.*;

import java.io.*;

public class SubversionVersionControl
       implements VersionControlIF
{
  public BlameIF executeBlame(File file)
  {
    BlameCmd cmd;

    cmd = new BlameCmd(file);
    cmd.execute();
    return cmd.getResultData();
  }

  public DiffIF executeDiff(File file, boolean recursive)
  {
    DiffCmd cmd;

    cmd = new DiffCmd(file, recursive);
    cmd.execute();
    return cmd.getResultData();
  }

  public StatusIF executeStatus(File file, boolean recursive)
  {
    StatusCmd cmd;

    cmd = new StatusCmd(file, recursive);
    cmd.execute();
    return cmd.getResultData();
  }

  public BaseFile getBaseFile(File file)
  {
    CatCmd cmd;

    cmd = new CatCmd(file);
    cmd.execute();
    return cmd.getResultData();
  }
}
