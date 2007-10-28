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

  /** Get a highlighter that will match the current l&f.
   */
  public static Color getTableRowHighLighterColor()
  {
    Color color;

    color = UIManager.getColor("Table.selectionBackground");
    if(color == null)
    {
      color = new Color(244, 242, 198);
    }
    color = ColorUtil.setSaturation(color, 0.05f);
    color = ColorUtil.setBrightness(color, 1.00f);

    return color;
  }
}
