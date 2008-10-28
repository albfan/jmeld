package org.jmeld.util.vc;

import java.io.*;

import java.util.*;

public class VersionControlUtil
{
  static private List<VersionControlIF> versionControlList;

  public static boolean isVersionControlled(File file)
  {
    return getVersionControl(file) != null;
  }

  public static VersionControlIF getVersionControl(File file)
  {
    for (VersionControlIF versionControl : getVersionControlList())
    {
      if (versionControl.accept(file))
      {
        return versionControl;
      }
    }

    return null;
  }

  public static List<VersionControlIF> getVersionControlList()
  {
    if (versionControlList == null)
    {
      versionControlList = new ArrayList<VersionControlIF>();
      versionControlList
          .add(new org.jmeld.util.vc.svn.SubversionVersionControl());
    }

    return versionControlList;
  }
}
