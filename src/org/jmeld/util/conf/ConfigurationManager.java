package org.jmeld.util.conf;

import org.jmeld.*;
import org.jmeld.util.*;

import java.io.*;
import java.util.*;

public class ConfigurationManager
{
  // class variables:
  private static ConfigurationManager instance = new ConfigurationManager();

  // instance variables:
  private Map<String, AbstractConfiguration>                configurations;
  private Map<String, WeakHashSet<ConfigurationListenerIF>> listenerMap;

  private ConfigurationManager()
  {
    configurations = new HashMap<String, AbstractConfiguration>();
    listenerMap = new HashMap<String, WeakHashSet<ConfigurationListenerIF>>();
  }

  public static ConfigurationManager getInstance()
  {
    return instance;
  }

  public AbstractConfiguration get(Class clazz)
  {
    AbstractConfiguration configuration;
    String                key;

    key = clazz.getName();

    configuration = configurations.get(key);
    if (configuration == null)
    {
      configuration = load(clazz);
      if (configuration == null)
      {
        try
        {
          configuration = (AbstractConfiguration) clazz.newInstance();
	  configuration.init();
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
        }
      }

      configurations.put(key, configuration);
    }

    return configuration;
  }

  private AbstractConfiguration load(Class clazz)
  {
    String                  fileName;
    int                     index;
    ConfigurationPreference preference;
    File                    file;

    try
    {
      preference = new ConfigurationPreference(clazz);
      file = preference.getFile();
      if (file.exists())
      {
        return ConfigurationPersister.getInstance().load(clazz, file);
      }

      return null;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    return null;
  }

  void addConfigurationListener(
    Class                   clazz,
    ConfigurationListenerIF listener)
  {
    WeakHashSet<ConfigurationListenerIF> listeners;
    String                               key;

    key = clazz.getName();

    listeners = listenerMap.get(key);
    if (listeners == null)
    {
      listeners = new WeakHashSet<ConfigurationListenerIF>();
      listenerMap.put(key, listeners);
    }

    listeners.add(listener);
  }

  void removeConfigurationListener(
    Class                   clazz,
    ConfigurationListenerIF listener)
  {
    Set<ConfigurationListenerIF> listeners;

    listeners = listenerMap.get(clazz.getName());
    if (listeners != null)
    {
      listeners.remove(listener);
    }
  }

  void fireChanged(Class clazz)
  {
    Set<ConfigurationListenerIF> listeners;

    listeners = listenerMap.get(clazz.getName());
    if (listeners != null)
    {
      for (ConfigurationListenerIF listener : listeners)
      {
        listener.configurationChanged();
      }
    }
  }
}
