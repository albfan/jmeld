package org.jmeld.util.vc.hg;

import org.jmeld.util.*;
import org.jmeld.util.vc.*;
import org.jmeld.util.vc.util.*;

public class InstalledCmd
    extends VcCmd<Boolean>
{
  public InstalledCmd()
  {
  }

  public Result execute()
  {
    super.execute("hg", "--version");

    return getResult();
  }

  protected void build(byte[] data)
  {
    setResultData(Boolean.TRUE);
  }
}
