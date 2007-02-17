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

import org.jdesktop.swingx.treetable.*;
import org.jmeld.ui.action.*;
import org.jmeld.ui.swing.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.file.*;
import org.jmeld.util.node.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import java.awt.*;
import java.awt.event.*;

public class FolderDiffPanel2
       extends FolderDiffForm
{
  private JMeldPanel    mainPanel;
  private FolderDiff2   diff;
  private ActionHandler actionHandler;

  FolderDiffPanel2(
    JMeldPanel  mainPanel,
    FolderDiff2 diff)
  {
    this.mainPanel = mainPanel;
    this.diff = diff;

    init();
  }

  private void init()
  {
    folder1Label.init();
    folder1Label.setText(
      diff.getLeftFolderName(),
      diff.getRightFolderName());

    folder2Label.init();
    folder2Label.setText(
      diff.getRightFolderName(),
      diff.getLeftFolderName());

    folderTreeTable.setTreeTableModel(getModel());
    folderTreeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    folderTreeTable.setToggleClickCount(1);
    folderTreeTable.setTerminateEditOnFocusLost(false);

    //folderTreeTable.setRowSelectionAllowed(true);
    //folderTreeTable.setColumnSelectionAllowed(false);
    //folderTreeTable.setCellSelectionEnabled(false);
    //folderTreeTable.setCellSelectionEnabled(false);
    //folderTreeTable.putClientProperty("JTree.lineStyle", "Angled");

    initActions();
  }

  private void initActions()
  {
    MeldAction action;

    actionHandler = new ActionHandler();

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
  }

  private void installKey(
    String     key,
    MeldAction action)
  {
    InputMap  inputMap;
    ActionMap actionMap;
    KeyStroke stroke;

    stroke = KeyStroke.getKeyStroke(key);

    inputMap = folderTreeTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    if (inputMap.get(stroke) != action.getName())
    {
      inputMap.put(
        stroke,
        action.getName());
    }

    actionMap = folderTreeTable.getActionMap();
    if (actionMap.get(action.getName()) != action)
    {
      actionMap.put(
        action.getName(),
        action);
    }
  }

  public String getTitle()
  {
    return diff.getLeftFolderShortName() + " - "
    + diff.getRightFolderShortName();
  }

  public FolderDiffTreeTableModel getModel()
  {
    return new FolderDiffTreeTableModel(diff);
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
    int        row;
    TreePath   path;
    JMDiffNode node;

    row = folderTreeTable.getSelectedRow();
    if (row == -1)
    {
      return;
    }

    path = folderTreeTable.getPathForRow(row);
    if (path == null)
    {
      return;
    }

    node = (JMDiffNode) path.getLastPathComponent();
    if (node == null)
    {
      return;
    }

    mainPanel.openFileComparison(node, background);
  }
}
