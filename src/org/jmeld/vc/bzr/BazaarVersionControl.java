package org.jmeld.vc.bzr;

import org.jmeld.vc.*;

import java.io.*;

public class BazaarVersionControl
    implements VersionControlIF
{
  private Boolean installed;

  public String getName()
  {
    return "bazaar";
  }

  public boolean isInstalled()
  {
    InstalledCmd cmd;

    if (installed == null)
    {
      cmd = new InstalledCmd();
      cmd.execute();
      installed = cmd.getResult().isTrue();
    }

    return installed.booleanValue();
  }

  public boolean isEnabled(File file)
  {
    ActiveCmd cmd;

    cmd = new ActiveCmd(file);
    cmd.execute();

    return cmd.getResult().isTrue();
  }

  public StatusResult executeStatus(File file)
  {
    StatusCmd cmd;

    cmd = new StatusCmd(file);
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

  @Override
  public String toString()
  {
    return getName();
  }
}
