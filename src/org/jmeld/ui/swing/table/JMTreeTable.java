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

import org.jdesktop.swingx.*;
import org.jdesktop.swingx.decorator.*;
import org.jdesktop.swingx.treetable.*;
import org.jmeld.*;
import org.jmeld.diff.*;
import org.jmeld.ui.search.*;
import org.jmeld.ui.swing.*;
import org.jmeld.ui.text.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.*;
import org.jmeld.util.file.*;
import org.jmeld.util.node.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;
import javax.swing.undo.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class JMTreeTable
       extends JXTreeTable
{
  private int                charWidth;
  private TreeTableHackerExt myTreeTableHacker;

  public JMTreeTable()
  {
    charWidth = getFontMetrics(getFont()).charWidth('W');

    setAutoResizeMode(AUTO_RESIZE_NEXT_COLUMN);
  }

  public void setTreeTableModel(JMTreeTableModel tableModel)
  {
    TableColumnModel  columnModel;
    TableColumn       column;
    int               preferredWidth;
    TableCellEditor   editor;
    TableCellRenderer renderer;

    super.setTreeTableModel(tableModel);

    setColumnControlVisible(true);
    /*
       ((DefaultCellEditor) getDefaultEditor(AbstractTreeTableModel.hierarchicalColumnClass))
         .setClickCountToStart(1);
     */
    if (tableModel != null)
    {
      // Make sure the icons fit well.
      if (getRowHeight() < 22)
      {
        setRowHeight(22);
      }

      columnModel = getColumnModel();
      for (int i = 0; i < tableModel.getColumnCount(); i++)
      {
        column = columnModel.getColumn(i);
        renderer = tableModel.getRenderer(i);
        if (renderer != null)
        {
          column.setCellRenderer(renderer);
        }

        editor = tableModel.getEditor(i);
        if (renderer != null)
        {
          column.setCellEditor(editor);
        }

        preferredWidth = charWidth * tableModel.getColumnSize(i);
        if (preferredWidth > 0)
        {
          column.setMinWidth(preferredWidth);
          column.setMaxWidth(preferredWidth);
          column.setPreferredWidth(preferredWidth);
        }
        else
        {
          getTableHeader().setResizingColumn(column);
        }
      }
    }
  }

  protected TreeTableHacker getTreeTableHacker()
  {
    if (myTreeTableHacker == null)
    {
      myTreeTableHacker = new TreeTableHackerExt()
          {
            @Override
            protected boolean isHitDetectionFromProcessMouse()
            {
              return false;
            }
          };
    }

    return myTreeTableHacker;
  }
}
