package org.jmeld.vc.hg;

import org.jmeld.util.*;
import org.jmeld.vc.*;
import org.jmeld.vc.util.*;

import java.io.*;

public class CatCmd
    extends VcCmd<BaseFile>
{
  // Instance variables:
  private File file;

  public CatCmd(File file)
  {
    this.file = file;
    
    initWorkingDirectory(file);
  }

  public Result execute()
  {
    super.execute("hg", "cat", "--noninteractive", file.getAbsolutePath());

    return getResult();
  }

  protected void build(byte[] data)
  {
    setResultData(new BaseFile(data));
  }
}
