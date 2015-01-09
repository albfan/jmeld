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

import org.jmeld.diff.JMChunk;
import org.jmeld.diff.JMDelta;
import org.jmeld.diff.JMRevision;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.search.SearchCommand;
import org.jmeld.ui.search.SearchHit;
import org.jmeld.ui.search.SearchHits;
import org.jmeld.ui.swing.*;
import org.jmeld.ui.text.BufferDocumentChangeListenerIF;
import org.jmeld.ui.text.BufferDocumentIF;
import org.jmeld.ui.text.JMDocumentEvent;
import org.jmeld.ui.util.FontUtil;
import org.jmeld.ui.util.ImageUtil;
import org.jmeld.util.StringUtil;
import org.jmeld.util.conf.ConfigurationListenerIF;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.PlainDocument;

import java.awt.*;
import java.awt.event.*;

public class FilePanel implements BufferDocumentChangeListenerIF, ConfigurationListenerIF {
    private static final int MAXSIZE_CHANGE_DIFF = 1000;

    private BufferDiffPanel diffPanel;
    private String name;
    private int position;
    private DiffLabel fileLabel;
    private JComboBox fileBox;
    private JScrollPane scrollPane;
    private JTextArea editor;
    private BufferDocumentIF bufferDocument;
    private JButton saveButton;
    private Timer timer;
    private SearchHits searchHits;
    private boolean selected;
    private FilePanelBar filePanelBar;

    FilePanel(BufferDiffPanel diffPanel, String name, int position) {
        this.diffPanel = diffPanel;
        this.name = name;
        this.position = position;

        init();
    }

    private void init() {
        ImageIcon icon;

        editor = new JTextArea();
        editor.setDragEnabled(true);
        editor.setHighlighter(new JMHighlighter());

        editor.addFocusListener(getFocusListener());
        editor.addCaretListener(getCaretListener());
        DefaultContextMenu contextMenu = new DefaultContextMenu();
        contextMenu.add(editor);


        scrollPane = new JScrollPane(editor);
//        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        scrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
        if (BufferDocumentIF.ORIGINAL.equals(name)) {
            // Dirty trick to have the scrollbar on the other side!
            LeftScrollPaneLayout layout;
            layout = new LeftScrollPaneLayout();
            scrollPane.setLayout(layout);
            layout.syncWithScrollPane(scrollPane);

            // Normally the leftside is not painted of a scrollbar that is
            //   NOT freestanding.
            scrollPane.getVerticalScrollBar().putClientProperty(
                    "JScrollBar.isFreeStanding", Boolean.TRUE);
        }

        fileBox = new JComboBox();
        fileBox.addActionListener(getFileBoxAction());

        fileLabel = new DiffLabel();

        saveButton = new JButton();
        saveButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        saveButton.setContentAreaFilled(false);
        icon = ImageUtil.getSmallImageIcon("stock_save");
        saveButton.setIcon(icon);
        saveButton.setDisabledIcon(ImageUtil.createTransparentIcon(icon));
        saveButton.addActionListener(getSaveButtonAction());

        timer = new Timer(100, refresh());
        timer.setRepeats(false);

        initConfiguration();
        getConfiguration().addConfigurationListener(this);
    }

    FilePanelBar getFilePanelBar() {
        if (filePanelBar == null) {
            filePanelBar = new FilePanelBar(this);
        }

        return filePanelBar;
    }

    JComboBox getFileBox() {
        return fileBox;
    }

