package org.jmeld.util.prefs;

import org.jmeld.util.*;

import java.util.*;
import java.util.prefs.*;

public abstract class Preference
{
  // Instance variables:
  private String preferenceName;

  Preference(String preferenceName)
  {
    this.preferenceName = preferenceName;
  }

  protected String getPreferenceName()
  {
    return preferenceName;
  }

  protected String getString(
    String name,
    String defaultValue)
  {
    return getPreferences().get(
      getKey(name),
      defaultValue);
  }

  protected void putString(
    String name,
    String value)
  {
    getPreferences().put(
      getKey(name),
      value);
  }

  protected List<String> getListOfString(
    String name,
    int    maxItems)
  {
    List<String> list;
    String       element;

    list = new ArrayList<String>(maxItems);
    for (int index = 0; index < maxItems; index++)
    {
      element = getString(name + index, null);
      if (StringUtil.isEmpty(element))
      {
        continue;
      }

      list.add(element);
    }

    return list;
  }

  protected void putListOfString(
    String       name,
    int          maxItems,
    List<String> list)
  {
    String element;

    for (int index = 0; index < maxItems; index++)
    {
      element = "";
      if (index < list.size())
      {
        element = list.get(index);
        if (StringUtil.isEmpty(element))
        {
          element = "";
        }
      }

      putString(name + index, element);
    }
  }

  protected int getInt(
    String name,
    int    defaultValue)
  {
    return getPreferences().getInt(
      getKey(name),
      defaultValue);
  }

  protected void putInt(
    String name,
    int    value)
  {
    getPreferences().putInt(
      getKey(name),
      value);
  }

  protected Preferences getPreferences()
  {
    return AppPreferences.getPreferences(getClass());
  }

  private String getKey(String name)
  {
    return preferenceName + "-" + name;
  }

  private String getKey(
    String name,
    int    index)
  {
    return preferenceName + "-" + name + "-" + index;
  }
}
