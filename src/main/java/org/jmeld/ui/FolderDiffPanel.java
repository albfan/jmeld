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

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.jmeld.JMeldException;
import org.jmeld.settings.EditorSettings;
import org.jmeld.settings.FolderSettings;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.action.ActionHandler;
import org.jmeld.ui.action.MeldAction;
import org.jmeld.ui.swing.table.JMTreeTableModel;
import org.jmeld.ui.util.Colors;
import org.jmeld.ui.util.ImageUtil;
import org.jmeld.ui.util.SwingUtil;
import org.jmeld.util.conf.ConfigurationListenerIF;
import org.jmeld.util.file.FolderDiff;
import org.jmeld.util.file.cmd.AbstractCmd;
import org.jmeld.util.node.JMDiffNode;

import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.undo.CompoundEdit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;

public class FolderDiffPanel extends FolderDiffForm implements ConfigurationListenerIF {
    protected JMeldPanel mainPanel;
    protected FolderDiff diff;
    protected ActionHandler actionHandler;
    protected JMTreeTableModel treeTableModel;
    protected FolderDiffMouseAdapter folderDiffMouseAdapter;

    FolderDiffPanel(JMeldPanel mainPanel, FolderDiff diff) {
        this.mainPanel = mainPanel;
        this.diff = diff;

        init();
    }

    protected void init() {
        actionHandler = new ActionHandler();

        hierarchyComboBox.setModel(new DefaultComboBoxModel(
                FolderSettings.FolderView.values()));
        hierarchyComboBox.setSelectedItem(getFolderSettings().getView());
        hierarchyComboBox.setFocusable(false);

        initActions();

        onlyRightButton.setText(null);
        onlyRightButton.setIcon(ImageUtil.getImageIcon("jmeld_only-right"));
        onlyRightButton.setFocusable(false);
        onlyRightButton.setSelected(getFolderSettings().getOnlyRight());

        leftRightChangedButton.setText(null);
        leftRightChangedButton.setIcon(ImageUtil
                .getImageIcon("jmeld_left-right-changed"));
        leftRightChangedButton.setFocusable(false);
        leftRightChangedButton.setSelected(getFolderSettings()
                .getLeftRightChanged());

        onlyLeftButton.setText(null);
        onlyLeftButton.setIcon(ImageUtil.getImageIcon("jmeld_only-left"));
        onlyLeftButton.setFocusable(false);
        onlyLeftButton.setSelected(getFolderSettings().getOnlyLeft());

        leftRightUnChangedButton.setText(null);
        leftRightUnChangedButton.setIcon(ImageUtil
                .getImageIcon("jmeld_left-right-unchanged"));
        leftRightUnChangedButton.setFocusable(false);
        leftRightUnChangedButton.setSelected(getFolderSettings()
                .getLeftRightUnChanged());

        expandAllButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        expandAllButton.setContentAreaFilled(false);
        expandAllButton.setText(null);
        expandAllButton.setIcon(ImageUtil.getSmallImageIcon("stock_expand-all"));
        expandAllButton.setPressedIcon(ImageUtil
                .createDarkerIcon((ImageIcon) expandAllButton.getIcon()));
        expandAllButton.setFocusable(false);

        collapseAllButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        collapseAllButton.setContentAreaFilled(false);
        collapseAllButton.setText(null);
        collapseAllButton
                .setIcon(ImageUtil.getSmallImageIcon("stock_collapse-all"));
        collapseAllButton.setPressedIcon(ImageUtil
                .createDarkerIcon((ImageIcon) collapseAllButton.getIcon()));
        collapseAllButton.setFocusable(false);

        folder1Label.init();
        folder1Label.setText(diff.getLeftFolderName(), diff.getRightFolderName());

        folder2Label.init();
        folder2Label.setText(diff.getRightFolderName(), diff.getLeftFolderName());

        folderTreeTable.setTreeTableModel(getTreeTableModel());

        folderTreeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        folderTreeTable.setToggleClickCount(1);
        folderTreeTable.setTerminateEditOnFocusLost(false);
        folderTreeTable.setRowSelectionAllowed(true);
        folderDiffMouseAdapter = new FolderDiffMouseAdapter(this);
        folderTreeTable.addMouseListener(folderDiffMouseAdapter);
        folderTreeTable.expandAll();

        folderTreeTable.addHighlighter(new ColorHighlighter(
                HighlightPredicate.EVEN, Color.white, Color.black));
        folderTreeTable.addHighlighter(new ColorHighlighter(HighlightPredicate.ODD,
                Colors.getTableRowHighLighterColor(), Color.black));

        JMeldSettings.getInstance().addConfigurationListener(this);
    }

