package org.jmeld.vc.svn;

import org.jmeld.util.*;

import java.io.*;

public class LogCmd
    extends SvnXmlCmd<LogData>
{
  private File file;

  public LogCmd(File file)
  {
    super(LogData.class);

    this.file = file;
  }

  public Result execute()
  {
    super.execute("svn", "log", "--non-interactive", "-v", "--xml", file
        .getPath());

    return getResult();
  }

  public static void main(String[] args)
  {
    LogCmd cmd;

    File file = parseFile(args);
    if (file == null) {
      return;
    }
    cmd = new LogCmd(file);
    if (cmd.execute().isTrue())
    {
      for (LogData.Entry entry : cmd.getResultData().getEntryList())
      {
        System.out.println(entry.getRevision() + " : " + entry.getDate());
        for (LogData.Path path : entry.getPathList())
        {
          System.out.println("  " + path.getPathName());
        }
      }
    }
    else
    {
      cmd.printError();
    }
  }
}
