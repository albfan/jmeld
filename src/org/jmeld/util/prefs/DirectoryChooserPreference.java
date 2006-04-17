package org.jmeld.util.prefs;

import javax.swing.*;

import java.io.*;

public class DirectoryChooserPreference
       extends Preference
{
  // Class variables:
  private static String DIRECTORY = "DIRECTORY";

  // Instance variables:
  private JFileChooser target;

  public DirectoryChooserPreference(String preferenceName, JFileChooser target)
  {
    super("DirectoryChooser-" + preferenceName);

    this.target = target;

    init();
  }

  private void init()
  {
    String fileName;

    fileName = getString(DIRECTORY, null);
    if (fileName != null)
    {
      target.setCurrentDirectory(new File(fileName));
    }
  }

  public void save()
  {
    String fileName;
    File   file;

    file = target.getSelectedFile();
    if (file == null || !file.exists())
    {
      return;
    }

    file = new File(file.getParent());
    if (file == null || !file.exists())
    {
      return;
    }

    try
    {
      fileName = file.getCanonicalPath();
      putString(DIRECTORY, fileName);
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
  }
}
