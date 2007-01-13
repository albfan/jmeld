package org.jmeld.util.conf;

import org.jmeld.*;

import java.io.*;
import java.util.*;

public abstract class AbstractConfiguration
{
  private List<ConfigurationListenerIF> listeners;
  private File                          configurationFile;

  public AbstractConfiguration()
  {
    listeners = new ArrayList<ConfigurationListenerIF>();
  }

  public abstract void init();

  void setConfigurationFile(File configurationFile)
  {
    this.configurationFile = configurationFile;
  }

  public static AbstractConfiguration load(
    Class  clazz,
    String fileName)
    throws Exception
  {
    return ConfigurationPersister.getInstance().load(
      clazz,
      new File(fileName));
  }

  public void save()
  {
    if (configurationFile != null)
    {
      try
      {
        ConfigurationPersister.getInstance().save(this, configurationFile);
      }
      catch (Exception ex)
      {
	ex.printStackTrace();
      }
    }
  }

  public void addConfigurationListener(ConfigurationListenerIF listener)
  {
    listeners.add(listener);
  }

  public void removeConfigurationListener(ConfigurationListenerIF listener)
  {
    listeners.remove(listener);
  }

  public void fireChanged()
  {
    for (ConfigurationListenerIF listener : listeners)
    {
      listener.configurationChanged();
    }

    save();
  }
}
