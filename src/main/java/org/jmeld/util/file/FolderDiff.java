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
package org.jmeld.util.file;

import org.jmeld.util.node.*;

import java.util.*;

public abstract class FolderDiff
{
  public enum Mode
  {
    ONE_WAY,
    TWO_WAY;
  }
  private String rightFolderShortName;
  private String leftFolderShortName;
  private String rightFolderName;
  private String leftFolderName;
  private Mode mode;

  public FolderDiff(Mode mode)
  {
    this.mode = mode;
  }

  protected void setLeftFolderShortName(String leftFolderShortName)
  {
    this.leftFolderShortName = leftFolderShortName;
  }

  public String getLeftFolderShortName()
  {
    return leftFolderShortName;
  }

  protected void setRightFolderShortName(String rightFolderShortName)
  {
    this.rightFolderShortName = rightFolderShortName;
  }

  public String getRightFolderShortName()
  {
    return rightFolderShortName;
  }

  protected void setLeftFolderName(String leftFolderName)
  {
    this.leftFolderName = leftFolderName;
  }

  public String getLeftFolderName()
  {
    return leftFolderName;
  }

  protected void setRightFolderName(String rightFolderName)
  {
    this.rightFolderName = rightFolderName;
  }

  public String getRightFolderName()
  {
    return rightFolderName;
  }

  public abstract JMDiffNode getRootNode();

  public abstract Collection<JMDiffNode> getNodes();

  public void refresh()
  {
    diff();
  }

  public abstract void diff();
}
