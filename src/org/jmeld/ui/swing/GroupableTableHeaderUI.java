package org.jmeld.ui.swing;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;

import java.awt.*;
import java.util.*;

public class GroupableTableHeaderUI
       extends BasicTableHeaderUI
{
  public void paint(
    Graphics   g,
    JComponent c)
  {
    Rectangle   clipBounds;
    int         column;
    Dimension   size;
    Rectangle   cellRect;
    Hashtable   h;
    Enumeration enumeration;
    TableColumn aColumn;
    Enumeration cGroups;
    int         groupHeight;

    clipBounds = g.getClipBounds();

    if (header.getColumnModel() == null)
    {
      return;
    }

    column = 0;
    size = header.getSize();
    cellRect = new Rectangle(0, 0, size.width, size.height);
    h = new Hashtable();

    enumeration = header.getColumnModel().getColumns();

    while (enumeration.hasMoreElements())
    {
      cellRect.height = size.height;
      cellRect.y = 0;
      aColumn = (TableColumn) enumeration.nextElement();
      cGroups = ((GroupableTableHeader) header).getColumnGroups(aColumn);

      if (cGroups != null)
      {
        groupHeight = 0;

        while (cGroups.hasMoreElements())
        {
          ColumnGroup cGroup = (ColumnGroup) cGroups.nextElement();
          Rectangle   groupRect = (Rectangle) h.get(cGroup);

          if (groupRect == null)
          {
            groupRect = new Rectangle(cellRect);
            Dimension d = cGroup.getSize(header.getTable());

            groupRect.width = d.width;
            groupRect.height = d.height;
            h.put(cGroup, groupRect);
          }
          paintCell(g, groupRect, cGroup);
          groupHeight += groupRect.height;
          cellRect.height = size.height - groupHeight;
          cellRect.y = groupHeight;
        }
      }

      cellRect.width = aColumn.getWidth();
      if (cellRect.intersects(clipBounds))
      {
        paintCell(g, cellRect, column);
      }
      cellRect.x += cellRect.width;
      column++;
    }
  }

  private void paintCell(
    Graphics  g,
    Rectangle cellRect,
    int       columnIndex)
  {
    TableColumn       aColumn;
    TableCellRenderer renderer;
    Component         component;

    aColumn = header.getColumnModel().getColumn(columnIndex);
    if ((renderer = aColumn.getHeaderRenderer()) == null)
    {
      renderer = header.getDefaultRenderer();
    }

    component = renderer.getTableCellRendererComponent(
        header.getTable(),
        aColumn.getHeaderValue(),
        false,
        false,
        -1,
        columnIndex);
    component.setFont(g.getFont());

    rendererPane.add(component);

    rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y,
      cellRect.width, cellRect.height, true);
  }

  private void paintCell(
    Graphics    g,
    Rectangle   cellRect,
    ColumnGroup cGroup)
  {
    TableCellRenderer renderer;
    Component         component;

    if ((renderer = cGroup.getHeaderRenderer()) == null)
    {
      renderer = header.getDefaultRenderer();
    }

    component = renderer.getTableCellRendererComponent(
        header.getTable(),
        cGroup.getHeaderValue(),
        false,
        false,
        -1,
        -1);

    //((JComponent) component).setOpaque(false);
    rendererPane.add(component);
    rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y,
      cellRect.width, cellRect.height, true);
  }

  private int getHeaderHeight()
  {
    TableCellRenderer renderer;
    int               height;
    TableColumnModel  columnModel;
    TableColumn       aColumn;
    Component         comp;
    int               cHeight;
    Enumeration       enumerate;
    ColumnGroup       cGroup;

    height = 0;
    columnModel = header.getColumnModel();

    for (int column = 0; column < columnModel.getColumnCount(); column++)
    {
      aColumn = columnModel.getColumn(column);

      if ((renderer = aColumn.getHeaderRenderer()) == null)
      {
        renderer = header.getDefaultRenderer();
      }

      comp = renderer.getTableCellRendererComponent(
          header.getTable(),
          aColumn.getHeaderValue(),
          false,
          false,
          -1,
          column);
      cHeight = comp.getPreferredSize().height;
      enumerate = ((GroupableTableHeader) header).getColumnGroups(aColumn);

      if (enumerate != null)
      {
        while (enumerate.hasMoreElements())
        {
          cGroup = (ColumnGroup) enumerate.nextElement();

          cHeight += cGroup.getSize(header.getTable()).height;
        }
      }
      height = Math.max(height, cHeight);
    }
    return height;
  }

  private Dimension createHeaderSize(long width)
  {
    TableColumnModel columnModel;

    columnModel = header.getColumnModel();

    if (width > Integer.MAX_VALUE)
    {
      width = Integer.MAX_VALUE;
    }

    return new Dimension(
      (int) width,
      getHeaderHeight());
  }

  public Dimension getPreferredSize(JComponent c)
  {
    long        width;
    Enumeration enumeration;
    TableColumn aColumn;

    width = 0;
    enumeration = header.getColumnModel().getColumns();

    while (enumeration.hasMoreElements())
    {
      aColumn = (TableColumn) enumeration.nextElement();

      width = width + aColumn.getPreferredWidth();
    }

    return createHeaderSize(width);
  }
}
