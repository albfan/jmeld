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
package org.jmeld;

import org.jmeld.util.*;

import java.util.*;

public class Version
{
  // Class variables:
  // Singleton:
  private static Version instance = new Version();

  // Instance variables:
  private String version;

  private Version()
  {
    init();
  }

  public static String getVersion()
  {
    return instance.version;
  }

  private void init()
  {
    Properties p;

    try
    {
      p = new Properties();
      p.load(ResourceLoader.getResourceAsStream("ini/Version.txt"));
      version = p.getProperty("version");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
