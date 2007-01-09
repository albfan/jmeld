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
package org.jmeld.ui;

import org.jmeld.diff.*;
import org.jmeld.ui.text.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.file.*;
import org.jmeld.util.node.*;

import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class FolderDiffTableModel
       extends AbstractTableModel
{
  private FolderDiff   diff;
  private List<Column> columns;
  private NodeFilter   nodeFilter;

  public FolderDiffTableModel(FolderDiff diff)
  {
    this.diff = diff;

    columns = new ArrayList<Column>();
    columns.add(new Column("orgNode", null, "Node", Icon.class, 40, null));
    columns.add(new Column("orgName", null, "Name", String.class, -1, null));
    columns.add(
      new Column("orgSize", "Original", "Size", Integer.class, 80,
        Colors.TABLEROW_ORG));
    columns.add(
      new Column("orgState", "Original", "", Icon.class, 25,
        Colors.TABLEROW_ORG));
    /*
    columns.add(
      new Column("copyToRight", "Original", "", Icon.class, 25,
        Colors.TABLEROW_ORG));
    columns.add(
      new Column("copyToLeft", "Mine", "", Icon.class, 25, Colors.TABLEROW_MINE));
    */
    columns.add(
      new Column("mineState", "Mine", "", Icon.class, 25, Colors.TABLEROW_MINE));
    columns.add(
      new Column("mineSize", "Mine", "Size", Integer.class, 80,
        Colors.TABLEROW_MINE));

    nodeFilter = new NodeFilter();
  }

  public int getColumnSize(int columnIndex)
  {
    return columns.get(columnIndex).columnSize;
  }

  public String getColumnName(int columnIndex)
  {
    return columns.get(columnIndex).columnName;
  }

  public Class getColumnClass(int columnIndex)
  {
    return columns.get(columnIndex).columnClass;
  }

  public Color getColumnBackground(int columnIndex)
  {
    return columns.get(columnIndex).background;
  }

  public String getColumnGroupName(int columnIndex)
  {
    return columns.get(columnIndex).columnGroupName;
  }

  public int getColumnCount()
  {
    return columns.size();
  }

  public int getRowCount()
  {
    return nodeFilter.getMineNodes().size();
  }

  public String getColumnId(int columnIndex)
  {
    return columns.get(columnIndex).id;
  }

  public Object getValueAt(
    int rowIndex,
    int columnIndex)
  {
    String id;

    id = getColumnId(columnIndex);

    if (id.equals("orgNode"))
    {
      return getNodeIcon(rowIndex);
    }

    if (id.equals("orgState"))
    {
      return getOriginalStateIcon(rowIndex);
    }

    if (id.equals("orgName"))
    {
      return getOriginalNode(rowIndex).getName();
    }

    if (id.equals("orgSize"))
    {
      return getOriginalNode(rowIndex).getSize();
    }

    if (id.equals("mineState"))
    {
      return getMineStateIcon(rowIndex);
    }

    if (id.equals("mineSize"))
    {
      return getMineNode(rowIndex).getSize();
    }

    if (id.equals("copyToRight"))
    {
      return ImageUtil.getSmallImageIcon("stock_copyToRight");
    }

    if (id.equals("copyToLeft"))
    {
      return ImageUtil.getSmallImageIcon("stock_copyToLeft");
    }

    return null;
  }

  private ImageIcon getNodeIcon(int rowIndex)
  {
    JMeldNode node;
    String    iconName;

    node = getOriginalNode(rowIndex);
    iconName = node.isLeaf() ? "stock_leaf"
                             : node.isCollapsed() ? "stock_folder_closed"
                                                  : "stock_folder_open";

    return ImageUtil.getSmallImageIcon(iconName);
  }

  private ImageIcon getOriginalStateIcon(int rowIndex)
  {
    JMeldNode node;
    String    iconName;

    node = getOriginalNode(rowIndex);
    return getStateIcon(node.getState());
  }

  private ImageIcon getMineStateIcon(int rowIndex)
  {
    JMeldNode node;
    String    iconName;

    node = getMineNode(rowIndex);
    return getStateIcon(node.getState());
  }

  private ImageIcon getStateIcon(char state)
  {
    String iconName;

    switch (state)
    {

      default:
      case JMeldNode.EQUAL:
        iconName = "stock_empty";
        break;

      case JMeldNode.ADDED:
        iconName = "stock_added2";
        break;

      case JMeldNode.CHANGED:
        iconName = "stock_changed2";
        break;

      case JMeldNode.DELETED:
        iconName = "stock_deleted2";
        break;
    }

    return ImageUtil.getSmallImageIcon(iconName);
  }

  public JMeldNode getOriginalNode(int rowIndex)
  {
    return nodeFilter.getOriginalNodes().get(rowIndex);
  }

  public JMeldNode getMineNode(int rowIndex)
  {
    return nodeFilter.getMineNodes().get(rowIndex);
  }

  class NodeFilter
  {
    List<JMeldNode> filteredOriginalNodes;
    List<JMeldNode> filteredMineNodes;

    NodeFilter()
    {
      filter();
    }

    void filter()
    {
      List<JMeldNode> originalNodes;
      List<JMeldNode> mineNodes;
      JMeldNode       originalNode;
      int             size;
      String          collapsedNodeName;
      String          originalNodeName;

      originalNodes = diff.getOriginalNodes();
      mineNodes = diff.getMineNodes();
      size = originalNodes.size();

      filteredOriginalNodes = new ArrayList<JMeldNode>(size);
      filteredMineNodes = new ArrayList<JMeldNode>(size);

      collapsedNodeName = null;
      for (int i = 0; i < size; i++)
      {
        originalNode = originalNodes.get(i);
        originalNodeName = originalNode.getName();

        if (collapsedNodeName != null
          && originalNodeName.startsWith(collapsedNodeName))
        {
          continue;
        }

        if (originalNode.isCollapsed())
        {
          collapsedNodeName = originalNodeName;
        }
        else
        {
          collapsedNodeName = null;
        }

        filteredOriginalNodes.add(originalNode);
        filteredMineNodes.add(mineNodes.get(i));
      }
    }

    List<JMeldNode> getOriginalNodes()
    {
      return filteredOriginalNodes;
    }

    List<JMeldNode> getMineNodes()
    {
      return filteredMineNodes;
    }
  }

  MouseListener getMouseListener()
  {
    return new MouseAdapter()
      {
        public void mouseClicked(MouseEvent me)
        {
          String    id;
          int       rowIndex;
          int       columnIndex;
          JMeldNode node;

          if (me.getClickCount() == 1)
          {
            rowIndex = ((JTable) me.getSource()).rowAtPoint(me.getPoint());
            columnIndex = ((JTable) me.getSource()).columnAtPoint(
                me.getPoint());
            id = getColumnId(columnIndex);
            if (id.equals("orgNode"))
            {
              node = getOriginalNode(rowIndex);
              if (!node.isLeaf())
              {
                node.setCollapsed(!node.isCollapsed());

                nodeFilter.filter();
                fireTableDataChanged();
              }
            }
          }
        }
      };
  }

  public class Column
  {
    String id;
    String columnGroupName;
    String columnName;
    Class  columnClass;
    int    columnSize;
    Color  background;

    public Column(
      String id,
      String columnGroupName,
      String columnName,
      Class  columnClass,
      int    columnSize,
      Color  background)
    {
      this.id = id;
      this.columnGroupName = columnGroupName;
      this.columnName = columnName;
      this.columnClass = columnClass;
      this.columnSize = columnSize;
      this.background = background;
    }
  }
}
