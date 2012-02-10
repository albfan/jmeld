/*
 * FilterPreferencePanel.java
 *
 * Created on August 22, 2007, 20:10
 */
package org.jmeld.ui.settings;

import org.jmeld.settings.FolderSettings;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.util.ImageUtil;
import org.jmeld.util.conf.ConfigurationListenerIF;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 *
 * @author  kees
 */
public class FolderSettingsPanel
    extends FolderSettingsForm
    implements ConfigurationListenerIF
{
  public FolderSettingsPanel()
  {
    init();

    JMeldSettings.getInstance().addConfigurationListener(this);
  }

  private void init()
  {
    FolderSettings settings;

    settings = getSettings();

    hierarchyComboBox.setModel(new DefaultComboBoxModel(
        FolderSettings.FolderView.values()));
    hierarchyComboBox.setSelectedItem(getSettings().getView());
    hierarchyComboBox.setFocusable(false);
    hierarchyComboBox.addActionListener(getHierarchyAction());

    onlyLeftButton.setText(null);
    onlyLeftButton.setIcon(ImageUtil.getImageIcon("jmeld_only-left"));
    onlyLeftButton.setFocusable(false);
    onlyLeftButton.setSelected(settings.getOnlyLeft());
    onlyLeftButton.addActionListener(getOnlyLeftAction());

    leftRightChangedButton.setText(null);
    leftRightChangedButton.setIcon(ImageUtil
        .getImageIcon("jmeld_left-right-changed"));
    leftRightChangedButton.setFocusable(false);
    leftRightChangedButton.setSelected(settings.getLeftRightChanged());
    leftRightChangedButton.addActionListener(getLeftRightChangedAction());

    onlyRightButton.setText(null);
    onlyRightButton.setIcon(ImageUtil.getImageIcon("jmeld_only-right"));
    onlyRightButton.setFocusable(false);
    onlyRightButton.setSelected(settings.getOnlyRight());
    onlyRightButton.addActionListener(getOnlyRightAction());

    leftRightUnChangedButton.setText(null);
    leftRightUnChangedButton.setIcon(ImageUtil
        .getImageIcon("jmeld_left-right-unchanged"));
    leftRightUnChangedButton.setFocusable(false);
    leftRightUnChangedButton.setSelected(settings.getLeftRightUnChanged());
    leftRightUnChangedButton.addActionListener(getLeftRightUnChangedAction());
  }

  private ActionListener getHierarchyAction()
  {
    return new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        getSettings().setView(
          (FolderSettings.FolderView) hierarchyComboBox.getSelectedItem());
      }
    };
  }

  private ActionListener getOnlyLeftAction()
  {
    return new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        getSettings().setOnlyLeft(onlyLeftButton.isSelected());
      }
    };
  }

  private ActionListener getLeftRightChangedAction()
  {
    return new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        getSettings().setLeftRightChanged(leftRightChangedButton.isSelected());
      }
    };
  }

  private ActionListener getOnlyRightAction()
  {
    return new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        getSettings().setOnlyRight(onlyRightButton.isSelected());
      }
    };
  }

  private ActionListener getLeftRightUnChangedAction()
  {
    return new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        getSettings().setLeftRightUnChanged(
          leftRightUnChangedButton.isSelected());
      }
    };
  }

  public void configurationChanged()
  {
    //initConfiguration();
  }

  private FolderSettings getSettings()
  {
    return JMeldSettings.getInstance().getFolder();
  }
}
