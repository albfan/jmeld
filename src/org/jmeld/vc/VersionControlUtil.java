package org.jmeld.vc;

import java.io.*;

import java.util.*;

public class VersionControlUtil
{
  static private List<VersionControlIF> versionControlList;

  public static boolean isVersionControlled(File file)
  {
    return getVersionControl(file) != null;
  }

  public static List<VersionControlIF> getVersionControl(File file)
  {
    List<VersionControlIF> list;

    list = new ArrayList<VersionControlIF>();
    for (VersionControlIF versionControl : getVersionControlList())
    {
      if (!versionControl.isInstalled())
      {
        continue;
      }

      if (!versionControl.isEnabled(file))
      {
        continue;
      }

      list.add(versionControl);
    }

    return list;
  }

  public static List<VersionControlIF> getVersionControlList()
  {
    if (versionControlList == null)
    {
      versionControlList = new ArrayList<VersionControlIF>();
      versionControlList
          .add(new org.jmeld.vc.svn.SubversionVersionControl());
      versionControlList
          .add(new org.jmeld.vc.hg.MercurialVersionControl());
      versionControlList
          .add(new org.jmeld.vc.bzr.BazaarVersionControl());
    }

    return versionControlList;
  }
}
