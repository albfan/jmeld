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
import javax.swing.text.*;
import javax.swing.undo.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class DirectoryDiffPanel
       extends JPanel
       implements JMeldPanelIF
{
  private JMeldPanel             mainPanel;
  private DirectoryDiff          diff;
  private JList                  originalList;
  private JList                  mineList;
  private ListScrollSynchronizer synchronizer;
  private MouseListener          mouseListener;

  DirectoryDiffPanel(JMeldPanel mainPanel, DirectoryDiff diff)
  {
    this.mainPanel = mainPanel;
    this.diff = diff;

    init();
  }

  private void init()
  {
    FormLayout       layout;
    String           columns;
    String           rows;
    CellConstraints  cc;
    JLabel           label;
    DefaultListModel model;
    JScrollPane      sp1;
    JScrollPane      sp2;
    JViewport        vp1;

    columns = "4px, 0:grow, 10px, 0:grow, 4px";
    rows = "6px, pref, 3px, fill:0:grow, 6px";
    layout = new FormLayout(columns, rows);
    cc = new CellConstraints();

    setLayout(layout);

    label = new JLabel(diff.getOriginalDirectory().getName());
    add(label, cc.xy(2, 2));
    originalList = new JList(new Vector(diff.getOriginalNodes()));
    originalList.setCellRenderer(new JMeldNodeRenderer());
    originalList.addMouseListener(getMouseListener());
    sp1 = new JScrollPane(originalList);
    sp1.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    vp1 = sp1.getViewport();
    vp1.setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
    vp1.setViewPosition(new Point(0, 0));
    add(sp1, cc.xy(2, 4));

    label = new JLabel(diff.getMineDirectory().getName());
    add(label, cc.xy(4, 2));
    mineList = new JList(new Vector(diff.getMineNodes()));
    mineList.setCellRenderer(new JMeldNodeRenderer());
    mineList.addMouseListener(getMouseListener());
    sp2 = new JScrollPane(mineList);
    sp2.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
    add(sp2, cc.xy(4, 4));

    synchronizer = new ListScrollSynchronizer(sp1, sp2);
    mineList.setSelectionModel(originalList.getSelectionModel());
  }

  public String getTitle()
  {
    return diff.getOriginalDirectory().getName() + " - "
    + diff.getMineDirectory().getName();
  }

  private MouseListener getMouseListener()
  {
    if (mouseListener == null)
    {
      mouseListener = new MouseAdapter()
          {
            public void mouseClicked(MouseEvent me)
            {
              int       index;
              JMeldNode node1;
              JMeldNode node2;

              if (me.getClickCount() == 2)
              {
                index = ((JList) me.getSource()).locationToIndex(me.getPoint());

                mainPanel.openFileComparison(diff.getOriginalFileName(index),
                  diff.getMineFileName(index));
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
