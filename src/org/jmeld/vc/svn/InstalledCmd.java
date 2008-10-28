package org.jmeld.vc.svn;

import org.jmeld.util.*;
import org.jmeld.vc.*;
import org.jmeld.vc.util.*;

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
