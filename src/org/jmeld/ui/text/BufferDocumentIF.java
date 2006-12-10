package org.jmeld.ui.text;

import org.jmeld.*;

import javax.swing.text.*;

public interface BufferDocumentIF
{
  // class variables:
  public static String ORIGINAL = "Original";
  public static String REVISED = "Revised";

  public String getName();

  public String getShortName();

  public void addChangeListener(BufferDocumentChangeListenerIF listener);

  public void removeChangeListener(BufferDocumentChangeListenerIF listener);

  public boolean isChanged();

  public PlainDocument getDocument();

  public AbstractBufferDocument.Line[] getLines();

  public String getLineText(int lineNumber);

  public int getOffsetForLine(int lineNumber);

  public int getLineForOffset(int offset);

  public void read()
    throws JMeldException;

  public void initLines();

  public void write()
    throws JMeldException;

  public void print();
}
