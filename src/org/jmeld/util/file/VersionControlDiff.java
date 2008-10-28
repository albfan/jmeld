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

import org.jmeld.vc.VersionControlUtil;
import org.jmeld.vc.VersionControlIF;
import org.jmeld.vc.StatusResult;
import org.apache.jmeld.tools.ant.*;
import org.jmeld.settings.*;
import org.jmeld.settings.util.*;
import org.jmeld.ui.*;
import org.jmeld.util.*;
import org.jmeld.util.node.*;
import org.jmeld.vc.*;

import java.io.*;
import java.util.*;

public class VersionControlDiff
    extends FolderDiff
{
  private File directory;
  private JMDiffNode rootNode;
  private Map<String, JMDiffNode> nodes;

  public VersionControlDiff(File directory, Mode mode)
  {
    super(mode);

    this.directory = directory;

    try
    {
      setLeftFolderShortName(directory.getName());
      setRightFolderShortName("");
      setLeftFolderName(directory.getCanonicalPath());
      setRightFolderName("");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public JMDiffNode getRootNode()
  {
    return rootNode;
  }

  public Collection<JMDiffNode> getNodes()
  {
    return nodes.values();
  }

  public void diff()
  {
    DirectoryScanner ds;
    JMDiffNode node;
    StopWatch stopWatch;
    int numberOfNodes;
    int currentNumber;
    File file;
    List<VersionControlIF> versionControlList;
    VersionControlIF versionControl;
    StatusResult statusResult;

    stopWatch = new StopWatch();
    stopWatch.start();

    StatusBar.getInstance().start();
    StatusBar.getInstance().setState("Start scanning directories...");

    rootNode = new JMDiffNode("<root>", false);
    nodes = new HashMap<String, JMDiffNode>();

    versionControlList = VersionControlUtil.getVersionControl(directory);
    if (versionControlList.isEmpty())
    {
      return;
    }

    // TODO: versioncontrol should be a parameter in the constructor. 
    //       The user has to decide which vc is used (popup)
    versionControl = versionControlList.get(0);

    statusResult = versionControl.executeStatus(directory);

    for (StatusResult.Entry entry : statusResult.getEntryList())
    {
      //file = new File(statusResult.getPath(), entry.getName());
      file = new File(entry.getName());

      node = addNode(entry.getName(), !file.isDirectory());

      node.setBufferNodeLeft(new VersionControlBaseNode(versionControl, entry,
          file));
      node.setBufferNodeRight(new FileNode(entry.getName(), file));

      switch (entry.getStatus())
      {
        case modified:
        case conflicted:
        case unversioned:
        case missing:
        case dontknow:
          node.setCompareState(JMDiffNode.Compare.NotEqual);
          break;
        case added:
          node.setCompareState(JMDiffNode.Compare.LeftMissing);
          break;
        case removed:
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

  private JMDiffNode addNode(String name, boolean leaf)
  {
    JMDiffNode node;

    node = nodes.get(name);
    if (node == null)
    {
      node = addNode(new JMDiffNode(name, leaf));
    }

    return node;
  }

  private JMDiffNode addNode(JMDiffNode node)
  {
    String parentName;
    JMDiffNode parent;
    File file;
    FileNode fn;

    nodes.put(node.getName(), node);

    parentName = node.getParentName();
    if (StringUtil.isEmpty(parentName))
    {
      parent = rootNode;
    }
    else
    {
      parent = nodes.get(parentName);
      if (parent == null)
      {
        parent = addNode(new JMDiffNode(parentName, false));
        fn = new FileNode(parentName, new File(directory, parentName));
        parent.setBufferNodeRight(fn);
        parent.setBufferNodeLeft(fn);
      }
    }

    parent.addChild(node);
    return node;
  }

  public void print()
  {
    rootNode.print("");
  }

  public static void main(String[] args)
  {
    VersionControlDiff diff;
    StopWatch stopWatch;

    diff = new VersionControlDiff(new File(args[0]),
        VersionControlDiff.Mode.TWO_WAY);
    stopWatch = new StopWatch();
    stopWatch.start();
    diff.diff();
    System.out.println("diff took " + stopWatch.getElapsedTime() + " msec.");
    diff.print();
  }
}
