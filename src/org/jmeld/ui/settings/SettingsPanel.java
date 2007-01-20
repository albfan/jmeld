/*
 * SettingsPanel.java
 *
 */
package org.jmeld.ui.settings;

import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.conf.*;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author  kees
 */
public class SettingsPanel
       extends SettingsPanelForm
       implements ConfigurationListenerIF
{
  private DefaultListModel listModel;

  public SettingsPanel()
  {
    init();
    initConfiguration();

    getConfiguration().addConfigurationListener(this);
  }

  private void init()
  {
    settingsPanel.setLayout(new CardLayout());
    for (Settings setting : Settings.values())
    {
      settingsPanel.add(
        setting.getPanel(),
        setting.getName());
    }

    initButton(saveButton, "stock_save", "Save settings");
    saveButton.addActionListener(getSaveButtonAction());

    initButton(saveAsButton, "stock_save-as",
      "Save settings to a different file");
    initButton(reloadButton, "stock_reload",
      "Reload settings from a different file");

    fileLabel.setText("");

    listModel = new DefaultListModel();
    for (Settings setting : Settings.values())
    {
      listModel.addElement(setting);
    }
    settingItems.setModel(listModel);
    settingItems.setCellRenderer(new SettingCellRenderer());
    settingItems.setSelectedIndex(0);
    settingItems.addListSelectionListener(getSettingItemsAction());
  }

  private void initButton(
    JButton button,
    String  iconName,
    String  toolTipText)
  {
    ImageIcon icon;

    button.setText("");
    button.setToolTipText(toolTipText);
    button.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    button.setContentAreaFilled(false);
    icon = ImageUtil.getSmallImageIcon(iconName);
    button.setIcon(icon);
    button.setDisabledIcon(ImageUtil.createTransparentIcon(icon));
    button.setFocusable(false);
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

  public ListSelectionListener getSettingItemsAction()
  {
    return new ListSelectionListener()
      {
        public void valueChanged(ListSelectionEvent event)
        {
	  CardLayout layout;
	  Settings   settings;

	  settings = (Settings) settingItems.getSelectedValue();
	  layout = (CardLayout) settingsPanel.getLayout();
	  layout.show(settingsPanel, settings.getName());
	}
      };
  }

  public void configurationChanged()
  {
    initConfiguration();
  }

  private void initConfiguration()
  {
    JMeldSettings c;

    c = getConfiguration();

    fileLabel.setText(c.getConfigurationFileName());
    saveButton.setEnabled(c.isChanged());
  }

  private JMeldSettings getConfiguration()
  {
    return JMeldSettings.getInstance();
  }
}
