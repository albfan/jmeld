/*
 * $Id: BasicGroupableTableHeaderUI.java,v 1.3 2005/09/22 03:58:42 evickroy Exp $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf.basic;

import org.jdesktop.swingx.JXGroupableTableHeader;
import org.jdesktop.swingx.plaf.GroupableTableHeaderUI;
import org.jdesktop.swingx.table.ColumnGroup;

import javax.swing.*;
import javax.swing.JComponent;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import java.awt.*;
import java.awt.Cursor;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 *
 * @author erik
 */
public class BasicGroupableTableHeaderUI
       extends GroupableTableHeaderUI
{
  protected static Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
  protected int           columnSelected = -1;
  protected ColumnGroup   columnGroupSelected = null;

//
// Instance Variables
//

  /** The JTableHeader that is delegating the painting to this UI. */
  protected JXGroupableTableHeader header;
  protected CellRendererPane rendererPane;

  // Listeners that are attached to the JTable
  protected MouseInputListener mouseInputListener;

  /**
   * Creates the mouse listener for the JTable.
   */
  protected MouseInputListener createMouseInputListener()
  {
    return new MyMouseInputHandler();
  }

  public void paint(
    Graphics   graphics,
    JComponent component)
  {
    TableColumnModel columnModel = header.getColumnModel();

// Only if there are columns
    if (columnModel != null)
    {
      Rectangle clipBounds = graphics.getClipBounds();
      Dimension headerSize = header.getSize();
      Rectangle cellRect = new Rectangle(0, 0, headerSize.width,
          headerSize.height);
      Hashtable paintedGroups = new Hashtable();
      ((JXGroupableTableHeader) header).setColumnMargin();

      TableColumn draggedColumn = header.getDraggedColumn();
      Rectangle   draggedCellRect = null;

      for (int columnIndex = 0; columnIndex < columnModel.getColumnCount();
        columnIndex++)
      {
        TableColumn column = columnModel.getColumn(columnIndex);
        int         groupHeight = 0;

        cellRect.height = headerSize.height;
        cellRect.y = 0;

        List<ColumnGroup> groupList = ((JXGroupableTableHeader) header)
          .getGroupsForColumn(column);
        for (ColumnGroup group : groupList)
        {
          Rectangle groupRect = (Rectangle) paintedGroups.get(group);

          if (groupRect == null)
          {
            groupRect = new Rectangle(cellRect);
            Dimension d = group.getSize(header.getTable());
            groupRect.width = d.width;
            groupRect.height = d.height;
            paintedGroups.put(group, groupRect);
          }

          paintCell(graphics, groupRect, group);
          groupHeight += groupRect.height;
          cellRect.height = headerSize.height - groupHeight;
          cellRect.y = groupHeight;
        }

        cellRect.width = column.getWidth();
        if (cellRect.intersects(clipBounds))
        {
          if (draggedColumn == column)
          {
            draggedCellRect = new Rectangle(cellRect);
/*
   graphics.setColor(header.getParent().getBackground());
   graphics.fillRect(draggedCellRect.x, draggedCellRect.y,
                   draggedCellRect.width, draggedCellRect.height);
   draggedCellRect.x += header.getDraggedDistance();
   // Fill the background.
   graphics.setColor(header.getBackground());
   graphics.fillRect(draggedCellRect.x, draggedCellRect.y,
                   draggedCellRect.width, draggedCellRect.height);
   paintCell(graphics, draggedCellRect, columnIndex);
 */
          }
          else
          {
            paintCell(graphics, cellRect, columnIndex);
          }
        }

        cellRect.x += cellRect.width;
      }

      // Paint the dragged column if we are dragging.
      if (draggedColumn != null)
      {
        int draggedColumnIndex = viewIndexForColumn(draggedColumn);
//                Rectangle draggedCellRect = header.getHeaderRect(draggedColumnIndex);

        // Draw a gray well in place of the moving column.
        graphics.setColor(header.getParent().getBackground());
        graphics.fillRect(draggedCellRect.x, draggedCellRect.y,
          draggedCellRect.width, draggedCellRect.height);

        draggedCellRect.x += header.getDraggedDistance();

        // Fill the background.
        graphics.setColor(header.getBackground());
        graphics.fillRect(draggedCellRect.x, draggedCellRect.y,
          draggedCellRect.width, draggedCellRect.height);

        paintCell(graphics, draggedCellRect, draggedColumnIndex);
      }

// Remove all components in the rendererPane.
      rendererPane.removeAll();
    }
  }

  protected void paintCell(
    Graphics  graphics,
    Rectangle cellRect,
    int       columnIndex)
  {
    TableColumn column = header.getColumnModel().getColumn(columnIndex);

    Component   component = getHeaderRenderer(columnIndex);

    rendererPane.paintComponent(graphics, component, header, cellRect.x,
      cellRect.y, cellRect.width, cellRect.height, true);
  }

  protected void paintCell(
    Graphics    graphics,
    Rectangle   cellRect,
    ColumnGroup group)
  {
    Component component = getHeaderRenderer(group);

    rendererPane.paintComponent(graphics, component, header, cellRect.x,
      cellRect.y, cellRect.width, cellRect.height, true);
  }

  /**
   * Retrieves renderer for the specified column header.
   *
   * @param columnIndex
   *            Column index.
   * @return Renderer for the specified column header.
   */
  protected Component getHeaderRenderer(int columnIndex)
  {
    TableColumn       aColumn = header.getColumnModel().getColumn(columnIndex);
    TableCellRenderer renderer = aColumn.getHeaderRenderer();
    if (renderer == null)
    {
      renderer = header.getDefaultRenderer();
    }
    return renderer.getTableCellRendererComponent(
      header.getTable(),
      aColumn.getHeaderValue(),
      false,
      false,
      -1,
      columnIndex);
  }

  /**
   * Retrieves renderer for the specified column header.
   *
   * @param columnIndex
   *            Column index.
   * @return Renderer for the specified column header.
   */
  protected Component getHeaderRenderer(ColumnGroup group)
  {
    return group.getHeaderRenderer().getTableCellRendererComponent(
      header.getTable(),
      group.getHeaderValue(),
      false,
      false,
      -1,
      -1);
  }

  /**
   * Retrieves view index for the specified column.
   *
   * @param aColumn
   *            Table column.
   * @return View index for the specified column.
   */
  protected int viewIndexForColumn(TableColumn aColumn)
  {
    TableColumnModel cm = header.getColumnModel();
    for (int column = 0; column < cm.getColumnCount(); column++)
    {
      if (cm.getColumn(column) == aColumn)
      {
        return column;
      }
    }
    return -1;
  }

  private int getHeaderHeight()
  {
    int               maxHeight = 0;
    TableColumnModel  columnModel = header.getColumnModel();
    TableCellRenderer renderer = header.getDefaultRenderer();

    for (int columnIndex = 0; columnIndex < columnModel.getColumnCount();
      columnIndex++)
    {
      TableColumn column = columnModel.getColumn(columnIndex);

      Component   component = renderer.getTableCellRendererComponent(
          header.getTable(),
          column.getHeaderValue(),
          false,
          false,
          -1,
          columnIndex);

// Calculate the collective height for this column
      int               height = component.getPreferredSize().height;

      List<ColumnGroup> groupList = ((JXGroupableTableHeader) header)
        .getGroupsForColumn(column);

      for (ColumnGroup group : groupList)
      {
        height += group.getSize(header.getTable()).height;
      }

// We only need to keep the maximum height for the entire header
      maxHeight = Math.max(maxHeight, height);
    }

    return maxHeight;
  }

  /**
   * Return the preferred size of the header. The preferred height is the
   * maximum of the preferred heights of all of the components provided
   * by the header renderers. The preferred width is the sum of the
   * preferred widths of each column (plus inter-cell spacing).
   *
   * @param component
   * @return the combined preferred size for the entire column header
   */
  public Dimension getPreferredSize(JComponent component)
  {
    long                     width = 0;
    TableColumn              column = null;
    TableColumnModel         columnModel = header.getColumnModel();

// Total up preferred width of each column
    Enumeration<TableColumn> columns = columnModel.getColumns();
    while (columns.hasMoreElements())
    {
      column = columns.nextElement();
      width += column.getPreferredWidth();
    }

// Don't forget to add the margin
    width += columnModel.getColumnMargin() * columnModel.getColumnCount();

    return createHeaderSize(width);
  }

//
//  The installation/uninstall procedures and support
//
  public static ComponentUI createUI(JComponent h)
  {
    return new BasicGroupableTableHeaderUI();
  }

//  Installation
  public void installUI(JComponent c)
  {
    header = (JXGroupableTableHeader) c;

    rendererPane = new CellRendererPane();
    header.add(rendererPane);

    installDefaults();
    installListeners();
    installKeyboardActions();
  }

  /**
   * Initialize JTableHeader properties, e.g. font, foreground, and background.
   * The font, foreground, and background properties are only set if their
   * current value is either null or a UIResource, other properties are set
   * if the current value is null.
   *
   * @see #installUI
   */
  protected void installDefaults()
  {
    LookAndFeel.installColorsAndFont(header, "TableHeader.background",
      "TableHeader.foreground", "TableHeader.font");
    LookAndFeel.installProperty(header, "opaque", Boolean.TRUE);
  }

  /**
   * Attaches listeners to the JTableHeader.
   */
  protected void installListeners()
  {
    mouseInputListener = createMouseInputListener();

    header.addMouseListener(mouseInputListener);
    header.addMouseMotionListener(mouseInputListener);
  }

  /**
   * Register all keyboard actions on the JTableHeader.
   */
  protected void installKeyboardActions()
  {
  }

// Uninstall methods
  public void uninstallUI(JComponent c)
  {
    uninstallDefaults();
    uninstallListeners();
    uninstallKeyboardActions();

    header.remove(rendererPane);
    rendererPane = null;
    header = null;
  }

  protected void uninstallDefaults()
  {
  }

  protected void uninstallListeners()
  {
    header.removeMouseListener(mouseInputListener);
    header.removeMouseMotionListener(mouseInputListener);

    mouseInputListener = null;
  }

  protected void uninstallKeyboardActions()
  {
  }

  private Dimension createHeaderSize(long width)
  {
    if (width > Integer.MAX_VALUE)
    {
      width = Integer.MAX_VALUE;
    }
    return new Dimension(
      (int) width,
      getHeaderHeight());
  }

  /**
   * Return the minimum size of the header. The minimum width is the sum
   * of the minimum widths of each column (plus inter-cell spacing).
   */
  public Dimension getMinimumSize(JComponent c)
  {
    long        width = 0;
    Enumeration enumeration = header.getColumnModel().getColumns();
    while (enumeration.hasMoreElements())
    {
      TableColumn aColumn = (TableColumn) enumeration.nextElement();
      width = width + aColumn.getMinWidth();
    }
    return createHeaderSize(width);
  }

  /**
   * Return the maximum size of the header. The maximum width is the sum
   * of the maximum widths of each column (plus inter-cell spacing).
   */
  public Dimension getMaximumSize(JComponent c)
  {
    long        width = 0;
    Enumeration enumeration = header.getColumnModel().getColumns();
    while (enumeration.hasMoreElements())
    {
      TableColumn aColumn = (TableColumn) enumeration.nextElement();
      width = width + aColumn.getMaxWidth();
    }
    return createHeaderSize(width);
  }

  /**
   * This inner class is marked &quot;public&quot; due to a compiler bug.
   * This class should be treated as a &quot;protected&quot; inner class.
   * Instantiate it only within subclasses of BasicTableUI.
   */

//    public class MouseInputHandler implements MouseInputListener {
  public class MyMouseInputHandler
         implements MouseInputListener
  {
    protected int    mouseXOffset;
    protected Cursor otherCursor = resizeCursor;
    protected int    columnOffset = 0;
    protected int    width = 0;

    public void mouseClicked(MouseEvent e)
    {
    }

    protected boolean canResize(TableColumn column)
    {
      return (column != null) && header.getResizingAllowed()
      && column.getResizable();
    }

    protected TableColumn getResizingColumn(Point p)
    {
      return getResizingColumn(
        p,
        header.columnAtPoint(p));
    }

    protected TableColumn getResizingColumn(
      Point p,
      int   column)
    {
      if (column == -1)
      {
        return null;
      }
      Rectangle r = header.getHeaderRect(column);
      r.grow(-3, 0);
      if (r.contains(p))
      {
        return null;
      }
      int midPoint = r.x + r.width / 2;
      int columnIndex;
      if (header.getComponentOrientation().isLeftToRight())
      {
        columnIndex = (p.x < midPoint) ? column - 1 : column;
      }
      else
      {
        columnIndex = (p.x < midPoint) ? column : column - 1;
      }
      if (columnIndex == -1)
      {
        return null;
      }
      return header.getColumnModel().getColumn(columnIndex);
    }

    protected void swapCursor()
    {
      Cursor tmp = header.getCursor();
      header.setCursor(otherCursor);
      otherCursor = tmp;
    }

    public void mouseMoved(MouseEvent e)
    {
      if (canResize(getResizingColumn(e.getPoint())) != (header.getCursor() == resizeCursor))
      {
        swapCursor();
      }

      Point            p = e.getPoint();
      TableColumnModel columnModel = header.getColumnModel();
      int              index = header.columnAtPoint(p);
      ColumnGroup      currentGroup = null;
      TableColumn      column = columnModel.getColumn(index);
      int              groupHeight = 0;

      columnGroupSelected = null;

      List<ColumnGroup> groupList = ((JXGroupableTableHeader) header)
        .getGroupsForColumn(column);
      for (ColumnGroup group : groupList)
      {
        Dimension d = group.getSize(header.getTable());
        groupHeight += d.height;
        if (p.y <= groupHeight)
        {
          columnGroupSelected = group;
          columnSelected = -1;
          break;
        }
      }

      if (columnGroupSelected == null)
      {
        columnSelected = index;
      }

      header.repaint();
    }

    public void mousePressed(MouseEvent e)
    {
      header.setDraggedColumn(null);
      header.setResizingColumn(null);
      header.setDraggedDistance(0);

      Point p = e.getPoint();

      // First find which header cell was hit
      TableColumnModel columnModel = header.getColumnModel();
      int              index = header.columnAtPoint(p);

      if (index != -1)
      {
        // The last 3 pixels + 3 pixels of next column are for resizing
        TableColumn resizingColumn = getResizingColumn(p, index);
        if (canResize(resizingColumn))
        {
          header.setResizingColumn(resizingColumn);
          if (header.getComponentOrientation().isLeftToRight())
          {
            mouseXOffset = p.x - resizingColumn.getWidth();
          }
          else
          {
            mouseXOffset = p.x + resizingColumn.getWidth();
          }
        }
        else if (header.getReorderingAllowed())
        {
          TableColumn hitColumn = columnModel.getColumn(index);
          header.setDraggedColumn(hitColumn);
          mouseXOffset = p.x;
        }
      }
    }

    public void mouseDragged(MouseEvent e)
    {
      int         mouseX = e.getX();
      boolean     canMove = false;

      TableColumn resizingColumn = header.getResizingColumn();
      TableColumn draggedColumn = header.getDraggedColumn();

      boolean     headerLeftToRight = header.getComponentOrientation()
                                            .isLeftToRight();

      if (resizingColumn != null)
      {
        int oldWidth = resizingColumn.getWidth();
        int newWidth;
        if (headerLeftToRight)
        {
          newWidth = mouseX - mouseXOffset;
        }
        else
        {
          newWidth = mouseXOffset - mouseX;
        }
        resizingColumn.setWidth(newWidth);

        Container container;
        if ((header.getParent() == null)
          || ((container = header.getParent().getParent()) == null)
          || !(container instanceof JScrollPane))
        {
          return;
        }

        if (!container.getComponentOrientation().isLeftToRight()
          && !headerLeftToRight)
        {
          JTable table = header.getTable();
          if (table != null)
          {
            JViewport viewport = ((JScrollPane) container).getViewport();
            int       viewportWidth = viewport.getWidth();
            int       diff = newWidth - oldWidth;
            int       newHeaderWidth = table.getWidth() + diff;

            /* Resize a table */
            Dimension tableSize = table.getSize();
            tableSize.width += diff;
            table.setSize(tableSize);

            /* If this table is in AUTO_RESIZE_OFF mode and
             * has a horizontal scrollbar, we need to update
             * a view's position.
             */
            if ((newHeaderWidth >= viewportWidth)
              && (table.getAutoResizeMode() == JTable.AUTO_RESIZE_OFF))
            {
              Point p = viewport.getViewPosition();
              p.x = Math.max(
                  0,
                  Math.min(newHeaderWidth - viewportWidth, p.x + diff));
              viewport.setViewPosition(p);

              /* Update the original X offset value. */
              mouseXOffset += diff;
            }
          }
        }
      }
      else if (draggedColumn != null)
      {
        TableColumnModel cm = header.getColumnModel();
        int              draggedDistance = mouseX - mouseXOffset;
        int              direction = (draggedDistance < 0) ? -1 : 1;
        int              columnIndex = viewIndexForColumn(draggedColumn);
        int              newColumnIndex = columnIndex
          + (headerLeftToRight ? direction : -direction) + columnOffset;
        if (0 <= newColumnIndex && newColumnIndex < cm.getColumnCount())
        {
          TableColumn evalColumn = cm.getColumn(newColumnIndex);
          int         evalWidth = evalColumn.getWidth();

//		    if (Math.abs(draggedDistance) > (width / 2)) {
          if (Math.abs(draggedDistance) > ((width + evalWidth) / 2))
          {
            width += evalWidth;
//                        if( newColumnIndex < cm.getColumnCount() ) {
            columnOffset = columnOffset + direction;

//                        }
            List<ColumnGroup> draggedGroupList = ((JXGroupableTableHeader) header)
              .getGroupsForColumn(draggedColumn);
            List<ColumnGroup> evalGroupList = ((JXGroupableTableHeader) header)
              .getGroupsForColumn(evalColumn);
            if (draggedGroupList.containsAll(evalGroupList)
              && evalGroupList.containsAll(draggedGroupList))
            {
              mouseXOffset = mouseXOffset + direction * width;
              header.setDraggedDistance(draggedDistance - direction * width);
              columnSelected = newColumnIndex;
              cm.moveColumn(columnIndex, newColumnIndex);
              columnOffset = 0;
              width = 0;
//                        }
              return;
            }
          }
        }
        setDraggedDistance(draggedDistance, columnIndex);
      }
    }

    public void mouseReleased(MouseEvent e)
    {
      setDraggedDistance(
        0,
        viewIndexForColumn(header.getDraggedColumn()));

      columnOffset = 0;
      width = 0;
      header.setResizingColumn(null);
      header.setDraggedColumn(null);
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
      columnGroupSelected = null;
      columnSelected = -1;
      header.repaint();
    }

//
// Protected & Private Methods
//
    private void setDraggedDistance(
      int draggedDistance,
      int column)
    {
      header.setDraggedDistance(draggedDistance);
      if (column != -1)
      {
        header.getColumnModel().moveColumn(column, column);
      }
    }
  }
}
