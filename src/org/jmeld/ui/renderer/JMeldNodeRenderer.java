package org.jmeld.ui.renderer;

import org.jmeld.ui.util.*;
import org.jmeld.util.scan.*;

import javax.swing.*;

import java.awt.*;
import java.awt.geom.*;

public class JMeldNodeRenderer
       extends JLabel
       implements ListCellRenderer
{
  private boolean strikeThrough;

  public JMeldNodeRenderer()
  {
    setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
  }

  public Component getListCellRendererComponent(JList list, Object value,
    int index, boolean isSelected, boolean cellHasFocus)
  {
    JMeldNode node;

    node = (JMeldNode) value;

    setText(node.toString());
    strikeThrough = false;

    if (node.getState() == JMeldNode.ADDED)
    {
      setForeground(Colors.ADDED_DARK);
    }
    else if (node.getState() == JMeldNode.DELETED)
    {
      setForeground(Colors.DELETED_DARK);
      strikeThrough = true;
    }
    else if (node.getState() == JMeldNode.CHANGED)
    {
      setForeground(Colors.CHANGED_DARK);
    }
    else
    {
      setForeground(Color.BLACK);
    }

    return this;
  }

  public void paintComponent2(Graphics g)
  {
    Rectangle2D b;
    int         y;
    int         w;
    int         x;

    super.paintComponent(g);

    if (strikeThrough)
    {
      b = getFontMetrics(getFont()).getStringBounds(getText(), g);
      y = (getHeight() / 2 + (int) (b.getHeight() / 2)) / 2;
      w = (int) b.getWidth();
      x = (getIcon() == null ? 0 : getIcon().getIconWidth() + getIconTextGap());

      g.setColor(Color.black);
      g.drawLine(0, y, x + w, y);
    }
  }
  public void paintComponent(Graphics g)
  {
    Rectangle cb;
    Rectangle2D b;
    int         y;
    int         w;
    int         x;
    Insets      insets;

    super.paintComponent(g);

    if (strikeThrough)
    {
      insets = getInsets();

      cb = g.getClipBounds();
      b = getFontMetrics(getFont()).getStringBounds(getText(), g);
      y = cb.y + insets.top + ((int)cb.getHeight() / 2);
      w = cb.y + (int) b.getWidth();
      x = cb.x + insets.left;

      g.setColor(Color.black);
      g.drawLine(x, y, x + w, y);
    }
  }
}
