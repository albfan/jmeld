package org.jmeld.ui.tree;

import org.jmeld.diff.JMDelta;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by alberto on 16/11/14.
 */
class JMDeltaNode extends DefaultMutableTreeNode {
    public JMDeltaNode(JMDelta delta) {
        super(delta);
        add(new DefaultMutableTreeNode("type:"+delta.getType().toString()));
    }
}
