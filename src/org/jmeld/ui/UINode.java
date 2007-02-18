package org.jmeld.ui;

import org.jmeld.util.node.*;

import javax.swing.tree.*;

import java.util.*;

public class UINode
       implements TreeNode
{
  private String       text;
  private String       name;
  private boolean      leaf;
  private JMDiffNode   diffNode;
  private UINode       parent;
  private List<UINode> children = new ArrayList<UINode>();

  public UINode(JMDiffNode diffNode)
  {
    this.diffNode = diffNode;

    this.name = diffNode.getName();
    this.leaf = diffNode.isLeaf();
  }

  public UINode(
    String  name,
    boolean leaf)
  {
    assert name != null;

    this.name = name;
    this.leaf = leaf;
  }

  public String getName()
  {
    return name;
  }

  public JMDiffNode getDiffNode()
  {
    return diffNode;
  }

  public void addChild(UINode child)
  {
    children.add(child);
    child.setParent(this);
  }

  private void setParent(UINode parent)
  {
    this.parent = parent;
  }

  public Enumeration<UINode> children()
  {
    return Collections.enumeration(children);
  }

  public boolean getAllowsChildren()
  {
    return isLeaf();
  }

  public UINode getChildAt(int childIndex)
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

  public UINode getParent()
  {
    return parent;
  }

  public boolean isLeaf()
  {
    return leaf;
  }

  public void print(String indent)
  {
    System.out.println(indent + name);
    indent += "  ";
    for (UINode node : children)
    {
      node.print(indent);
    }
  }

  public String toString()
  {
    String pn;

    if (text == null)
    {
      text = name;
      if (parent != null)
      {
        pn = parent.getName();
        if (name.startsWith(pn))
        {
          text = name.substring(pn.length());
          if (text.startsWith("/"))
          {
            text = text.substring(1);
          }
        }
      }
    }

    return text;
  }
}
