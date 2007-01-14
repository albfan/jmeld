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
    String fileName;
    int    index;

    try
    {
      fileName = clazz.getName();
      index = fileName.lastIndexOf(".");
      if (index != -1)
      {
        fileName = fileName.substring(index + 1);
      }

      return ConfigurationPersister.getInstance().load(
        clazz,
        new File(fileName + ".xml"));
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
