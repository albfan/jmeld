/*
 * $Id: ColumnGroup.java,v 1.3 2004/09/03 22:20:20 bcbeck Exp $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.table;

import org.jdesktop.swingx.table.ColumnHeaderRenderer;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
//import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/**  ColumnGroup
   @version .01 05/23/05
   @author Erik Vickroy
 */
public class ColumnGroup
{
  protected TableCellRenderer renderer;
  protected List<TableColumn> columns;
  protected List<ColumnGroup> groups;
  protected Object            headerValue;
  protected int               margin;

  public ColumnGroup(Object headerValue)
  {
    this(null, headerValue);
  }

  public ColumnGroup(
    TableCellRenderer renderer,
    Object            headerValue)
  {
    setHeaderValue(headerValue);

    if (renderer == null)
    {
      setHeaderRenderer(ColumnHeaderRenderer.getSharedInstance());

/*            setHeaderRenderer(new DefaultTableCellRenderer() {
   public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
       JTableHeader header = table.getTableHeader();
       if( header != null ) {
           setForeground(header.getForeground());
           setBackground(header.getBackground());
           setFont(header.getFont());
           header.setDefaultRenderer(this);
       }

       setHorizontalAlignment(JLabel.CENTER);
       setText( value == null ? "" : value.toString() );
       setBorder(UIManager.getBorder("TableHeader.cellBorder"));

       return this;
   }
   });*/
    }
    else
    {
      setHeaderRenderer(renderer);
    }
  }

  /**  Add a <pre>TableColumn</pre> to this group.
   * @param column The <pre>TableColumn</pre> to be added
   */
  public void add(TableColumn column)
  {
    if (columns == null)
    {
      columns = new ArrayList<TableColumn>();
    }
    columns.add(column);
  }

  /**  Add another <pre>ColumnGroup</pre> to this group.
   * @param group The <pre>ColumnGroup</pre> to be added
   */
  public void add(ColumnGroup group)
  {
    if (groups == null)
    {
      groups = new ArrayList<ColumnGroup>();
    }
    groups.add(group);
  }

  /**  Remove a <pre>TableColumn</pre> from this group.
   * @param column The <pre>TableColumn</pre> to be removed
   */
  public void remove(TableColumn column)
  {
    if (columns != null)
    {
      columns.remove(column);
    }
  }

  /**  Remove a nested <pre>ColumnGroup</pre> from this group.
   * @param group The <pre>ColumnGroup</pre> to be removed
   */
  public void remove(ColumnGroup group)
  {
    if (groups != null)
    {
      groups.remove(group);
    }
  }

  /**  Get all groups that a specific column falls under.
   * @param column The specific <pre>TableColumn</pre> that is contained
   * @param groupList The list of groups the specific <pre>TableColumn</pre> is contained in
   */
  public void getGroupsForColumn(
    TableColumn       column,
    List<ColumnGroup> groupList)
  {
    if (columns != null)
    {
      if (!columns.contains(column))
      {
        if (groups != null)
        {
          for (ColumnGroup group : groups)
          {
            group.getGroupsForColumn(column, groupList);
          }

          if (!groupList.isEmpty())
          {
            groupList.add(groupList.size() - 1, this);
          }
        }
      }
      else
      {
//                if( column instanceof TableColumnExt ) {
//                    TableColumnExt columnExt = (TableColumnExt) column;
//                    if( columnExt.isVisible() ) {
        groupList.add(this);
//                    }
//                }
      }
    }
  }

  /** Get the table header renderer for the group.
   * @return The table header renderer for the group
   */
  public TableCellRenderer getHeaderRenderer()
  {
    return renderer;
  }

  /** Sets the table header renderer for the group.
   * @param renderer The table header renderer for the group
   */
  public void setHeaderRenderer(TableCellRenderer renderer)
  {
    if (renderer != null)
    {
      this.renderer = renderer;
    }
  }

  /** Get the table header value for the group.
   * @return The table header value for the group
   */
  public Object getHeaderValue()
  {
    return headerValue;
  }

  /** Sets the table header value for the group.
   * @param headerValue The table header value for the group
   */
  public void setHeaderValue(Object headerValue)
  {
    this.headerValue = headerValue;
  }

  /** Get the size of the table header group.
   * @return The size of the table header group
   */
  public Dimension getSize(JTable table)
  {
    Component comp = getHeaderRenderer().getTableCellRendererComponent(
        table,
        getHeaderValue(),
        false,
        false,
        -1,
        -1);
    Dimension size = new Dimension(0, comp.getPreferredSize().height);

// Add the width for all visible TableColumns      
    if (columns != null)
    {
      for (TableColumn column : columns)
      {
        if (column instanceof TableColumnExt)
        {
          TableColumnExt columnExt = (TableColumnExt) column;
          if (columnExt.isVisible())
          {
            size.width += column.getWidth();
          }
        }
        else
        {
          size.width += column.getWidth();
        }
      }
    }

// Add the width for all ColumnGroups        
    if (groups != null)
    {
      for (ColumnGroup group : groups)
      {
        size.width += group.getSize(table).width;
        size.height += group.getSize(table).height;
      }
    }
    return size;
  }

  /** Sets the column margin for the group.
   * @param margin The column margin for the group
   */
  public void setColumnMargin(int margin)
  {
    this.margin = margin;

// Set the margin for nested groups    
    if (groups != null)
    {
      for (ColumnGroup group : groups)
      {
        group.setColumnMargin(margin);
      }
    }
  }
}
