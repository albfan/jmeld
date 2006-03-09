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
