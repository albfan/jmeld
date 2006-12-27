package org.jmeld.ui.util;

import javax.swing.*;

import java.awt.*;

public class WaitCursor
{
  private WaitCursor()
  {
  }

  public static void wait(Component c)
  {
    JFrame    frame;
    Component pane;

    frame = (JFrame) SwingUtilities.getRoot(c);

    if (frame != null)
    {
      pane = frame.getRootPane().getGlassPane();

      //pane.setVisible(true);
      //pane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
  }

  public static void resume(Component c)
  {
    JFrame    frame;
    Component pane;

    frame = (JFrame) SwingUtilities.getRoot(c);

    if (frame != null)
    {
      pane = frame.getRootPane().getGlassPane();

      //pane.setVisible(false);
      //pane.setCursor(null);
    }
  }
}
