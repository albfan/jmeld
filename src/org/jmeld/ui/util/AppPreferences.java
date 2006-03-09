package org.jmeld.ui.util;

import java.util.prefs.*;

public class AppPreferences
{
  public static Preferences getPreferences(Class clazz)
  {
    return Preferences.userNodeForPackage(clazz);
  }
}
