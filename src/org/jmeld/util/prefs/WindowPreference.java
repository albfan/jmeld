package org.jmeld.util.prefs;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class WindowPreference
       extends Preference
{
  // Class variables:
  private static String X = "X";
  private static String Y = "Y";
  private static String WIDTH = "WIDTH";
  private static String HEIGHT = "HEIGHT";

  // Instance variables:
  private Window target;

  public WindowPreference(
    String preferenceName,
    Window target)
  {
    super("Window-" + preferenceName);

    this.target = target;
    init();
  }

  private void init()
  {
    target.setLocation(
      getInt(X, 0),
      getInt(Y, 0));
    target.setSize(
      getInt(WIDTH, 500),
      getInt(HEIGHT, 400));

    target.addWindowListener(getWindowListener());
  }

  private void save()
  {
    putInt(X, target.getLocation().x);
    putInt(Y, target.getLocation().y);
    putInt(WIDTH, target.getSize().width);
    putInt(HEIGHT, target.getSize().height);
  }

  private WindowListener getWindowListener()
  {
    return new WindowAdapter()
      {
        public void windowClosing(WindowEvent we)
        {
          save();
        }
      };
  }
}
