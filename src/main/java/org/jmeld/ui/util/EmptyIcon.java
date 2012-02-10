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

public class EmptyIcon
    implements Icon
{
  private int width;
  private int height;
  private Color color;

  public EmptyIcon(Color color, int width, int height)
  {
    this.color = color;
    this.width = width;
    this.height = height;
  }

  public EmptyIcon(int width, int height)
  {
    this(null, width, height);
  }

  public void setColor(Color color)
  {
    this.color = color;
  }

  public int getIconWidth()
  {
    return width;
  }

  public int getIconHeight()
  {
    return height;
  }

  public void paintIcon(Component c, Graphics g, int x, int y)
  {
    if (color != null)
    {
      g.setColor(color);
      g.fillRect(x, y, getIconWidth(), getIconHeight());
    }
  }
}
