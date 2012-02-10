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
package org.jmeld.ui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ListScrollSynchronizer
    implements ChangeListener
{
  private JViewport vp1;
  private JViewport vp2;

  public ListScrollSynchronizer(JScrollPane sc1, JScrollPane sc2)
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
