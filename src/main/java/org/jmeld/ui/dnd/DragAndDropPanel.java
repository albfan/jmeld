package org.jmeld.ui.dnd;

import org.jmeld.JMeld;
import org.jmeld.ui.util.ColorUtil;
import org.jmeld.ui.util.Colors;
import org.jmeld.util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.*;
import java.io.File;
import java.net.URL;

public class DragAndDropPanel
    extends JPanel
{
  private JComponent leftDragAndDropArea;
  private JComponent rightDragAndDropArea;
  private String leftFileName = "";
  private String rightFileName = "";

  public DragAndDropPanel()
  {
    setOpaque(true);
    setBackground(Color.white);
    setBorder(BorderFactory.createCompoundBorder(BorderFactory
        .createLineBorder(Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(3,
      3, 3, 3)));

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

        if ((e.getChangeFlags() & e.PARENT_CHANGED) != 0)
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
      Component orgGlassPane;
      JPanel glassPane;

      public void dragEnter(DropTargetDragEvent dtde)
      {
        super.dragEnter(dtde);

        if (orgGlassPane == null)
        {
          glassPane = new JPanel(new GridLayout(0, 2, 40, 40));
          glassPane.setBorder(BorderFactory.createEmptyBorder(60, 10, 40, 10));
          glassPane.setOpaque(false);

          glassPane.add(createDropPane(leftFileName));
          glassPane.add(createDropPane(rightFileName));

          orgGlassPane = getRootPane().getGlassPane();
          getRootPane().setGlassPane(glassPane);
          glassPane.setVisible(true);
          getRootPane().repaint();
        }
      }

      private JPanel createDropPane(String text)
      {
        JPanel p;
        JLabel label;

        label = new JLabel(text);
        label.setOpaque(false);
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setVerticalAlignment(JLabel.TOP);
        label.setFont(label.getFont().deriveFont(16.0f));

        p = new JPanel(new BorderLayout());
        p.add(label, BorderLayout.CENTER);
        p.setBackground(new Color(238, 227, 187, 200));
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory
            .createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(10,
          10, 10, 10)));

        return p;
      }

      public void dragOver(DropTargetDragEvent dtde)
      {
        super.dragOver(dtde);
      }

      public void dragExit(DropTargetEvent dte)
      {
        super.dragExit(dte);
        resetGlassPane();
      }

      private void resetGlassPane()
      {
        if (orgGlassPane != null)
        {
          getRootPane().setGlassPane(orgGlassPane);
          orgGlassPane.setVisible(false);
          orgGlassPane = null;
        }
      }

      public void drop(DropTargetDropEvent dtde)
      {
        Rectangle b;
        Point p;
        boolean left;
        String fileName;

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
          leftDragAndDropArea.setBackground(Colors.DND_SELECTED_NEW);
          leftFileName = fileName;
        }
        else
        {
          rightDragAndDropArea.setBackground(Colors.DND_SELECTED_NEW);
          rightFileName = fileName;
        }

        resetGlassPane();
      }

      private String getFileName(DropTargetDropEvent dtde)
      {
        Transferable t;
        Object data;
        DataFlavor[] dataFlavors;
        String fileName;

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

        leftDragAndDropArea.setBackground(Colors.DND_SELECTED_USED);
        rightDragAndDropArea.setBackground(Colors.DND_SELECTED_USED);

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
