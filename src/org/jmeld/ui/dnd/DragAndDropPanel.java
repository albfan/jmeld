package org.jmeld.ui.dnd;

import org.jmeld.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.*;

import javax.swing.*;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class DragAndDropPanel
       extends JPanel
{
  private JComponent leftDragAndDropArea;
  private JComponent rightDragAndDropArea;
  private String     leftFileName;
  private String     rightFileName;

  public DragAndDropPanel()
  {
    setOpaque(true);
    setBackground(Color.white);
    setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(3, 3, 3, 3)));

    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

    leftDragAndDropArea = createDragAndDropArea();
    rightDragAndDropArea = createDragAndDropArea();

    add(leftDragAndDropArea);
    add(Box.createRigidArea(new Dimension(3, 0)));
    add(rightDragAndDropArea);

    addHierarchyListener(getHierarchyListener());
    addMouseListener(getMouseListener());
  }

  private HierarchyListener getHierarchyListener()
  {
    return new HierarchyListener()
      {
        public void hierarchyChanged(HierarchyEvent e)
        {
          JRootPane rootPane;

          if ((e.getChangeFlags() & e.PARENT_CHANGED) > 0)
          {
            rootPane = getRootPane();
            if (rootPane == null)
            {
              return;
            }

            rootPane.setDropTarget(getDragAndDropTarget());
          }
        }
      };
  }

  private DropTarget getDragAndDropTarget()
  {
    return new DropTarget()
      {
        public void drop(DropTargetDropEvent dtde)
        {
          Rectangle b;
          Point     p;
          boolean   left;
          String    fileName;

          b = getRootPane().getBounds();
          p = dtde.getLocation();

          fileName = getFileName(dtde);
          if (StringUtil.isEmpty(fileName))
          {
            return;
          }

          left = p.x < (b.width - b.x) / 2;
          if (left)
          {
            leftDragAndDropArea.setBackground(Colors.DND_SELECTED_DARK);
            leftFileName = fileName;
          }
          else
          {
            rightDragAndDropArea.setBackground(Colors.DND_SELECTED_DARK);
            rightFileName = fileName;
          }
        }

        private String getFileName(DropTargetDropEvent dtde)
        {
          Transferable t;
          Object       data;
          DataFlavor[] dataFlavors;
          String       fileName;

          t = dtde.getTransferable();
          dataFlavors = t.getTransferDataFlavors();
          if (dataFlavors == null)
          {
            return null;
          }

          dtde.acceptDrop(dtde.getSourceActions());

          try
          {
            // Simplistic method that searches for a string which 
            //   starts with the prefix "file:"
            for (DataFlavor dataFlavor : dataFlavors)
            {
              data = t.getTransferData(dataFlavor);
              if (data.getClass() != String.class)
              {
                continue;
              }

              fileName = (String) data;
              if (fileName.startsWith("file:"))
              {
                return fileName;
              }
            }
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }

          return null;
        }
      };
  }

  private JComponent createDragAndDropArea()
  {
    JPanel p;

    p = new JPanel();
    p.setOpaque(true);
    p.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    p.setBackground(ColorUtil.brighter(Color.LIGHT_GRAY));
    p.setPreferredSize(new Dimension(20, 0));

    return p;
  }

  private MouseListener getMouseListener()
  {
    return new MouseAdapter()
      {
        public void mousePressed(MouseEvent me)
        {
          if (StringUtil.isEmpty(leftFileName)
            || StringUtil.isEmpty(rightFileName))
          {
            return;
          }

          if (leftFileName.equals(rightFileName))
          {
            return;
          }

          leftDragAndDropArea.setBackground(Colors.DND_SELECTED_LIGHT);
          rightDragAndDropArea.setBackground(Colors.DND_SELECTED_LIGHT);

          try
          {
            JMeld.getJMeldPanel().openComparison(
              new File(new URL(leftFileName).toURI()).getAbsolutePath(),
              new File(new URL(rightFileName).toURI()).getAbsolutePath());
          }
          catch (Exception ex)
          {
            ex.printStackTrace();
          }
        }
      };
  }
}
