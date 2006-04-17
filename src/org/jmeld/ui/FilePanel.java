package org.jmeld.ui;

import org.apache.commons.jrcs.diff.*;
import org.jmeld.*;
import org.jmeld.ui.text.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.prefs.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class FilePanel
       implements BufferDocumentChangeListenerIF
{
  // Class variables:
  private static final int MAXSIZE_CHANGE_DIFF = 1000;

  // Instance variables:
  private BufferDiffPanel  diffPanel;
  private String           name;
  private JLabel           fileLabel;
  private JButton          browseButton;
  private JComboBox        fileBox;
  private JScrollPane      scrollPane;
  private JTextComponent   editor;
  private BufferDocumentIF bufferDocument;
  private JButton          saveButton;
  private Timer            timer;

  FilePanel(BufferDiffPanel diffPanel, String name)
  {
    this.diffPanel = diffPanel;
    this.name = name;

    init();
  }

  private void init()
  {
    Font        font;
    FontMetrics fm;
    ImageIcon   icon;

    font = new Font("monospaced", Font.PLAIN, 14);

    editor = new JTextArea();
    editor.setDragEnabled(true);
    editor.setFont(font);
    editor.setSelectedTextColor(Color.red);
    fm = editor.getFontMetrics(font);

    scrollPane = new JScrollPane(editor);
    scrollPane.getHorizontalScrollBar().setUnitIncrement(fm.getHeight());
    if (BufferDocumentIF.ORIGINAL.equals(name))
    {
      scrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    }

    browseButton = new JButton("Browse...");
    browseButton.addActionListener(getBrowseButtonAction());

    fileBox = new JComboBox();
    fileBox.addActionListener(getFileBoxAction());

    fileLabel = new JLabel();

    saveButton = new JButton();
    saveButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    saveButton.setContentAreaFilled(false);
    icon = ImageUtil.getSmallImageIcon("stock_save");
    saveButton.setIcon(icon);
    saveButton.setDisabledIcon(ImageUtil.createTransparentIcon(icon));
    saveButton.addActionListener(getSaveButtonAction());

    timer = new Timer(500, refresh());
    timer.setRepeats(false);
  }

  JButton getBrowseButton()
  {
    return browseButton;
  }

  JComboBox getFileBox()
  {
    return fileBox;
  }

  JLabel getFileLabel()
  {
    return fileLabel;
  }

  JScrollPane getScrollPane()
  {
    return scrollPane;
  }

  JTextComponent getEditor()
  {
    return editor;
  }

  BufferDocumentIF getBufferDocument()
  {
    return bufferDocument;
  }

  JButton getSaveButton()
  {
    return saveButton;
  }

  private void setFile(File file)
  {
    BufferDocumentIF bd;

    try
    {
      bd = new FileDocument(file);
      bd.read();

      setBufferDocument(bd);
      checkActions();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public void setBufferDocument(BufferDocumentIF bd)
  {
    Document previousDocument;
    Document document;
    String   fileName;
    String   text;

    try
    {
      if (bufferDocument != null)
      {
        bufferDocument.removeChangeListener(this);

        previousDocument = bufferDocument.getDocument();
        if (previousDocument != null)
        {
          previousDocument.removeUndoableEditListener(diffPanel.getUndoHandler());
        }
      }

      bufferDocument = bd;

      document = bufferDocument.getDocument();
      editor.setDocument(document);
      bufferDocument.addChangeListener(this);
      document.addUndoableEditListener(diffPanel.getUndoHandler());

      fileName = bufferDocument.getName();
      fileBox.addItem(fileName);
      fileBox.setSelectedItem(fileName);

      text = fileName;
      fileLabel.setText(text);

      checkActions();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();

      JOptionPane.showMessageDialog(diffPanel,
        "Could not read file: " + bufferDocument.getName() + "\n"
        + ex.getMessage(), "Error opening file", JOptionPane.ERROR_MESSAGE);
      return;
    }
  }

  public void setRevision(Revision revision)
  {
    Delta    delta;
    Chunk    original;
    Chunk    revised;
    int      fromOffset;
    int      toOffset;
    int      fromOffset2;
    int      toOffset2;
    Revision changeRev;
    Delta    changeDelta;
    Chunk    changeOriginal;
    Chunk    changeRevised;

    if (bufferDocument == null)
    {
      return;
    }

    removeHighlights(editor);

    for (int i = 0; i < revision.size(); i++)
    {
      delta = revision.getDelta(i);
      original = delta.getOriginal();
      revised = delta.getRevised();

      if (BufferDocumentIF.ORIGINAL.equals(name))
      {
        fromOffset = bufferDocument.getOffsetForLine(original.anchor());
        toOffset = bufferDocument.getOffsetForLine(original.anchor()
            + original.size());

        if (delta instanceof AddDelta)
        {
          setHighlight(fromOffset, fromOffset + 1, DiffHighlighter.ADDED_LINE);
        }
        else if (delta instanceof DeleteDelta)
        {
          setHighlight(fromOffset, toOffset, DiffHighlighter.DELETED);
        }
        else if (delta instanceof ChangeDelta)
        {
          // Mark the changes in a change in a different color.
          if (original.size() < MAXSIZE_CHANGE_DIFF
            && revised.size() < MAXSIZE_CHANGE_DIFF)
          {
            changeRev = getChangeRevision(original.toString(),
                revised.toString());
            if (changeRev != null)
            {
              for (int j = 0; j < changeRev.size(); j++)
              {
                changeDelta = changeRev.getDelta(j);
                changeOriginal = changeDelta.getOriginal();
                fromOffset2 = fromOffset + changeOriginal.anchor();
                toOffset2 = fromOffset2 + changeOriginal.size();

                if (changeDelta instanceof DeleteDelta)
                {
                  setHighlight(fromOffset2, toOffset2, DiffHighlighter.CHANGED2);
                }
                else if (changeDelta instanceof ChangeDelta)
                {
                  setHighlight(fromOffset2, toOffset2, DiffHighlighter.CHANGED2);
                }
              }
            }
          }

          // First color the changes in changes and after that the entire change
          //   (It seems that you can only color a range once!)
          setHighlight(fromOffset, toOffset, DiffHighlighter.CHANGED);
        }
      }
      else if (BufferDocumentIF.REVISED.equals(name))
      {
        fromOffset = bufferDocument.getOffsetForLine(revised.anchor());
        toOffset = bufferDocument.getOffsetForLine(revised.anchor()
            + revised.size());

        if (delta instanceof AddDelta)
        {
          setHighlight(fromOffset, toOffset, DiffHighlighter.ADDED);
        }
        else if (delta instanceof DeleteDelta)
        {
          setHighlight(fromOffset, fromOffset + 1, DiffHighlighter.DELETED_LINE);
        }
        else if (delta instanceof ChangeDelta)
        {
          if (original.size() < MAXSIZE_CHANGE_DIFF
            && revised.size() < MAXSIZE_CHANGE_DIFF)
          {
            changeRev = getChangeRevision(original.toString(),
                revised.toString());
            if (changeRev != null)
            {
              for (int j = 0; j < changeRev.size(); j++)
              {
                changeDelta = changeRev.getDelta(j);
                changeRevised = changeDelta.getRevised();
                fromOffset2 = fromOffset + changeRevised.anchor();
                toOffset2 = fromOffset2 + changeRevised.size();

                if (changeDelta instanceof AddDelta)
                {
                  setHighlight(fromOffset2, toOffset2, DiffHighlighter.CHANGED2);
                }
                else if (changeDelta instanceof ChangeDelta)
                {
                  setHighlight(fromOffset2, toOffset2, DiffHighlighter.CHANGED2);
                }
              }
            }
          }

          setHighlight(fromOffset, toOffset, DiffHighlighter.CHANGED);
        }
      }
    }
  }

  public void removeHighlights(JTextComponent textComp)
  {
    Highlighter             hilite;
    Highlighter.Highlight[] hilites;

    // Don't remove highlights which have not been added by some diff!
    //   (for instance: the highlights made by selecting text)
    hilite = textComp.getHighlighter();
    hilites = hilite.getHighlights();

    for (int i = 0; i < hilites.length; i++)
    {
      if (hilites[i].getPainter() instanceof org.jmeld.ui.DiffHighlighter)
      {
        hilite.removeHighlight(hilites[i]);
      }
    }
  }

  private Revision getChangeRevision(String original, String revised)
  {
    Diff        diff;
    char[]      original1;
    Character[] original2;
    char[]      revised1;
    Character[] revised2;

    original1 = original.toString().toCharArray();
    original2 = new Character[original1.length];
    for (int j = 0; j < original1.length; j++)
    {
      original2[j] = new Character(original1[j]);
    }

    revised1 = revised.toString().toCharArray();
    revised2 = new Character[revised1.length];
    for (int j = 0; j < revised1.length; j++)
    {
      revised2[j] = new Character(revised1[j]);
    }

    try
    {
      diff = new Diff(original2);
      return diff.diff(revised2);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    return null;
  }

  private void setHighlight(int offset, int size,
    Highlighter.HighlightPainter highlight)
  {
    try
    {
      editor.getHighlighter().addHighlight(offset, size, highlight);
    }
    catch (BadLocationException ex)
    {
      ex.printStackTrace();
    }
  }

  public ActionListener getBrowseButtonAction()
  {
    return new ActionListener()
      {
        public void actionPerformed(ActionEvent ae)
        {
          FileChooserPreference pref;
          JFileChooser          chooser;
          int                   result;
          File                  file;

          chooser = new JFileChooser();
          pref = new FileChooserPreference("Browse", chooser);
          result = chooser.showOpenDialog(diffPanel);

          if (result == JFileChooser.APPROVE_OPTION)
          {
            pref.save();
            setFile(chooser.getSelectedFile());
          }
        }
      };
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
              + ex.getMessage(), "Error saving file", JOptionPane.ERROR_MESSAGE);
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

  public void documentChanged()
  {
    timer.restart();
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

  public ActionListener refresh()
  {
    return new ActionListener()
      {
        public void actionPerformed(ActionEvent ae)
        {
          bufferDocument.initLines();
          diffPanel.diff();
        }
      };
  }
}
