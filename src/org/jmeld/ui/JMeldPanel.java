package org.jmeld.ui;

import com.jgoodies.forms.builder.*;

import de.hardcode.util.swing.*;

import org.jmeld.ui.action.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.net.*;
import java.util.*;

public class JMeldPanel
       extends JPanel
{
  // class variables:
  // All actions:
  private static final String NEW_ACTION = "New";
  private static final String SAVE_ACTION = "Save";
  private static final String UNDO_ACTION = "Undo";
  private static final String REDO_ACTION = "Redo";

  // instance variables:
  private ActionHandler actionHandler;
  private JTabbedPane   tabbedPane;

  public JMeldPanel(String fileName1, String fileName2)
  {
    tabbedPane = new JTabbedPane();

    initActions();

    setLayout(new BorderLayout());
    add(getToolBar(), BorderLayout.PAGE_START);
    add(tabbedPane, BorderLayout.CENTER);

    tabbedPane.getModel().addChangeListener(getChangeListener());

    open(fileName1, fileName2);
  }

  public void open(String fileName1, String fileName2)
  {
    DiffPanel panel;

    panel = new DiffPanel(this, fileName1, fileName2);
    tabbedPane.add(panel, new TabIcon(null, panel.getTitle()));
  }

  private JComponent getToolBar()
  {
    JButton        button;
    JToolBar       toolBar;
    ToolBarBuilder builder;

    toolBar = new JToolBar();
    toolBar.setFloatable(false);
    toolBar.setRollover(true);

    builder = new ToolBarBuilder(toolBar);

    button = WidgetFactory.getToolBarButton(actionHandler.get(NEW_ACTION));
    builder.addButton(button);
    button = WidgetFactory.getToolBarButton(actionHandler.get(SAVE_ACTION));
    builder.addButton(button);

    builder.addSeparator();

    button = WidgetFactory.getToolBarButton(actionHandler.get(UNDO_ACTION));
    builder.addButton(button);
    button = WidgetFactory.getToolBarButton(actionHandler.get(REDO_ACTION));
    builder.addButton(button);

    return toolBar;
  }

  public JMenuBar getMenuBar()
  {
    JMenuBar menuBar;
    JMenu    menu;

    menuBar = new JMenuBar();

    menu = menuBar.add(new JMenu("File"));
    menu.add(WidgetFactory.getMenuItem(actionHandler.get(NEW_ACTION)));
    menu.add(WidgetFactory.getMenuItem(actionHandler.get(SAVE_ACTION)));
    //menu.add(new JMenuItem("Save as"));
    //menu.add(new JMenuItem("Close"));
    //menu.add(new JMenuItem("Quit"));
    menu = menuBar.add(new JMenu("Edit"));
    menu.add(WidgetFactory.getMenuItem(actionHandler.get(UNDO_ACTION)));
    menu.add(WidgetFactory.getMenuItem(actionHandler.get(REDO_ACTION)));
    //menu.add(new JMenuItem("Find"));
    //menu.add(new JMenuItem("Find next"));
    //menu.add(new JMenuItem("Down"));
    //menu.add(new JMenuItem("Up"));
    //menu.add(new JMenuItem("Cut"));
    //menu.add(new JMenuItem("Copy"));
    //menu.add(new JMenuItem("Paste"));
    //menu = menuBar.add(new JMenu("Settings"));
    //menu.add(new JMenuItem("Preferences"));
    //menu = menuBar.add(new JMenu("Help"));
    //menu.add(new JMenuItem("Contents"));
    //menu.add(new JMenuItem("Report bug"));
    //menu.add(new JMenuItem("About"));
    return menuBar;
  }

  public void initActions()
  {
    MeldAction action;

    actionHandler = new ActionHandler();

    action = actionHandler.createAction(this, NEW_ACTION);
    action.setIcon("stock_new");
    action.setToolTip("Merge 2 new files");

    action = actionHandler.createAction(this, SAVE_ACTION);
    action.setIcon("stock_save");
    action.setToolTip("Save the changed files");

    action = actionHandler.createAction(this, UNDO_ACTION);
    action.setIcon("stock_undo");
    action.setToolTip("Undo the latest change");

    action = actionHandler.createAction(this, REDO_ACTION);
    action.setIcon("stock_redo");
    action.setToolTip("Redo the latest change");
  }

  public ActionHandler getActionHandler()
  {
    return actionHandler;
  }

  public void checkActions()
  {
    if (actionHandler != null)
    {
      actionHandler.checkActions();
    }
  }

  public void doNew(ActionEvent ae)
  {
    System.out.println("new diff");

    //open(fileName1, fileName2);
  }

  public boolean isSaveEnabled()
  {
    DiffPanel dp;

    dp = getCurrentDiffPanel();
    if (dp == null)
    {
      return false;
    }

    return dp.isSaveEnabled();
  }

  public void doSave(ActionEvent ae)
  {
    System.out.println("save files");
  }

  public void doUndo(ActionEvent ae)
  {
    getCurrentDiffPanel().doUndo();
  }

  public boolean isUndoEnabled()
  {
    DiffPanel dp;

    dp = getCurrentDiffPanel();
    if (dp == null)
    {
      return false;
    }

    return dp.isUndoEnabled();
  }

  public void doRedo(ActionEvent ae)
  {
    getCurrentDiffPanel().doRedo();
  }

  public boolean isRedoEnabled()
  {
    DiffPanel dp;

    dp = getCurrentDiffPanel();
    if (dp == null)
    {
      return false;
    }

    return dp.isRedoEnabled();
  }

  private ChangeListener getChangeListener()
  {
    return new ChangeListener()
      {
        public void stateChanged(ChangeEvent e)
        {
          checkActions();
        }
      };
  }

  private DiffPanel getCurrentDiffPanel()
  {
    return (DiffPanel) tabbedPane.getSelectedComponent();
  }
}
