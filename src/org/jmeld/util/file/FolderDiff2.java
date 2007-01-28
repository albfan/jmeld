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

public abstract class FolderDiff2
{
  private String mineFolderShortName;
  private String originalFolderShortName;
  private String mineFolderName;
  private String originalFolderName;

  public FolderDiff2()
  {
  }

  protected void setOriginalFolderShortName(String originalFolderShortName)
  {
    this.originalFolderShortName = originalFolderShortName;
  }

  public String getOriginalFolderShortName()
  {
    return originalFolderShortName;
  }

  protected void setMineFolderShortName(String mineFolderShortName)
  {
    this.mineFolderShortName = mineFolderShortName;
  }

  public String getMineFolderShortName()
  {
    return mineFolderShortName;
  }

  protected void setOriginalFolderName(String originalFolderName)
  {
    this.originalFolderName = originalFolderName;
  }

  public String getOriginalFolderName()
  {
    return originalFolderName;
  }

  protected void setMineFolderName(String mineFolderName)
  {
    this.mineFolderName = mineFolderName;
  }

  public String getMineFolderName()
  {
    return mineFolderName;
  }

  public abstract DiffNode getRootNode();

  public abstract void diff();
}
