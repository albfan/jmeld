package org.jmeld.ui.tree;

import org.jmeld.JMeldException;
import org.jmeld.diff.JMChunk;
import org.jmeld.diff.JMDelta;
import org.jmeld.diff.JMDiff;
import org.jmeld.diff.JMRevision;
import org.jmeld.settings.JMeldSettings;
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
        if (JMeldSettings.getInstance().getEditor().isShowTreeRaw()) {
            addRawNodes(revision, root);
        } else {
            addNodes(revision, root);
        }
        return new DefaultTreeModel(root);
    }

    private static void addNodes(JMRevision revision, DefaultMutableTreeNode root) {
        if (revision != null) {
            for (JMDelta delta : revision.getDeltas()) {
                int initLine = delta.getOriginal().getAnchor();
                int numLines = delta.getRevised().getSize();
                JMRevision changeRevision = delta.getChangeRevision();
                for (JMDelta changeDelta : changeRevision.getDeltas()) {
                    JMChunk orgChunk = changeDelta.getOriginal();
                    JMChunk revChunk = changeDelta. getRevised();
                    int startCol = orgChunk.getAnchor();
                    int endCol = revChunk.getAnchor();
                    int modifiedchars = revChunk.getSize() - orgChunk.getSize();
                    JMChunkNode orgNode = new JMChunkNode(orgChunk, changeRevision.getOriginalString(orgChunk));
                    JMChunkNode revNode = new JMChunkNode(revChunk, changeRevision.getRevisedString(revChunk));
                    DefaultMutableTreeNode childDelta = new DefaultMutableTreeNode(changeDelta);
                    childDelta.add(orgNode);
                    childDelta.add(revNode);
                    JMChange change = new JMChange(initLine, numLines, startCol, endCol, modifiedchars);
                    DefaultMutableTreeNode changeNode = new DefaultMutableTreeNode(change);
                    changeNode.add(childDelta);
                    root.add(changeNode);
                }
            }
        }
    }

    private static void addRawNodes(JMRevision revision, DefaultMutableTreeNode parent) {
        if (revision != null) {
            for (JMDelta delta : revision.getDeltas()) {
                DefaultMutableTreeNode deltaNode = buildDeltaNode(delta, revision);
                JMRevision changeRevision = delta.getChangeRevision();
                for (JMDelta changeDelta : changeRevision.getDeltas()) {
                    DefaultMutableTreeNode changeDeltaNode = buildDeltaNode(changeDelta, changeRevision);
                    deltaNode.add(changeDeltaNode);
                }
                parent.add(deltaNode);
            }
        }
    }

    private static DefaultMutableTreeNode buildDeltaNode(JMDelta delta, JMRevision revision) {
        JMChunk orgChunk = delta.getOriginal();
        JMChunk revChunk = delta.getRevised();
        DefaultMutableTreeNode deltaNode = new JMDeltaNode(delta);
        deltaNode.add(new JMChunkNode(orgChunk, revision.getOriginalString(orgChunk)));
        deltaNode.add(new JMChunkNode(revChunk, revision.getRevisedString(revChunk)));

        return deltaNode;
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