    protected void initActions() {
        MeldAction action;

        action = actionHandler.createAction(this, mainPanel.actions.FOLDER_SELECT_NEXT_ROW);
        installKey("DOWN", action);

        action = actionHandler.createAction(this, mainPanel.actions.FOLDER_SELECT_PREVIOUS_ROW);
        installKey("UP", action);

        action = actionHandler.createAction(this, mainPanel.actions.FOLDER_NEXT_NODE);
        installKey("RIGHT", action);

        action = actionHandler.createAction(this, mainPanel.actions.FOLDER_PREVIOUS_NODE);
        installKey("LEFT", action);

        action = actionHandler.createAction(this, mainPanel.actions.FOLDER_OPEN_FILE_COMPARISON);
        action.setIcon("stock_compare");
        compareButton.setAction(action);
        compareButton.setText(null);
        compareButton.setFocusable(false);
        compareButton.setDisabledIcon(action.getTransparentSmallImageIcon());
        installKey("ENTER", action);

        action = actionHandler.createAction(this, mainPanel.actions.FOLDER_OPEN_FILE_COMPARISON_BACKGROUND);
        action.setIcon("stock_compare");
        installKey("alt ENTER", action);

        action = actionHandler.createAction(this, mainPanel.actions.FOLDER_EXPAND_ALL);
        expandAllButton.setAction(action);

        action = actionHandler.createAction(this, mainPanel.actions.FOLDER_COLLAPSE_ALL);
        collapseAllButton.setAction(action);

        action = actionHandler.createAction(this, mainPanel.actions.FOLDER_REFRESH);
        action.setIcon("stock_refresh");
        refreshButton.setAction(action);
        refreshButton.setText(null);
        refreshButton.setFocusable(false);
        refreshButton.setDisabledIcon(action.getTransparentSmallImageIcon());

        action = actionHandler.createAction(this, mainPanel.actions.FOLDER_REMOVE_RIGHT);
        action.setIcon("stock_delete");
        deleteRightButton.setAction(action);
        deleteRightButton.setText(null);
        deleteRightButton.setFocusable(false);
        deleteRightButton.setDisabledIcon(action.getTransparentSmallImageIcon());
        installKey("ctrl alt RIGHT", action);
        installKey("ctrl alt KP_RIGHT", action);

        action = actionHandler.createAction(this, mainPanel.actions.FOLDER_REMOVE_LEFT);
        action.setIcon("stock_delete");
        deleteLeftButton.setAction(action);
        deleteLeftButton.setText(null);
        deleteLeftButton.setFocusable(false);
        deleteLeftButton.setDisabledIcon(action.getTransparentSmallImageIcon());
        installKey("ctrl alt LEFT", action);
        installKey("ctrl alt KP_LEFT", action);

        action = actionHandler.createAction(this, mainPanel.actions.FOLDER_COPY_TO_LEFT);
        action.setIcon("stock_left");
        copyToLeftButton.setAction(action);
        copyToLeftButton.setText(null);
        copyToLeftButton.setFocusable(false);
        copyToLeftButton.setDisabledIcon(action.getTransparentSmallImageIcon());
        installKey("alt LEFT", action);
        installKey("alt KP_LEFT", action);

        action = actionHandler.createAction(this, mainPanel.actions.FOLDER_COPY_TO_RIGHT);
        action.setIcon("stock_right");
        copyToRightButton.setAction(action);
        copyToRightButton.setText(null);
        copyToRightButton.setFocusable(false);
        copyToRightButton.setDisabledIcon(action.getTransparentSmallImageIcon());
        installKey("alt RIGHT", action);
        installKey("alt KP_RIGHT", action);

        action = actionHandler.createAction(this, mainPanel.actions.FOLDER_FILTER);
        onlyRightButton.setAction(action);
        leftRightChangedButton.setAction(action);
        onlyLeftButton.setAction(action);
        leftRightUnChangedButton.setAction(action);
        hierarchyComboBox.setAction(action);
    }

