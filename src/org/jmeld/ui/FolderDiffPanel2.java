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

import org.jdesktop.swingx.treetable.*;
import org.jmeld.util.file.*;
import org.jmeld.util.node.*;

public class FolderDiffPanel2
       extends FolderDiffForm
{
  private JMeldPanel  mainPanel;
  private FolderDiff2 diff;

  FolderDiffPanel2(
    JMeldPanel  mainPanel,
    FolderDiff2 diff)
  {
    this.mainPanel = mainPanel;
    this.diff = diff;

    init();
  }

  private void init()
  {
    folder1Label.init();
    folder1Label.setText(
      diff.getOriginalFolderName(),
      diff.getMineFolderName());

    folder2Label.init();
    folder2Label.setText(
      diff.getMineFolderName(),
      diff.getOriginalFolderName());

    folderTreeTable.setTreeTableModel(getModel());
  }

  public String getTitle()
  {
    return diff.getOriginalFolderShortName() + " - "
    + diff.getMineFolderShortName();
  }

  public DefaultTreeTableModel getModel()
  {
    return new DirectoryDiffTreeTableModel(diff.getRootNode());
  }

  class DirectoryDiffTreeTableModel
         extends DefaultTreeTableModel
  {
    DirectoryDiffTreeTableModel(DiffNode rootNode)
    {
      super(rootNode);
    }

    public Object getChild(
      Object parent,
      int    index)
    {
      return ((DiffNode) parent).getChildAt(index);
    }

    public int getChildCount(Object parent)
    {
      return ((DiffNode) parent).getChildCount();
    }

    public int getColumnCount()
    {
      return 1;
    }

    public String getColumnName(int column)
    {
      switch (column)
      {

        case 0:
          return "Name";
      }

      return "??";
    }

    public Object getValueAt(
      Object node,
      int    column)
    {
      switch (column)
      {

        case 0:
          return ((DiffNode) node).getShortName();
      }

      return null;
    }
  }
}
