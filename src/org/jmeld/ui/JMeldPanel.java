package org.jmeld.ui;

import com.jgoodies.forms.builder.*;

import org.jdesktop.swingworker.SwingWorker;
import org.jmeld.diff.*;
import org.jmeld.ui.action.*;
import org.jmeld.ui.text.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.*;
import org.jmeld.util.file.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
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
  private static final String RIGHT_ACTION = "Right";
  private static final String LEFT_ACTION = "Left";
  private static final String UP_ACTION = "Up";
  private static final String DOWN_ACTION = "Down";
  private static final String ZOOMPLUS_ACTION = "ZoomPlus";
  private static final String ZOOMMIN_ACTION = "ZoomMin";

  // instance variables:
  private ActionHandler actionHandler;
  private JTabbedPane   tabbedPane;

  public JMeldPanel(
    String originalName,
    String mineName)
  {
    tabbedPane = new JTabbedPane();

    initActions();

    setLayout(new BorderLayout());
    add(
      getToolBar(),
      BorderLayout.PAGE_START);
    add(tabbedPane, BorderLayout.CENTER);
    add(
      getStatusBar(),
      BorderLayout.PAGE_END);

    tabbedPane.getModel().addChangeListener(getChangeListener());

    openComparison(originalName, mineName);
  }

  public void openComparison(
    String originalName,
    String mineName)
  {
    File original;
    File mine;

    if (!StringUtil.isEmpty(originalName) && !StringUtil.isEmpty(mineName))
    {
      original = new File(originalName);
      mine = new File(mineName);
      if (original.isDirectory() && mine.isDirectory())
      {
        openDirectoryComparison(originalName, mineName);
      }
      else
      {
        openFileComparison(originalName, mineName);
      }
    }
  }

  public void openFileComparison(
    String originalName,
    String mineName)
  {
    WaitCursor.wait(this);

    new NewFileComparisonPanel(originalName, mineName).execute();
  }

  public void openDirectoryComparison(
    String originalName,
    String mineName)
  {
    WaitCursor.wait(this);

    new NewDirectoryComparisonPanel(originalName, mineName).execute();
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

  private JComponent getStatusBar()
  {
    return StatusBar.getInstance();
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
    installKey("control Z", action);

    action = actionHandler.createAction(this, REDO_ACTION);
    action.setIcon("stock_redo");
    action.setToolTip("Redo the latest change");
    installKey("control R", action);

    action = actionHandler.createAction(this, LEFT_ACTION);
    installKey("alt LEFT", action);
    installKey("alt KP_LEFT", action);

    action = actionHandler.createAction(this, RIGHT_ACTION);
    installKey("alt RIGHT", action);
    installKey("alt KP_RIGHT", action);

    action = actionHandler.createAction(this, UP_ACTION);
    installKey("alt UP", action);
    installKey("alt KP_UP", action);

    action = actionHandler.createAction(this, DOWN_ACTION);
    installKey("alt DOWN", action);
    installKey("alt KP_DOWN", action);

    action = actionHandler.createAction(this, ZOOMPLUS_ACTION);
    installKey("alt EQUALS", action);
    installKey("shift alt EQUALS", action);
    installKey("alt ADD", action);

    action = actionHandler.createAction(this, ZOOMMIN_ACTION);
    installKey("alt MINUS", action);
    installKey("shift alt MINUS", action);
    installKey("alt SUBTRACT", action);
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
    NewPanelDialog dialog;
    File           file1;
    File           file2;
    String         fileName;

    dialog = new NewPanelDialog(this);
    dialog.show();

    if (dialog.getValue() == NewPanelDialog.FILE_COMPARISON)
    {
      openFileComparison(
        dialog.getOriginalFileName(),
        dialog.getMineFileName());
    }
    else if (dialog.getValue() == NewPanelDialog.DIRECTORY_COMPARISON)
    {
      openDirectoryComparison(
        dialog.getOriginalDirectoryName(),
        dialog.getMineDirectoryName());
    }
  }

  public void doSave(ActionEvent ae)
  {
    getCurrentJMeldContentPanel().doSave();
  }

  public boolean isSaveEnabled()
  {
    JMeldContentPanelIF panel;

    panel = getCurrentJMeldContentPanel();
    if (panel == null)
    {
      return false;
    }

    return panel.isSaveEnabled();
  }

  public void doUndo(ActionEvent ae)
  {
    getCurrentJMeldContentPanel().doUndo();
  }

  public boolean isUndoEnabled()
  {
    JMeldContentPanelIF panel;

    panel = getCurrentJMeldContentPanel();
    if (panel == null)
    {
      return false;
    }

    return panel.isUndoEnabled();
  }

  public void doRedo(ActionEvent ae)
  {
    getCurrentJMeldContentPanel().doRedo();
  }

  public boolean isRedoEnabled()
  {
    JMeldContentPanelIF panel;

    panel = getCurrentJMeldContentPanel();
    if (panel == null)
    {
      return false;
    }

    return panel.isRedoEnabled();
  }

  public void doLeft(ActionEvent ae)
  {
    getCurrentJMeldContentPanel().doLeft();
    repaint();
  }

  public void doRight(ActionEvent ae)
  {
    getCurrentJMeldContentPanel().doRight();
    repaint();
  }

  public void doUp(ActionEvent ae)
  {
    getCurrentJMeldContentPanel().doUp();
    repaint();
  }

  public void doDown(ActionEvent ae)
  {
    getCurrentJMeldContentPanel().doDown();
    repaint();
  }

  public void doZoomPlus(ActionEvent ae)
  {
    getCurrentJMeldContentPanel().doZoom(true);
    repaint();
  }

  public void doZoomMin(ActionEvent ae)
  {
    getCurrentJMeldContentPanel().doZoom(false);
    repaint();
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

  private JMeldContentPanelIF getCurrentJMeldContentPanel()
  {
    return (JMeldContentPanelIF) tabbedPane.getSelectedComponent();
  }

  class NewFileComparisonPanel
         extends SwingWorker<String, Object>
  {
    private String           originalName;
    private String           mineName;
    private BufferDocumentIF bd1;
    private BufferDocumentIF bd2;
    private JMDiff           diff;
    private JMRevision       revision;

    NewFileComparisonPanel(
      String originalName,
      String mineName)
    {
      this.originalName = originalName;
      this.mineName = mineName;
    }

    public String doInBackground()
    {
      if (StringUtil.isEmpty(originalName))
      {
        return "original filename is empty";
      }

      if (!new File(originalName).exists())
      {
        return "original filename(" + originalName + ") doesn't exist";
      }

      if (StringUtil.isEmpty(mineName))
      {
        return "mine filename is empty";
      }

      if (!new File(mineName).exists())
      {
        return "mine filename(" + mineName + ") doesn't exist";
      }

      try
      {
        StatusBar.start();
        StatusBar.setStatus("Reading file: " + originalName);
        bd1 = new FileDocument(new File(originalName));
        bd1.read();

        StatusBar.setStatus("Reading file: " + mineName);
        bd2 = new FileDocument(new File(mineName));
        bd2.read();

        StatusBar.setStatus("Calculating differences...");
        diff = new JMDiff();
        revision = diff.diff(
            bd1.getLines(),
            bd2.getLines());
        StatusBar.setStatus("Ready calculating differences");
        StatusBar.stop();
      }
      catch (Exception ex)
      {
        ex.printStackTrace();

        return ex.getMessage();
      }

      return null;
    }

    protected void done()
    {
      try
      {
        String          result;
        BufferDiffPanel panel;

        result = get();

        if (result != null)
        {
          JOptionPane.showMessageDialog(JMeldPanel.this, result,
            "Error opening file", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
          panel = new BufferDiffPanel(JMeldPanel.this);
          panel.setBufferDocuments(bd1, bd2, diff, revision);

          tabbedPane.add(
            panel,
            new TabIcon(
              null,
              panel.getTitle()));
          tabbedPane.setSelectedComponent(panel);
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
      finally
      {
        WaitCursor.resume(JMeldPanel.this);
      }
    }
  }

  class NewDirectoryComparisonPanel
         extends SwingWorker<String, Object>
  {
    private String        originalName;
    private String        mineName;
    private File          original;
    private File          mine;
    private DirectoryDiff diff;

    NewDirectoryComparisonPanel(
      String originalName,
      String mineName)
    {
      this.originalName = originalName;
      this.mineName = mineName;
    }

    public String doInBackground()
    {
      if (StringUtil.isEmpty(originalName))
      {
        return "original directoryName is empty";
      }

      original = new File(originalName);
      if (!original.exists())
      {
        return "original directoryName(" + originalName + ") doesn't exist";
      }

      if (!original.isDirectory())
      {
        return "original directoryName(" + originalName
        + ") is not a directory";
      }

      if (StringUtil.isEmpty(mineName))
      {
        return "mine directoryName is empty";
      }

      mine = new File(mineName);
      if (!mine.exists())
      {
        return "mine directoryName(" + mineName + ") doesn't exist";
      }

      if (!mine.isDirectory())
      {
        return "mine directoryName(" + mineName + ") is not a directory";
      }

      diff = new DirectoryDiff(original, mine);
      diff.diff();

      return null;
    }

    protected void done()
    {
      try
      {
        String          result;
        FolderDiffPanel panel;

        result = get();

        if (result != null)
        {
          JOptionPane.showMessageDialog(JMeldPanel.this, result,
            "Error opening file", JOptionPane.ERROR_MESSAGE);
        }

        panel = new FolderDiffPanel(JMeldPanel.this, diff);

        tabbedPane.add(
          panel,
          new TabIcon(
            null,
            panel.getTitle()));
        tabbedPane.setSelectedComponent(panel);
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
      finally
      {
        WaitCursor.resume(JMeldPanel.this);
      }
    }
  }

  private void installKey(
    String     key,
    MeldAction action)
  {
    InputMap  inputMap;
    ActionMap actionMap;

    inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    inputMap.put(
      KeyStroke.getKeyStroke(key),
      action.getName());

    actionMap = getActionMap();
    actionMap.put(
      action.getName(),
      action);
  }
}
