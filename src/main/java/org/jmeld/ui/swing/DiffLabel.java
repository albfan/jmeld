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
package org.jmeld.ui.swing;

import org.jmeld.diff.JMChunk;
import org.jmeld.diff.JMDelta;
import org.jmeld.diff.JMDiff;
import org.jmeld.diff.JMRevision;
import org.jmeld.util.Ignore;
import org.jmeld.util.TokenizerFactory;
import org.jmeld.util.WordTokenizer;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.List;

public class DiffLabel
    extends JTextPane
{
  public DiffLabel()
  {
    init();
  }

  public void init()
  {
    Style s;
    Style defaultStyle;
    StyledDocument doc;

    setEditable(false);
    setOpaque(false);
    // Bug in Nimbus L&F doesn't honour the opaqueness of a JLabel.
    // Setting a fully transparent color is a workaround:
    setBackground(new Color(0, 0, 0, 0));
    setBorder(null);

    defaultStyle = getStyle(StyleContext.DEFAULT_STYLE);

    doc = getStyledDocument();
    s = doc.addStyle("bold", defaultStyle);
    StyleConstants.setBold(s, true);
  }

  /** Set the text on this label.
   *  Some parts of the text will be displayed in bold-style.
   *  These parts are the differences between text and otherText.
   */
  public void setText(String text, String otherText)
  {
    WordTokenizer wt;
    List<String> textList;
    List<String> otherTextList;
    JMRevision revision;
    JTextPane fl;
    String[] styles;
    JMChunk chunk;
    String styleName;
    StyledDocument doc;

    try
    {
      wt = TokenizerFactory.getFileNameTokenizer();
      textList = wt.getTokens(text);
      otherTextList = wt.getTokens(otherText);

      styles = new String[textList.size()];

      if (otherTextList.size() != 0)
      {
        revision = new JMDiff().diff(textList, otherTextList,
          Ignore.NULL_IGNORE);

        for (JMDelta delta : revision.getDeltas())
        {
          chunk = delta.getOriginal();
          for (int i = 0; i < chunk.getSize(); i++)
          {
            styles[chunk.getAnchor() + i] = "bold";
          }
        }
      }

      doc = getStyledDocument();
      doc.remove(0, doc.getLength());

      for (int i = 0; i < textList.size(); i++)
      {
        doc.insertString(doc.getLength(), textList.get(i),
          (styles[i] != null ? doc.getStyle(styles[i]) : null));
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();

      // Make the best out of this situation. (Should never happen)
      setText(text);
    }
  }
}
