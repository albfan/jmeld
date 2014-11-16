package org.jmeld.ui.tree;

import org.jmeld.diff.JMChunk;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by alberto on 16/11/14.
 */
class JMChunkNode extends DefaultMutableTreeNode {
    private JMChunk chunk;
    private String string;

    public JMChunkNode(JMChunk chunk, String string) {
        this.chunk = chunk;
        this.string = string;
    }

    public JMChunk getChunk() {
        return chunk;
    }

    public String getString() {
        return string;
    }
}
