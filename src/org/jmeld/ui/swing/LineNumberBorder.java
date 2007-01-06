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

    clip = g.getClipRect();
    lineHeight = textArea.getFontMetrics(textArea.getFont()).getHeight();

    heightCorrection = (lineHeight - fontHeight) / 2;

    try
    {
      startLine = textArea.getLineOfOffset(startOffset);
      endLine = textArea.getLineOfOffset(endOffset);
      y = clip.y;

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