    private void installKey(String key, MeldAction action) {
        SwingUtil.installKey(folderTreeTable, key, action);
    }

    public String getTitle() {
        return diff.getLeftFolderShortName() + " - "
                + diff.getRightFolderShortName();
    }

    protected TreeTableNode getRootNode() {
        return filter(diff.getRootNode());
    }

    private TreeTableNode filter(JMDiffNode diffNode) {
        List<JMDiffNode> nodes;
        UINode uiParentNode;
        UINode uiNode;
        UINode rootNode;
        JMDiffNode parent;
        Object hierarchy;

        // Filter the nodes:
        nodes = new ArrayList();
        for (JMDiffNode node : diff.getNodes()) {
            if (!node.isLeaf()) {
                continue;
            }

            if (node.isCompareEqual(JMDiffNode.Compare.Equal)) {
                if (leftRightUnChangedButton.isSelected()) {
                    nodes.add(node);
                }
            } else if (node.isCompareEqual(JMDiffNode.Compare.NotEqual)) {
                if (leftRightChangedButton.isSelected()) {
                    nodes.add(node);
                }
            } else if (node.isCompareEqual(JMDiffNode.Compare.RightMissing)) {
                if (onlyLeftButton.isSelected()) {
                    nodes.add(node);
                }
            } else if (node.isCompareEqual(JMDiffNode.Compare.LeftMissing)) {
                if (onlyRightButton.isSelected()) {
                    nodes.add(node);
                }
            }
        }

        rootNode = new UINode(getTreeTableModel().getColumnCount(), "<root>", false);
        hierarchy = hierarchyComboBox.getSelectedItem();

        // Build the hierarchy:
        if (hierarchy == FolderSettings.FolderView.packageView) {
            for (JMDiffNode node : nodes) {
                parent = node.getParent();
                uiNode = new UINode(getTreeTableModel().getColumnCount(), node);

                if (parent != null) {
                    String parentName = parent.getName();
                    uiParentNode = new UINode(getTreeTableModel().getColumnCount(), parent);
                    uiParentNode = rootNode.addChild(uiParentNode);
                    uiParentNode.addChild(uiNode);
                } else {
                    rootNode.addChild(uiNode);
                }
            }
        } else if (hierarchy == FolderSettings.FolderView.fileView) {
            for (JMDiffNode node : nodes) {
                rootNode.addChild(new UINode(getTreeTableModel().getColumnCount(), node));
            }
        } else if (hierarchy == FolderSettings.FolderView.directoryView) {
            for (JMDiffNode node : nodes) {
                addDirectoryViewNode(rootNode, node);
            }
        }

        return rootNode;
    }

    private void addDirectoryViewNode(UINode rootNode, JMDiffNode node) {
        UINode parent;
        JMDiffNode uiNode;
        List<JMDiffNode> uiNodes;

        uiNodes = new ArrayList<JMDiffNode>();
        do {
            uiNodes.add(node);
        }
        while ((node = node.getParent()) != null);

        Collections.reverse(uiNodes);

        parent = rootNode;
        for (int i = 1; i < uiNodes.size(); i++) {
            uiNode = uiNodes.get(i);
            parent = parent.addChild(new UINode(getTreeTableModel().getColumnCount(), uiNode));
        }
    }

    protected void openFileOnRow(int row, boolean background, boolean openInNewTab) {
        TreePath path = folderTreeTable.getPathForRow(row);
        if (path == null) {
            return;
        }

        UINode uiNode = (UINode) path.getLastPathComponent();
        if (uiNode == null) {
            return;
        }

        List<JMDiffNode> diffNodeList = new CollectDiffNodeLeaf(uiNode).getResult();
        if (diffNodeList.isEmpty()) {
            return;
        }

        openDiffNodeList(row, diffNodeList, background, openInNewTab);
    }

