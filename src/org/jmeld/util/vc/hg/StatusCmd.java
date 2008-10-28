package org.jmeld.util.vc.hg;

import org.jmeld.util.vc.*;
import org.jmeld.util.vc.util.*;

import org.jmeld.util.*;

import java.io.*;

public class StatusCmd
    extends VcCmd<StatusResult>
{
  private File file;

  public StatusCmd(File file)
  {
    this.file = file;
  }

  public Result execute()
  {
    super.execute("hg", "status", "--noninteractive", "--cwd", file.getPath());

    return getResult();
  }

  protected void build(byte[] data)
  {
    StatusResult statusResult;
    StatusResult.Status status;

    statusResult = new StatusResult(file);

    setResultData(statusResult);
  }

  public static void main(String[] args)
  {
    StatusCmd cmd;
    StatusResult result;

    result = new MercurialVersionControl().executeStatus(new File(args[0]));
    if (result != null)
    {
      for (StatusResult.Entry entry : result.getEntryList())
      {
        System.out.println(entry.getStatus().getShortText() + " "
                           + entry.getName());
      }
    }
  }
}
