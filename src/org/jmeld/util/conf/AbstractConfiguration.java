package org.jmeld.util.conf;

import org.jmeld.*;

import java.io.*;
import java.util.*;

public abstract class AbstractConfiguration
{
  private boolean                 changed;
  private ConfigurationPreference preference;

  public AbstractConfiguration()
  {
    preference = new ConfigurationPreference(getClass());
  }

  public abstract void init();

  public String getConfigurationFileName()
  {
    try
    {
      return preference.getFile().getCanonicalPath();
    }
    catch (IOException ex)
    {
      return "??";
    }
  }

  public boolean isChanged()
  {
    return changed;
  }

  public void save()
  {
    try
    {
      ConfigurationPersister.getInstance().save(
        this,
        preference.getFile());
      changed = false;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
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
