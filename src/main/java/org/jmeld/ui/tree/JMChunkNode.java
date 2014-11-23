package org.jmeld.ui.tree;

import org.jmeld.diff.JMChunk;
import org.jmeld.util.StringUtil;

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
        add(new DefaultMutableTreeNode("anchor: "+chunk.getAnchor()));
        add(new DefaultMutableTreeNode("size: "+chunk.getSize()));
    }

    public JMChunk getChunk() {
        return chunk;
    }

    public String getString() {
        return StringUtil.replaceNewLines(string);
    }
}

