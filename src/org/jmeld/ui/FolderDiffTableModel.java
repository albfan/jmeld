package org.jmeld.ui;

import org.jmeld.diff.*;
import org.jmeld.ui.text.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.file.*;
import org.jmeld.util.scan.*;

import javax.swing.*;
import javax.swing.table.*;

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
    columns.add(new Column("orgNode", "Node", Icon.class, 40));
    columns.add(new Column("orgState", "", Icon.class, 25));
    columns.add(new Column("orgName", "Name", String.class, -1));
    columns.add(new Column("orgSize", "Size", Integer.class, 100));
    columns.add(new Column("mineState", "", Icon.class, 25));
    columns.add(new Column("mineSize", "Size", Integer.class, 100));

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

  public Object getValueAt(int rowIndex, int columnIndex)
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
        iconName = "stock_added";
        break;

      case JMeldNode.CHANGED:
        iconName = "stock_changed";
        break;

      case JMeldNode.DELETED:
        iconName = "stock_deleted";
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
            columnIndex = ((JTable) me.getSource()).columnAtPoint(me.getPoint());
            id = getColumnId(columnIndex);
            if (id.equals("orgNode"))
            {
              node = getOriginalNode(rowIndex);
              if(!node.isLeaf())
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
    String columnName;
    Class  columnClass;
    int    columnSize;

    public Column(String id, String columnName, Class columnClass,
      int columnSize)
    {
      this.id = id;
      this.columnName = columnName;
      this.columnClass = columnClass;
      this.columnSize = columnSize;
    }
  }
}
