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

import org.apache.jmeld.tools.ant.*;
import org.jmeld.settings.*;
import org.jmeld.settings.util.*;
import org.jmeld.ui.*;
import org.jmeld.util.*;
import org.jmeld.util.node.*;
import org.jmeld.util.vc.*;

import java.io.*;
import java.util.*;

public class VersionControlDiff
    extends FolderDiff
{
  private File                    directory;
  private JMDiffNode              rootNode;
  private Map<String, JMDiffNode> nodes;

  public VersionControlDiff(File directory, Mode mode)
  {
    super(mode);

    this.directory = directory;

    try
    {
      setLeftFolderShortName(directory.getName() + " (revision XXXX)");
      setRightFolderShortName(directory.getName() + " (workingcopy)");
      setLeftFolderName(directory.getCanonicalPath() + " (revision XXXX)");
      setRightFolderName(directory.getCanonicalPath() + " (workingcopy)");
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
    FileNode fn;
    VersionControlIF versionControl;
    StatusIF status;

    stopWatch = new StopWatch();
    stopWatch.start();

    StatusBar.getInstance().start();
    StatusBar.getInstance().setState("Start scanning directories...");

    rootNode = new JMDiffNode("<root>", false);
    nodes = new HashMap<String, JMDiffNode>();

    versionControl = VersionControlUtil.getVersionControl(directory);
    if (versionControl == null)
    {
      return;
    }

    status = versionControl.executeStatus(directory, true);

    for (StatusIF.TargetIF target : status.getTargetList())
    {
      for (StatusIF.EntryIF entry : target.getEntryList())
      {
        fn = new FileNode(entry.getPath(), new File(entry.getPath()));
        node = addNode(entry.getPath());
        node.setBufferNodeLeft(fn);
        node.setBufferNodeRight(fn);
      }
    }

    StatusBar.getInstance().setState("Comparing nodes...");
    numberOfNodes = nodes.size();
    currentNumber = 0;
    for (JMDiffNode n : nodes.values())
    {
      n.compareContents();
      StatusBar.getInstance().setProgress(++currentNumber, numberOfNodes);
    }

    StatusBar.getInstance().setState(
                                     "Ready comparing directories (took "
                                         + (stopWatch.getElapsedTime() / 1000)
                                         + " seconds)");
    StatusBar.getInstance().stop();
  }

  private JMDiffNode addNode(String name)
  {
    JMDiffNode node;

    node = nodes.get(name);
    if (node == null)
    {
      node = addNode(new JMDiffNode(name, true));
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
