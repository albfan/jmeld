package org.jmeld.ui;

import com.jgoodies.forms.layout.*;

import org.jmeld.*;
import org.jmeld.diff.*;
import org.jmeld.ui.renderer.*;
import org.jmeld.ui.text.*;
import org.jmeld.util.*;
import org.jmeld.util.file.*;
import org.jmeld.util.scan.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;
import javax.swing.undo.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class FolderDiffPanel
       extends JPanel
       implements JMeldPanelIF
{
  private JMeldPanel             mainPanel;
  private FolderDiff             diff;
  private JList                  originalList;
  private JList                  mineList;
  private ListScrollSynchronizer synchronizer;
  private MouseListener          mouseListener;
  private FolderDiffTableModel   tableModel;

  FolderDiffPanel(JMeldPanel mainPanel, FolderDiff diff)
  {
    this.mainPanel = mainPanel;
    this.diff = diff;

    init();
  }

  private void init()
  {
    JTable           table;
    JScrollPane      sp;
    TableColumnModel columnModel;
    TableColumn      column;
    int              preferredWidth;

    setLayout(new BorderLayout());

    tableModel = new FolderDiffTableModel(diff);
    table = new JTable(tableModel);
    table.addMouseListener(tableModel.getMouseListener());

    // Make sure the icons fit well.
    if (table.getRowHeight() < 20)
    {
      table.setRowHeight(20);
    }

    columnModel = table.getColumnModel();
    for (int i = 0; i < tableModel.getColumnCount(); i++)
    {
      column = columnModel.getColumn(i);

      preferredWidth = tableModel.getColumnSize(i);
      if (preferredWidth > 0)
      {
        column.setMinWidth(preferredWidth);
        column.setMaxWidth(preferredWidth);
        column.setPreferredWidth(preferredWidth);
      }
    }

    // double-click will show the differences of a node.
    table.addMouseListener(getMouseListener());

    sp = new JScrollPane(table);
    add(sp, BorderLayout.CENTER);
  }

  public String getTitle()
  {
    return diff.getOriginalFolderShortName() + " - "
    + diff.getMineFolderShortName();
  }

  private MouseListener getMouseListener()
  {
    if (mouseListener == null)
    {
      mouseListener = new MouseAdapter()
          {
            public void mouseClicked(MouseEvent me)
            {
              int       rowIndex;
              JMeldNode originalNode;
              JMeldNode mineNode;

              if (me.getClickCount() == 2)
              {
                rowIndex = ((JTable) me.getSource()).rowAtPoint(me.getPoint());

                originalNode = tableModel.getOriginalNode(rowIndex);
                mineNode = tableModel.getMineNode(rowIndex);

                mainPanel.openFileComparison(originalNode.getName(),
                  mineNode.getName());
              }
            }
          };
    }

    return mouseListener;
  }

  public boolean isSaveEnabled()
  {
    return false;
  }

  public void doSave()
  {
  }

  public boolean isUndoEnabled()
  {
    return false;
  }

  public void doUndo()
  {
  }

  public boolean isRedoEnabled()
  {
    return false;
  }

  public void doRedo()
  {
  }
}
