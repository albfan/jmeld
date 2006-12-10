package org.jmeld.ui;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

public class ListScrollSynchronizer
       implements ChangeListener
{
  private JViewport vp1;
  private JViewport vp2;

  public ListScrollSynchronizer(
    JScrollPane sc1,
    JScrollPane sc2)
  {
    vp1 = sc1.getViewport();
    vp2 = sc2.getViewport();

    vp1.addChangeListener(this);
    vp2.addChangeListener(this);
  }

  public void stateChanged(ChangeEvent event)
  {
    JViewport fromViewport;
    JViewport toViewport;

    fromViewport = event.getSource() == vp1 ? vp1 : vp2;
    toViewport = fromViewport == vp1 ? vp2 : vp1;

    toViewport.removeChangeListener(this);
    toViewport.setViewPosition(fromViewport.getViewPosition());
    toViewport.addChangeListener(this);
  }
}
