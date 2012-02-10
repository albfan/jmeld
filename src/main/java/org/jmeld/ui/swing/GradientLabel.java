package org.jmeld.ui.swing;

import org.jmeld.ui.util.Colors;

import javax.swing.*;
import java.awt.*;

public class GradientLabel
    extends JLabel
{
  private Color fromColor;
  private Color toColor;

  public GradientLabel(String text)
  {
    super(text);

    initialize();
  }

  private void initialize()
  {
    setOpaque(false);
    setGradientColor(Colors.getDarkLookAndFeelColor());
    setForeground(Color.white);
  }

  public GradientLabel()
  {
    super();

    initialize();
  }

  public void setGradientColor(Color fromColor)
  {
    setGradientColor(fromColor, new Color(fromColor.getRed(), fromColor
        .getGreen(), fromColor.getBlue(), 0));
  }

  public void setGradientColor(Color fromColor, Color toColor)
  {
    this.fromColor = fromColor;
    this.toColor = toColor;
  }

  public Color getFromColor()
  {
    return fromColor;
  }

  public Color getToColor()
  {
    return toColor;
  }

  public void paint(Graphics g)
  {
    Rectangle r;
    GradientPaint paint;
    Graphics2D g2;

    g2 = (Graphics2D) g;

    r = getBounds();

    paint = new GradientPaint(0, 0, fromColor, (int) (r.width / 1.10),
        r.height, toColor);

    g2.setPaint(paint);
    g2.fillRect(0, 0, r.width, r.height);

    super.paint(g);
  }
}
