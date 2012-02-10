package org.jmeld.ui;

import org.jmeld.JMeldException;
import org.jmeld.diff.JMChunk;
import org.jmeld.diff.JMDelta;
import org.jmeld.diff.JMDiff;
import org.jmeld.diff.JMRevision;
import org.jmeld.util.Ignore;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * Arbol de diferencias
 * User: alberto
 * Date: 29/01/13
 * Time: 0:06
 */
public class JMTreeDelta {

    public static void main(String[] args) throws IOException, JMeldException {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(400, 300));


        BufferedReader readerOrg = new BufferedReader(new FileReader(args[0]));
        String line;
        Vector<String> vOrg = new Vector<String>();
        while ((line = readerOrg.readLine()) != null) {
            vOrg.add(line);
        }

        BufferedReader readerRev = new BufferedReader(new FileReader(args[1]));
        Vector<String> vRev = new Vector<String>();
        while ((line = readerRev.readLine()) != null) {
            vRev.add(line);
        }

        JMRevision revision = new JMDiff().diff(vOrg.toArray(), vRev.toArray(), Ignore.NULL_IGNORE);

        JTree tree = buildTreeRevision(revision);
        frame.add(new JScrollPane(tree));
        frame.pack();
        frame.setVisible(true);
    }

    public static JTree buildTreeRevision(JMRevision revision) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(revision);
        if (revision != null) {
            for (JMDelta delta : revision.getDeltas()) {
                DefaultMutableTreeNode childDelta = new DefaultMutableTreeNode(delta);
                JMChunk orgChunk = delta.getOriginal();
                childDelta.add(new JMChunkNode(orgChunk, revision.getOriginalString(orgChunk)));
                JMChunk revChunk = delta.getRevised();
                childDelta.add(new JMChunkNode(revChunk, revision.getRevisedString(revChunk)));
                root.add(childDelta);

                JMRevision changeRevision = delta.getChangeRevision();
                for (JMDelta delta2 : changeRevision.getDeltas()) {
                    DefaultMutableTreeNode childDelta2 = new DefaultMutableTreeNode(delta2);
                    orgChunk = delta2.getOriginal();
                    childDelta2.add(new JMChunkNode(orgChunk, changeRevision.getOriginalString(orgChunk)));
                    revChunk = delta2.getRevised();
                    childDelta2.add(new JMChunkNode(revChunk, changeRevision.getRevisedString(revChunk)));
                    childDelta.add(childDelta2);
                }
            }
        }
        JTree tree = new JTree(new DefaultTreeModel(root));
        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();
                if (userObject instanceof JMRevision) {
                    setIcon(new ColorIcon(new Color(79, 128, 189)));
                    setText(((JMRevision)userObject).getDeltas().size()+" cambios");
                } else if (userObject instanceof JMDelta) {
                    setIcon(new ColorIcon(new Color(237, 236, 59)));
                    setText(((JMDelta)userObject).getType()+"");
                } else if (value instanceof JMChunkNode) {
                    setIcon(new ColorIcon(new Color(217, 94, 24)));
                    JMChunkNode chunkNode = (JMChunkNode) value;
                    setText(chunkNode.getChunk().getAnchor()+", "+chunkNode.getChunk().getSize()+ ": "+chunkNode.getString());
                }
                return this;
            }
        });
        return tree;
    }
}

class ColorIcon implements Icon {
    private static int HEIGHT = 14;
    private static int WIDTH = 14;

    private Color color;

    public ColorIcon(Color color) {
        this.color = color;
    }

    public int getIconHeight() {
        return HEIGHT;
    }

    public int getIconWidth() {
        return WIDTH;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color);
        g.fillRect(x, y, WIDTH - 1, HEIGHT - 1);

        g.setColor(Color.black);
        g.drawRect(x, y, WIDTH - 1, HEIGHT - 1);
    }
}

class JMRevisionNode extends DefaultMutableTreeNode {
    public JMRevisionNode(Object userObject) {
        super(userObject);
    }


}

class JMDeltaNode extends DefaultMutableTreeNode {
    public JMDeltaNode(Object userObject) {
        super(userObject);
    }

}

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