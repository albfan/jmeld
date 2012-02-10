package org.jmeld.vc.hg;

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

    initWorkingDirectory(file);
  }

  public Result execute()
  {
    // If a root can be found than we have a mercurial working directory!
    super.execute("hg", "root", "--noninteractive");

    return getResult();
  }

  protected void build(byte[] data)
  {
    setResultData(Boolean.TRUE);
  }
}
