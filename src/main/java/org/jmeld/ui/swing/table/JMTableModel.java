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
package org.jmeld.ui.swing.table;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.ArrayList;
import java.util.List;

public abstract class JMTableModel
    extends AbstractTableModel
{
  private List<Column> columns;

  public JMTableModel()
  {
    columns = new ArrayList<Column>();
  }

  public Column addColumn(String id, String columnGroupName, String columnName,
      Class columnClass, int columnSize, boolean editable)
  {
    Column column;

    column = new Column(id, columns.size(), columnGroupName, columnName,
        columnClass, columnSize, editable);
    columns.add(column);

    return column;
  }

  public abstract int getRowCount();

  public abstract Object getValueAt(int rowIndex, Column column);

  public void setValueAt(Object value, int rowIndex, Column column)
  {
    // empty: override if you want to use it
  }

  public int getColumnSize(int columnIndex)
  {
    return getColumn(columnIndex).columnSize;
  }

  public String getColumnName(int columnIndex)
  {
    return getColumn(columnIndex).columnName;
  }

  public Class getColumnClass(int columnIndex)
  {
    return getColumn(columnIndex).columnClass;
  }

  public Class getColumnClass(int rowIndex, Column column)
  {
    return null;
  }

  public Class getColumnClass(int rowIndex, int columnIndex)
  {
    return getColumnClass(rowIndex, getColumn(columnIndex));
  }

  public String getColumnGroupName(int columnIndex)
  {
    return getColumn(columnIndex).columnGroupName;
  }

  public int getColumnCount()
  {
    return columns.size();
  }

  public String getColumnId(int columnIndex)
  {
    return getColumn(columnIndex).id;
  }

  public TableCellRenderer getRenderer(int columnIndex)
  {
    return getColumn(columnIndex).renderer;
  }

  public TableCellEditor getEditor(int columnIndex)
  {
    return getColumn(columnIndex).editor;
  }

  public Column getColumn(int columnIndex)
  {
    return columns.get(columnIndex);
  }

  public boolean isCellEditable(int rowIndex, Column column)
  {
    return column.isEditable();
  }

  public final boolean isCellEditable(int rowIndex, int columnIndex)
  {
    if (!checkRowCount(rowIndex))
    {
      return false;
    }

    return isCellEditable(rowIndex, getColumn(columnIndex));
  }

  public final Object getValueAt(int rowIndex, int columnIndex)
  {
    if (!checkRowCount(rowIndex))
    {
      return "";
    }

    return getValueAt(rowIndex, getColumn(columnIndex));
  }

  public final void setValueAt(Object value, int rowIndex, int columnIndex)
  {
    if (!checkRowCount(rowIndex))
    {
      return;
    }

    setValueAt(value, rowIndex, getColumn(columnIndex));
  }

  private boolean checkRowCount(int rowIndex)
  {
    return rowIndex >= 0 && rowIndex < getRowCount();
  }

  public class Column
  {
    private String id;
    private int columnIndex;
    private String columnGroupName;
    private String columnName;
    private Class columnClass;
    private int columnSize;
    private boolean editable;
    private TableCellRenderer renderer;
    private TableCellEditor editor;

    public Column(String id, int columnIndex, String columnGroupName,
        String columnName, Class columnClass, int columnSize, boolean editable)
    {
      this.id = id;
      this.columnIndex = columnIndex;
      this.columnGroupName = columnGroupName;
      this.columnName = columnName;
      this.columnClass = columnClass;
      this.columnSize = columnSize;
      this.editable = editable;
    }

    public String getId()
    {
      return id;
    }

    public int getColumnIndex()
    {
      return columnIndex;
    }

    public String getColumnGroupName()
    {
      return columnGroupName;
    }

    public String getColumnName()
    {
      return columnName;
    }

    public Class getColumnClass()
    {
      return columnClass;
    }

    public int getColumnSize()
    {
      return columnSize;
    }

    public boolean isEditable()
    {
      return editable;
    }

    public String toString()
    {
      return "column[id=" + id + "]";
    }

    public void setRenderer(TableCellRenderer renderer)
    {
      this.renderer = renderer;
    }

    public void setEditor(TableCellEditor editor)
    {
      this.editor = editor;
    }
  }
}
