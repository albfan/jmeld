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
package org.jmeld.ui.settings;

import com.jgoodies.forms.layout.*;

import org.jmeld.*;
import org.jmeld.ui.*;
import org.jmeld.util.*;
import org.jmeld.settings.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SaveSettingsDialog
{
// Instance variables:
  private JMeldPanel meldPanel;
  private boolean    ok;

  public SaveSettingsDialog(JMeldPanel meldPanel)
  {
    this.meldPanel = meldPanel;
  }

  public void show()
  {
    JOptionPane pane;
    JDialog     dialog;

    pane = new JOptionPane(
        getSaveSettings(),
        JOptionPane.WARNING_MESSAGE);
    pane.setOptionType(JOptionPane.OK_CANCEL_OPTION);

    dialog = pane.createDialog(meldPanel, "Save settings");
    dialog.setResizable(true);
    try
    {
      dialog.show();

      if (ObjectUtil.equals(
          pane.getValue(),
          JOptionPane.OK_OPTION))
      {
        ok = true;
      }
    }
    finally
    {
      // Don't allow memoryleaks!
      dialog.dispose();
    }
  }

  public boolean isOK()
  {
    return ok;
  }

  public void doSave()
  {
    JMeldSettings.getInstance().save();
  }

  private JComponent getSaveSettings()
  {
    JPanel          panel;
    String          columns;
    String          rows;
    FormLayout      layout;
    CellConstraints cc;
    JLabel          label;
    Font            font;

    columns = "10px, fill:pref, 10px";
    rows = "10px, fill:pref, 5px, fill:pref, 10px,";
    rows += " 10px";

    layout = new FormLayout(columns, rows);
    cc = new CellConstraints();

    panel = new JPanel(layout);
    label = new JLabel("Settings have changed");
    font = label.getFont().deriveFont(Font.BOLD);
    label.setFont(font);
    label.setHorizontalAlignment(JLabel.LEFT);
    panel.add(
      label,
      cc.xy(2, 2));
    label = new JLabel("Would you like to save the settings?");
    label.setFont(font);
    label.setHorizontalAlignment(JLabel.LEFT);
    panel.add(
      label,
      cc.xy(2, 4));

    return panel;
  }
}
