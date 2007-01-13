/*
   JMeld is a visual diff and merge tool.
   Copyright (C) 2007  Kees Kuip
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA  02110-1301  USA
 */
package org.jmeld.ui.conf;

import com.jgoodies.forms.layout.*;

import org.jmeld.conf.*;
import org.jmeld.ui.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.conf.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class ConfigurationPanel
       extends AbstractContentPanel
       implements ConfigurationListenerIF
{
  private JButton saveButton;
  private JLabel  fileLabel;

  public ConfigurationPanel()
  {
    init();

    getConfiguration().addConfigurationListener(this);
  }

  private void init()
  {
    String          columns;
    String          rows;
    FormLayout      layout;
    CellConstraints cc;
    JTabbedPane     tabbedPane;
    ImageIcon       icon;

    tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
    tabbedPane.setFocusable(false);
    tabbedPane.addTab(
      "Editor",
      new EmptyIcon(10, 40),
      new EditorPreferencePanel());
    tabbedPane.addTab(
      "Display",
      new EmptyIcon(10, 40),
      new JButton("Display"));

    saveButton = new JButton();
    saveButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    saveButton.setContentAreaFilled(false);
    icon = ImageUtil.getSmallImageIcon("stock_save");
    saveButton.setIcon(icon);
    saveButton.setDisabledIcon(ImageUtil.createTransparentIcon(icon));
    saveButton.addActionListener(getSaveButtonAction());
    fileLabel = new JLabel();

    columns = "3px, pref, 3px, 0:grow, 3px";
    rows = "6px, pref, 3px, fill:0:grow, 6px";
    layout = new FormLayout(columns, rows);
    setLayout(layout);
    cc = new CellConstraints();

    add(
      saveButton,
      cc.xy(2, 2));
    add(
      fileLabel,
      cc.xy(4, 2));
    add(
      tabbedPane,
      cc.xyw(2, 4, 3));

    initConfiguration();
  }

  public ActionListener getSaveButtonAction()
  {
    return new ActionListener()
      {
        public void actionPerformed(ActionEvent ae)
        {
          getConfiguration().save();
          initConfiguration();
        }
      };
  }

  public void configurationChanged()
  {
    initConfiguration();
  }

  private void initConfiguration()
  {
    JMeldConfiguration c;

    c = getConfiguration();

    fileLabel.setText(c.getConfigurationFileName());
    saveButton.setEnabled(c.isChanged());
  }

  private JMeldConfiguration getConfiguration()
  {
    return JMeldConfiguration.getInstance();
  }
}
