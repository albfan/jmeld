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

import org.jmeld.ui.swing.table.JMTreeTableModel;
import org.jmeld.ui.util.ImageUtil;
import org.jmeld.util.node.JMDiffNode;
import org.jmeld.util.node.VersionControlBaseNode;

import javax.swing.*;

public class VersionControlTreeTableModel
        extends JMTreeTableModel
{
    private Column fileNameColumn;
    private Column statusColumn;

    public VersionControlTreeTableModel() {
        fileNameColumn = addColumn("fileName", null, "File", null, -1, false);
        statusColumn = addColumn("status", "Left", "Status", Icon.class, 12, false);
    }

    public Object getValueAt(Object objectNode, Column column) {
        UINode uiNode;
        JMDiffNode diffNode;
        VersionControlBaseNode vcbNode;

        uiNode = (UINode) objectNode;
        diffNode = uiNode.getDiffNode();

        if (column == fileNameColumn) {
            return uiNode.toString();
        }

        if (column == statusColumn) {
            vcbNode = (VersionControlBaseNode) diffNode.getBufferNodeLeft();
            if(vcbNode == null) {
                return "";
            }

            return ImageUtil.getImageIcon("16x16/" + vcbNode.getEntry().getStatus().getIconName());
        }

        return null;
    }
}
