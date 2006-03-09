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

  public ToolBarSeparator(
    int width,
    int height)
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
