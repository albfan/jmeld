package org.jmeld.ui.swing;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class MultiLineHeaderRenderer
    extends JLabel
    implements TableCellRenderer
{
  private Icon icon;

  public MultiLineHeaderRenderer()
  {
    ListCellRenderer renderer;

    LookAndFeel.installColorsAndFont(this, "TableHeader.background",
      "TableHeader.foreground", "TableHeader.font");
    LookAndFeel.installBorder(this, "TableHeader.cellBorder");

    /*
        renderer = getCellRenderer();
        if (renderer instanceof JLabel)
        {
          ((JLabel) renderer).setHorizontalAlignment(JLabel.CENTER);
          setCellRenderer(renderer);
        }
        */

    setOpaque(false);
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column)
  {
    String str;

    str = (value == null) ? "" : value.toString();

    setText(str);
    setHorizontalAlignment(JLabel.CENTER);

    return this;
  }

  public void setIcon(Icon icon)
  {
    this.icon = icon;
  }

  /*
    public void paintComponent(Graphics g)
    {
      Rectangle r;
      int       x;
      int       y;

      super.paintComponent(g);

      if (icon != null)
      {
        r = getBounds();
        x = r.width - icon.getIconWidth();
        y = ((r.height - icon.getIconHeight()) / 2);

        icon.paintIcon(this, g, x, y);
      }
    }
    */

  private static final long serialVersionUID = 101783804743496189L;
}
