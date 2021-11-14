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
package org.jmeld.ui;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.settings.util.Filter;
import org.jmeld.util.ObjectUtil;
import org.jmeld.util.prefs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PanelDialog
{
  // Class variables:
  // File comparison:
  public enum Function
  {
    FILE_COMPARISON,
    DIRECTORY_COMPARISON,
    VERSION_CONTROL;
  }

  private static String RIGHT_FILENAME = "RIGHT_FILENAME";
  private static String LEFT_FILENAME = "LEFT_FILENAME";
  private static String previousDirectoryName;

  // Directory comparison:
  private static String RIGHT_DIRECTORY = "RIGHT_DIRECTORY";
  private static String LEFT_DIRECTORY = "LEFT_DIRECTORY";

  // Version control :
  private static String VERSION_CONTROL_DIRECTORY = "VERSION_CONTROL_DIRECTORY";

  // Instance variables:
  private JMeldPanel meldPanel;
  private JTabbedPane tabbedPane;
  private Function function;
  private String leftFileName;
  private String rightFileName;
  private JComboBox leftFileComboBox;
  private JComboBox rightFileComboBox;
  private String leftDirectoryName;
  private String rightDirectoryName;
  private JComboBox leftDirectoryComboBox;
  private JComboBox rightDirectoryComboBox;
  private String versionControlDirectoryName;
  private JComboBox versionControlDirectoryComboBox;
  private JComboBox filterComboBox;
  private JDialog dialog;

  public PanelDialog(JMeldPanel meldPanel)
  {
    this.meldPanel = meldPanel;
  }

  public void show()
  {
    JOptionPane pane;

    pane = new JOptionPane(getChooseFilePanel());
    pane.setOptionType(JOptionPane.OK_CANCEL_OPTION);

    dialog = pane.createDialog(meldPanel, "Choose files");
    dialog.setResizable(true);
    try
    {
      dialog.setVisible(true);

      if (ObjectUtil.equals(pane.getValue(), JOptionPane.OK_OPTION))
      {
        switch (tabbedPane.getSelectedIndex())
        {

          case 0:
            setFunction(Function.FILE_COMPARISON);
            break;

          case 1:
            setFunction(Function.DIRECTORY_COMPARISON);
            break;

          case 2:
            setFunction(Function.VERSION_CONTROL);
            break;
        }
      }
    }
    finally
    {
      // Always dispose a dialog -> otherwise there is a memory leak
      dialog.dispose();
    }
  }

  private void setFunction(Function function)
  {
    this.function = function;
  }

  public Function getFunction()
  {
    return function;
  }

  public String getLeftFileName()
  {
    return leftFileName;
  }

  public String getRightFileName()
  {
    return rightFileName;
  }

  public String getLeftDirectoryName()
  {
    return leftDirectoryName;
  }

  public String getRightDirectoryName()
  {
    return rightDirectoryName;
  }

  public String getVersionControlDirectoryName()
  {
    return versionControlDirectoryName;
  }

  public Filter getFilter()
  {
    if (filterComboBox.getSelectedItem() instanceof Filter)
    {
      return (Filter) filterComboBox.getSelectedItem();
    }

    return null;
  }

  private JComponent getChooseFilePanel()
  {
    JPanel panel;

    tabbedPane = new JTabbedPane();
    tabbedPane.add("File Comparison", getFileComparison());
    tabbedPane.add("Directory Comparison", getDirectoryComparisonPanel());
    tabbedPane.add("Version control", getVersionControlPanel());

    new TabbedPanePreference("NewPanelTabbedPane", tabbedPane);

    panel = new JPanel(new BorderLayout());
    panel.add(tabbedPane, BorderLayout.CENTER);

    return panel;
  }

  private JComponent getFileComparison()
  {
    JPanel panel;
    String columns;
    String rows;
    FormLayout layout;
    CellConstraints cc;
    JLabel label;
    JButton button;

    columns = "10px, right:pref, 10px, max(150dlu;pref):grow, 5px, pref, 10px";
    rows = "10px, fill:pref, 5px, fill:pref, 5px, fill:pref, 10px";
    layout = new FormLayout(columns, rows);
    cc = new CellConstraints();

    panel = new JPanel(layout);

    label = new JLabel("Left");
    button = new JButton("Browse...");
    leftFileComboBox = new JComboBox();
    leftFileComboBox.setEditable(true);
    leftFileComboBox.addActionListener(getFileSelectAction());
    new ComboBoxPreference("LeftFile", leftFileComboBox);

    button.setActionCommand(LEFT_FILENAME);
    button.addActionListener(getFileBrowseAction());
    panel.add(label, cc.xy(2, 2));
    panel.add(leftFileComboBox, cc.xy(4, 2));
    panel.add(button, cc.xy(6, 2));

    label = new JLabel("Right");
    button = new JButton("Browse...");
    button.setActionCommand(RIGHT_FILENAME);
    button.addActionListener(getFileBrowseAction());
    rightFileComboBox = new JComboBox();
    rightFileComboBox.setEditable(true);
    rightFileComboBox.addActionListener(getFileSelectAction());
    new ComboBoxPreference("RightFile", rightFileComboBox);
    panel.add(label, cc.xy(2, 4));
    panel.add(rightFileComboBox, cc.xy(4, 4));
    panel.add(button, cc.xy(6, 4));

    return panel;
  }

  public ActionListener getFileBrowseAction()
  {
    return new ActionListener()
    {
      public void actionPerformed(ActionEvent ae)
      {
        FileChooserPreference pref;
        JFileChooser chooser;
        int result;
        File file;
        String fileName;
        JComboBox comboBox;
        Window ancestor;

        // Don't allow accidentaly creation or rename of files.
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        chooser = new JFileChooser();
        // Reset the readOnly property as it is systemwide.
        UIManager.put("FileChooser.readOnly", Boolean.FALSE);
        chooser.setApproveButtonText("Choose");
        chooser.setDialogTitle("Choose file");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        pref = new FileChooserPreference("Browse-" + ae.getActionCommand(),
            chooser);

        ancestor = SwingUtilities.getWindowAncestor((Component) ae.getSource());
        result = chooser.showOpenDialog(ancestor);

        if (result == JFileChooser.APPROVE_OPTION)
        {
          pref.save();

          try
          {
            fileName = chooser.getSelectedFile().getCanonicalPath();

            comboBox = null;
            if (ae.getActionCommand().equals(LEFT_FILENAME))
            {
              comboBox = leftFileComboBox;
            }
            else if (ae.getActionCommand().equals(RIGHT_FILENAME))
            {
              comboBox = rightFileComboBox;
            }

            if (comboBox != null)
            {
              comboBox.insertItemAt(fileName, 0);
              comboBox.setSelectedIndex(0);
              dialog.pack();
            }
          }
          catch (Exception ex)
          {
            ex.printStackTrace();
          }
        }
      }
    };
  }

  public ActionListener getFileSelectAction()
  {
    return new ActionListener()
    {
      public void actionPerformed(ActionEvent ae)
      {
        Object source;

        source = ae.getSource();
        if (source == leftFileComboBox)
        {
          leftFileName = (String) leftFileComboBox.getSelectedItem();
        }
        else if (source == rightFileComboBox)
        {
          rightFileName = (String) rightFileComboBox.getSelectedItem();
        }
      }
    };
  }

  private JComponent getDirectoryComparisonPanel()
  {
    JPanel panel;
    String columns;
    String rows;
    FormLayout layout;
    CellConstraints cc;
    JLabel label;
    JButton button;

    columns = "10px, right:pref, 10px, max(150dlu;pref):grow, 5px, pref, 10px";
    rows = "10px, fill:pref, 5px, fill:pref, 5px, fill:pref, 5px, fill:pref, 10px";
    layout = new FormLayout(columns, rows);
    cc = new CellConstraints();

    panel = new JPanel(layout);

    label = new JLabel("Left");
    button = new JButton("Browse...");
    leftDirectoryComboBox = new JComboBox();
    leftDirectoryComboBox.setEditable(true);
    leftDirectoryComboBox.addActionListener(getDirectorySelectAction());
    new ComboBoxPreference("LeftDirectory", leftDirectoryComboBox);

    button.setActionCommand(LEFT_DIRECTORY);
    button.addActionListener(getDirectoryBrowseAction());
    panel.add(label, cc.xy(2, 2));
    panel.add(leftDirectoryComboBox, cc.xy(4, 2));
    panel.add(button, cc.xy(6, 2));

    label = new JLabel("Right");
    button = new JButton("Browse...");
    button.setActionCommand(RIGHT_DIRECTORY);
    button.addActionListener(getDirectoryBrowseAction());
    rightDirectoryComboBox = new JComboBox();
    rightDirectoryComboBox.setEditable(true);
    rightDirectoryComboBox.addActionListener(getDirectorySelectAction());
    new ComboBoxPreference("RightDirectory", rightDirectoryComboBox);
    panel.add(label, cc.xy(2, 4));
    panel.add(rightDirectoryComboBox, cc.xy(4, 4));
    panel.add(button, cc.xy(6, 4));

    label = new JLabel("Filter");
    filterComboBox = new JComboBox(getFilters());
    panel.add(label, cc.xy(2, 6));
    panel.add(filterComboBox, cc.xy(4, 6));
    new ComboBoxSelectionPreference("Filter", filterComboBox);

    return panel;
  }

  public ActionListener getDirectoryBrowseAction()
  {
    return new ActionListener()
    {
      public void actionPerformed(ActionEvent ae)
      {
        DirectoryChooserPreference pref;
        JFileChooser chooser;
        int result;
        File file;
        String fileName;
        JComboBox comboBox;
        Window ancestor;

        // Don't allow accidentaly creation or rename of files.
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        chooser = new JFileChooser();
        // Reset the readOnly property as it is systemwide.
        UIManager.put("FileChooser.readOnly", Boolean.FALSE);
        chooser.setApproveButtonText("Choose");
        chooser.setDialogTitle("Choose directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        pref = new DirectoryChooserPreference(
            "Browse-" + ae.getActionCommand(), chooser, previousDirectoryName);

        ancestor = SwingUtilities.getWindowAncestor((Component) ae.getSource());
        result = chooser.showOpenDialog(ancestor);

        if (result == JFileChooser.APPROVE_OPTION)
        {
          pref.save();

          try
          {
            fileName = chooser.getSelectedFile().getCanonicalPath();
            previousDirectoryName = fileName;

            comboBox = null;
            if (ae.getActionCommand().equals(LEFT_DIRECTORY))
            {
              comboBox = leftDirectoryComboBox;
            }
            else if (ae.getActionCommand().equals(RIGHT_DIRECTORY))
            {
              comboBox = rightDirectoryComboBox;
            }

            if (comboBox != null)
            {
              comboBox.insertItemAt(fileName, 0);
              comboBox.setSelectedIndex(0);
              dialog.pack();
            }
          }
          catch (Exception ex)
          {
            ex.printStackTrace();
          }
        }
      }
    };
  }

  public ActionListener getDirectorySelectAction()
  {
    return new ActionListener()
    {
      public void actionPerformed(ActionEvent ae)
      {
        Object source;

        source = ae.getSource();
        if (source == leftDirectoryComboBox)
        {
          leftDirectoryName = (String) leftDirectoryComboBox.getSelectedItem();
        }
        else if (source == rightDirectoryComboBox)
        {
          rightDirectoryName = (String) rightDirectoryComboBox
              .getSelectedItem();
        }
      }
    };
  }

  private JComponent getVersionControlPanel()
  {
    JPanel panel;
    String columns;
    String rows;
    FormLayout layout;
    CellConstraints cc;
    JLabel label;
    JButton button;

    columns = "10px, right:pref, 10px, max(150dlu;pref):grow, 5px, pref, 10px";
    rows = "10px, fill:pref, 5px, fill:pref, 5px, fill:pref, 5px, fill:pref, 10px";
    layout = new FormLayout(columns, rows);
    cc = new CellConstraints();

    panel = new JPanel(layout);

    label = new JLabel("Directory");
    button = new JButton("Browse...");
    versionControlDirectoryComboBox = new JComboBox();
    versionControlDirectoryComboBox.setEditable(true);
    versionControlDirectoryComboBox
        .addActionListener(getVersionControlDirectorySelectAction());
    new ComboBoxPreference("VersionControlDirectory",
        versionControlDirectoryComboBox);

    button.setActionCommand(VERSION_CONTROL_DIRECTORY);
    button.addActionListener(getVersionControlDirectoryBrowseAction());
    panel.add(label, cc.xy(2, 2));
    panel.add(versionControlDirectoryComboBox, cc.xy(4, 2));
    panel.add(button, cc.xy(6, 2));

    return panel;
  }

  public ActionListener getVersionControlDirectoryBrowseAction()
  {
    return new ActionListener()
    {
      public void actionPerformed(ActionEvent ae)
      {
        DirectoryChooserPreference pref;
        JFileChooser chooser;
        int result;
        File file;
        String fileName;
        JComboBox comboBox;
        Window ancestor;

        // Don't allow accidentaly creation or rename of files.
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        chooser = new JFileChooser();
        // Reset the readOnly property as it is systemwide.
        UIManager.put("FileChooser.readOnly", Boolean.FALSE);
        chooser.setApproveButtonText("Choose");
        chooser.setDialogTitle("Choose directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        pref = new DirectoryChooserPreference("VersionControlBrowse", chooser, previousDirectoryName);

        ancestor = SwingUtilities.getWindowAncestor((Component) ae.getSource());
        result = chooser.showOpenDialog(ancestor);

        if (result == JFileChooser.APPROVE_OPTION)
        {
          pref.save();

          try
          {
            fileName = chooser.getSelectedFile().getCanonicalPath();
            previousDirectoryName = fileName;

            comboBox = versionControlDirectoryComboBox;
            comboBox.insertItemAt(fileName, 0);
            comboBox.setSelectedIndex(0);
            dialog.pack();
          }
          catch (Exception ex)
          {
            ex.printStackTrace();
          }
        }
      }
    };
  }

  public ActionListener getVersionControlDirectorySelectAction()
  {
    return new ActionListener()
    {
      public void actionPerformed(ActionEvent ae)
      {
        versionControlDirectoryName = (String) versionControlDirectoryComboBox
            .getSelectedItem();
      }
    };
  }

  private Object[] getFilters()
  {
    List filters;

    filters = new ArrayList();
    filters.add("No filter");
    for (Filter f : JMeldSettings.getInstance().getFilter().getFilters())
    {
      filters.add(f);
    }

    return filters.toArray();
  }
}
