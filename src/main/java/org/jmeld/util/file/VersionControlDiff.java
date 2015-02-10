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
package org.jmeld.util.file;

import org.jmeld.ui.*;
import org.jmeld.util.*;
import org.jmeld.util.node.*;
import org.jmeld.vc.*;
import org.jmeld.vc.util.VcCmd;

import java.io.*;
import java.util.*;

public class VersionControlDiff extends FolderDiff {
    private File file;
    private JMDiffNode rootNode;
    private Map<String, JMDiffNode> nodes;
    private VersionControlIF versionControl;

    public VersionControlDiff(File file, Mode mode) {
        super(mode);

        setFile(file);

        setVersionControl(VersionControlUtil.getFirstVersionControl(getFile()));

        try {
            setLeftFolderShortName(file.getName());
            setRightFolderShortName("");
            setLeftFolderName(file.getCanonicalPath());
            setRightFolderName("");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public JMDiffNode getRootNode() {
        return rootNode;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public VersionControlIF getVersionControl() {
        return versionControl;
    }

    public void setVersionControl(VersionControlIF versionControl) {
        this.versionControl = versionControl;
    }

    public Collection<JMDiffNode> getNodes() {
        return nodes.values();
    }

    public void diff() {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        StatusBar.getInstance().start();
        StatusBar.getInstance().setState("Start scanning directories...");

        rootNode = new JMDiffNode("<root>", false);
        nodes = new HashMap<String, JMDiffNode>();

        StatusResult statusResult = getVersionControl().executeStatus(getFile());

        for (StatusResult.Entry entry : statusResult.getEntryList()) {
            File file = new File(statusResult.getPath(), entry.getName());

            JMDiffNode node = buildNode(entry.getName(), file.isFile());

            FileNode fileNode = new FileNode(entry.getName(), file);
            node.setBufferNodeLeft(new VersionControlBaseNode(getVersionControl(), entry, fileNode, file));
            node.setBufferNodeRight(fileNode);

            switch (entry.getStatus()) {
                case unmodified:
                    node.setCompareState(JMDiffNode.Compare.Equal);
                    break;
                case modified:
                case index_modified:
                case conflicted:
                case missing:
                case dontknow:
                    node.setCompareState(JMDiffNode.Compare.NotEqual);
                    break;
                case unversioned:
                case added:
                case index_added:
                    node.setCompareState(JMDiffNode.Compare.LeftMissing);
                    break;
                case removed:
                case index_removed:
                    node.setCompareState(JMDiffNode.Compare.RightMissing);
                    break;
                case clean:
                case ignored:
                    node.setCompareState(JMDiffNode.Compare.Equal);
                    break;
            }
        }

        StatusBar.getInstance().setState(
                "Ready comparing directories (took "
                        + (stopWatch.getElapsedTime() / 1000) + " seconds)");
        StatusBar.getInstance().stop();
    }

    private JMDiffNode buildNode(String name, boolean leaf) {
        JMDiffNode node = nodes.get(name);
        if (node == null) {
            node = addNode(name, leaf);
        }
        return node;
    }

    private JMDiffNode addNode(String name, boolean leaf) {
        JMDiffNode node = new JMDiffNode(name, leaf);
        nodes.put(name, node);
        buildParentNode(node);
        return node;
    }

    private void buildParentNode(JMDiffNode node) {
        String parentName = node.getParentName();
        JMDiffNode parentnode;
        if (StringUtil.isEmpty(parentName)) {
            parentnode = rootNode;
        } else {
            parentnode = nodes.get(parentName);
            if (parentnode == null) {
                parentnode = addNode(parentName, false);
                FileNode fn = new FileNode(parentName, new File(file, parentName));
                parentnode.setBufferNodeRight(fn);
                parentnode.setBufferNodeLeft(fn);
            }
        }
        parentnode.addChild(node);
    }

    public void print() {
        rootNode.print("");
    }

    public static void main(String[] args) {
        VersionControlDiff diff;
        StopWatch stopWatch;

        File file = VcCmd.parseFile(args);
        if (file == null) {
            return;
        }

        diff = new VersionControlDiff(file, VersionControlDiff.Mode.TWO_WAY);
        stopWatch = new StopWatch();
        stopWatch.start();
        diff.diff();
        System.err.println("diff took " + stopWatch.getElapsedTime() + " msec.");
        diff.print();
    }
}
