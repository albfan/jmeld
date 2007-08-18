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

import org.jdesktop.swingx.decorator.*;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.treetable.*;
import org.jmeld.settings.*;
import org.jmeld.ui.action.*;
import org.jmeld.ui.swing.*;
import org.jmeld.ui.swing.table.*;
import org.jmeld.ui.util.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.file.*;
import org.jmeld.util.node.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class FolderDiffPanel
       extends FolderDiffForm
{
  private JMeldPanel    mainPanel;
  private FolderDiff    diff;
  private ActionHandler actionHandler;

  FolderDiffPanel(
    JMeldPanel mainPanel,
    FolderDiff diff)
  {
    this.mainPanel = mainPanel;
    this.diff = diff;

    init();
  }

  private void init()
  {
    actionHandler = new ActionHandler();

    hierarchyComboBox.setModel(
      new DefaultComboBoxModel(DirectorySettings.DirectoryView.values()));
    hierarchyComboBox.setSelectedItem(getSettings().getView());
    hierarchyComboBox.setFocusable(false);

    initActions();

    //deleteLeftButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    //deleteLeftButton.setContentAreaFilled(false);
    deleteLeftButton.setText(null);
    deleteLeftButton.setIcon(ImageUtil.getSmallImageIcon("stock_delete"));
    deleteLeftButton.setFocusable(false);

    //copyToRightButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    //copyToRightButton.setContentAreaFilled(false);
    copyToRightButton.setText(null);
    copyToRightButton.setIcon(ImageUtil.getSmallImageIcon("stock_right"));
    copyToRightButton.setFocusable(false);

    //copyToLeftButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    //copyToLeftButton.setContentAreaFilled(false);
    copyToLeftButton.setText(null);
    copyToLeftButton.setIcon(ImageUtil.getSmallImageIcon("stock_left"));
    copyToLeftButton.setFocusable(false);

    //deleteRightButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    //deleteRightButton.setContentAreaFilled(false);
    deleteRightButton.setText(null);
    deleteRightButton.setIcon(ImageUtil.getSmallImageIcon("stock_delete"));
    deleteRightButton.setFocusable(false);
		
    onlyRightButton.setText(null);
    onlyRightButton.setIcon(ImageUtil.getImageIcon("jmeld_only-right"));
    onlyRightButton.setFocusable(false);
    onlyRightButton.setSelected(getSettings().getOnlyRight());

    leftRightChangedButton.setText(null);
    leftRightChangedButton.setIcon(
      ImageUtil.getImageIcon("jmeld_left-right-changed"));
    leftRightChangedButton.setFocusable(false);
    leftRightChangedButton.setSelected(getSettings().getLeftRightChanged());

    onlyLeftButton.setText(null);
    onlyLeftButton.setIcon(ImageUtil.getImageIcon("jmeld_only-left"));
    onlyLeftButton.setFocusable(false);
    onlyLeftButton.setSelected(getSettings().getOnlyLeft());

    leftRightUnChangedButton.setText(null);
    leftRightUnChangedButton.setIcon(
      ImageUtil.getImageIcon("jmeld_left-right-unchanged"));
    leftRightUnChangedButton.setFocusable(false);
    leftRightUnChangedButton.setSelected(
      getSettings().getLeftRightUnChanged());

    expandAllButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    expandAllButton.setContentAreaFilled(false);
    expandAllButton.setText(null);
    expandAllButton.setIcon(ImageUtil.getSmallImageIcon("stock_expand-all"));
    expandAllButton.setPressedIcon(
      ImageUtil.createDarkerIcon((ImageIcon) expandAllButton.getIcon()));
    expandAllButton.setFocusable(false);

    collapseAllButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    collapseAllButton.setContentAreaFilled(false);
    collapseAllButton.setText(null);
    collapseAllButton.setIcon(
      ImageUtil.getSmallImageIcon("stock_collapse-all"));
    collapseAllButton.setPressedIcon(
      ImageUtil.createDarkerIcon((ImageIcon) collapseAllButton.getIcon()));
    collapseAllButton.setFocusable(false);

    folder1Label.init();
    folder1Label.setText(
      diff.getLeftFolderName(),
      diff.getRightFolderName());

    folder2Label.init();
    folder2Label.setText(
      diff.getRightFolderName(),
      diff.getLeftFolderName());

    folderTreeTable.setTreeTableModel(
      new FolderDiffTreeTableModel(getRootNode()));
    folderTreeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    folderTreeTable.setToggleClickCount(1);
    folderTreeTable.setTerminateEditOnFocusLost(false);
    folderTreeTable.setRowSelectionAllowed(true);
    folderTreeTable.addMouseListener(getMouseListener());

    folderTreeTable.setHighlighters(
      new HighlighterPipeline(
        new Highlighter[]
        {
          new AlternateRowHighlighter(
            Color.white,
            Colors.getTableRowHighLighterColor(),
            Color.black),
        }));
  }

  private void initActions()
  {
    MeldAction action;

    action = actionHandler.createAction(this, "SelectNextRow");
    installKey("DOWN", action);

    action = actionHandler.createAction(this, "SelectPreviousRow");
    installKey("UP", action);

    action = actionHandler.createAction(this, "NextNode");
    installKey("RIGHT", action);

    action = actionHandler.createAction(this, "PreviousNode");
    installKey("LEFT", action);

    action = actionHandler.createAction(this, "OpenFileComparison");
    installKey("ENTER", action);

    action = actionHandler.createAction(this, "OpenFileComparisonBackground");
    installKey("alt ENTER", action);

    action = actionHandler.createAction(this, "ExpandAll");
    expandAllButton.setAction(action);

    action = actionHandler.createAction(this, "CollapseAll");
    collapseAllButton.setAction(action);

    action = actionHandler.createAction(this, "RemoveRight");
    deleteRightButton.setAction(action);
    installKey("ctrl alt RIGHT", action);
    installKey("ctrl alt KP_RIGHT", action);

    action = actionHandler.createAction(this, "RemoveLeft");
    deleteLeftButton.setAction(action);
    installKey("ctrl alt LEFT", action);
    installKey("ctrl alt KP_LEFT", action);

    action = actionHandler.createAction(this, "CopyRightToLeft");
    copyToLeftButton.setAction(action);
    installKey("alt LEFT", action);
    installKey("alt KP_LEFT", action);

    action = actionHandler.createAction(this, "CopyLeftToRight");
    copyToRightButton.setAction(action);
    installKey("alt RIGHT", action);
    installKey("alt KP_RIGHT", action);

    action = actionHandler.createAction(this, "Filter");
    onlyRightButton.setAction(action);
    leftRightChangedButton.setAction(action);
    onlyLeftButton.setAction(action);
    leftRightUnChangedButton.setAction(action);
    hierarchyComboBox.setAction(action);
  }

  private void installKey(
    String     key,
    MeldAction action)
  {
    SwingUtil.installKey(folderTreeTable, key, action);
  }

  public String getTitle()
  {
    return diff.getLeftFolderShortName() + " - "
    + diff.getRightFolderShortName();
  }

  private TreeNode getRootNode()
  {
    return filter(diff.getRootNode());
  }

  private TreeNode filter(JMDiffNode diffNode)
  {
    List<JMDiffNode> nodes;
    UINode           uiParentNode;
    UINode           uiNode;
    String           parentName;
    UINode           rootNode;
    JMDiffNode       parent;
    Object           hierarchy;

    // Filter the nodes:
    nodes = new ArrayList();
    for (JMDiffNode node : diff.getNodes())
    {
      if (!node.isLeaf())
      {
        continue;
      }

      if (node.isCompareEqual(JMDiffNode.Compare.Equal))
      {
        if (leftRightUnChangedButton.isSelected())
        {
          nodes.add(node);
        }
      }
      else if (node.isCompareEqual(JMDiffNode.Compare.NotEqual))
      {
        if (leftRightChangedButton.isSelected())
        {
          nodes.add(node);
        }
      }
      else if (node.isCompareEqual(JMDiffNode.Compare.RightMissing))
      {
        if (onlyLeftButton.isSelected())
        {
          nodes.add(node);
        }
      }
      else if (node.isCompareEqual(JMDiffNode.Compare.LeftMissing))
      {
        if (onlyRightButton.isSelected())
        {
          nodes.add(node);
        }
      }
    }

    rootNode = new UINode("<root>", false);
    hierarchy = hierarchyComboBox.getSelectedItem();

    // Build the hierarchy:
    if (hierarchy == DirectorySettings.DirectoryView.packageView)
    {
      for (JMDiffNode node : nodes)
      {
        parent = node.getParent();
        uiNode = new UINode(node);

        if (parent != null)
        {
          parentName = parent.getName();
          uiParentNode = new UINode(parentName, false);
          uiParentNode = rootNode.addChild(uiParentNode);
          uiParentNode.addChild(uiNode);
        }
        else
        {
          rootNode.addChild(uiNode);
        }
      }
    }
    else if (hierarchy == DirectorySettings.DirectoryView.fileView)
    {
      for (JMDiffNode node : nodes)
      {
        rootNode.addChild(new UINode(node));
      }
    }
    else if (hierarchy == DirectorySettings.DirectoryView.directoryView)
    {
      for (JMDiffNode node : nodes)
      {
        addDirectoryViewNode(rootNode, node);
      }
    }

    return rootNode;
  }

  private void addDirectoryViewNode(
    UINode     rootNode,
    JMDiffNode node)
  {
    UINode           parent;
    JMDiffNode       uiNode;
    List<JMDiffNode> uiNodes;

    uiNodes = new ArrayList<JMDiffNode>();
    do
    {
      uiNodes.add(node);
    }
    while ((node = node.getParent()) != null);

    Collections.reverse(uiNodes);

    parent = rootNode;
    for (int i = 1; i < uiNodes.size(); i++)
    {
      uiNode = uiNodes.get(i);
      parent = parent.addChild(new UINode(uiNode));
    }
  }

  public void doSelectPreviousRow(ActionEvent ae)
  {
    int row;

    row = folderTreeTable.getSelectedRow() - 1;
    row = row < 0 ? (folderTreeTable.getRowCount() - 1) : row;
    folderTreeTable.setRowSelectionInterval(row, row);
    folderTreeTable.scrollRowToVisible(row);
  }

  public void doSelectNextRow(ActionEvent ae)
  {
    int row;

    row = folderTreeTable.getSelectedRow() + 1;
    row = row >= folderTreeTable.getRowCount() ? 0 : row;
    folderTreeTable.setRowSelectionInterval(row, row);
    folderTreeTable.scrollRowToVisible(row);
  }

  public void doNextNode(ActionEvent ae)
  {
    int row;

    row = folderTreeTable.getSelectedRow();
    if (row == -1)
    {
      return;
    }

    if (folderTreeTable.isCollapsed(row))
    {
      folderTreeTable.expandRow(row);
    }

    doSelectNextRow(ae);
  }

  public void doPreviousNode(ActionEvent ae)
  {
    int row;

    row = folderTreeTable.getSelectedRow();
    if (row == -1)
    {
      return;
    }

    if (folderTreeTable.isExpanded(row))
    {
      folderTreeTable.collapseRow(row);
    }

    doSelectPreviousRow(ae);
  }

  public void doOpenFileComparisonBackground(ActionEvent ae)
  {
    doOpenFileComparison(ae, true);
  }

  public void doOpenFileComparison(ActionEvent ae)
  {
    doOpenFileComparison(ae, false);
  }

  private void doOpenFileComparison(
    ActionEvent ae,
    boolean     background)
  {
    JMDiffNode diffNode;

    diffNode = getSelectedDiffNode();
    if (diffNode == null)
    {
      return;
    }

    mainPanel.openFileComparison(diffNode, background);
  }

  @Override
  public boolean checkExit()
  {
    return false;
  }

  public void doExpandAll(ActionEvent ae)
  {
    folderTreeTable.expandAll();
  }

  public void doCollapseAll(ActionEvent ae)
  {
    folderTreeTable.collapseAll();
  }

  public void doCopyRightToLeft(ActionEvent ae)
  {
    JMDiffNode diffNode;

    diffNode = getSelectedDiffNode();
    if (diffNode == null)
    {
      return;
    }

    diffNode.copyRightToLeft();
    repaint();
  }

  public void doCopyLeftToRight(ActionEvent ae)
  {
    JMDiffNode diffNode;

    diffNode = getSelectedDiffNode();
    if (diffNode == null)
    {
      return;
    }

    diffNode.copyLeftToRight();
    repaint();
  }


  public void doRemoveRight(ActionEvent ae)
  {
    JMDiffNode diffNode;

    diffNode = getSelectedDiffNode();
    if (diffNode == null)
    {
      return;
    }

    diffNode.removeRight();
    repaint();
  }

  public void doRemoveLeft(ActionEvent ae)
  {
    JMDiffNode diffNode;

    diffNode = getSelectedDiffNode();
    if (diffNode == null)
    {
      return;
    }

    diffNode.removeLeft();
    repaint();
  }

  public void doFilter(ActionEvent ae)
  {
    ((JMTreeTableModel) folderTreeTable.getTreeTableModel()).setRoot(
      getRootNode());
  }

  private DirectorySettings getSettings()
  {
    return JMeldSettings.getInstance().getDirectory();
  }

  private JMDiffNode getSelectedDiffNode()
  {
    int      row;
    TreePath path;
    UINode   uiNode;

    row = folderTreeTable.getSelectedRow();
    if (row == -1)
    {
      return null;
    }

    path = folderTreeTable.getPathForRow(row);
    if (path == null)
    {
      return null;
    }

    uiNode = (UINode) path.getLastPathComponent();
    if (uiNode == null)
    {
      return null;
    }

    return uiNode.getDiffNode();
  }

  private MouseListener getMouseListener()
  {
    return new MouseAdapter()
      {
        public void mouseClicked(MouseEvent me)
        {
          UINode     uiNode;
          JMDiffNode diffNode;
          TreePath   path;
          int        row;
          boolean    open;
          boolean    background;

          background = me.getClickCount() == 1
            && me.getButton() == MouseEvent.BUTTON2;
          open = me.getClickCount() == 2 || background;

          if (open)
          {
            row = ((JTable) me.getSource()).rowAtPoint(me.getPoint());

            path = folderTreeTable.getPathForRow(row);
            if (path == null)
            {
              return;
            }

            uiNode = (UINode) path.getLastPathComponent();
            if (uiNode == null)
            {
              return;
            }

            diffNode = uiNode.getDiffNode();
            if (diffNode == null)
            {
              return;
            }

            mainPanel.openFileComparison(diffNode, background);

            // Hack to make it possible to select with the MIDDLE 
            //   button of a mouse. 
            if(folderTreeTable.getSelectedRow() != row)
            {
              folderTreeTable.setRowSelectionInterval(row, row);
            }

            // Make sure that UP and DOWN keys work the way I want.
            folderTreeTable.requestFocus();
          }
        }
      };
  }
}
