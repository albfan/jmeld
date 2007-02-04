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
import org.jmeld.ui.swing.table.*;
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
       extends JMTableModel
{
  private FolderDiff diff;
  private NodeFilter nodeFilter;
  private Column     leftNodeColumn;
  private Column     leftNameColumn;
  private Column     leftSizeColumn;
  private Column     leftStateColumn;
  private Column     rightStateColumn;
  private Column     rightSizeColumn;

  public FolderDiffTableModel(FolderDiff diff)
  {
    this.diff = diff;

    leftNodeColumn = addColumn("leftNode", null, "Node", Icon.class, 4, false,
        null);
    leftNameColumn = addColumn("leftName", null, "Name", String.class, -1,
        false, null);
    leftSizeColumn = addColumn("leftSize", "Left", "Size", Integer.class, 8,
        false, Colors.TABLEROW_LEFT);
    leftStateColumn = addColumn("leftState", "Left", "", Icon.class, 3, false,
        Colors.TABLEROW_LEFT);
    rightStateColumn = addColumn("rightState", "Right", "", Icon.class, 3,
        false, Colors.TABLEROW_RIGHT);
    rightSizeColumn = addColumn("rightSize", "Right", "Size", Integer.class,
        8, false, Colors.TABLEROW_RIGHT);

    nodeFilter = new NodeFilter();
  }

  public int getRowCount()
  {
    return nodeFilter.getRightNodes().size();
  }

  public Object getValueAt(
    int    rowIndex,
    Column column)
  {
    if (column == leftNodeColumn)
    {
      return getNodeIcon(rowIndex);
    }

    if (column == leftStateColumn)
    {
      return getLeftStateIcon(rowIndex);
    }

    if (column == leftNameColumn)
    {
      return getLeftNode(rowIndex).getName();
    }

    if (column == leftSizeColumn)
    {
      return getLeftNode(rowIndex).getSize();
    }

    if (column == rightStateColumn)
    {
      return getRightStateIcon(rowIndex);
    }

    if (column == rightSizeColumn)
    {
      return getRightNode(rowIndex).getSize();
    }

    return null;
  }

  private ImageIcon getNodeIcon(int rowIndex)
  {
    JMeldNode node;
    String    iconName;

    node = getLeftNode(rowIndex);
    iconName = node.isLeaf() ? "stock_leaf"
                             : node.isCollapsed() ? "stock_folder_closed"
                                                  : "stock_folder_open";

    return ImageUtil.getSmallImageIcon(iconName);
  }

  private ImageIcon getLeftStateIcon(int rowIndex)
  {
    JMeldNode node;
    String    iconName;

    node = getLeftNode(rowIndex);
    return getStateIcon(node.getState());
  }

  private ImageIcon getRightStateIcon(int rowIndex)
  {
    JMeldNode node;
    String    iconName;

    node = getRightNode(rowIndex);
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
        iconName = "stock_equal";
        iconName = "stock_changed2";
        break;

      case JMeldNode.CHANGED:
        iconName = "stock_changed2";
        break;

      case JMeldNode.DELETED:
        iconName = "stock_deleted2";
        iconName = "stock_deleted3";
        break;
    }

    return ImageUtil.getSmallImageIcon(iconName);
  }

  public JMeldNode getLeftNode(int rowIndex)
  {
    return nodeFilter.getLeftNodes().get(rowIndex);
  }

  public JMeldNode getRightNode(int rowIndex)
  {
    return nodeFilter.getRightNodes().get(rowIndex);
  }

  class NodeFilter
  {
    List<JMeldNode> filteredLeftNodes;
    List<JMeldNode> filteredRightNodes;

    NodeFilter()
    {
      filter();
    }

    void filter()
    {
      List<JMeldNode> leftNodes;
      List<JMeldNode> rightNodes;
      JMeldNode       leftNode;
      int             size;
      String          collapsedNodeName;
      String          leftNodeName;

      leftNodes = diff.getLeftNodes();
      rightNodes = diff.getRightNodes();
      size = leftNodes.size();

      filteredLeftNodes = new ArrayList<JMeldNode>(size);
      filteredRightNodes = new ArrayList<JMeldNode>(size);

      collapsedNodeName = null;
      for (int i = 0; i < size; i++)
      {
        leftNode = leftNodes.get(i);
        leftNodeName = leftNode.getName();

        if (collapsedNodeName != null
          && leftNodeName.startsWith(collapsedNodeName))
        {
          continue;
        }

        if (leftNode.isCollapsed())
        {
          collapsedNodeName = leftNodeName;
        }
        else
        {
          collapsedNodeName = null;
        }

        filteredLeftNodes.add(leftNode);
        filteredRightNodes.add(rightNodes.get(i));
      }
    }

    List<JMeldNode> getLeftNodes()
    {
      return filteredLeftNodes;
    }

    List<JMeldNode> getRightNodes()
    {
      return filteredRightNodes;
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
            if (id.equals("leftNode"))
            {
              node = getLeftNode(rowIndex);
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
}
