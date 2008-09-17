package org.jmeld.util.vc.svn;

import org.jmeld.util.*;

import java.io.*;

public class StatusCmd
       extends SvnXmlCmd<StatusData>
{
  private File file;

  public StatusCmd(File file)
  {
    super(StatusData.class);

    this.file = file;
  }

  public Result execute()
  {
    super.execute(
      "svn",
      "status",
      "--non-interactive",
      "-v",
      "--xml",
      file.getPath());

    return getResult();
  }

  public StatusData getStatusData()
  {
    return getResultData();
  }

  public static void main(String[] args)
  {
    StatusCmd           cmd;
    StatusData.WcStatus wcStatus;

    cmd = new StatusCmd(new File(args[0]));
    if (cmd.execute().isTrue())
    {
      for (StatusData.Target target : cmd.getStatusData().getTargetList())
      {
        for (StatusData.Entry entry : target.getEntryList())
        {
          wcStatus = entry.getWcStatus();
          if(wcStatus.getItem() == StatusData.ItemStatus.normal)
          {
            continue;
          }

          System.out.println(wcStatus.getItem().getShortText() + " "
            + entry.getPath());
        }
      }
    }
    else
    {
      cmd.printError();
    }
  }
}
