package org.jmeld.util.vc.svn;

import org.jmeld.util.*;
import org.jmeld.util.vc.*;
import org.jmeld.util.vc.util.*;

import java.io.*;

public class InstalledCmd
    extends VcCmd<Boolean>
{
  public InstalledCmd()
  {
  }

  public Result execute()
  {
    super.execute("svn", "--version");

    return getResult();
  }

  protected void build(byte[] data)
  {
    setResultData(Boolean.TRUE);
  }
}
