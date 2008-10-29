package org.jmeld.vc.svn;

import org.jmeld.util.*;
import org.jmeld.vc.*;
import org.jmeld.vc.util.*;

import java.io.*;

public class ActiveCmd
    extends VcCmd<Boolean>
{
  private File file;

  public ActiveCmd(File file)
  {
    this.file = file;
  }

  public Result execute()
  {
    // If a root can be found than we have a mercurial working directory!
    super.execute("svn", "info", file.getAbsolutePath());

    return getResult();
  }

  protected void build(byte[] data)
  {
    setResultData(Boolean.TRUE);
  }
}