    DiffLabel getFileLabel() {
        return fileLabel;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public JTextArea getEditor() {
        return editor;
    }

    public BufferDocumentIF getBufferDocument() {
        return bufferDocument;
    }

    JButton getSaveButton() {
        return saveButton;
    }

    public void setBufferDocument(BufferDocumentIF bd) {
        Document previousDocument;
        Document document;
        String fileName;

        try {
            if (bufferDocument != null) {
                bufferDocument.removeChangeListener(this);

                previousDocument = bufferDocument.getDocument();
                if (previousDocument != null) {
                    previousDocument.removeUndoableEditListener(diffPanel
                            .getUndoHandler());
                }
            }

            bufferDocument = bd;

            document = bufferDocument.getDocument();
            if (document != null) {
                editor.setDocument(document);
                editor.setTabSize(getConfiguration().getEditor().getTabSize());
                bufferDocument.addChangeListener(this);
                document.addUndoableEditListener(diffPanel.getUndoHandler());
            }

            fileName = bufferDocument.getName();
            fileBox.addItem(fileName);
            fileBox.setSelectedItem(fileName);

            fileLabel.setText(fileName);

            checkActions();
            initConfiguration();
        } catch (Exception ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(diffPanel, "Could not read file: "
                    + bufferDocument.getName()
                    + "\n" + ex.getMessage(),
                    "Error opening file", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    void updateFileLabel(String name1, String name2) {
        fileLabel.setText(name1, name2);
    }

    void doStopSearch() {
        searchHits = null;
        reDisplay();
    }

    private void checkSearch() {
        doSearch();
    }

    SearchHits doSearch() {
        int numberOfLines;
        BufferDocumentIF doc;
        String text;
        int index, fromIndex;
        boolean regularExpression;
        String searchText;
        SearchHit searchHit;
        int offset;
        int length;
        SearchCommand searchCommand;

        searchCommand = diffPanel.getSearchCommand();
        if (searchCommand == null) {
            return null;
        }

        searchText = searchCommand.getSearchText();
        regularExpression = searchCommand.isRegularExpression();

        doc = getBufferDocument();
        numberOfLines = doc.getNumberOfLines();

        searchHits = new SearchHits();

        if (!StringUtil.isEmpty(searchText)) {
            for (int line = 0; line < numberOfLines; line++) {
                text = doc.getLineText(line);
                if (!regularExpression) {
                    fromIndex = 0;
                    while ((index = text.indexOf(searchText, fromIndex)) != -1) {
                        offset = bufferDocument.getOffsetForLine(line);
                        if (offset < 0) {
                            continue;
                        }

                        searchHit = new SearchHit(line, offset + index, searchText.length());
                        searchHits.add(searchHit);

                        fromIndex = index + searchHit.getSize() + 1;
                    }
                }
            }
        }

        reDisplay();

        return getSearchHits();
    }

    void setShowLineNumbers(boolean showLineNumbers) {
        Border originalBorder;
        String propertyName;

        propertyName = "JMeld.originalBorder";
        originalBorder = (Border) editor.getClientProperty(propertyName);

        if (showLineNumbers) {
            if (originalBorder == null) {
                originalBorder = editor.getBorder();
                editor.setBorder(new LineNumberBorder(this));
                editor.putClientProperty(propertyName, originalBorder);
            }
        } else {
            if (originalBorder != null) {
                editor.setBorder(originalBorder);
                editor.putClientProperty(propertyName, null);
            }
        }
    }

    SearchHits getSearchHits() {
        return searchHits;
    }

    public void reDisplay() {
        getHighlighter().setDoNotRepaint(true);

        removeHighlights();
        paintSearchHighlights();
        paintRevisionHighlights();

        getHighlighter().setDoNotRepaint(false);
        getHighlighter().repaint();
    }

    private void paintSearchHighlights() {
        if (searchHits != null) {
            for (SearchHit sh : searchHits.getSearchHits()) {
                setHighlight(JMHighlighter.LAYER2, sh.getFromOffset(),
                        sh.getToOffset(),
                        searchHits.isCurrent(sh)
                                ? JMHighlightPainter.CURRENT_SEARCH: JMHighlightPainter.SEARCH);
            }
        }
    }

    private void paintRevisionHighlights() {

        if (bufferDocument == null) {
            return;
        }

        JMRevision revision = diffPanel.getCurrentRevision();
        if (revision == null) {
            return;
        }

        for (JMDelta delta : revision.getDeltas()) {
            if (BufferDocumentIF.ORIGINAL.equals(name)) {
                new HighlightOriginal(delta).highlight();
            } else if (BufferDocumentIF.REVISED.equals(name)) {
                new HighlightRevised(delta).highlight();
            }
        }
    }

    abstract class AbstractHighlight {
        protected JMDelta delta;

        public AbstractHighlight(JMDelta delta) {
            this.delta = delta;
        }

        protected void highlight() {
            int fromOffset;
            int toOffset;
            JMRevision changeRev;
            JMChunk changeOriginal;
            int fromOffset2;
            int toOffset2;
            fromOffset = bufferDocument.getOffsetForLine(getPrimaryChunk().getAnchor());
            if (fromOffset < 0) {
                return;
            }

            toOffset = bufferDocument.getOffsetForLine(getPrimaryChunk().getAnchor() + getPrimaryChunk().getSize());
            if (toOffset < 0) {
                return;
            }

            boolean isEndAndIsLastNewLine = isEndAndIsLastNewLine(toOffset);

            JMHighlightPainter highlight = null;
            if (delta.isChange()) {
                if (delta.getOriginal().getSize() < MAXSIZE_CHANGE_DIFF
                        && delta.getRevised().getSize() < MAXSIZE_CHANGE_DIFF) {
                    changeRev = delta.getChangeRevision();
                    if (changeRev != null) {
                        for (JMDelta changeDelta : changeRev.getDeltas()) {
                            changeOriginal = getPrimaryChunk(changeDelta);
                            if (changeOriginal.getSize() <= 0) {
                                continue;
                            }

                            fromOffset2 = fromOffset + changeOriginal.getAnchor();
                            toOffset2 = fromOffset2 + changeOriginal.getSize();

                            setHighlight(JMHighlighter.LAYER1, fromOffset2, toOffset2,
                                    JMHighlightPainter.CHANGED_LIGHTER);
                        }
                    }
                }

                highlight = isEndAndIsLastNewLine ? JMHighlightPainter.CHANGED_NEWLINE : JMHighlightPainter.CHANGED;
            } else {
                if (isEmptyLine()) {
                    toOffset = fromOffset + 1;
                }
                if (delta.isAdd()) {
                    highlight = getAddedHighlightPainter(isOriginal(), isEndAndIsLastNewLine);
                } else if (delta.isDelete()) {
                    highlight = getDeleteHighlightPainter(!isOriginal(), isEndAndIsLastNewLine);
                }
            }
            setHighlight(fromOffset, toOffset, highlight);
        }

        /**
         * If last change reach the end and has a newline as final char, final line is
         * virtual. Document has no line for it, since newline stars that line but it
         * contains nothing yet.
         *
         * @param toOffset last offset of change
         *
         * @see org.jmeld.ui.swing.JMHighlightPainter#ADDED_NEWLINE
         * @see org.jmeld.ui.swing.JMHighlightPainter#CHANGED_NEWLINE
         * @see org.jmeld.ui.swing.JMHighlightPainter#DELETED_NEWLINE
         * @return
         */
        private boolean isEndAndIsLastNewLine(int toOffset) {
            boolean isEndAndIsLastNewLine = false;
            try {
                PlainDocument document = bufferDocument.getDocument();
                int endOffset = toOffset - 1;
                boolean changeReachEnd = endOffset == document.getLength();
                boolean lastCharIsNewLine = "\n".equals(document.getText(endOffset, 1));
                isEndAndIsLastNewLine = changeReachEnd && lastCharIsNewLine;
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            return isEndAndIsLastNewLine;
        }

        private JMChunk getPrimaryChunk() {
            return getPrimaryChunk(delta);
        }

        private boolean isOriginal() {
            return delta.getOriginal() == getPrimaryChunk();
        }

        private JMHighlightPainter getAddedHighlightPainter(boolean line, boolean isLastNewLine) {
            return line
                    ? JMHighlightPainter.ADDED_LINE
                    : isLastNewLine
                    ? JMHighlightPainter.ADDED_NEWLINE
                    : JMHighlightPainter.ADDED;
        }

        private JMHighlightPainter getDeleteHighlightPainter(boolean line, boolean isLastNewLine) {
            return line
                    ? JMHighlightPainter.DELETED_LINE
                    : isLastNewLine
                        ? JMHighlightPainter.DELETED_NEWLINE
                        : JMHighlightPainter.DELETED;
        }

        protected abstract JMChunk getPrimaryChunk(JMDelta changeDelta);

        public abstract boolean isEmptyLine();
    }

    class HighlightOriginal extends AbstractHighlight {

        public HighlightOriginal(JMDelta delta) {
            super(delta);
        }

        public boolean isEmptyLine() {
            return delta.isAdd();
        }

        protected JMChunk getPrimaryChunk(JMDelta changeDelta) {
            return changeDelta.getOriginal();
        }
    }

    class HighlightRevised extends AbstractHighlight {

        public HighlightRevised(JMDelta delta) {
            super(delta);
        }

        public boolean isEmptyLine() {
            return delta.isDelete();
        }

        protected JMChunk getPrimaryChunk(JMDelta changeDelta) {
            return changeDelta.getRevised();
        }
    }

    private JMHighlighter getHighlighter() {
        return (JMHighlighter) editor.getHighlighter();
    }

    private void removeHighlights() {
        JMHighlighter jmhl;

        jmhl = getHighlighter();
        jmhl.removeHighlights(JMHighlighter.LAYER0);
        jmhl.removeHighlights(JMHighlighter.LAYER1);
        jmhl.removeHighlights(JMHighlighter.LAYER2);
    }

    private void setHighlight(int offset, int size,
                              Highlighter.HighlightPainter highlight) {
        setHighlight(JMHighlighter.LAYER0, offset, size, highlight);
    }

    private void setHighlight(Integer layer, int offset, int size,
                              Highlighter.HighlightPainter highlight) {
        try {
            getHighlighter().addHighlight(layer, offset, size, highlight);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    public ActionListener getSaveButtonAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    bufferDocument.write();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(SwingUtilities.getRoot(editor),
                            "Could not save file: " + bufferDocument.getName() + "\n"
                                    + ex.getMessage(), "Error saving file",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    public ActionListener getFileBoxAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                //System.out.println("fileBox: " + fileBox.getSelectedItem());
            }
        };
    }

    public void documentChanged(JMDocumentEvent de) {
        if (de.getStartLine() == -1 && de.getDocumentEvent() == null) {
            // Refresh the diff of whole document.
            timer.restart();
        } else {
            // Try to update the revision instead of doing a full diff.
            if (!diffPanel.revisionChanged(de)) {
                timer.restart();
            }
        }

        checkSearch();
        checkActions();
    }

    private void checkActions() {
        if (saveButton.isEnabled() != isDocumentChanged()) {
            saveButton.setEnabled(isDocumentChanged());
        }

        diffPanel.checkActions();
    }

    boolean isDocumentChanged() {
        return bufferDocument != null ? bufferDocument.isChanged() : false;
    }

    private ActionListener refresh() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                diffPanel.diff();
            }
        };
    }

    public FocusListener getFocusListener() {
        return new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent fe) {
                diffPanel.setSelectedPanel(FilePanel.this);
            }
        };
    }

