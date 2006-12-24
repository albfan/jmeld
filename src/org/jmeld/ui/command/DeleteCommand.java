package org.jmeld.ui.command;

import org.jmeld.diff.*;
import org.jmeld.ui.*;
import org.jmeld.ui.text.*;

import javax.swing.*;
import javax.swing.text.*;

public class DeleteCommand
       implements Runnable
{
  private BufferDiffPanel diffPanel;
  private FilePanel       filePanelOriginal;
  private FilePanel       filePanelRevised;
  private boolean         originalToRevised;
  private JMDelta         delta;

  public DeleteCommand(
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
    BufferDocumentIF bufferDocument;
    PlainDocument    document;
    String           s;
    int              fromLine;
    int              fromOffset;
    int              toOffset;
    int              size;
    JMChunk          chunk;
    JTextComponent   toEditor;

    try
    {
      if (originalToRevised)
      {
        bufferDocument = filePanelOriginal.getBufferDocument();
        chunk = delta.getOriginal();
        toEditor = filePanelOriginal.getEditor();
      }
      else
      {
        bufferDocument = filePanelRevised.getBufferDocument();
        chunk = delta.getRevised();
        toEditor = filePanelRevised.getEditor();
      }

      if (bufferDocument == null)
      {
        return;
      }

      document = bufferDocument.getDocument();

      fromLine = chunk.getAnchor();
      size = chunk.getSize();
      fromOffset = bufferDocument.getOffsetForLine(fromLine);
      toOffset = bufferDocument.getOffsetForLine(fromLine + size);

      diffPanel.getUndoHandler().start("remove");
      toEditor.setSelectionStart(fromOffset);
      toEditor.setSelectionEnd(toOffset);
      toEditor.replaceSelection("");
      diffPanel.getUndoHandler().end("remove");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
