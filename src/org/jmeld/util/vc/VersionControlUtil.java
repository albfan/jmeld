package org.jmeld.util.vc;

import java.io.*;
import org.jmeld.util.vc.svn.*;

public class VersionControlUtil
{
  public static boolean isVersionControlled(File file)
  {
    return getVersionControl(file) != null;
  }

  public static VersionControlIF getVersionControl(File file)
  {
    if (isSubversion(file))
    {
      return new SubversionVersionControl();
    }

    return null;
  }

  private static boolean isSubversion(File file)
  {
    if (new File(file, ".svn").exists())
    {
      return true;
    }

    //return new InfoCmd(file).execute().isTrue();

    return false;
  }
}
