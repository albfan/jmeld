package org.jmeld.util;

public class Option
{
  private boolean enabled;

  public Option(boolean enabled)
  {
    this.enabled = enabled;
  }

  public boolean isEnabled()
  {
    return enabled;
  }

  public void enable()
  {
    this.enabled = true;
  }

  public void disable()
  {
    this.enabled = false;
  }
}
