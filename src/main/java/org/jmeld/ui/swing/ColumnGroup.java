package org.jmeld.ui.swing;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Enumeration;
import java.util.Vector;

public class ColumnGroup
{
  protected TableCellRenderer renderer;
  protected Vector v;
  protected String text;

  public ColumnGroup(String text)
  {
    this(null, text);
  }

  public ColumnGroup(TableCellRenderer renderer, String text)
  {
    MultiLineHeaderRenderer multiHeaderRenderer;
    ListCellRenderer internalRenderer;

    if (renderer == null)
    {
      multiHeaderRenderer = new MultiLineHeaderRenderer();

      /*
            internalRenderer = multiHeaderRenderer.getCellRenderer();
            if (internalRenderer instanceof JLabel)
            {
              ((JLabel) internalRenderer).setOpaque(false);
              multiHeaderRenderer.setCellRenderer(internalRenderer);
            }
            */
      this.renderer = multiHeaderRenderer;
    }
    else
    {
      this.renderer = renderer;
    }
    this.text = text;
    v = new Vector();
  }

  /**
   * @param obj    TableColumn or ColumnGroup
   */
  public void add(Object obj)
  {
    if (obj == null)
    {
      return;
    }
    v.addElement(obj);
  }

  /**
   * @param c    TableColumn
   * @param v    ColumnGroups
   */
  public Vector getColumnGroups(TableColumn c, Vector g)
  {
    g.addElement(this);
    if (v.contains(c))
    {
      return g;
    }
    Enumeration enumerate = v.elements();

    while (enumerate.hasMoreElements())
    {
      Object obj = enumerate.nextElement();

      if (obj instanceof ColumnGroup)
      {
        Vector groups = (Vector) ((ColumnGroup) obj).getColumnGroups(c,
          (Vector) g.clone());

        if (groups != null)
        {
          return groups;
        }
      }
    }
    return null;
  }

  public TableCellRenderer getHeaderRenderer()
  {
    return renderer;
  }

  public void setHeaderRenderer(TableCellRenderer renderer)
  {
    if (renderer != null)
    {
      this.renderer = renderer;
    }
  }

  public Object getHeaderValue()
  {
    return text;
  }

  public Dimension getSize(JTable table)
  {
    Component comp;
    int height;
    int width;
    Enumeration enumerate;
    Object obj;
    TableColumn aColumn;

    comp = renderer.getTableCellRendererComponent(table, getHeaderValue(),
      false, false, -1, -1);
    height = comp.getPreferredSize().height;
    width = 0;
    enumerate = v.elements();

    while (enumerate.hasMoreElements())
    {
      obj = enumerate.nextElement();

      if (obj instanceof TableColumn)
      {
        aColumn = (TableColumn) obj;

        width += aColumn.getWidth();
      }
      else
      {
        width += ((ColumnGroup) obj).getSize(table).width;
      }
    }
    return new Dimension(width, height);
  }

  public String toString()
  {
    return super.toString() + ":" + text;
  }
}
