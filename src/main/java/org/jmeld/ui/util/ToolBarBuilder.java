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
package org.jmeld.ui.util;

import com.jgoodies.forms.builder.AbstractFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;

public class ToolBarBuilder
    extends AbstractFormBuilder
{
  public ToolBarBuilder(JComponent toolBar)
  {
    super(toolBar, new FormLayout("", "fill:p"));

    initialize();
  }

  private void initialize()
  {
    getContainer().setLayout(getLayout());
  }

  public void addButton(AbstractButton button)
  {
    appendColumn("pref:none");
    //getLayout().addGroupedColumn(getColumn());
    //button.putClientProperty(NARROW_KEY, Boolean.TRUE);
    add(button);
    nextColumn();
  }

  public void addComponent(JComponent component)
  {
    appendColumn("pref:none");
    add(component);
    nextColumn();
  }

  public void addSeparator()
  {
    appendColumn("pref:none");
    add(new ToolBarSeparator());
    nextColumn();
  }

  public void addSpring()
  {
    appendColumn("pref:grow");

    // Any old component will do here!
    add(Box.createHorizontalGlue());
    nextColumn();
  }
}
