package org.jmeld.util.svn;

import org.jmeld.util.*;

import java.io.*;

public class InfoCmd
       extends SvnXmlCmd<InfoData>
{
  private File file;

  public InfoCmd(File file)
  {
    this.file = file;
  }

  public Result execute()
  {
    return super.execute(
      InfoData.class,
      "svn",
      "info",
      "--non-interactive",
      "-R",
      "--xml",
      file.getPath());
  }

  public InfoData getInfoData()
  {
    return getResultData();
  }

  public static void main(String[] args)
  {
    InfoCmd cmd;

    cmd = new InfoCmd(new File(args[0]));
    if (cmd.execute().isTrue())
    {
      for (InfoData.Entry entry : cmd.getInfoData().getEntryList())
      {
        System.out.println(entry.getRevision() + " : " + entry.getPath());
      }
    }
    else
    {
      cmd.printError();
    }
  }
}
