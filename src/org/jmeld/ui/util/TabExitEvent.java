package org.jmeld.ui.util;

public class TabExitEvent
{
  private TabIcon tabIcon;
  private int     tabIndex;

  public TabExitEvent(
    TabIcon tabIcon,
    int     tabIndex)
  {
    this.tabIcon = tabIcon;
    this.tabIndex = tabIndex;
  }

  public TabIcon getTabIcon()
  {
    return tabIcon;
  }

  public int getTabIndex()
  {
    return tabIndex;
  }
}
