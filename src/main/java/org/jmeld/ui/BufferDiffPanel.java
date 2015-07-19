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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.util.ArrayList;
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
            if (StringUtil.isEmpty(title)) {
                continue;
            }

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
            try {
                currentRevision = diff.diff(bd1.getLines(), bd2.getLines()
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
        bPanel.add(new JLabel("Min Column Width"));
        bPanel.add(spinner);
        panelGraph.add(bPanel, BorderLayout.SOUTH);
        return panelGraph;
    }

    private void refreshLevensteinModel() {

        if (isShowLevenstein()) {
            try {
                PlainDocument orgDoc = filePanels[LEFT].getBufferDocument().getDocument();
                PlainDocument revDoc = filePanels[RIGHT].getBufferDocument().getDocument();
                LevenshteinTableModel model = new LevenshteinTableModel(orgDoc.getText(0, orgDoc.getLength()), revDoc.getText(0, revDoc.getLength()));

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
