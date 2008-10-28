package org.jmeld.util.vc.svn;

import org.jmeld.util.vc.*;

import org.jmeld.util.*;

import java.io.*;

public class StatusCmd
    extends SvnXmlCmd<StatusData>
{
  private File file;
  private boolean recursive;

  public StatusCmd(File file, boolean recursive)
  {
    super(StatusData.class);

    this.file = file;
    this.recursive = recursive;
  }

  public Result execute()
  {
    super.execute("svn", "status", "--non-interactive", "-v", "--xml",
      recursive ? "" : "-N", file.getPath());

    return getResult();
  }

  public StatusResult getStatusResult()
  {
    StatusData sd;
    StatusResult result;
    StatusResult.Status status;

    sd = getResultData();

    result = new StatusResult(file);
    if (sd != null)
    {
      for (StatusData.Target t : sd.getTargetList())
      {
        for (StatusData.Entry te : t.getEntryList())
        {
          status = null;
          switch (te.getWcStatus().getItem())
          {
            case added:
              status = StatusResult.Status.added;
              break;
            case conflicted:
              status = StatusResult.Status.conflicted;
              break;
          }

          result.addEntry(te.getPath(), status);
        }
      }
    }

    return result;
  }

  public static void main(String[] args)
  {
    StatusCmd cmd;
    StatusResult result;

    result = new SubversionVersionControl().executeStatus(new File(args[0]));
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
