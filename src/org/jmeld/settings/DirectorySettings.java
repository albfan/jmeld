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
package org.jmeld.settings;

import org.jmeld.ui.util.*;
import org.jmeld.util.conf.*;

import javax.xml.bind.annotation.*;

import java.awt.*;

@XmlAccessorType(XmlAccessType.NONE)
public class DirectorySettings
       extends AbstractConfigurationElement
{
  public enum DirectoryView
  {
    fileView("File view"),
    directoryView("Directory view"),
    packageView("Package view");

    // instance variables:
    private String text;

    private DirectoryView(String text)
    {
      this.text = text;
    }

    public String toString()
    {
      return text;
    }
  }

  // Instance variables:
  @XmlElement
  private DirectoryView            view = DirectoryView.packageView;
  @XmlElement
  private boolean                  onlyLeft = true;
  @XmlElement
  private boolean                  leftRightChanged = true;
  @XmlElement
  private boolean                  onlyRight = false;
  @XmlElement
  private boolean                  leftRightUnChanged = false;

  public DirectorySettings()
  {
  }

  public DirectoryView getView()
  {
    return view;
  }

  public void setView(DirectoryView view)
  {
    this.view = view;
    fireChanged();
  }

  public void setOnlyLeft(boolean onlyLeft)
  {
    this.onlyLeft = onlyLeft;
  }

  public boolean getOnlyLeft()
  {
    return onlyLeft;
  }

  public void setLeftRightChanged(boolean leftRightChanged)
  {
    this.leftRightChanged = leftRightChanged;
  }

  public boolean getLeftRightChanged()
  {
    return leftRightChanged;
  }

  public void setOnlyRight(boolean onlyRight)
  {
    this.onlyRight = onlyRight;
  }

  public boolean getOnlyRight()
  {
    return onlyRight;
  }

  public void setLeftRightUnChanged(boolean leftRightUnChanged)
  {
    this.leftRightUnChanged = leftRightUnChanged;
  }

  public boolean getLeftRightUnChanged()
  {
    return leftRightUnChanged;
  }
}
