package org.jmeld.util.prefs;

import java.util.prefs.*;

public class AppPreferences
{
  public static Preferences getPreferences(Class clazz)
  {
    return Preferences.userNodeForPackage(clazz);
  }
}
