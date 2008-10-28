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
package org.jmeld.util.conf;

import org.jmeld.util.prefs.*;

import java.io.*;

public class ConfigurationPreference
    extends Preference
{
  // Class variables:
  private static String FILENAME = "FILENAME";

  // Instance variables:
  private Class clazz;
  private File file;

  public ConfigurationPreference(Class clazz)
  {
    super("Configuration-" + clazz);

    this.clazz = clazz;

    init();
  }

  private void init()
  {
    String fileName;
    String defaultFileName;
    int index;

    defaultFileName = clazz.getName();
    index = defaultFileName.lastIndexOf(".");
    if (index != -1)
    {
      defaultFileName = defaultFileName.substring(index + 1);
    }
    try
    {
      defaultFileName = new File(System.getProperty("user.home"),
          defaultFileName).getCanonicalPath();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    defaultFileName += ".xml";

    fileName = getString(FILENAME, defaultFileName);

    file = new File(fileName);
  }

  public File getFile()
  {
    return file;
  }

  public void setFile(File file)
  {
    this.file = file;
    save();
  }

  public void save()
  {
    try
    {
      putString(FILENAME, file.getCanonicalPath());
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
  }
}
