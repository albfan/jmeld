package org.jmeld.ui.command;

import org.jmeld.diff.*;
import org.jmeld.ui.*;
import org.jmeld.ui.text.*;

import javax.swing.*;
import javax.swing.text.*;

public class ChangeCommand
       implements Runnable
{
  private BufferDiffPanel diffPanel;
  private FilePanel       filePanelOriginal;
  private FilePanel       filePanelRevised;
  private boolean         originalToRevised;
  private JMDelta         delta;

  public ChangeCommand(
    BufferDiffPanel diffPanel,
    FilePanel       filePanelOriginal,
    FilePanel       filePanelRevised,
    boolean         originalToRevised,
    JMDelta         delta)
  {
    this.diffPanel = diffPanel;
    this.filePanelOriginal = filePanelOriginal;
    this.filePanelRevised = filePanelRevised;
    this.originalToRevised = originalToRevised;
    this.delta = delta;
  }

  public void run()
  {
    BufferDocumentIF fromBufferDocument;
    BufferDocumentIF toBufferDocument;
    PlainDocument    from;
    PlainDocument    to;
    String           s;
    int              fromLine;
    int              toLine;
    int              fromOffset;
    int              toOffset;
    int              size;
    JMChunk          fromChunk;
    JMChunk          toChunk;
    JTextComponent   toEditor;

    try
    {
      if (originalToRevised)
      {
        fromBufferDocument = filePanelOriginal.getBufferDocument();
        toBufferDocument = filePanelRevised.getBufferDocument();
        fromChunk = delta.getOriginal();
        toChunk = delta.getRevised();
        toEditor = filePanelRevised.getEditor();
      }
      else
      {
        fromBufferDocument = filePanelRevised.getBufferDocument();
        toBufferDocument = filePanelOriginal.getBufferDocument();
        fromChunk = delta.getRevised();
        toChunk = delta.getOriginal();
        toEditor = filePanelOriginal.getEditor();
      }

      if (fromBufferDocument == null || toBufferDocument == null)
      {
        return;
      }

      from = fromBufferDocument.getDocument();
      to = toBufferDocument.getDocument();

      fromLine = fromChunk.getAnchor();
      size = fromChunk.getSize();
      fromOffset = fromBufferDocument.getOffsetForLine(fromLine);
      toOffset = fromBufferDocument.getOffsetForLine(fromLine + size);

      s = from.getText(fromOffset, toOffset - fromOffset);

      fromLine = toChunk.getAnchor();
      size = toChunk.getSize();
      fromOffset = toBufferDocument.getOffsetForLine(fromLine);
      toOffset = toBufferDocument.getOffsetForLine(fromLine + size);

      diffPanel.getUndoHandler().start("replace");
      toEditor.setSelectionStart(fromOffset);
      toEditor.setSelectionEnd(toOffset);
      toEditor.replaceSelection(s);
      diffPanel.getUndoHandler().end("replace");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
