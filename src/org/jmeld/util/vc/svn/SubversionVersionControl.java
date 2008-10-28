package org.jmeld.util.vc.svn;

import org.jmeld.util.vc.*;

import java.io.*;
import java.util.*;

public class SubversionVersionControl
    implements VersionControlIF
{
  public String getName()
  {
    return "subversion";
  }

  public boolean accept(File file)
  {
    StatusCmd cmd;
    StatusData statusData;

    // Don't check for existence of '.svn' because an installations
    //   can change that default.
    // Don't use the info command because it will fail for unversioned
    //   files that ARE in a versioned directory.

    cmd = new StatusCmd(file, false);
    if (!cmd.execute().isTrue())
    {
      return false;
    }

    // Subversion has a bug until 1.5.1.
    // It will return an invalid xmldocument on a file that is not
    //   in a working copy.
    statusData = cmd.getStatusData();
    if (statusData == null)
    {
      return false;
    }

    if (statusData.getTargetList().size() != 1)
    {
      return false;
    }


    // Check for the existence of 1 entry!
    // If it is not a workingcopy it won't have an entry!
    // TODO: Check with subversion 1.5.1 and higher!
    return statusData.getTargetList().get(0).getEntryList().size() != 1;
  }

  public BlameIF executeBlame(File file)
  {
    BlameCmd cmd;

    cmd = new BlameCmd(file);
    cmd.execute();
    return cmd.getResultData();
  }

  public DiffIF executeDiff(File file, boolean recursive)
  {
    DiffCmd cmd;

    cmd = new DiffCmd(file, recursive);
    cmd.execute();
    return cmd.getResultData();
  }

  public StatusIF executeStatus(File file, boolean recursive)
  {
    StatusCmd cmd;

    cmd = new StatusCmd(file, recursive);
    cmd.execute();
    return cmd.getResultData();
  }

  public BaseFile getBaseFile(File file)
  {
    CatCmd cmd;

    cmd = new CatCmd(file);
    cmd.execute();
    return cmd.getResultData();
  }

  public String toString()
  {
    return getName();
  }
}
