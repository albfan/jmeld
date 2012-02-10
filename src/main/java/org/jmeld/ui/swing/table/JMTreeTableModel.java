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

import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jmeld.ui.UINode;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.ArrayList;
import java.util.List;

public abstract class JMTreeTableModel extends DefaultTreeTableModel {
    private List<Column> columns;

    public JMTreeTableModel() {
        columns = new ArrayList<Column>();
    }

    public Object getChild(Object parent, int index) {
        return ((UINode) parent).getChildAt(index);
    }

    public int getChildCount(Object parent) {
        return ((UINode) parent).getChildCount();
    }

    public Column addColumn(String id, String columnGroupName, String columnName,
                            Class columnClass, int columnSize, boolean editable) {
        Column column;

        column = new Column(id, columns.size(), columnGroupName, columnName,
                columnClass, columnSize, editable);
        columns.add(column);

        return column;
    }

    public abstract Object getValueAt(Object objectNode, Column column);

    public int getColumnSize(int columnIndex) {
        return getColumn(columnIndex).columnSize;
    }

    public String getColumnName(int columnIndex) {
        return getColumn(columnIndex).columnName;
    }

    public Class getColumnClass(int columnIndex) {
        Class clazz;

        clazz = getColumn(columnIndex).columnClass;
        if (clazz != null) {
            return clazz;
        }

        return super.getColumnClass(columnIndex);
    }

    public int getColumnCount() {
        return columns.size();
    }

    public String getColumnId(int columnIndex) {
        return getColumn(columnIndex).id;
    }

    public TableCellRenderer getRenderer(int columnIndex) {
        return getColumn(columnIndex).renderer;
    }

    public TableCellEditor getEditor(int columnIndex) {
        return getColumn(columnIndex).editor;
    }

    public Column getColumn(int columnIndex) {
        return columns.get(columnIndex);
    }

    public boolean isCellEditable(int rowIndex, Column column) {
        return column.isEditable();
    }

    public final boolean isCellEditable(int rowIndex, int columnIndex) {
        return isCellEditable(rowIndex, getColumn(columnIndex));
    }

    public final Object getValueAt(Object objectNode, int columnIndex) {
        return getValueAt(objectNode, getColumn(columnIndex));
    }

    public class Column {
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
                      String columnName, Class columnClass, int columnSize, boolean editable) {
            this.id = id;
            this.columnIndex = columnIndex;
            this.columnGroupName = columnGroupName;
            this.columnName = columnName;
            this.columnClass = columnClass;
            this.columnSize = columnSize;
            this.editable = editable;
        }

        public String getId() {
            return id;
        }

        public int getColumnIndex() {
            return columnIndex;
        }

        public String getColumnGroupName() {
            return columnGroupName;
        }

        public String getColumnName() {
            return columnName;
        }

        public Class getColumnClass() {
            return columnClass;
        }

        public int getColumnSize() {
            return columnSize;
        }

        public boolean isEditable() {
            return editable;
        }

        public String toString() {
            return "column[id=" + id + "]";
        }

        public void setRenderer(TableCellRenderer renderer) {
            this.renderer = renderer;
        }

        public void setEditor(TableCellEditor editor) {
            this.editor = editor;
        }
    }
}
