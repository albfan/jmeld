/*
   JMeld is a visual diff and merge tool.
   Copyright (C) 2007  Kees Kuip
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA  02110-1301  USA
 */
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
  private String defaultFileName;

  public DirectoryChooserPreference(String preferenceName, JFileChooser target, String defaultFileName)
  {
    super("DirectoryChooser-" + preferenceName);

    this.target = target;
    this.defaultFileName = defaultFileName;

    init();
  }

  private void init()
  {
    String fileName;

    fileName = getString(DIRECTORY, defaultFileName);
    if (fileName != null)
    {
      target.setCurrentDirectory(new File(fileName));
    }
  }

  public void save()
  {
    String fileName;
    File file;

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
