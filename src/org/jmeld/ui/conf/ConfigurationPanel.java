package org.jmeld.ui.conf;

import org.jmeld.ui.*;
import org.jmeld.ui.util.*;

import javax.swing.*;

import java.awt.*;

public class ConfigurationPanel
       extends AbstractContentPanel
{
  public ConfigurationPanel()
  {
    init();
  }

  private void init()
  {
    JTabbedPane tabbedPane;

    tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
    tabbedPane.addTab(
      "Editor",
      ImageUtil.getImageIcon("stock_help-agent"),
      new JButton("Editor"));
    tabbedPane.addTab(
      "Display",
      ImageUtil.getImageIcon("stock_help-agent"),
      new JButton("Display"));

    setLayout(new BorderLayout());
    add(tabbedPane, BorderLayout.CENTER);
  }
}
