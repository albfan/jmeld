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
import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.search.*;
import org.jmeld.ui.swing.*;
import org.jmeld.ui.text.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.*;
import org.jmeld.util.conf.*;
import org.jmeld.util.prefs.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class FilePanel
    implements BufferDocumentChangeListenerIF, ConfigurationListenerIF
{
  // Class variables:
  private static final int MAXSIZE_CHANGE_DIFF = 1000;

  // Instance variables:
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

  FilePanel(BufferDiffPanel diffPanel, String name, int position)
  {
    this.diffPanel = diffPanel;
    this.name = name;
    this.position = position;

    init();
  }

  private void init()
  {
    ImageIcon icon;

    editor = new JTextArea();
    editor.setDragEnabled(true);
    System.out.println("highlighter = " + editor.getHighlighter());
    editor.setHighlighter(new JMHighlighter());
    System.out.println("after highlighter = " + editor.getHighlighter());

    editor.addFocusListener(getFocusListener());
    editor.addCaretListener(getCaretListener());

    scrollPane = new JScrollPane(editor);
    scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
    if (BufferDocumentIF.ORIGINAL.equals(name))
    {
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

  FilePanelBar getFilePanelBar()
  {
    if (filePanelBar == null)
    {
      filePanelBar = new FilePanelBar(this);
    }

    return filePanelBar;
  }

  JComboBox getFileBox()
  {
    return fileBox;
  }

  DiffLabel getFileLabel()
  {
    return fileLabel;
  }

  JScrollPane getScrollPane()
  {
    return scrollPane;
  }

  public JTextArea getEditor()
  {
    return editor;
  }

  public BufferDocumentIF getBufferDocument()
  {
    return bufferDocument;
  }

  JButton getSaveButton()
  {
    return saveButton;
  }

  public void setBufferDocument(BufferDocumentIF bd)
  {
    Document previousDocument;
    Document document;
    String fileName;

    try
    {
      if (bufferDocument != null)
      {
        bufferDocument.removeChangeListener(this);

        previousDocument = bufferDocument.getDocument();
        if (previousDocument != null)
        {
          previousDocument.removeUndoableEditListener(diffPanel
              .getUndoHandler());
        }
      }

      bufferDocument = bd;

      document = bufferDocument.getDocument();
      if (document != null)
      {
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
    }
    catch (Exception ex)
    {
      ex.printStackTrace();

      JOptionPane.showMessageDialog(diffPanel, "Could not read file: "
                                               + bufferDocument.getName()
                                               + "\n" + ex.getMessage(),
        "Error opening file", JOptionPane.ERROR_MESSAGE);
      return;
    }
  }

  void updateFileLabel(String name1, String name2)
  {
    fileLabel.setText(name1, name2);
  }

  void doStopSearch()
  {
    searchHits = null;
    reDisplay();
  }

  private void checkSearch()
  {
    doSearch();
  }

  SearchHits doSearch()
  {
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
    if (searchCommand == null)
    {
      return null;
    }

    searchText = searchCommand.getSearchText();
    regularExpression = searchCommand.isRegularExpression();

    doc = getBufferDocument();
    numberOfLines = doc.getNumberOfLines();

    searchHits = new SearchHits();

    if (!StringUtil.isEmpty(searchText))
    {
      for (int line = 0; line < numberOfLines; line++)
      {
        text = doc.getLineText(line);
        if (!regularExpression)
        {
          fromIndex = 0;
          while ((index = text.indexOf(searchText, fromIndex)) != -1)
          {
            offset = bufferDocument.getOffsetForLine(line);
            if (offset < 0)
            {
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

  void setShowLineNumbers(boolean showLineNumbers)
  {
    Border originalBorder;
    String propertyName;

    propertyName = "JMeld.originalBorder";
    originalBorder = (Border) editor.getClientProperty(propertyName);

    if (showLineNumbers)
    {
      if (originalBorder == null)
      {
        originalBorder = editor.getBorder();
        editor.setBorder(new LineNumberBorder(this));
        editor.putClientProperty(propertyName, originalBorder);
      }
    }
    else
    {
      if (originalBorder != null)
      {
        editor.setBorder(originalBorder);
        editor.putClientProperty(propertyName, null);
      }
    }
  }

  SearchHits getSearchHits()
  {
    return searchHits;
  }

  public void reDisplay()
  {
    getHighlighter().setDoNotRepaint(true);

    removeHighlights();
    paintSearchHighlights();
    paintRevisionHighlights();

    getHighlighter().setDoNotRepaint(false);
    getHighlighter().repaint();
  }

  private void paintSearchHighlights()
  {
    if (searchHits != null)
    {
      for (SearchHit sh : searchHits.getSearchHits())
      {
        setHighlight(JMHighlighter.LAYER2, sh.getFromOffset(),
          sh.getToOffset(),
          searchHits.isCurrent(sh) ? JMHighlightPainter.CURRENT_SEARCH
                                  : JMHighlightPainter.SEARCH);
      }
    }
  }

  private void paintRevisionHighlights()
  {
    JMChunk original;
    JMChunk revised;
    int fromOffset;
    int toOffset;
    int fromOffset2;
    int toOffset2;
    JMRevision revision;
    JMRevision changeRev;
    JMChunk changeOriginal;
    JMChunk changeRevised;

    if (bufferDocument == null)
    {
      return;
    }

    revision = diffPanel.getCurrentRevision();
    if (revision == null)
    {
      return;
    }

    for (JMDelta delta : revision.getDeltas())
    {
      original = delta.getOriginal();
      revised = delta.getRevised();

      if (BufferDocumentIF.ORIGINAL.equals(name))
      {
        fromOffset = bufferDocument.getOffsetForLine(original.getAnchor());
        if (fromOffset < 0)
        {
          continue;
        }

        toOffset = bufferDocument.getOffsetForLine(original.getAnchor()
                                                   + original.getSize());
        if (toOffset < 0)
        {
          continue;
        }

        if (delta.isAdd())
        {
          setHighlight(fromOffset, fromOffset + 1,
            JMHighlightPainter.ADDED_LINE);
        }
        else if (delta.isDelete())
        {
          setHighlight(fromOffset, toOffset, JMHighlightPainter.DELETED);
        }
        else if (delta.isChange())
        {
          // Mark the changes in a change in a different color.
          if (original.getSize() < MAXSIZE_CHANGE_DIFF
              && revised.getSize() < MAXSIZE_CHANGE_DIFF)
          {
            changeRev = delta.getChangeRevision();
            if (changeRev != null)
            {
              for (JMDelta changeDelta : changeRev.getDeltas())
              {
                changeOriginal = changeDelta.getOriginal();
                if (changeOriginal.getSize() <= 0)
                {
                  continue;
                }

                fromOffset2 = fromOffset + changeOriginal.getAnchor();
                toOffset2 = fromOffset2 + changeOriginal.getSize();

                setHighlight(JMHighlighter.LAYER1, fromOffset2, toOffset2,
                  JMHighlightPainter.CHANGED_LIGHTER);
              }
            }
          }

          // First color the changes in changes and after that the entire change
          //   (It seems that you can only color a range once!)
          setHighlight(fromOffset, toOffset, JMHighlightPainter.CHANGED);
        }
      }
      else if (BufferDocumentIF.REVISED.equals(name))
      {
        fromOffset = bufferDocument.getOffsetForLine(revised.getAnchor());
        if (fromOffset < 0)
        {
          continue;
        }

        toOffset = bufferDocument.getOffsetForLine(revised.getAnchor()
                                                   + revised.getSize());
        if (toOffset < 0)
        {
          continue;
        }

        if (delta.isAdd())
        {
          setHighlight(fromOffset, toOffset, JMHighlightPainter.ADDED);
        }
        else if (delta.isDelete())
        {
          setHighlight(fromOffset, fromOffset + 1,
            JMHighlightPainter.DELETED_LINE);
        }
        else if (delta.isChange())
        {
          if (original.getSize() < MAXSIZE_CHANGE_DIFF
              && revised.getSize() < MAXSIZE_CHANGE_DIFF)
          {
            changeRev = delta.getChangeRevision();
            if (changeRev != null)
            {
              for (JMDelta changeDelta : changeRev.getDeltas())
              {
                changeRevised = changeDelta.getRevised();
                if (changeRevised.getSize() <= 0)
                {
                  continue;
                }

                fromOffset2 = fromOffset + changeRevised.getAnchor();
                toOffset2 = fromOffset2 + changeRevised.getSize();

                setHighlight(JMHighlighter.LAYER1, fromOffset2, toOffset2,
                  JMHighlightPainter.CHANGED_LIGHTER);
              }
            }
          }

          setHighlight(fromOffset, toOffset, JMHighlightPainter.CHANGED);
        }
      }
    }
  }

  private JMHighlighter getHighlighter()
  {
    return (JMHighlighter) editor.getHighlighter();
  }

  private void removeHighlights()
  {
    JMHighlighter jmhl;

    jmhl = getHighlighter();
    jmhl.removeHighlights(JMHighlighter.LAYER0);
    jmhl.removeHighlights(JMHighlighter.LAYER1);
    jmhl.removeHighlights(JMHighlighter.LAYER2);
  }

  private void setHighlight(int offset, int size,
      Highlighter.HighlightPainter highlight)
  {
    setHighlight(JMHighlighter.LAYER0, offset, size, highlight);
  }

  private void setHighlight(Integer layer, int offset, int size,
      Highlighter.HighlightPainter highlight)
  {
    try
    {
      getHighlighter().addHighlight(layer, offset, size, highlight);
    }
    catch (BadLocationException ex)
    {
      ex.printStackTrace();
    }
  }

  public ActionListener getSaveButtonAction()
  {
    return new ActionListener()
    {
      public void actionPerformed(ActionEvent ae)
      {
        try
        {
          bufferDocument.write();
        }
        catch (Exception ex)
        {
          JOptionPane.showMessageDialog(SwingUtilities.getRoot(editor),
            "Could not save file: " + bufferDocument.getName() + "\n"
                + ex.getMessage(), "Error saving file",
            JOptionPane.ERROR_MESSAGE);
        }
      }
    };
  }

  public ActionListener getFileBoxAction()
  {
    return new ActionListener()
    {
      public void actionPerformed(ActionEvent ae)
      {
        //System.out.println("fileBox: " + fileBox.getSelectedItem());
      }
    };
  }

  public void documentChanged(JMDocumentEvent de)
  {
    if (de.getStartLine() == -1 && de.getDocumentEvent() == null)
    {
      // Refresh the diff of whole document.
      timer.restart();
    }
    else
    {
      // Try to update the revision instead of doing a full diff.
      if (!diffPanel.revisionChanged(de))
      {
        timer.restart();
      }
    }

    checkSearch();
    checkActions();
  }

  private void checkActions()
  {
    if (saveButton.isEnabled() != isDocumentChanged())
    {
      saveButton.setEnabled(isDocumentChanged());
    }

    diffPanel.checkActions();
  }

  boolean isDocumentChanged()
  {
    return bufferDocument != null ? bufferDocument.isChanged() : false;
  }

  private ActionListener refresh()
  {
    return new ActionListener()
    {
      public void actionPerformed(ActionEvent ae)
      {
        diffPanel.diff();
      }
    };
  }

  public FocusListener getFocusListener()
  {
    return new FocusAdapter()
    {
      @Override
      public void focusGained(FocusEvent fe)
      {
        diffPanel.setSelectedPanel(FilePanel.this);
      }
    };
  }

  public CaretListener getCaretListener()
  {
    return new CaretListener()
    {
      public void caretUpdate(CaretEvent fe)
      {
        updateFilePanelBar();
      }
    };
  }

  public void setSelected(boolean selected)
  {
    this.selected = selected;
    updateFilePanelBar();
    checkSearch();
  }

  private void updateFilePanelBar()
  {
    if (filePanelBar != null)
    {
      filePanelBar.update();
    }
  }

  public boolean isSelected()
  {
    return selected;
  }

  public void configurationChanged()
  {
    initConfiguration();
  }

  private void initConfiguration()
  {
    JMeldSettings c;
    boolean readonly;
    Font font;
    FontMetrics fm;

    c = getConfiguration();

    setShowLineNumbers(c.getEditor().getShowLineNumbers());

    font = c.getEditor().isCustomFontEnabled() ? c.getEditor().getFont() : null;
    font = font != null ? font : FontUtil.defaultTextAreaFont;
    editor.setFont(font);
    fm = editor.getFontMetrics(font);
    scrollPane.getHorizontalScrollBar().setUnitIncrement(fm.getHeight());

    getEditor().setTabSize(c.getEditor().getTabSize());

    readonly = false;
    if (position == BufferDiffPanel.LEFT)
    {
      readonly = c.getEditor().getLeftsideReadonly();
    }
    else if (position == BufferDiffPanel.RIGHT)
    {
      readonly = c.getEditor().getRightsideReadonly();
    }

    if (bufferDocument != null && bufferDocument.isReadonly())
    {
      readonly = true;
    }

    editor.setEditable(!readonly);
  }

  private JMeldSettings getConfiguration()
  {
    return JMeldSettings.getInstance();
  }

  public String getSelectedText()
  {
    return editor.getSelectedText();
  }
}
