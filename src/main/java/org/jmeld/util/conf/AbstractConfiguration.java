package org.jmeld.util.conf;


import java.io.*;

public abstract class AbstractConfiguration
{
  private boolean changed;
  private ConfigurationPreference preference;
  private boolean disableFireChanged;
    private boolean drawCurves;
    private int curveType;

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

  public void setConfigurationFile(File file)
  {
    preference.setFile(file);
  }

  public boolean isChanged()
  {
    return changed;
  }

  public void save()
  {
    try
    {
      ConfigurationPersister.getInstance().save(this, preference.getFile());
      changed = false;
      fireChanged(changed);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public void addConfigurationListener(ConfigurationListenerIF listener)
  {
    getManager().addConfigurationListener(getClass(), listener);
  }

  public void removeConfigurationListener(ConfigurationListenerIF listener)
  {
    getManager().removeConfigurationListener(getClass(), listener);
  }

  void disableFireChanged(boolean disableFireChanged)
  {
    this.disableFireChanged = disableFireChanged;
  }

  public void fireChanged()
  {
    if (disableFireChanged)
    {
      return;
    }

    fireChanged(true);
  }

  public void fireChanged(boolean changed)
  {
    this.changed = changed;
    getManager().fireChanged(getClass());
  }

  private ConfigurationManager getManager()
  {
    return ConfigurationManager.getInstance();
  }

    public boolean getDrawCurves() {
        return drawCurves;
    }

    public boolean isDrawCurves() {
        return drawCurves;
    }

    public void setDrawCurves(boolean drawCurves) {
        this.drawCurves = drawCurves;
    }

    public int getCurveType() {
        return curveType;
    }

    public void setCurveType(int curveType) {
        this.curveType = curveType;
    }
}