    /**
     * Open a list of diffs
     * @param row
     * @param diffNodeList
     * @param openInNewTab open on same tab or new tab
     * @param background don't focus new tab
     */
    private void openDiffNodeList(int row, List<JMDiffNode> diffNodeList, boolean background, boolean openInNewTab) {
        if (openInNewTab) {

            int openCount = 0;
            for (JMDiffNode diffNode : diffNodeList) {
                if (openCount++ > 20) {
                    break;
                }

                FileComparison fileComparison = new FileComparison(mainPanel, diffNode);
                fileComparison.setOpenInBackground(background);
                fileComparison.execute();
            }

            // Hack to make it possible to select with the MIDDLE
            //   button of a mouse.
            if (folderTreeTable.getSelectedRow() != row) {
                folderTreeTable.setRowSelectionInterval(row, row);
            }

            // Make sure that UP and DOWN keys work the way I want.
            folderTreeTable.requestFocus();
        } else {
            try {
                openInContext(diffNodeList.get(0));
            } catch (JMeldException e) {
                e.printStackTrace();
            }
        }
    }

    public void doSelectPreviousRow(ActionEvent ae) {
        int row;

        row = folderTreeTable.getSelectedRow() - 1;
        row = row < 0 ? (folderTreeTable.getRowCount() - 1) : row;
        folderTreeTable.setRowSelectionInterval(row, row);
        folderTreeTable.scrollRowToVisible(row);
    }

    public void doSelectNextRow(ActionEvent ae) {
        int row;

        row = folderTreeTable.getSelectedRow() + 1;
        row = row >= folderTreeTable.getRowCount() ? 0 : row;
        folderTreeTable.setRowSelectionInterval(row, row);
        folderTreeTable.scrollRowToVisible(row);
    }

    public void doNextNode(ActionEvent ae) {
        int row;

        row = folderTreeTable.getSelectedRow();
        if (row == -1) {
            return;
        }

        if (folderTreeTable.isCollapsed(row)) {
            folderTreeTable.expandRow(row);
        }

        doSelectNextRow(ae);
    }

    public void doPreviousNode(ActionEvent ae) {
        int row;

        row = folderTreeTable.getSelectedRow();
        if (row == -1) {
            return;
        }

        if (folderTreeTable.isExpanded(row)) {
            folderTreeTable.collapseRow(row);
        }

        doSelectPreviousRow(ae);
    }

    public void doOpenFileComparisonBackground(ActionEvent ae) {
        doOpenFileComparison(ae, true);
    }

    public void doOpenFileComparison(ActionEvent ae) {
        doOpenFileComparison(ae, false);
    }

    private void doOpenFileComparison(ActionEvent ae, boolean background) {
        for (UINode uiNode : getSelectedUINodes()) {
            FileComparison fileComparison = new FileComparison(mainPanel, uiNode.getDiffNode());
            fileComparison.setOpenInBackground(background);
            fileComparison.execute();
        }
    }

    @Override
    public boolean checkExit() {
        return false;
    }

    public void doExpandAll(ActionEvent ae) {
        folderTreeTable.expandAll();
    }

    public void doCollapseAll(ActionEvent ae) {
        folderTreeTable.collapseAll();
    }

    public boolean isCopyToLeftEnabled() {
        return !getEditorSettings().getLeftsideReadonly();
    }

