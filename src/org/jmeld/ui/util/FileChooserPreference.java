package org.jmeld.ui.util;

import javax.swing.*;

import java.io.*;
import java.util.prefs.*;

public class FileChooserPreference
{
  private String preferenceName;

  public FileChooserPreference(String preferenceName)
  {
    this.preferenceName = preferenceName;
  }

  public void init(JFileChooser c)
  {
    String fileName;

    fileName = getPreferences().get(preferenceName, null);
    if (fileName == null)
    {
      return;
    }

    c.setCurrentDirectory(new File(fileName));
  }

  public void save(JFileChooser c)
  {
    String fileName;
    File   file;

    file = c.getSelectedFile();
    if (file == null)
    {
      return;
    }

    try
    {
      fileName = file.getCanonicalPath();
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
      return;
    }

    getPreferences().put(preferenceName, fileName);
  }

  private Preferences getPreferences()
  {
    return AppPreferences.getPreferences(getClass());
  }
}
