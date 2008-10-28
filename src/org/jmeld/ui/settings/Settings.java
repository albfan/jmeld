/*
 * SettingsPanel2.java
 *
 * Created on January 19, 2007, 8:02 PM
 */
package org.jmeld.ui.settings;

import javax.swing.*;

public enum Settings
{
  Editor("Editor", "stock_edit", new EditorSettingsPanel()),
  Filter("Filter", "stock_standard-filter", new FilterSettingsPanel()),
  Folder("Folder", "stock_folder", new FolderSettingsPanel());

  // Instance variables:
  private String name;
  private String iconName;
  private JPanel panel;

  Settings(String name, String iconName, JPanel panel)
  {
    this.name = name;
    this.iconName = iconName;
    this.panel = panel;
  }

  String getName()
  {
    return name;
  }

  String getIconName()
  {
    return iconName;
  }

  JPanel getPanel()
  {
    return panel;
  }

  public String toString()
  {
    return name;
  }
}
