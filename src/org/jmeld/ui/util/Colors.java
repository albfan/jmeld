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

import javax.swing.*;

import java.awt.*;

public class Colors
{
  public static Color ADDED = new Color(180, 255, 180);
  public static Color CHANGED = new Color(160, 200, 255);
  public static Color CHANGED2 = new Color(200, 227, 255);
  public static Color DELETED = new Color(255, 160, 180);
  public static Color ADDED_DARK = new Color(13, 143, 13);
  public static Color CHANGED_DARK = new Color(62, 122, 172);
  public static Color DELETED_DARK = new Color(193, 50, 0);
  public static Color TABLEROW_LEFT = null;
  public static Color TABLEROW_RIGHT = null;
  public static Color DND_SELECTED_NEW = ADDED_DARK;
  public static Color DND_SELECTED_USED = new Color(238, 214, 128);

  /** Get a highlighter that will match the current l&f.
   */
  public static Color getTableRowHighLighterColor()
  {
    Color color;

    color = getSelectionColor();
    color = ColorUtil.setSaturation(color, 0.05f);
    color = ColorUtil.setBrightness(color, 1.00f);

    return color;
  }

  public static Color getDarkLookAndFeelColor()
  {
    Color color;

    color = getSelectionColor();
    color = ColorUtil.setBrightness(color, 0.40f);

    return color;
  }

  public static Color getSelectionColor()
  {
    // DO NOT USE UIManager to get colors because it is not lookandfeel
    //   independent! (Learned it the hard way with Nimbus l&f)
    return new JList().getSelectionBackground();
  }

  public static Color getPanelBackground()
  {
    return new JPanel().getBackground();
  }
}
