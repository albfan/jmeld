package org.jmeld.util.vc.svn;

import org.jmeld.util.vc.*;

import java.io.*;

public class SubversionVersionControl
       implements VersionControlIF
{
  public BlameIF getBlame(File file)
  {
    BlameCmd cmd;

    cmd = new BlameCmd(file);
    cmd.execute();
    return cmd.getBlame();
  }
}
