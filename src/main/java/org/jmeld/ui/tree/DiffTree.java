package org.jmeld.ui.tree;

import org.jmeld.JMeldException;
import org.jmeld.diff.JMChunk;
import org.jmeld.diff.JMDelta;
import org.jmeld.diff.JMDiff;
import org.jmeld.diff.JMRevision;
import org.jmeld.util.Ignore;
import org.jmeld.vc.util.VcCmd;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * Diff tree panel
 * User: alberto
 * Date: 29/01/13
 * Time: 0:06
 */
public class DiffTree extends JTree {


    public DiffTree() {
        this(new DefaultTreeModel(new DefaultMutableTreeNode(null)));
    }

    public DiffTree(JMRevision revision) {
        this(buildTreemodel(revision));
    }

    public DiffTree(DefaultTreeModel model) {
        super(model);
        setCellRenderer(new DiffTreeCellRenderer());
    }

    private static DefaultTreeModel buildTreemodel(JMRevision revision) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(revision);
        if (revision != null) {
            addNodes(revision, root);
        }
        return new DefaultTreeModel(root);
    }

    private static void addNodes(JMRevision revision, DefaultMutableTreeNode parent) {
        for (JMDelta delta : revision.getDeltas()) {
            int initLine = delta.getOriginal().getAnchor();
            int numLines = delta.getRevised().getSize();
            JMRevision changeRevision = delta.getChangeRevision();
            JMDelta changeDelta = changeRevision.getDeltas().get(0);
            JMChunk orgChunk = changeDelta.getOriginal();
            JMChunk revChunk = changeDelta.getRevised();
            int startCol = orgChunk.getSize();
            int endCol = revChunk.getSize();
            JMChange change = new JMChange(initLine, numLines, startCol, endCol);
            DefaultMutableTreeNode changeNode = new DefaultMutableTreeNode(change);
            parent.add(changeNode);
            DefaultMutableTreeNode childDelta = new DefaultMutableTreeNode(changeDelta);
            childDelta.add(new JMChunkNode(orgChunk, changeRevision.getOriginalString(orgChunk)));
            childDelta.add(new JMChunkNode(revChunk, changeRevision.getRevisedString(revChunk)));
            changeNode.add(childDelta);
        }
    }

    public void setRevision(JMRevision revision) {
        setModel(buildTreemodel(revision));
    }

    public static void main(String[] args) throws IOException, JMeldException {

        File file = VcCmd.parseFile(args, 0);
        if (file == null) {
            return;
        }

        File file2 = VcCmd.parseFile(args, 1);
        if (file2 == null) {
            return;
        }

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(400, 300));

        BufferedReader readerOrg = new BufferedReader(new FileReader(file));
        String line;
        Vector<String> vOrg = new Vector<String>();
        while ((line = readerOrg.readLine()) != null) {
            vOrg.add(line);
        }

        BufferedReader readerRev = new BufferedReader(new FileReader(file2));
        Vector<String> vRev = new Vector<String>();
        while ((line = readerRev.readLine()) != null) {
            vRev.add(line);
        }

        JMRevision revision = new JMDiff().diff(vOrg.toArray(), vRev.toArray(), Ignore.NULL_IGNORE);

        JTree tree = new DiffTree(buildTreemodel(revision));
        frame.add(new JScrollPane(tree));
        frame.pack();
        frame.setVisible(true);
    }
}
