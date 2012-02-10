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

import javax.swing.*;
import javax.swing.table.*;

public class JMTable
        extends JTable {
    private int charWidth;

    public JMTable() {
        charWidth = getFontMetrics(getFont()).charWidth('W');
    }

    public void setModel(JMTableModel tableModel) {
        TableColumnModel columnModel;
        TableColumn column;
        int preferredWidth;
        TableCellEditor editor;
        TableCellRenderer renderer;

        super.setModel(tableModel);

        if (tableModel != null) {
            // Make sure the icons fit well.
            if (getRowHeight() < 22) {
                setRowHeight(22);
            }

            columnModel = getColumnModel();

            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                column = columnModel.getColumn(i);

                renderer = tableModel.getRenderer(i);
                if (renderer != null) {
                    column.setCellRenderer(renderer);
                }

                editor = tableModel.getEditor(i);
                if (renderer != null) {
                    column.setCellEditor(editor);
                }

//                preferredWidth = charWidth * tableModel.getColumnSize(i);
//                if (preferredWidth > 0) {
//                    column.setMinWidth(preferredWidth);
//                    column.setMaxWidth(preferredWidth);
//                    column.setPreferredWidth(preferredWidth);
//                } else {
//                    getTableHeader().setResizingColumn(column);
//                }
            }
        }
    }

    public TableCellEditor getCellEditor(int row, int column) {
        Class clazz;
        TableCellEditor editor;

        clazz = ((JMTableModel) getModel()).getColumnClass(row, column);
        editor = getDefaultEditor(clazz);
        if (editor != null) {
            return editor;
        }

        return super.getCellEditor(row, column);
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        Class clazz;
        TableCellRenderer renderer;
        TableModel model;

        model = getModel();
        if (model instanceof JMTableModel) {
            clazz = ((JMTableModel) model).getColumnClass(row, column);
            renderer = getDefaultRenderer(clazz);
            if (renderer != null) {
                return renderer;
            }
        }

        return super.getCellRenderer(row, column);
    }
}
