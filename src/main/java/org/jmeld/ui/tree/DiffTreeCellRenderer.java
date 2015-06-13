package org.jmeld.ui.tree;

import org.jmeld.diff.JMChunk;
import org.jmeld.diff.JMDelta;
import org.jmeld.diff.JMRevision;
import org.jmeld.diff.TypeDiff;
import org.jmeld.ui.util.RevisionUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
* Created by alberto on 16/11/14.
*/
class DiffTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final Color COLOR_CHUNK = new Color(217, 154, 13);
    private static final Color COLOR_CHANGE = new Color(237, 38, 139);
    private static final Color COLOR_DELTA = new Color(209, 70, 237);
    private static final Color COLOR_REVISION = new Color(39, 86, 189);

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();
        if (userObject instanceof JMRevision) {
            setIcon(new TreeColorIcon(COLOR_REVISION));
            JMRevision revision = (JMRevision) userObject;
            int numChanges = revision.getDeltas().size();
            setText(String.format("%d changes, %s", numChanges, revision.getIgnore()));
        } else if (userObject instanceof JMChange) {
            JMChange JMChange = (JMChange) userObject;
            setIcon(new TreeColorIcon(COLOR_CHANGE));
            setText(JMChange.toString());
        } else if (userObject instanceof JMDelta) {
            JMDelta delta = (JMDelta) userObject;
            setIcon(new TreeColorIcon(RevisionUtil.getColor(delta)));
            TypeDiff type = delta.getType();
            setText(type.toString());
        } else if (value instanceof JMChunkNode) {
            setIcon(new TreeColorIcon(COLOR_CHUNK));
            JMChunkNode chunkNode = (JMChunkNode) value;
            JMChunk chunk = chunkNode.getChunk();
            setText(String.format("%d, %d: \"%s\"", chunk.getAnchor(), chunk.getSize(), chunkNode.getString()));
        }
        return this;
    }
}
