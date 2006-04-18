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
  private boolean   strikeThrough;
  private ImageIcon empty;
  private ImageIcon added;
  private ImageIcon changed;
  private ImageIcon deleted;

  public JMeldNodeRenderer()
  {
    setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
    setOpaque(true);

    empty = ImageUtil.getSmallImageIcon("stock_empty");
    added = ImageUtil.getSmallImageIcon("stock_added");
    changed = ImageUtil.getSmallImageIcon("stock_changed");
    deleted = ImageUtil.getSmallImageIcon("stock_deleted");
  }

  public Component getListCellRendererComponent(JList list, Object value,
    int index, boolean isSelected, boolean cellHasFocus)
  {
    JMeldNode node;

    node = (JMeldNode) value;

    setText(node.toString());
    setBackground(Color.WHITE);
    setIcon(empty);
    strikeThrough = false;

    if (node.getState() == JMeldNode.ADDED)
    {
      setForeground(Colors.ADDED_DARK);
      setIcon(added);
    }
    else if (node.getState() == JMeldNode.DELETED)
    {
      setForeground(Colors.DELETED_DARK);
      strikeThrough = true;
      setIcon(deleted);
    }
    else if (node.getState() == JMeldNode.CHANGED)
    {
      setForeground(Colors.CHANGED_DARK);
      setIcon(changed);
    }
    else
    {
      setForeground(Color.BLACK);
    }

    if (isSelected)
    {
      setBackground(list.getSelectionBackground());
    }

    return this;
  }

  public void paintComponent(Graphics g)
  {
    Rectangle   cb;
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
      y = cb.y + insets.top
        + ((int) (cb.getHeight() - insets.top - insets.bottom) / 2);
      w = cb.y + (int) b.getWidth();
      x = insets.left
        + (getIcon() == null ? 0 : getIcon().getIconWidth() + getIconTextGap());

      g.setColor(Color.black);
      g.drawLine(x, y, x + w, y);
    }
  }
}
