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

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;

public class LineNumberBorder
       extends EmptyBorder
{
  private JTextArea textArea;
  private Color     background;
  private Color     lineColor;
  private Font      font;
  private int       fontWidth;
  private int       fontHeight;

  public LineNumberBorder(JTextArea textArea)
  {
    super(0, 40, 0, 0);

    this.textArea = textArea;

    init();

  }

  private void init()
  {
    FontMetrics fm;

    background = new Color(233, 232, 226);
    lineColor = new Color(184, 184, 184);
    font = new Font("Monospaced", Font.PLAIN, 10);

    fm = textArea.getFontMetrics(font);
    fontWidth = fm.stringWidth("0");
    fontHeight = fm.getHeight();
  }

  public void paintBefore(Graphics g)
  {
    Rectangle clip;

    clip = g.getClipRect();

    g.setColor(new Color(233, 232, 226));
    g.fillRect(0, clip.y, left, clip.y + clip.height);
  }

  public void paintAfter(
    Graphics g,
    int      startOffset,
    int      endOffset)
  {
    Rectangle clip;
    int       startLine;
    int       endLine;
    int       y;
    int       lineHeight;
    String    s;
    int       heightCorrection;
    Rectangle r1;

    clip = g.getClipRect();

    try
    {
      startLine = textArea.getLineOfOffset(startOffset);
      endLine = textArea.getLineOfOffset(endOffset);
      r1 = textArea.modelToView(startOffset);
      y = r1.y;
      lineHeight = r1.height;
      heightCorrection = (lineHeight - fontHeight) / 2;

      g.setColor(lineColor);
      g.drawLine(left, clip.y, left, clip.y + clip.height);

      g.setFont(font);
      g.setColor(Color.black);
      for (int line = startLine; line <= endLine; line++)
      {
        s = Integer.toString(line);
        g.drawString(s, left - (fontWidth * s.length()) - 1,
          y - heightCorrection);
        y += lineHeight;
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
