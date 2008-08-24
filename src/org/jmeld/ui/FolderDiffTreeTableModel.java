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

import org.jmeld.ui.swing.table.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.node.*;

import javax.swing.*;


public class FolderDiffTreeTableModel
       extends JMTreeTableModel
{
  private Column fileNameColumn;
  private Column leftSizeColumn;
  private Column leftStateColumn;
  private Column rightStateColumn;
  private Column rightSizeColumn;

  public FolderDiffTreeTableModel()
  {
    fileNameColumn = addColumn("fileName", null, "File", null, -1, false, null);
    leftSizeColumn = addColumn("leftSize", "Left", "Size", Integer.class, 8,
        false, Colors.TABLEROW_LEFT);
    leftStateColumn = addColumn("leftState", "Left", "L", Icon.class, 3,
        false, Colors.TABLEROW_LEFT);
    rightStateColumn = addColumn("rightState", "Right", "R", Icon.class, 3,
        false, Colors.TABLEROW_RIGHT);
    rightSizeColumn = addColumn("rightSize", "Right", "Size", Integer.class,
        8, false, Colors.TABLEROW_RIGHT);
  }

  public Object getValueAt(
    Object objectNode,
    Column column)
  {
    UINode     uiNode;
    JMDiffNode diffNode;
    BufferNode bufferNode;

    uiNode = (UINode) objectNode;
    diffNode = uiNode.getDiffNode();

    if (column == fileNameColumn)
    {
      return uiNode.toString();
    }

    if (column == leftStateColumn)
    {
      return ImageUtil.getSmallImageIcon(getLeftStateIconName(diffNode));
    }

    if (column == leftSizeColumn)
    {
      if (diffNode == null)
      {
        return "";
      }

      bufferNode = diffNode.getBufferNodeLeft();
      if (bufferNode == null)
      {
        return "";
      }

      return bufferNode.getSize();
    }

    if (column == rightStateColumn)
    {
      return ImageUtil.getSmallImageIcon(getRightStateIconName(diffNode));
    }

    if (column == rightSizeColumn)
    {
      if (diffNode == null)
      {
        return "";
      }

      bufferNode = diffNode.getBufferNodeRight();
      if (bufferNode == null)
      {
        return "";
      }

      return bufferNode.getSize();
    }

    return null;
  }

  public void setValueAt(
    Object value,
    Object objectNode,
    Column column)
  {
  }

  private String getLeftStateIconName(JMDiffNode diffNode)
  {
    if (diffNode != null)
    {
      if (diffNode.isCompareEqual(JMDiffNode.Compare.NotEqual))
      {
        return "stock_changed2";
      }

      if (diffNode.isCompareEqual(JMDiffNode.Compare.LeftMissing) ||
          diffNode.isCompareEqual(JMDiffNode.Compare.BothMissing))
      {
        return "stock_deleted3";
      }
    }

    return "stock_equal";
  }

  private String getRightStateIconName(JMDiffNode diffNode)
  {
    if (diffNode != null)
    {
      if (diffNode.isCompareEqual(JMDiffNode.Compare.NotEqual))
      {
        return "stock_changed2";
      }

      if (diffNode.isCompareEqual(JMDiffNode.Compare.RightMissing) ||
          diffNode.isCompareEqual(JMDiffNode.Compare.BothMissing))
      {
        return "stock_deleted3";
      }
    }

    return "stock_equal";
  }
}
