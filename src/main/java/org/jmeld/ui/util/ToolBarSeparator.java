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

public class ToolBarSeparator
    extends JComponent
{
  public ToolBarSeparator()
  {
    this(10, 10);
  }

  public ToolBarSeparator(int width, int height)
  {
    Dimension dimension;

    dimension = new Dimension(width, height);

    setMinimumSize(dimension);
    setPreferredSize(dimension);
    setMaximumSize(dimension);
  }

  protected void paintComponent(Graphics g)
  {
    Dimension d;
    int h;
    int x;

    d = getSize();

    x = d.width / 2;
    h = d.height / 4;

    g.setColor(getBackground().darker());
    g.drawLine(x, h, x, d.height - h - 1);
    g.drawLine(x, h - 1, x + 1, h - 1);

    g.setColor(getBackground().brighter());
    g.drawLine(x + 1, h, x + 1, d.height - h - 1);
    g.drawLine(x, d.height - h, x + 1, d.height - h);
  }
}
