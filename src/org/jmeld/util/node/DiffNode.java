package org.jmeld.util.node;

import javax.swing.tree.*;

import java.util.*;

public class DiffNode
       implements TreeNode
{
  private String         name;
  private String         shortName;
  private String         parentName;
  private DiffNode       parent;
  private List<DiffNode> children;
  private BufferNode     bufferNode1;
  private BufferNode     bufferNode2;
  private boolean        leaf;
  private char           state;

  public DiffNode(
    String  name,
    boolean leaf)
  {
    this.name = name;
    this.shortName = name;
    this.leaf = leaf;

    children = new ArrayList();
    calculateNames();
  }

  public String getName()
  {
    return name;
  }

  public String getShortName()
  {
    return shortName;
  }

  public String getParentName()
  {
    return parentName;
  }

  public void addChild(DiffNode child)
  {
    children.add(child);
    child.setParent(this);
  }

  private void setParent(DiffNode parent)
  {
    this.parent = parent;
  }

  public void setBufferNode1(BufferNode bufferNode)
  {
    bufferNode1 = bufferNode;
  }

  public BufferNode getBufferNode1()
  {
    return bufferNode1;
  }

  public void setBufferNode2(BufferNode bufferNode)
  {
    bufferNode2 = bufferNode;
  }

  public BufferNode getBufferNode2()
  {
    return bufferNode2;
  }

  public Enumeration<DiffNode> children()
  {
    return Collections.enumeration(children);
  }

  public boolean getAllowsChildren()
  {
    return false;
  }

  public DiffNode getChildAt(int childIndex)
  {
    return children.get(childIndex);
  }

  public int getChildCount()
  {
    return children.size();
  }

  public int getIndex(TreeNode node)
  {
    return children.indexOf(node);
  }

  public DiffNode getParent()
  {
    return parent;
  }

  public boolean isLeaf()
  {
    return leaf;
  }

  private void calculateNames()
  {
    int index;

    index = name.lastIndexOf("/");
    if (index == -1)
    {
      parentName = null;
      return;
    }

    parentName = name.substring(0, index);
    shortName = name.substring(index + 1);
  }

  public void compareContents()
  {
    if(isLeaf() ||
       (bufferNode1 == null && bufferNode2 == null))
    {
      state = 0;
      return;
    }

    if(bufferNode1 != null || bufferNode2 == null)
    {
      state = 'E';
      return;
    }

    if(bufferNode1 == null || bufferNode1 != null)
    {
      state = 'A';
      return;
    }
  }

  public void print(String indent)
  {
    System.out.println(indent + shortName);
    indent += "  ";
    for (DiffNode node : children)
    {
      node.print(indent);
    }
  }

  public String toString()
  {
    return shortName;
  }
}
