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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.jmeld.JMeldException;
import org.jmeld.diff.JMChunk;
import org.jmeld.diff.JMDelta;
import org.jmeld.diff.JMDiff;
import org.jmeld.diff.JMRevision;
import org.jmeld.model.LevenshteinTableModel;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.diffbar.DiffScrollComponent;
import org.jmeld.ui.search.SearchCommand;
import org.jmeld.ui.search.SearchHit;
import org.jmeld.ui.search.SearchHits;
import org.jmeld.ui.text.AbstractBufferDocument;
import org.jmeld.ui.text.BufferDocumentIF;
import org.jmeld.ui.text.JMDocumentEvent;
import org.jmeld.ui.tree.DiffTree;
import org.jmeld.util.StringUtil;
import org.jmeld.util.conf.ConfigurationListenerIF;
import org.jmeld.util.node.BufferNode;
import org.jmeld.util.node.JMDiffNode;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BufferDiffPanel extends AbstractContentPanel implements ConfigurationListenerIF {
    public static final int LEFT = 0;
    public static final int MIDDLE = 1; //TODO: Usar el comparador del medio con dos JDiff
    public static final int RIGHT = 2;
    public static final int NUMBER_OF_PANELS = 3;
    private JMeldPanel mainPanel;
    private FilePanel[] filePanels;
    private JMDiffNode diffNode;
    int filePanelSelectedIndex = -1;
    private JMRevision currentRevision;
    private JMDelta selectedDelta;
    private int selectedLine;
    private ScrollSynchronizer scrollSynchronizer;
    private JMDiff diff;
    private JTable levensteinGraphTable;
    private DiffTree diffTree;

    private boolean showTree;
    private boolean showLevenstein;
    private JSplitPane splitPane;
    private JCheckBox checkSolutionPath;

    static Color selectionColor = Color.BLUE;
    static Color newColor = Color.CYAN;
    static Color mixColor = Color.WHITE;
    static {
        selectionColor = new Color(selectionColor.getRed() * newColor.getRed()/mixColor.getRed()
                ,selectionColor.getGreen() * newColor.getGreen()/mixColor.getGreen()
                ,selectionColor.getBlue() * newColor.getBlue()/mixColor.getBlue());
    }

    BufferDiffPanel(JMeldPanel mainPanel) {
        this.mainPanel = mainPanel;
        readConfig();
        JMeldSettings.getInstance().addConfigurationListener(this);
        diff = new JMDiff();

        init();

        setFocusable(true);
    }

    public boolean isShowTree() {
        return showTree;
    }

    public void setShowTree(boolean showTree) {
        this.showTree = showTree;
    }

    public boolean isShowLevenstein() {
        return showLevenstein;
    }

    public void setShowLevenstein(boolean showLevenstein) {
        this.showLevenstein = showLevenstein;
    }

    public void setDiffNode(JMDiffNode diffNode) {
       this.diffNode = diffNode;
       refreshDiffNode();
    }

    public JMDiffNode getDiffNode() {
        return diffNode;
    }

    private void refreshDiffNode() {
        BufferNode bnLeft = getDiffNode().getBufferNodeLeft();
        BufferNode bnRight = getDiffNode().getBufferNodeRight();

        BufferDocumentIF leftDocument = bnLeft == null ? null : bnLeft.getDocument();
        BufferDocumentIF rightDocument = bnRight == null ? null : bnRight.getDocument();

        setBufferDocuments(leftDocument, rightDocument, getDiffNode().getDiff(), getDiffNode().getRevision());
    }

    private void setBufferDocuments(BufferDocumentIF bd1, BufferDocumentIF bd2,
                                    JMDiff diff, JMRevision revision) {
        this.diff = diff;

        currentRevision = revision;

        if (bd1 != null) {
            filePanels[LEFT].setBufferDocument(bd1);
        }

        if (bd2 != null) {
            filePanels[RIGHT].setBufferDocument(bd2);
        }

        if (bd1 != null && bd2 != null) {
            filePanels[LEFT].updateFileLabel(bd1.getName(), bd2.getName());
            filePanels[RIGHT].updateFileLabel(bd2.getName(), bd1.getName());
        }

        if (bd1 != null && bd2 != null) {
            reDisplay();
        }
    }

    private void reDisplay() {
        for (FilePanel fp : filePanels) {
            if (fp != null) {
                fp.reDisplay();
            }
        }

        refreshTreeModel();
        refreshLevensteinModel();

        mainPanel.repaint();
    }

    private void refreshTreeModel() {
        if (isShowTree()) {
            diffTree.setRevision(getCurrentRevision());
        }
    }

    public String getTitle() {
        String title;
        BufferDocumentIF bd;
        List<String> titles;

        title = "";

        titles = new ArrayList<String>();
        for (FilePanel filePanel : filePanels) {
            if (filePanel == null) {
                continue;
            }

            bd = filePanel.getBufferDocument();
            if (bd == null) {
                continue;
            }

            title = bd.getShortName();

            titles.add(title);
        }

        title = "";
        if (titles.size() == 1) {
            title = titles.get(0);
        } else {
            if (titles.get(0).equals(titles.get(1))) {
                title = titles.get(0);
            } else {
                title = titles.get(0) + "-" + titles.get(1);
            }
        }

        return title;
    }

    public boolean revisionChanged(JMDocumentEvent de) {
        FilePanel fp;
        BufferDocumentIF bd1;
        BufferDocumentIF bd2;

        if (currentRevision == null) {
            diff();
        } else {
            fp = getFilePanel(de.getDocument());
            if (fp == null) {
                return false;
            }

            bd1 = filePanels[LEFT].getBufferDocument();
            bd2 = filePanels[RIGHT].getBufferDocument();

            if (!currentRevision.update(bd1 != null ? bd1.getLines() : null,
                    bd2 != null ? bd2.getLines() : null, fp == filePanels[LEFT], de
                    .getStartLine(), de.getNumberOfLines())) {
                return false;
            }

            reDisplay();
        }

        return true;
    }

    private FilePanel getFilePanel(AbstractBufferDocument document) {
        for (FilePanel fp : filePanels) {
            if (fp == null) {
                continue;
            }

            if (fp.getBufferDocument() == document) {
                return fp;
            }
        }

        return null;
    }

    public void diff() {
        BufferDocumentIF bd1;
        BufferDocumentIF bd2;

        bd1 = filePanels[LEFT].getBufferDocument();
        bd2 = filePanels[RIGHT].getBufferDocument();

        if (bd1 != null && bd2 != null) {
            try {currentRevision = diff.diff(bd1.getLines(), bd2.getLines()
                        , getDiffNode().getIgnore());

                reDisplay();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void init() {
        FormLayout layout;
        String columns;
        String rows;
        CellConstraints cc;

        columns = "3px, pref, 3px, 0:grow, 5px, min, 60px, 0:grow, 25px, min, 3px, pref, 3px";
        rows = "6px, pref, 3px, fill:0:grow, pref";

        setLayout(new BorderLayout());

        if (splitPane != null) {
            remove(splitPane);
        }
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, buildFilePanel(columns, rows), buildBottonSplit());
        add(splitPane);

        scrollSynchronizer = new ScrollSynchronizer(this, filePanels[LEFT], filePanels[RIGHT]);

        setSelectedPanel(filePanels[LEFT]);
    }

    private JComponent buildBottonSplit() {
        JScrollPane scrollTreePane = buildTreePane();

        JComponent levensteinPanel = buildLevenstheinTable();

        JComponent bottomSplit;
        if (scrollTreePane != null) {
            if (levensteinPanel != null) {
                bottomSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, scrollTreePane, levensteinPanel);
            } else {
                bottomSplit = scrollTreePane;
            }
        } else {
            bottomSplit = levensteinPanel;
        }
        return bottomSplit;
    }

    private JPanel buildFilePanel(String columns, String rows) {
        FormLayout layout;
        CellConstraints cc;
        JPanel filePanel = new JPanel();
        layout = new FormLayout(columns, rows);
        cc = new CellConstraints();

        filePanel.setLayout(layout);

        filePanels = new FilePanel[NUMBER_OF_PANELS];

        filePanels[LEFT] = new FilePanel(this, BufferDocumentIF.ORIGINAL, LEFT);
        filePanels[RIGHT] = new FilePanel(this, BufferDocumentIF.REVISED, RIGHT);

        // panel for file1
        filePanel.add(new RevisionBar(this, filePanels[LEFT], true), cc.xy(2, 4));
        if (mainPanel.SHOW_FILE_TOOLBAR_OPTION.isEnabled()) {
            filePanel.add(filePanels[LEFT].getSaveButton(), cc.xy(2, 2));
            filePanel.add(filePanels[LEFT].getFileLabel(), cc.xyw(4, 2, 3));
        }
        filePanel.add(filePanels[LEFT].getScrollPane(), cc.xyw(4, 4, 3));
        if (mainPanel.SHOW_FILE_STATUSBAR_OPTION.isEnabled()) {
            filePanel.add(filePanels[LEFT].getFilePanelBar(), cc.xyw(4, 5, 3));
        }

        DiffScrollComponent diffScrollComponent = new DiffScrollComponent(this, LEFT, RIGHT);
        filePanel.add(diffScrollComponent, cc.xy(7, 4));

        // panel for file2
        filePanel.add(new RevisionBar(this, filePanels[RIGHT], false), cc.xy(12, 4));
        if (mainPanel.SHOW_FILE_TOOLBAR_OPTION.isEnabled()) {
            filePanel.add(filePanels[RIGHT].getFileLabel(), cc.xyw(8, 2, 3));
        }
        filePanel.add(filePanels[RIGHT].getScrollPane(), cc.xyw(8, 4, 3));
        if (mainPanel.SHOW_FILE_TOOLBAR_OPTION.isEnabled()) {
            filePanel.add(filePanels[RIGHT].getSaveButton(), cc.xy(12, 2));
        }
        if (mainPanel.SHOW_FILE_STATUSBAR_OPTION.isEnabled()) {
            filePanel.add(filePanels[RIGHT].getFilePanelBar(), cc.xyw(8, 5, 3));
        }

        filePanels[RIGHT].getEditor().addKeyListener(diffScrollComponent.getKeyListener());
        filePanels[LEFT].getEditor().addKeyListener(diffScrollComponent.getKeyListener());
        filePanel.setMinimumSize(new Dimension(300,200));
        return filePanel;
    }

    private JScrollPane buildTreePane() {
        JScrollPane scrollTreePane = null;
        if (isShowTree()) {
            scrollTreePane = new JScrollPane();
            diffTree = new DiffTree();
            diffTree.addMouseListener(new MouseAdapter() {

                private void showPopup(MouseEvent e) {
                    int x = e.getX();
                    int y = e.getY();
                    JTree tree = (JTree)e.getSource();
                    TreePath path = tree.getPathForLocation(x, y);
                    if (path == null) {
                        return;
                    }

                    tree.setSelectionPath(path);

                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();

                    final Object userObject = node.getUserObject();

                    JPopupMenu popup = new JPopupMenu();
                    if (userObject instanceof JMDelta) {
                        JMenuItem menuItem = buildDeleteItem(userObject);
                        menuItem.setEnabled(enableDeleteAction(node));
                        popup.add(menuItem);
                    }
                    if (userObject instanceof JMRevision) {
                        JMenuItem menuItem = buildInsertItem();
                        menuItem.setEnabled(enableInsertAction(node));
                        popup.add(menuItem);
                    }

                    popup.show(tree, x, y);
                }

                private boolean enableDeleteAction(DefaultMutableTreeNode node) {
                    Object parentUserObject = ((DefaultMutableTreeNode) (node.getParent())).getUserObject();
                    return parentUserObject instanceof JMRevision
                            || (parentUserObject instanceof JMDelta && ((JMDelta)parentUserObject).isChange());
                }

                private JMenuItem buildDeleteItem(final Object userObject) {
                    JMenuItem menuItem = new JMenuItem("Delete node");
                    menuItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            List<JMDelta> deltas = currentRevision.getDeltas();
                            for (JMDelta delta : deltas) {
                                if (deleteDelta(deltas, delta)) {
                                    return;
                                }
                                if (delta.isChange()){
                                    List<JMDelta> changeDeltas = delta.getChangeRevision().getDeltas();
                                    for (JMDelta changeDelta : changeDeltas) {
                                        if (deleteDelta(changeDeltas, changeDelta)) {
                                            return;
                                        }
                                    }
                                }
                            }
                        }

                        private boolean deleteDelta(List<JMDelta> deltas, JMDelta delta) {
                            if (delta.equals(userObject)) {
                                deltas.remove(delta);
                                reDisplay();
                                return true;
                            }
                            return false;
                        }
                    });
                    return menuItem;
                }

                private JMenuItem buildInsertItem() {
                    JMenuItem menuItem;
                    menuItem = new JMenuItem("Insert node");
                    menuItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int selectedRow = levensteinGraphTable.getSelectedRow();
                            int selectedColumn = levensteinGraphTable.getSelectedColumn();
                            int selectedRowCount = levensteinGraphTable.getSelectedRowCount();
                            int selectedColumnCount = levensteinGraphTable.getSelectedColumnCount();

                            if (selectedRowCount == 1) {
                                selectedRowCount = 0;
                            }
                            if (selectedColumnCount == 1) {
                                selectedColumnCount = 0;
                            }

                            try {
                                JTextArea leftEditor = filePanels[LEFT].getEditor();
                                int firstLine = leftEditor.getLineOfOffset(selectedRow - 2);
                                int endLine = leftEditor.getLineOfOffset(selectedRow - 2 + selectedRowCount - 1);
                                JMChunk original = new JMChunk(firstLine, endLine - firstLine + 1);

                                JTextArea rightEditor = filePanels[RIGHT].getEditor();
                                int firstCol = rightEditor.getLineOfOffset(selectedColumn - 2);
                                int endCol = rightEditor.getLineOfOffset(selectedColumn - 2 + selectedColumnCount - 1);
                                JMChunk revised = new JMChunk(firstCol, endCol - firstCol + 1);

                                JMDelta newDdelta = new JMDelta(original, revised);
                                newDdelta.setRevision(currentRevision);
                                boolean createNew  = true;
                                List<JMDelta> deltas = currentRevision.getDeltas();
                                for (int i = 0; i < deltas.size(); i++) {
                                    JMDelta delta = deltas.get(i);
                                    if (delta.equals(newDdelta)) {
                                        newDdelta = delta;
                                        createNew = false;
                                    }
                                }
                                JMRevision changeRevision;
                                if (createNew) {
                                    changeRevision = currentRevision.createChangeRevision(original, revised, false);
                                    newDdelta.setChangeRevision(changeRevision);
                                    deltas.add(newDdelta);
                                } else {
                                    changeRevision = newDdelta.getChangeRevision();
                                }

                                List<JMDelta> changeDeltas = changeRevision.getDeltas();
                                changeDeltas.add(new JMDelta(new JMChunk(selectedRow - 2 - leftEditor.getLineStartOffset(firstLine), selectedRowCount)
                                        , new JMChunk(selectedColumn - 2 - leftEditor.getLineStartOffset(firstCol), selectedColumnCount)
                                ));
                                reDisplay();
                            } catch (BadLocationException e1) {
                                System.out.println("Error building delta: " + e1.getMessage());
                                e1.printStackTrace();
                            }
                        }
                    });
                    return menuItem;
                }

                private boolean enableInsertAction(DefaultMutableTreeNode node) {
                    int selectedRow = levensteinGraphTable.getSelectedRow();
                    int selectedColumn = levensteinGraphTable.getSelectedColumn();
                    int selectedRowCount = levensteinGraphTable.getSelectedRowCount();
                    int selectedColumnCount = levensteinGraphTable.getSelectedColumnCount();
                    //TODO: if selection is next to a unchanged path

                    boolean deltaDefined = selectedColumnCount > 0 || selectedRowCount > 0;
                    return deltaDefined;
                }

                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        showPopup(e);
                    }
                }

                @Override
                public void mouseClicked(MouseEvent me) {
                    TreePath tp = diffTree.getPathForLocation(me.getX(), me.getY());
                    if (tp != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode)tp.getLastPathComponent();

                        DefaultMutableTreeNode root = (DefaultMutableTreeNode)node.getRoot();
                        while (node != root) {
                            Object userObject = node.getUserObject();
                            if (userObject instanceof JMDelta) {
                                Object parentUserObject = ((DefaultMutableTreeNode) node.getParent()).getUserObject();
                                JMDelta lineDelta = (JMDelta) userObject;
                                JMDelta wordDelta = null;
                                if (parentUserObject instanceof JMDelta) {
                                    wordDelta = lineDelta;
                                    lineDelta = (JMDelta) parentUserObject;
                                }
                                doSelection(lineDelta, wordDelta);
                                break;
                            }
                            node = (DefaultMutableTreeNode)node.getParent();
                        }
                    }
                }

                private void doSelection(JMDelta lineDelta, JMDelta wordDelta) {
                    int firstLine = lineDelta.getOriginal().getAnchor();
                    int lines = lineDelta.getOriginal().getSize();
                    int firstCol = lineDelta.getRevised().getAnchor();
                    int cols = lineDelta.getRevised().getSize();

                    int lineStartOffset = 0;
                    int lineEndOffset = 0;
                    int colStartOffset = 0;
                    int colEndOffset = 0;
                    try {
                        FilePanel leftFilePanel = filePanels[LEFT];
                        FilePanel rightFilePanel = filePanels[RIGHT];
                        int leftLineCount = leftFilePanel.getEditor().getLineCount();
                        int rightLineCount = rightFilePanel.getEditor().getLineCount();
                        if (firstLine == leftLineCount) {
                            lineStartOffset = leftFilePanel.getEditor().getLineEndOffset(firstLine - 1);
                        } else {
                            lineStartOffset = leftFilePanel.getEditor().getLineStartOffset(firstLine);
                        }
                        lineEndOffset = leftFilePanel.getEditor().getLineEndOffset(firstLine + lines - 1);
                        if (firstCol == rightLineCount) {
                            colStartOffset = rightFilePanel.getEditor().getLineEndOffset(firstCol - 1);
                        } else {
                            colStartOffset = rightFilePanel.getEditor().getLineStartOffset(firstCol);
                        }
                        colEndOffset = rightFilePanel.getEditor().getLineEndOffset(firstCol + cols - 1);

                        int lineOffset = 0;
                        if (lineDelta.isAdd()) {
                            lineOffset--;
                        }

                        int colOffset = 0;
                        if (lineDelta.isDelete()) {
                            colOffset--;
                        }

                        int startRow = lineStartOffset + 2 + lineOffset;
                        int endRow = lineEndOffset + 2 - 1;
                        int startCol = colStartOffset + 2 + colOffset;
                        int endCol = colEndOffset + 2 - 1;
                        if (wordDelta != null) {
                            int offsetLine = 0;
                            if (wordDelta.isChange() || wordDelta.isDelete()) {
                                offsetLine--;
                            }
                            int offsetCol = 0;
                            if (wordDelta.isChange() || wordDelta.isAdd()) {
                                offsetCol--;
                            }
                            JMChunk wordDeltaOriginal = wordDelta.getOriginal();
                            JMChunk wordDeltaRevised = wordDelta.getRevised();
                            startRow += wordDeltaOriginal.getAnchor();
                            endRow = startRow + wordDeltaOriginal.getSize() +offsetLine;
                            startCol += wordDeltaRevised.getAnchor();
                            endCol = startCol + wordDeltaRevised.getSize() +offsetCol;
                        }
                        levensteinGraphTable.clearSelection();
                        if (lineDelta.isAdd()) {
                            HashMap<Point, MatteBorder> rowLineSelection = createRowLineSelection(startRow - 2);
                            ((LevenshteinTableModel)levensteinGraphTable.getModel()).setBorderSelections(rowLineSelection);
                            startRow = -1;
                            endRow = -1;
                        }
                        if (lineDelta.isDelete()) {
                            HashMap<Point, MatteBorder> columnLineSelection = createColumnLineSelection(startCol - 2);
                            ((LevenshteinTableModel)levensteinGraphTable.getModel()).setBorderSelections(columnLineSelection);
                            startCol = -1;
                            endCol = -1;
                        }
                        levensteinGraphTable.changeSelection(startRow, startCol, false, false);
                        levensteinGraphTable.changeSelection(endRow, endCol, false, true);
                        levensteinGraphTable.repaint();
                    } catch (BadLocationException e) {
                        System.err.printf("(%d, %d, %d, %d)%n", lineStartOffset, lineEndOffset, colStartOffset, colEndOffset);
                        System.err.printf("(%d, %d, %d, %d)%n", firstLine, lines, firstCol, cols);
                        e.printStackTrace();
                    }
                }
            });

            scrollTreePane.setViewportView(diffTree);
        }
        return scrollTreePane;
    }

    private JComponent buildLevenstheinTable() {

        if (!isShowLevenstein()) {
            return null;
        }

        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(15, 5, Integer.MAX_VALUE, 1));

        levensteinGraphTable = new JTable() {
            /**
             * Falso si el tamaño de la tabla no es el tamaño del JScrollPane
             * @return
             */
            public boolean getScrollableTracksViewportWidth() {
                boolean ok = false;
                if (autoResizeMode != AUTO_RESIZE_OFF) {
                    if (getParent() instanceof JViewport) {
                        int parentWidth = getParent().getWidth();
                        int tableWidth = getPreferredSize().width;
                        ok = parentWidth > tableWidth;
                    }
                }
                return ok;
            }

            @Override
            public void addColumn(TableColumn aColumn) {
                aColumn.setPreferredWidth((Integer) spinner.getValue());
                super.addColumn(aColumn);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                TableColumnModel cm = levensteinGraphTable.getColumnModel();
                Integer preferredWidth = (Integer) spinner.getValue();
                for (int i = 0; i < cm.getColumnCount(); i++) {
                    //TODO: Si es menor que el mininumWidth, no hace nada
                    cm.getColumn(i).setPreferredWidth(preferredWidth);
                }
            }
        });

        levensteinGraphTable.setTableHeader(null);
        levensteinGraphTable.setFillsViewportHeight(true);

        JPanel panelGraph = new JPanel(new BorderLayout());
        panelGraph.add(new JScrollPane(levensteinGraphTable));
        JPanel bPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));

        Box box = Box.createHorizontalBox();
        bPanel.add(new JLabel("Min Column Width"));
        bPanel.add(spinner);
        box.add(bPanel);
        box.add(Box.createHorizontalGlue());
        checkSolutionPath = new JCheckBox("Show solution path");
        checkSolutionPath.setSelected(true);
        checkSolutionPath.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ((LevenshteinTableModel) levensteinGraphTable.getModel())
                        .setShowSelectionPath(checkSolutionPath.isSelected());
                levensteinGraphTable.repaint();
            }
        });
        box.add(checkSolutionPath);
        box.add(Box.createHorizontalGlue());

        final JLabel cellSelected = new JLabel("(,)");
        box.add(cellSelected);
        ListSelectionListener listSelectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int rowOrigin = levensteinGraphTable.getSelectedRow() - 2;
                int rowCount = levensteinGraphTable.getSelectedRowCount() +1 - 2;
                int columnOrigin = levensteinGraphTable.getSelectedColumn() - 2;
                int columnCount = levensteinGraphTable.getSelectedColumnCount() +1 - 2;
                cellSelected.setText("(" + rowOrigin + "," + (rowOrigin + rowCount) + ")," +
                        " (" + columnOrigin + "," + (columnOrigin + columnCount) + ")");
                //TODO: Highlight position.
                levensteinGraphTable.repaint();
            }
        };
        levensteinGraphTable.getColumnModel().getSelectionModel()
                .addListSelectionListener(listSelectionListener);
        levensteinGraphTable.getSelectionModel()
                .addListSelectionListener(listSelectionListener);
        filePanels[LEFT].getEditor().addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                int dot = e.getDot();
                int mark = e.getMark();
                HashMap<Point, MatteBorder> borderSelection;
                levensteinGraphTable.clearSelection();
                if (dot == mark) {
                    borderSelection = createRowLineSelection(dot);
                    levensteinGraphTable.scrollRectToVisible(levensteinGraphTable.getCellRect(dot, 0, true));
                } else {
                    borderSelection = new HashMap<>();
                    if (mark > dot) {
                        int temp = dot;
                        dot = mark;
                        mark = temp;
                    }
                    levensteinGraphTable.changeSelection(dot + 2 - 1, levensteinGraphTable.getSelectedColumn(), false, false);
                    levensteinGraphTable.changeSelection(mark + 2, levensteinGraphTable.getSelectedColumn(), false, true);
                }
                ((LevenshteinTableModel) levensteinGraphTable.getModel()).setBorderSelections(borderSelection);
                levensteinGraphTable.repaint();
            }
        });
        filePanels[RIGHT].getEditor().addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                int dot = e.getDot();
                int mark = e.getMark();
                HashMap<Point, MatteBorder> borderSelection;
                levensteinGraphTable.clearSelection();
                if (dot == mark) {
                    borderSelection = createColumnLineSelection(dot);
                    levensteinGraphTable.scrollRectToVisible(levensteinGraphTable.getCellRect(0, dot, true));
                } else {
                    borderSelection = new HashMap<>();
                    if (mark > dot) {
                        int temp = dot;
                        dot = mark;
                        mark = temp;
                    }
                    levensteinGraphTable.changeSelection(levensteinGraphTable.getSelectedRow(), dot + 2 - 1, false, false);
                    levensteinGraphTable.changeSelection(levensteinGraphTable.getSelectedRow(), mark + 2, false, true);
                }
                ((LevenshteinTableModel) levensteinGraphTable.getModel()).setBorderSelections(borderSelection);
                levensteinGraphTable.repaint();
            }
        });
        panelGraph.add(box, BorderLayout.SOUTH);
        return panelGraph;
    }

    private HashMap<Point, MatteBorder> createRowLineSelection(int row) {
        HashMap<Point, MatteBorder> borderSelection = new HashMap<>();
        for (int i = 0; i < filePanels[LEFT].getBufferDocument().getDocument().getLength(); i++) {
            borderSelection.put(new Point(row, i), BorderFactory.createMatteBorder(2, 0, 0, 0, selectionColor));
        }
        return borderSelection;
    }

    private HashMap<Point, MatteBorder> createColumnLineSelection(int column) {
        HashMap<Point, MatteBorder> borderSelection = new HashMap<>();
        for (int i = 0; i < filePanels[LEFT].getBufferDocument().getDocument().getLength(); i++) {
            borderSelection.put(new Point(i, column), BorderFactory.createMatteBorder(0, 2, 0, 0, selectionColor));
        }
        return borderSelection;
    }

    private void refreshLevensteinModel() {

        if (isShowLevenstein()) {
            try {
                PlainDocument orgDoc = filePanels[LEFT].getBufferDocument().getDocument();
                PlainDocument revDoc = filePanels[RIGHT].getBufferDocument().getDocument();
                LevenshteinTableModel model = new LevenshteinTableModel();
                model.setOrigin(orgDoc.getText(0, orgDoc.getLength()));
                model.setDestiny(revDoc.getText(0, revDoc.getLength()));
                //Add to renderer instead
                model.setCurrentRevision(currentRevision);
                model.setFilePanels(filePanels);
                model.setShowSelectionPath(checkSolutionPath.isSelected());
                model.buildModel();

                levensteinGraphTable.setModel(model);
                levensteinGraphTable.setDefaultRenderer(Object.class, model.getCellRenderer());
            } catch (BadLocationException e) {
            }
        }
    }

    public void toNextDelta(boolean next) {
        if (next) {
            doDown();
        } else {
            doUp();
        }
    }

    public JMRevision getCurrentRevision() {
        return currentRevision;
    }

    @Override
    public boolean checkSave() {
        SavePanelDialog dialog;

        if (!isSaveEnabled()) {
            return true;
        }

        dialog = new SavePanelDialog(mainPanel);
        for (FilePanel filePanel : filePanels) {
            if (filePanel != null) {
                dialog.add(filePanel.getBufferDocument());
            }
        }

        dialog.show();

        if (dialog.isOK()) {
            dialog.doSave();
            return true;
        }

        return false;
    }

    @Override
    public void doSave() {
        BufferDocumentIF document;

        for (FilePanel filePanel : filePanels) {
            if (filePanel == null) {
                continue;
            }

            if (!filePanel.isDocumentChanged()) {
                continue;
            }

            document = filePanel.getBufferDocument();

            try {
                document.write();
            } catch (JMeldException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainPanel, "Can't write file"
                        + document.getName(),
                        "Problem writing file", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public boolean isSaveEnabled() {
        for (FilePanel filePanel : filePanels) {
            if (filePanel != null && filePanel.isDocumentChanged()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void doStopSearch() {
        for (FilePanel filePanel : filePanels) {
            if (filePanel != null) {
                filePanel.doStopSearch();
            }
        }
    }

    public SearchCommand getSearchCommand() {
        return mainPanel.getSearchCommand();
    }

    @Override
    public SearchHits doSearch() {
        FilePanel fp;
        SearchHits searchHits;

        fp = getSelectedPanel();
        if (fp == null) {
            return null;
        }

        searchHits = fp.doSearch();

        scrollToSearch(fp, searchHits);

        return searchHits;
    }

    @Override
    public void doNextSearch() {
        FilePanel fp;
        SearchHits searchHits;

        fp = getSelectedPanel();
        if (fp == null) {
            return;
        }

        searchHits = fp.getSearchHits();
        searchHits.next();
        fp.reDisplay();

        scrollToSearch(fp, searchHits);
    }

    @Override
    public void doPreviousSearch() {
        FilePanel fp;
        SearchHits searchHits;

        fp = getSelectedPanel();
        if (fp == null) {
            return;
        }

        searchHits = fp.getSearchHits();
        searchHits.previous();
        fp.reDisplay();

        scrollToSearch(fp, searchHits);
    }

    @Override
    public void doRefresh() {
        diff();
    }

    @Override
    public void doMergeMode(boolean mergeMode) {
        for (FilePanel fp : filePanels) {
            if (fp != null) {
                fp.getEditor().setFocusable(!mergeMode);
            }
        }
    }

    private void scrollToSearch(FilePanel fp, SearchHits searchHits) {
        SearchHit currentHit;
        int line;

        if (searchHits == null) {
            return;
        }

        currentHit = searchHits.getCurrent();
        if (currentHit != null) {
            line = currentHit.getLine();

            scrollSynchronizer.scrollToLine(fp, line);
            setSelectedLine(line);
        }
    }

    private FilePanel getSelectedPanel() {
        if (filePanelSelectedIndex >= 0
                && filePanelSelectedIndex < filePanels.length) {
            return filePanels[filePanelSelectedIndex];
        }

        return null;
    }

    void setSelectedPanel(FilePanel fp) {
        int index;

        index = -1;
        for (int i = 0; i < filePanels.length; i++) {
            if (filePanels[i] == fp) {
                index = i;
            }
        }

        if (index != filePanelSelectedIndex) {
            if (filePanelSelectedIndex != -1) {
                filePanels[filePanelSelectedIndex].setSelected(false);
            }

            filePanelSelectedIndex = index;

            if (filePanelSelectedIndex != -1) {
                filePanels[filePanelSelectedIndex].setSelected(true);
            }
        }
    }

    @Override
    public void checkActions() {
        mainPanel.checkActions();
    }

    @Override
    public void doLeft(boolean shift) {
        runChange(RIGHT, LEFT, shift);
    }

    @Override
    public void doRight(boolean shift) {
        runChange(LEFT, RIGHT, shift);
    }

    public void runChange(int fromPanelIndex, int toPanelIndex, boolean shift) {
        JMDelta delta;
        BufferDocumentIF fromBufferDocument;
        BufferDocumentIF toBufferDocument;
        PlainDocument from;
        String s;
        int fromLine;
        int fromOffset;
        int toOffset;
        int size;
        JMChunk fromChunk;
        JMChunk toChunk;
        JTextComponent toEditor;

        delta = getSelectedDelta();
        if (delta == null) {
            return;
        }

        // Some sanity checks.
        if (fromPanelIndex < 0 || fromPanelIndex >= filePanels.length) {
            return;
        }

        if (toPanelIndex < 0 || toPanelIndex >= filePanels.length) {
            return;
        }

        try {
            fromBufferDocument = filePanels[fromPanelIndex].getBufferDocument();
            toBufferDocument = filePanels[toPanelIndex].getBufferDocument();

            // TODO: delta and revision are not yet ready for 3-way merge!
            if (fromPanelIndex < toPanelIndex) {
                fromChunk = delta.getOriginal();
                toChunk = delta.getRevised();
            } else {
                fromChunk = delta.getRevised();
                toChunk = delta.getOriginal();
            }
            toEditor = filePanels[toPanelIndex].getEditor();

            if (fromBufferDocument == null || toBufferDocument == null) {
                return;
            }

            fromLine = fromChunk.getAnchor();
            size = fromChunk.getSize();
            fromOffset = fromBufferDocument.getOffsetForLine(fromLine);
            if (fromOffset < 0) {
                return;
            }

            toOffset = fromBufferDocument.getOffsetForLine(fromLine + size);
            if (toOffset < 0) {
                return;
            }

            from = fromBufferDocument.getDocument();
            s = from.getText(fromOffset, toOffset - fromOffset);

            fromLine = toChunk.getAnchor();
            size = toChunk.getSize();
            fromOffset = toBufferDocument.getOffsetForLine(fromLine);
            if (fromOffset < 0) {
                return;
            }

            toOffset = toBufferDocument.getOffsetForLine(fromLine + size);
            if (toOffset < 0) {
                return;
            }

            getUndoHandler().start("replace");
            toEditor.setSelectionStart(fromOffset);
            toEditor.setSelectionEnd(toOffset);
            if (!shift) {
                toEditor.replaceSelection(s);
            } else {
//                toEditor.getDocument().insertString(fromOffset, s, null);
                toEditor.getDocument().insertString(toOffset, s, null);
            }
            getUndoHandler().end("replace");

            setSelectedDelta(null);
            setSelectedLine(delta.getOriginal().getAnchor());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void runDelete(int fromPanelIndex, int toPanelIndex) {
        JMDelta delta;
        BufferDocumentIF bufferDocument;
        PlainDocument document;
        String s;
        int fromLine;
        int fromOffset;
        int toOffset;
        int size;
        JMChunk chunk;
        JTextComponent toEditor;

        try {
            delta = getSelectedDelta();
            if (delta == null) {
                return;
            }

            // Some sanity checks.
            if (fromPanelIndex < 0 || fromPanelIndex >= filePanels.length) {
                return;
            }

            if (toPanelIndex < 0 || toPanelIndex >= filePanels.length) {
                return;
            }

            bufferDocument = filePanels[fromPanelIndex].getBufferDocument();
            if (fromPanelIndex < toPanelIndex) {
                chunk = delta.getOriginal();
            } else {
                chunk = delta.getRevised();
            }
            toEditor = filePanels[fromPanelIndex].getEditor();

            if (bufferDocument == null) {
                return;
            }

            document = bufferDocument.getDocument();
            fromLine = chunk.getAnchor();
            size = chunk.getSize();
            fromOffset = bufferDocument.getOffsetForLine(fromLine);
            if (fromOffset < 0) {
                return;
            }

            toOffset = bufferDocument.getOffsetForLine(fromLine + size);
            if (toOffset < 0) {
                return;
            }

            getUndoHandler().start("remove");
            toEditor.setSelectionStart(fromOffset);
            toEditor.setSelectionEnd(toOffset);
            toEditor.replaceSelection("");
            getUndoHandler().end("remove");

            setSelectedDelta(null);
            setSelectedLine(delta.getOriginal().getAnchor());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void doDown() {
        JMDelta d;
        JMDelta sd;
        List<JMDelta> deltas;
        int index;

        if (currentRevision == null) {
            return;
        }

        deltas = currentRevision.getDeltas();
        sd = getSelectedDelta();
        index = deltas.indexOf(sd);
        if (index == -1 || sd.getOriginal().getAnchor() != selectedLine) {
            // Find the delta that would have been next to the
            //   disappeared delta:
            d = null;
            for (JMDelta delta : deltas) {
                d = delta;
                if (delta.getOriginal().getAnchor() > selectedLine) {
                    break;
                }
            }

            setSelectedDelta(d);
        } else {
            // Select the next delta if there is any.
            if (index + 1 < deltas.size()) {
                setSelectedDelta(deltas.get(index + 1));
            }
        }

        showSelectedDelta();
    }

    @Override
    public void doUp() {
        JMDelta d;
        JMDelta sd;
        JMDelta previousDelta;
        List<JMDelta> deltas;
        int index;

        if (currentRevision == null) {
            return;
        }

        deltas = currentRevision.getDeltas();
        sd = getSelectedDelta();
        index = deltas.indexOf(sd);
        if (index == -1 || sd.getOriginal().getAnchor() != selectedLine) {
            // Find the delta that would have been previous to the
            //   disappeared delta:
            d = null;
            previousDelta = null;
            for (JMDelta delta : deltas) {
                d = delta;
                if (delta.getOriginal().getAnchor() > selectedLine) {
                    if (previousDelta != null) {
                        d = previousDelta;
                    }
                    break;
                }

                previousDelta = delta;
            }

            setSelectedDelta(d);
        } else {
            // Select the next delta if there is any.
            if (index - 1 >= 0) {
                setSelectedDelta(deltas.get(index - 1));
            }
        }
        showSelectedDelta();
    }

    public void doGotoDelta(JMDelta delta) {
        setSelectedDelta(delta);
        showSelectedDelta();
    }

    public void doGotoLine(int line) {
        BufferDocumentIF bd;
        int offset;
        int startOffset;
        int endOffset;
        JViewport viewport;
        JTextComponent editor;
        Point p;
        FilePanel fp;
        Rectangle rect;

        setSelectedLine(line);

        fp = getFilePanel(0);

        bd = fp.getBufferDocument();
        if (bd == null) {
            return;
        }

        offset = bd.getOffsetForLine(line);
        viewport = fp.getScrollPane().getViewport();
        editor = fp.getEditor();

        // Don't go anywhere if the line is already visible.
        rect = viewport.getViewRect();
        startOffset = editor.viewToModel(rect.getLocation());
        endOffset = editor.viewToModel(new Point(rect.x, rect.y + rect.height));
        if (offset >= startOffset && offset <= endOffset) {
            return;
        }

        try {
            p = editor.modelToView(offset).getLocation();
            p.x = 0;

            viewport.setViewPosition(p);
        } catch (BadLocationException ex) {
        }
    }

    @Override
    public void doZoom(boolean direction) {
        JTextComponent c;
        Font font;
        float size;
        Zoom zoom;

        for (FilePanel p : filePanels) {
            if (p == null) {
                continue;
            }

            c = p.getEditor();

            zoom = (Zoom) c.getClientProperty("JMeld.zoom");
            if (zoom == null) {
                // Save the orginal font because that's the font which will
                //   give the derived font.
                zoom = new Zoom();
                zoom.font = c.getFont();
                c.putClientProperty("JMeld.zoom", zoom);
            }

            size = c.getFont().getSize() + (direction ? 1.0f : -1.0f);
            size = size > 100.0f ? 100.0f : size;
            size = size < 5.0f ? 5.0f : size;

            c.setFont(zoom.font.deriveFont(size));
        }
    }

    @Override
    public void doGoToSelected() {
        showSelectedDelta();
    }

    @Override
    public void doGoToFirst() {
        JMDelta d;
        List<JMDelta> deltas;

        if (currentRevision == null) {
            return;
        }

        deltas = currentRevision.getDeltas();
        if (deltas.size() > 0) {
            setSelectedDelta(deltas.get(0));
            showSelectedDelta();
        }
    }

    @Override
    public void doGoToLast() {
        JMDelta d;
        List<JMDelta> deltas;

        if (currentRevision == null) {
            return;
        }

        deltas = currentRevision.getDeltas();
        if (deltas.size() > 0) {
            setSelectedDelta(deltas.get(deltas.size() - 1));
            showSelectedDelta();
        }
    }

    @Override
    public void doGoToLine(int line) {
        FilePanel fp;

        fp = getSelectedPanel();
        if (fp == null) {
            return;
        }

        scrollSynchronizer.scrollToLine(fp, line);
        setSelectedLine(line);
    }

    @Override
    public void configurationChanged() {
        readConfig();
        init();
        refreshDiffNode();
        reDisplay();
        diff();
    }

    private void readConfig() {
        setShowTree(JMeldSettings.getInstance().getEditor().isShowTreeChunks());
        setShowLevenstein(JMeldSettings.getInstance().getEditor().isShowLevenstheinEditor());
    }

    class Zoom {
        Font font;
    }

    public void setSelectedDelta(JMDelta delta) {
        selectedDelta = delta;
        setSelectedLine(delta == null ? 0 : delta.getOriginal().getAnchor());
    }

    private void setSelectedLine(int line) {
        selectedLine = line;
    }

    private void showSelectedDelta() {
        JMDelta delta;

        delta = getSelectedDelta();
        if (delta == null) {
            return;
        }

        scrollSynchronizer.showDelta(delta);
    }

    public JMDelta getSelectedDelta() {
        List<JMDelta> deltas;

        if (currentRevision == null) {
            return null;
        }

        deltas = currentRevision.getDeltas();
        if (deltas.size() == 0) {
            return null;
        }

        return selectedDelta;
    }

    public FilePanel getFilePanel(int index) {
        if (index < 0 || index > filePanels.length) {
            return null;
        }

        return filePanels[index];
    }

    @Override
    public String getSelectedText() {
        FilePanel fp;

        fp = getSelectedPanel();
        if (fp == null) {
            return null;
        }

        return fp.getSelectedText();
    }
}
