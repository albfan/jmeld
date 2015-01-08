package org.jmeld.util.node;

import java.io.*;

public class JMDiffNodeFactory
{
  public static JMDiffNode create(String fileLeftName, File fileLeft,
      String fileRightName, File fileRight)
  {
    JMDiffNode node = new JMDiffNode(fileLeftName, true);
    node.setBufferNodeLeft(new FileNode(fileLeftName, fileLeft));
    node.setBufferNodeRight(new FileNode(fileRightName, fileRight));

    return node;
  }
}
