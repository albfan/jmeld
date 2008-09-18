package org.jmeld.util.vc.svn;

import org.jmeld.util.vc.*;

import org.jmeld.util.*;

import java.io.*;

public class StatusCmd
    extends SvnXmlCmd<StatusData>
{
  private File    file;
  private boolean recursive;

  public StatusCmd(File file, boolean recursive)
  {
    super(StatusData.class);

    this.file = file;
    this.recursive = recursive;
  }

  public Result execute()
  {
    super.execute("svn", "status", "--non-interactive", /* "-v",*/ "--xml",
                  recursive ? "" : "-N", file.getPath());

    return getResult();
  }

  public StatusData getStatusData()
  {
    return getResultData();
  }

  public static void main(String[] args)
  {
    StatusCmd cmd;
    StatusIF result;
    StatusIF.WcStatusIF wcStatus;

    result = new SubversionVersionControl().executeStatus(new File(args[0]),
                                                          true);
    if (result != null)
    {
      for (StatusIF.TargetIF target : result.getTargetList())
      {
        for (StatusIF.EntryIF entry : target.getEntryList())
        {
          wcStatus = entry.getWcStatus();
          if (wcStatus.getItem() == StatusData.ItemStatus.normal)
          {
            continue;
          }

          System.out.println(wcStatus.getItem().getShortText() + " "
                             + entry.getPath());
        }
      }
    }
  }
}
