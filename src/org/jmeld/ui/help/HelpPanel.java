/*
 * HelpPanel.java
 *
 */
package org.jmeld.ui.help;

import org.jmeld.settings.*;
import org.jmeld.ui.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.conf.*;
import org.jmeld.util.prefs.*;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 *
 * @author  kees
 */
public class HelpPanel
       extends AbstractContentPanel
{
  private JMeldPanel mainPanel;

  public HelpPanel(JMeldPanel mainPanel)
  {
    this.mainPanel = mainPanel;

    init();
  }

  private void init()
  {
    JSplitPane splitPane;
    JList      urlList;

    urlList = new JList(new String[] { "Shortcuts", "Licenses" });

    splitPane = new JSplitPane();
    splitPane.setLeftComponent(urlList);
    splitPane.setRightComponent(new JButton("hhaa"));

    setLayout(new BorderLayout());
    add(splitPane, BorderLayout.CENTER);
  }

  class HelpURI
  {
    private String name;
    private String resourceName;

    HelpURI(
      String name,
      String resourceName)
    {
      this.name = name;
      this.resourceName = resourceName;
    }

    public String getName()
    {
      return name;
    }

    public String getResourceName()
    {
      return resourceName;
    }
  }
}
