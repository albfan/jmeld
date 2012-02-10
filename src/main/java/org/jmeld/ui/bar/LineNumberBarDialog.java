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
package org.jmeld.ui.bar;

import org.jmeld.ui.AbstractBarDialog;
import org.jmeld.ui.JMeldPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LineNumberBarDialog
    extends AbstractBarDialog
{
  // Instance variables:
  private JTextField lineNumberField;

  public LineNumberBarDialog(JMeldPanel meldPanel)
  {
    super(meldPanel);
  }

  protected void init()
  {
    setLayout(new FlowLayout(FlowLayout.LEADING));

    // Incremental search:
    lineNumberField = new JTextField(15);
    lineNumberField.addKeyListener(getSearchKeyAction());

    add(Box.createHorizontalStrut(5));
    add(new JLabel("Linenumber:"));
    add(lineNumberField);
  }

  public void _activate()
  {
    lineNumberField.setText("");
    lineNumberField.requestFocus();
  }

  public void _deactivate()
  {
  }

  private KeyListener getSearchKeyAction()
  {
    return new KeyAdapter()
    {
      public void keyReleased(KeyEvent e)
      {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
        {
          getMeldPanel().doGoToLine(Integer.valueOf(lineNumberField.getText()));
        }
      }
    };
  }
}
