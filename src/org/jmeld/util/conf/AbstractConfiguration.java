package org.jmeld.util.conf;

import org.jmeld.*;

import java.io.*;
import java.util.*;

public abstract class AbstractConfiguration
{
  private String  configurationFileName;
  private boolean changed;

  public AbstractConfiguration()
  {
  }

  public abstract void init();

  void setConfigurationFileName(String configurationFileName)
  {
    this.configurationFileName = configurationFileName;
  }

  public boolean isChanged()
  {
    return changed;
  }

  public String getConfigurationFileName()
  {
    return configurationFileName;
  }

  public void save()
  {
    if (configurationFileName != null)
    {
      try
      {
        ConfigurationPersister.getInstance().save(
          this,
          new File(configurationFileName));
        changed = false;
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }

  public void addConfigurationListener(ConfigurationListenerIF listener)
  {
    getManager().addConfigurationListener(
      getClass(),
      listener);
  }

  public void removeConfigurationListener(ConfigurationListenerIF listener)
  {
    getManager().removeConfigurationListener(
      getClass(),
      listener);
  }

  public void fireChanged()
  {
    changed = true;
    getManager().fireChanged(getClass());
  }

  private ConfigurationManager getManager()
  {
    return ConfigurationManager.getInstance();
  }
}
