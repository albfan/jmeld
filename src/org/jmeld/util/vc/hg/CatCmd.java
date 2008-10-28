package org.jmeld.util.vc.hg;

import org.jmeld.util.*;
import org.jmeld.util.vc.*;
import org.jmeld.util.vc.util.*;

import java.io.*;

public class CatCmd
    extends VcCmd<BaseFile>
{
  // Instance variables:
  private File file;

  public CatCmd(File file)
  {
    this.file = file;
  }

  public Result execute()
  {
    super.execute("hg", "cat", "--noninteractive", file.getPath());

    return getResult();
  }

  protected void build(byte[] data)
  {
    setResultData(new BaseFile(data));
  }
}
