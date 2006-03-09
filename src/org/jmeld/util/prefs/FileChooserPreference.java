package org.jmeld.util.prefs;

import javax.swing.*;

import java.io.*;

public class FileChooserPreference extends Preference
{
  // Class variables:
  private static String FILE = "FILE";
  // Instance variables:
  private JFileChooser target;

  public FileChooserPreference(String preferenceName, JFileChooser target)
  {
    super("FileChooser-" + preferenceName);

    this.target = target;

    init();
  }

  private void init()
  {
    String fileName;

    fileName = getString(FILE, null);
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
    if (file != null)
    {
      try
      {
        fileName = file.getCanonicalPath();
        putString(FILE, fileName);
      }
      catch (IOException ex)
      {
        ex.printStackTrace();
      }
    }
  }
}