    public void doCopyToLeft(ActionEvent ae) {
        CompoundCommand cc;

        cc = new CompoundCommand();
        for (UINode uiNode : getSelectedUINodes()) {
            try {
                cc.add(uiNode, uiNode.getDiffNode().getCopyToLeftCmd());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        cc.execute();
    }

    public boolean isCopyToRightEnabled() {
        return !getEditorSettings().getRightsideReadonly();
    }

    public void doCopyToRight(ActionEvent ae) {
        CompoundCommand cc;

        cc = new CompoundCommand();
        for (UINode uiNode : getSelectedUINodes()) {
            try {
                cc.add(uiNode, uiNode.getDiffNode().getCopyToRightCmd());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        cc.execute();
    }

    class CompoundCommand
            extends CompoundEdit {
        List<AbstractCmd> cmds;
        Map<AbstractCmd, UINode> uiNodeMap;

        CompoundCommand() {
            uiNodeMap = new HashMap<AbstractCmd, UINode>();
            cmds = new ArrayList<AbstractCmd>();
        }

        void add(UINode uiNode, AbstractCmd cmd) {
            if (cmd == null) {
                return;
            }

            uiNodeMap.put(cmd, uiNode);
            cmds.add(cmd);
        }

        void execute() {
            try {
                for (AbstractCmd cmd : cmds) {
                    cmd.execute();
                    addEdit(cmd);
                }
                end();

                getUndoHandler().add(this);
                compareContents();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            check();
        }

        @Override
        public void redo() {
            super.redo();
            compareContents();
            check();
        }

        @Override
        public void undo() {
            super.undo();
            compareContents();
            check();
        }

        private void check() {
            mainPanel.checkActions();
            repaint();
        }

        private void compareContents() {
            for (AbstractCmd cmd : cmds) {
                uiNodeMap.get(cmd).getDiffNode().compareContents();
            }
        }
    }

    public void doRefresh(ActionEvent ae) {
        new RefreshAction().execute();
    }

    class RefreshAction
            extends SwingWorker<String, Object> {
        RefreshAction() {
        }

        @Override
        public String doInBackground() {
            diff.refresh();

            return null;
        }

        @Override
        protected void done() {
            treeTableModel = null;
            folderTreeTable.setTreeTableModel(getTreeTableModel());
            folderTreeTable.expandAll();
        }
    }

    public boolean isRemoveRightEnabled() {
        return !getEditorSettings().getRightsideReadonly();
    }

    public void doRemoveRight(ActionEvent ae) {
        CompoundCommand cc;

        cc = new CompoundCommand();
        for (UINode uiNode : getSelectedUINodes()) {
            try {
                cc.add(uiNode, uiNode.getDiffNode().getRemoveRightCmd());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        cc.execute();
    }

    public boolean isRemoveLeftEnabled() {
        return !getEditorSettings().getLeftsideReadonly();
    }

    public void doRemoveLeft(ActionEvent ae) {
        CompoundCommand cc;

        cc = new CompoundCommand();
        for (UINode uiNode : getSelectedUINodes()) {
            try {
                cc.add(uiNode, uiNode.getDiffNode().getRemoveLeftCmd());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        cc.execute();
    }

    public void doFilter(ActionEvent ae) {
        ((JMTreeTableModel) folderTreeTable.getTreeTableModel()).setRoot(getRootNode());
        folderTreeTable.expandAll();
    }

    private EditorSettings getEditorSettings() {
        return JMeldSettings.getInstance().getEditor();
    }

    private FolderSettings getFolderSettings() {
        return JMeldSettings.getInstance().getFolder();
    }

    private Set<UINode> getSelectedUINodes() {
        Set<UINode> result;
        TreePath path;
        UINode uiNode;

        result = new HashSet<UINode>();
        for (int row : folderTreeTable.getSelectedRows()) {
            path = folderTreeTable.getPathForRow(row);
            if (path == null) {
                continue;
            }

            uiNode = (UINode) path.getLastPathComponent();
            if (uiNode == null) {
                continue;
            }

            buildResult(result, uiNode);
        }

        return result;
    }

    private void buildResult(Set<UINode> result, UINode uiNode) {
        if (uiNode.isLeaf() && uiNode.getDiffNode() != null) {
            result.add(uiNode);
            return;
        }

        for (UINode node : uiNode.getChildren()) {
            buildResult(result, node);
        }
    }

    protected void openInContext(JMDiffNode diffNode) throws JMeldException { }

    protected JMTreeTableModel getTreeTableModel() {
        if (treeTableModel == null) {
            treeTableModel = createTreeTableModel();
            treeTableModel.setRoot(getRootNode());
        }

        return treeTableModel;
    }

    protected JMTreeTableModel createTreeTableModel() {
        return new FolderDiffTreeTableModel();
    }

    public void configurationChanged() {
        actionHandler.checkActions();
    }
}
