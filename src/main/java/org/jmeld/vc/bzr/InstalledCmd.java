package org.jmeld.vc.bzr;

import org.jmeld.util.*;
import org.jmeld.vc.*;
import org.jmeld.vc.util.*;

public class InstalledCmd
    extends VcCmd<Boolean>
{
  public InstalledCmd()
  {
  }

  public Result execute()
  {
    super.execute("bzr", "version");

    return getResult();
  }

  protected void build(byte[] data)
  {
    setResultData(Boolean.TRUE);
  }
}
