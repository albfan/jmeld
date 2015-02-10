package org.jmeld.ui;

import  java.util.*;
import org.jmeld.util.node.JMDiffNode;

public class CollectDiffNodeLeaf {
   private Set<JMDiffNode> diffNodeSet;

   CollectDiffNodeLeaf(UINode uiNode) {
      diffNodeSet = new HashSet<JMDiffNode>();

      collectDiffNode(uiNode);
   }

   private void collectDiffNode(UINode uiNode) {
      JMDiffNode diffNode;

      if (!uiNode.isLeaf()) {
         for (UINode childUINode : uiNode.getChildren()) {
            collectDiffNode(childUINode);
         }
      } else {
         diffNode = uiNode.getDiffNode();
         if (diffNode != null) {
            diffNodeSet.add(diffNode);
         }
      }
   }

   public List<JMDiffNode> getResult() {
      return new ArrayList(diffNodeSet);
   }
}
