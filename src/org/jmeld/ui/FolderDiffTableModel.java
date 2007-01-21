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
  private Column     orgNodeColumn;
  private Column     orgNameColumn;
  private Column     orgSizeColumn;
  private Column     orgStateColumn;
  private Column     mineStateColumn;
  private Column     mineSizeColumn;

  public FolderDiffTableModel(FolderDiff diff)
  {
    this.diff = diff;

    orgNodeColumn = addColumn("orgNode", null, "Node", Icon.class, 4, false,
        null);
    orgNameColumn = addColumn("orgName", null, "Name", String.class, -1,
        false, null);
    orgSizeColumn = addColumn("orgSize", "Original", "Size", Integer.class, 8,
        false, Colors.TABLEROW_ORG);
    orgStateColumn = addColumn("orgState", "Original", "", Icon.class, 3,
        false, Colors.TABLEROW_ORG);
    mineStateColumn = addColumn("mineState", "Mine", "", Icon.class, 3, false,
        Colors.TABLEROW_MINE);
    mineSizeColumn = addColumn("mineSize", "Mine", "Size", Integer.class, 8,
        false, Colors.TABLEROW_MINE);

    nodeFilter = new NodeFilter();
  }

  public int getRowCount()
  {
    return nodeFilter.getMineNodes().size();
  }

  public Object getValueAt(
    int    rowIndex,
    Column column)
  {
    if (column == orgNodeColumn)
    {
      return getNodeIcon(rowIndex);
    }

    if (column == orgStateColumn)
    {
      return getOriginalStateIcon(rowIndex);
    }

    if (column == orgNameColumn)
    {
      return getOriginalNode(rowIndex).getName();
    }

    if (column == orgSizeColumn)
    {
      return getOriginalNode(rowIndex).getSize();
    }

    if (column == mineStateColumn)
    {
      return getMineStateIcon(rowIndex);
    }

    if (column == mineSizeColumn)
    {
      return getMineNode(rowIndex).getSize();
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
}
