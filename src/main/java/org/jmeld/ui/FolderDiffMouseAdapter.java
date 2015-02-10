package org.jmeld.ui;

import org.jmeld.JMeldException;
import org.jmeld.util.node.JMDiffNode;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
* Created by alberto on 11/01/15.
*/
class FolderDiffMouseAdapter extends MouseAdapter {
    private FolderDiffPanel folderDiffPanel;
    private int row;

    public FolderDiffMouseAdapter(FolderDiffPanel folderDiffPanel) {
        this.folderDiffPanel = folderDiffPanel;
        row = -1;
    }

    public int getRow() {
        return row;
    }

    @Override
    public void mouseClicked(MouseEvent me) {

        row = ((JTable) me.getSource()).rowAtPoint(me.getPoint());

        boolean background = me.getClickCount() == 1 && me.getButton() == MouseEvent.BUTTON2;
        boolean openInNewTab = me.getClickCount() == 2 || background;

        folderDiffPanel.openFileOnRow(row, background, openInNewTab);
    }
}
