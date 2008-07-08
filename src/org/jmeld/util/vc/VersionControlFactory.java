package org.jmeld.util.vc;

import org.jmeld.util.vc.svn.*;

import java.io.*;

public class VersionControlFactory
{
  public static VersionControlIF getInstance(File file)
  {
    if (isSubversion(file))
    {
      return new SubversionVersionControl();
    }

    return null;
  }

  private static boolean isSubversion(File file)
  {
    return new InfoCmd(file).execute().isTrue();
  }
}
