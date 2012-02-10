package org.jmeld.ui.text;

import javax.swing.event.DocumentEvent;

public class JMDocumentEvent
{
  private AbstractBufferDocument document;
  private DocumentEvent de;
  private int startLine;
  private int numberOfLines;

  public JMDocumentEvent(AbstractBufferDocument document)
  {
    this.document = document;
  }

  public JMDocumentEvent(AbstractBufferDocument document, DocumentEvent de)
  {
    this(document);

    this.de = de;
  }

  public AbstractBufferDocument getDocument()
  {
    return document;
  }

  public DocumentEvent getDocumentEvent()
  {
    return de;
  }

  public void setStartLine(int startLine)
  {
    this.startLine = startLine;
  }

  public int getStartLine()
  {
    return startLine;
  }

  public void setNumberOfLines(int numberOfLines)
  {
    this.numberOfLines = numberOfLines;
  }

  public int getNumberOfLines()
  {
    return numberOfLines;
  }
}
