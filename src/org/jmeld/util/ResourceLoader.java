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
package org.jmeld.util;

import javax.swing.*;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ResourceLoader
{
  static ResourceLoader resourceLoader = new ResourceLoader();

  private ClassLoader getClassLoader()
  {
    return this.getClass().getClassLoader();
  }

  public static synchronized ImageIcon getSmallImageIcon(String iconName)
  {
    return getImageIcon("16x16/" + iconName + "-16");
  }

  public static synchronized ImageIcon getImageIcon(String iconName)
  {
    ImageIcon icon;
    URL       url;

    iconName = "images/" + iconName + ".png";

    url = getResource(iconName);
    if (url == null)
    {
      return null;
    }

    return new ImageIcon(url);
  }

  public static synchronized InputStream getResourceAsStream(
    String resourceName)
  {
    return resourceLoader.getClassLoader().getResourceAsStream(resourceName);
  }

  public static synchronized URL getResource(String resourceName)
  {
    return resourceLoader.getClassLoader().getResource(resourceName);
  }
}
