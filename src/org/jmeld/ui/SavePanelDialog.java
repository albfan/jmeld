package org.jmeld.ui;

import com.jgoodies.forms.layout.*;

import org.jmeld.*;
import org.jmeld.ui.text.*;
import org.jmeld.util.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class SavePanelDialog
{
// Instance variables:
  private JMeldPanel             meldPanel;
  private boolean                ok;
  private List<BufferDocumentIF> documents;
  private JCheckBox[]            checkBoxes;

  public SavePanelDialog(JMeldPanel meldPanel)
  {
    this.meldPanel = meldPanel;

    documents = new ArrayList<BufferDocumentIF>();
  }

  public void add(BufferDocumentIF document)
  {
    documents.add(document);
  }

  public void show()
  {
    JOptionPane pane;
    JDialog     dialog;

    pane = new JOptionPane(
        getSavePanel(),
        JOptionPane.WARNING_MESSAGE);
    pane.setOptionType(JOptionPane.OK_CANCEL_OPTION);

    dialog = pane.createDialog(meldPanel, "Save files");
    dialog.setResizable(true);
    dialog.show();

    if (ObjectUtil.equals(
        pane.getValue(),
        JOptionPane.OK_OPTION))
    {
      ok = true;
    }
  }

  public boolean isOK()
  {
    return ok;
  }

  public void doSave()
  {
    BufferDocumentIF document;

    if (checkBoxes == null)
    {
      return;
    }

    for (int i = 0; i < checkBoxes.length; i++)
    {
      if (!checkBoxes[i].isSelected())
      {
        continue;
      }

      document = documents.get(i);

      try
      {
        document.write();
      }
      catch (JMeldException ex)
      {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(meldPanel,
          "Can't write file" + document.getName(), "Problem writing file",
          JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private JComponent getSavePanel()
  {
    JPanel           panel;
    String           columns;
    String           rows;
    FormLayout       layout;
    CellConstraints  cc;
    JLabel           label;
    JCheckBox        checkBox;
    BufferDocumentIF document;
    Font             font;

    columns = "10px, fill:pref, 10px";
    rows = "10px, fill:pref, 5px, fill:pref, 10px,";
    for (int i = 0; i < documents.size(); i++)
    {
      rows += " fill:pref, ";
    }

    rows += " 10px";

    layout = new FormLayout(columns, rows);
    cc = new CellConstraints();

    panel = new JPanel(layout);
    label = new JLabel("Some files have been changed");
    font = label.getFont().deriveFont(Font.BOLD);
    label.setFont(font);
    label.setHorizontalAlignment(JLabel.LEFT);
    panel.add(
      label,
      cc.xy(2, 2));
    label = new JLabel("Which ones would you like to save?");
    label.setFont(font);
    label.setHorizontalAlignment(JLabel.LEFT);
    panel.add(
      label,
      cc.xy(2, 4));

    checkBoxes = new JCheckBox[documents.size()];
    for (int i = 0; i < documents.size(); i++)
    {
      document = documents.get(i);

      checkBox = new JCheckBox(document.getName());
      checkBoxes[i] = checkBox;
      if (!document.isChanged())
      {
        checkBox.setEnabled(false);
      }

      panel.add(
        checkBox,
        cc.xy(2, 6 + i));
    }

    return panel;
  }
}
