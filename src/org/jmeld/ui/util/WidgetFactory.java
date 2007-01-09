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

import org.jmeld.ui.action.*;

import javax.swing.*;

import java.awt.*;

public class WidgetFactory
{
  public static JMenuItem getMenuItem(Action action)
  {
    JMenuItem item;
    ImageIcon icon;

    item = new JMenuItem(action);

    icon = (ImageIcon) action.getValue(MeldAction.SMALL_ICON);
    if (icon != null)
    {
      item.setDisabledIcon(ImageUtil.createTransparentIcon(icon));
    }

    return item;
  }

  public static JButton getToolBarButton(Action action)
  {
    JButton   button;
    ImageIcon icon;
    Dimension size;

    button = new JButton(action);
    button.setVerticalTextPosition(AbstractButton.BOTTOM);
    button.setHorizontalTextPosition(AbstractButton.CENTER);
    button.setFocusable(false);

    icon = (ImageIcon) action.getValue(MeldAction.LARGE_ICON_KEY);
    if (icon != null)
    {
      button.setIcon(icon);
      button.setDisabledIcon(ImageUtil.createTransparentIcon(icon));
    }

    size = button.getPreferredSize();
    if (size.height > size.width)
    {
      size.width = size.height;
    }

    button.setPreferredSize(size);

    return button;
  }
}
