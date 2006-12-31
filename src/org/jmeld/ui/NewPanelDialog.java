package org.jmeld.ui;

import com.jgoodies.forms.layout.*;

import org.jmeld.util.*;
import org.jmeld.util.prefs.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class NewPanelDialog
{
// Class variables:
// File comparison:
  public static String  FILE_COMPARISON = "FILE_COMPARISON";
  private static String MINE_FILENAME = "MINE_FILENAME";
  private static String ORIGINAL_FILENAME = "ORIGINAL_FILENAME";

// Directory comparison:
  public static String  DIRECTORY_COMPARISON = "DIRECTORY_COMPARISON";
  private static String MINE_DIRECTORY = "MINE_DIRECTORY";
  private static String ORIGINAL_DIRECTORY = "ORIGINAL_DIRECTORY";

// Instance variables:
  private JMeldPanel  meldPanel;
  private JTabbedPane tabbedPane;
  private String      value;
  private String      originalFileName;
  private String      mineFileName;
  private JComboBox   originalFileComboBox;
  private JComboBox   mineFileComboBox;
  private String      originalDirectoryName;
  private String      mineDirectoryName;
  private JComboBox   originalDirectoryComboBox;
  private JComboBox   mineDirectoryComboBox;

  public NewPanelDialog(JMeldPanel meldPanel)
  {
    this.meldPanel = meldPanel;
  }

  public void show()
  {
    JOptionPane pane;
    JDialog     dialog;

    pane = new JOptionPane(getChooseFilePanel());
    pane.setOptionType(JOptionPane.OK_CANCEL_OPTION);

    dialog = pane.createDialog(meldPanel, "Choose files");
    dialog.setResizable(true);
    dialog.show();

    if (ObjectUtil.equals(
        pane.getValue(),
        JOptionPane.OK_OPTION))
    {
      switch (tabbedPane.getSelectedIndex())
      {

        case 0:
          setValue(FILE_COMPARISON);
          break;

        case 1:
          setValue(DIRECTORY_COMPARISON);
          break;
      }
    }
  }

  private void setValue(String value)
  {
    this.value = value;
  }

  public String getValue()
  {
    return value;
  }

  public String getOriginalFileName()
  {
    return originalFileName;
  }

  public String getMineFileName()
  {
    return mineFileName;
  }

  public String getOriginalDirectoryName()
  {
    return originalDirectoryName;
  }

  public String getMineDirectoryName()
  {
    return mineDirectoryName;
  }

  private JComponent getChooseFilePanel()
  {
    JPanel panel;

    tabbedPane = new JTabbedPane();
    tabbedPane.add(
      "File Comparison",
      getFileComparisonPanel());
    /*
    tabbedPane.add(
      "Directory Comparison",
      getDirectoryComparisonPanel());
    */

    panel = new JPanel(new BorderLayout());
    panel.add(tabbedPane, BorderLayout.CENTER);

    return panel;
  }

  private JComponent getFileComparisonPanel()
  {
    JPanel          panel;
    String          columns;
    String          rows;
    FormLayout      layout;
    CellConstraints cc;
    JLabel          label;
    JButton         button;

    columns = "10px, right:pref, 10px, max(150dlu;pref):grow, 5px, pref, 10px";
    rows = "10px, fill:pref, 5px, fill:pref, 5px, fill:pref, 10px";
    layout = new FormLayout(columns, rows);
    cc = new CellConstraints();

    panel = new JPanel(layout);

    label = new JLabel("Original");
    button = new JButton("Browse...");
    originalFileComboBox = new JComboBox();
    originalFileComboBox.setEditable(false);
    originalFileComboBox.addActionListener(getFileSelectAction());
    new ComboBoxPreference("OriginalFile", originalFileComboBox);

    button.setActionCommand(ORIGINAL_FILENAME);
    button.addActionListener(getFileBrowseAction());
    panel.add(
      label,
      cc.xy(2, 2));
    panel.add(
      originalFileComboBox,
      cc.xy(4, 2));
    panel.add(
      button,
      cc.xy(6, 2));

    label = new JLabel("Mine");
    button = new JButton("Browse...");
    button.setActionCommand(MINE_FILENAME);
    button.addActionListener(getFileBrowseAction());
    mineFileComboBox = new JComboBox();
    mineFileComboBox.setEditable(false);
    mineFileComboBox.addActionListener(getFileSelectAction());
    new ComboBoxPreference("MineFile", mineFileComboBox);
    panel.add(
      label,
      cc.xy(2, 4));
    panel.add(
      mineFileComboBox,
      cc.xy(4, 4));
    panel.add(
      button,
      cc.xy(6, 4));

    return panel;
  }

  public ActionListener getFileBrowseAction()
  {
    return new ActionListener()
      {
        public void actionPerformed(ActionEvent ae)
        {
          FileChooserPreference pref;
          JFileChooser          chooser;
          int                   result;
          File                  file;
          String                fileName;
          JComboBox             comboBox;

          chooser = new JFileChooser();
          pref = new FileChooserPreference("Browse", chooser);
          result = chooser.showOpenDialog(meldPanel);

          if (result == JFileChooser.APPROVE_OPTION)
          {
            pref.save();

            try
            {
              fileName = chooser.getSelectedFile().getCanonicalPath();

              comboBox = null;
              if (ae.getActionCommand().equals(ORIGINAL_FILENAME))
              {
                comboBox = originalFileComboBox;
              }
              else if (ae.getActionCommand().equals(MINE_FILENAME))
              {
                comboBox = mineFileComboBox;
              }

              if (comboBox != null)
              {
                comboBox.insertItemAt(fileName, 0);
                comboBox.setSelectedIndex(0);
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
          if (source == originalFileComboBox)
          {
            originalFileName = (String) originalFileComboBox.getSelectedItem();
          }
          else if (source == mineFileComboBox)
          {
            mineFileName = (String) mineFileComboBox.getSelectedItem();
          }
        }
      };
  }

  private JComponent getDirectoryComparisonPanel()
  {
    JPanel          panel;
    String          columns;
    String          rows;
    FormLayout      layout;
    CellConstraints cc;
    JLabel          label;
    JButton         button;

    columns = "10px, right:pref, 10px, max(150dlu;pref):grow, 5px, pref, 10px";
    rows = "10px, fill:pref, 5px, fill:pref, 5px, fill:pref, 10px";
    layout = new FormLayout(columns, rows);
    cc = new CellConstraints();

    panel = new JPanel(layout);

    label = new JLabel("Original");
    button = new JButton("Browse...");
    originalDirectoryComboBox = new JComboBox();
    originalDirectoryComboBox.setEditable(false);
    originalDirectoryComboBox.addActionListener(getDirectorySelectAction());
    new ComboBoxPreference("OriginalDirectory", originalDirectoryComboBox);

    button.setActionCommand(ORIGINAL_DIRECTORY);
    button.addActionListener(getDirectoryBrowseAction());
    panel.add(
      label,
      cc.xy(2, 2));
    panel.add(
      originalDirectoryComboBox,
      cc.xy(4, 2));
    panel.add(
      button,
      cc.xy(6, 2));

    label = new JLabel("Mine");
    button = new JButton("Browse...");
    button.setActionCommand(MINE_DIRECTORY);
    button.addActionListener(getDirectoryBrowseAction());
    mineDirectoryComboBox = new JComboBox();
    mineDirectoryComboBox.setEditable(false);
    mineDirectoryComboBox.addActionListener(getDirectorySelectAction());
    new ComboBoxPreference("MineDirectory", mineDirectoryComboBox);
    panel.add(
      label,
      cc.xy(2, 4));
    panel.add(
      mineDirectoryComboBox,
      cc.xy(4, 4));
    panel.add(
      button,
      cc.xy(6, 4));

    return panel;
  }

  public ActionListener getDirectoryBrowseAction()
  {
    return new ActionListener()
      {
        public void actionPerformed(ActionEvent ae)
        {
          DirectoryChooserPreference pref;
          JFileChooser               chooser;
          int                        result;
          File                       file;
          String                     fileName;
          JComboBox                  comboBox;

          chooser = new JFileChooser();
          chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          pref = new DirectoryChooserPreference("Browse", chooser);
          result = chooser.showOpenDialog(meldPanel);

          if (result == JFileChooser.APPROVE_OPTION)
          {
            pref.save();

            try
            {
              fileName = chooser.getSelectedFile().getCanonicalPath();

              comboBox = null;
              if (ae.getActionCommand().equals(ORIGINAL_DIRECTORY))
              {
                comboBox = originalDirectoryComboBox;
              }
              else if (ae.getActionCommand().equals(MINE_DIRECTORY))
              {
                comboBox = mineDirectoryComboBox;
              }

              if (comboBox != null)
              {
                comboBox.insertItemAt(fileName, 0);
                comboBox.setSelectedIndex(0);
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
          if (source == originalDirectoryComboBox)
          {
            originalDirectoryName = (String) originalDirectoryComboBox
              .getSelectedItem();
          }
          else if (source == mineDirectoryComboBox)
          {
            mineDirectoryName = (String) mineDirectoryComboBox.getSelectedItem();
          }
        }
      };
  }
}
