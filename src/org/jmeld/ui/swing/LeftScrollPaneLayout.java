package org.jmeld.ui.swing;

import javax.swing.*;

import java.awt.*;

public class LeftScrollPaneLayout
       extends javax.swing.ScrollPaneLayout
{
  public void layoutContainer(Container parent)
  {
    ComponentOrientation originalOrientation;

    // Dirty trick to get the vertical scrollbar to the left side of
    //  a scrollpane.
    originalOrientation = parent.getComponentOrientation();
    parent.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    super.layoutContainer(parent);
    parent.setComponentOrientation(originalOrientation);
  }
}
