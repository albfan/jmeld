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
package org.jmeld.ui.text;

import org.jmeld.JMeldException;
import org.jmeld.vc.BlameIF;

import javax.swing.text.PlainDocument;
import java.io.Reader;

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

  public BlameIF getVersionControlBlame();

  public AbstractBufferDocument.Line[] getLines();

  public String getLineText(int lineNumber);

  public int getNumberOfLines();

  public int getOffsetForLine(int lineNumber);

  public int getLineForOffset(int offset);

  public void read()
      throws JMeldException;

  public void write()
      throws JMeldException;

  public void print();

  public Reader getReader()
      throws JMeldException;

  public boolean isReadonly();
}