    public CaretListener getCaretListener() {
        return new CaretListener() {
            public void caretUpdate(CaretEvent fe) {
                updateFilePanelBar();
            }
        };
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        updateFilePanelBar();
        checkSearch();
    }

    private void updateFilePanelBar() {
        if (filePanelBar != null) {
            filePanelBar.update();
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void configurationChanged() {
        initConfiguration();
    }

    private void initConfiguration() {
        JMeldSettings settings;
        boolean readonly;
        Font font;
        FontMetrics fm;

        settings = getConfiguration();

        setShowLineNumbers(settings.getEditor().getShowLineNumbers());

        font = settings.getEditor().isCustomFontEnabled() ? settings.getEditor().getFont() : null;
        font = font != null ? font : FontUtil.defaultTextAreaFont;
        editor.setFont(font);
        fm = editor.getFontMetrics(font);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(fm.getHeight());

        getEditor().setTabSize(settings.getEditor().getTabSize());

        readonly = false;
        if (position == BufferDiffPanel.LEFT) {
            readonly = settings.getEditor().getLeftsideReadonly();
        } else if (position == BufferDiffPanel.RIGHT) {
            readonly = settings.getEditor().getRightsideReadonly();
        }

        if (bufferDocument != null && bufferDocument.isReadonly()) {
            readonly = true;
        }

        editor.setEditable(!readonly);
    }

    private JMeldSettings getConfiguration() {
        return JMeldSettings.getInstance();
    }

    public String getSelectedText() {
        return editor.getSelectedText();
    }
}
