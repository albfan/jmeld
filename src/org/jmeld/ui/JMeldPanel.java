/*
   JMeld is a visual diff and merge tool.
   Copyright (C) 2007  Kees Kuip
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA  02110-1301  USA
 */
package org.jmeld.ui;

import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.layout.*;

import org.jdesktop.swingworker.SwingWorker;
import org.jmeld.*;
import org.jmeld.diff.*;
import org.jmeld.ui.action.*;
import org.jmeld.ui.conf.*;
import org.jmeld.ui.search.*;
import org.jmeld.ui.text.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.*;
import org.jmeld.util.file.*;

import javax.help.*;
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
  private static final String GOTOSELECTED_ACTION = "GoToSelected";
  private static final String GOTOFIRST_ACTION = "GoToFirst";
  private static final String GOTOLAST_ACTION = "GoToLast";
  private static final String STARTSEARCH_ACTION = "StartSearch";
  private static final String STOPSEARCH_ACTION = "StopSearch";
  private static final String NEXTSEARCH_ACTION = "NextSearch";
  private static final String PREVIOUSSEARCH_ACTION = "PreviousSearch";
  private static final String REFRESH_ACTION = "Refresh";
  private static final String MERGEMODE_ACTION = "MergeMode";
  private static final String HELP_ACTION = "Help";
  private static final String ABOUT_ACTION = "About";
  private static final String CONFIGURATION_ACTION = "Configuration";
  private static final String EXIT_ACTION = "Exit";

  // instance variables:
  private ActionHandler actionHandler;
  private JTabbedPane   tabbedPane;
  private JPanel        bar;
  private SearchBar     searchBar;
  private boolean       mergeMode;

  public JMeldPanel(
    String originalName,
    String mineName)
  {
    setFocusable(true);

    tabbedPane = new JTabbedPane();
    tabbedPane.setFocusable(false);

    initActions();

    setLayout(new BorderLayout());
    add(
      getToolBar(),
      BorderLayout.PAGE_START);
    add(tabbedPane, BorderLayout.CENTER);
    add(
      getBar(),
      BorderLayout.PAGE_END);

    tabbedPane.getModel().addChangeListener(getChangeListener());

    openComparison(originalName, mineName);
  }

  public void openComparison(
    String originalName,
    String mineName)
  {
    File originalFile;
    File mineFile;

    if (!StringUtil.isEmpty(originalName) && !StringUtil.isEmpty(mineName))
    {
      originalFile = new File(originalName);
      mineFile = new File(mineName);
      if (originalFile.isDirectory() && mineFile.isDirectory())
      {
        openDirectoryComparison(originalFile, mineFile);
      }
      else
      {
        openFileComparison(originalFile, mineFile, false);
      }
    }
  }

  public void openFileComparison(
    File    originalFile,
    File    mineFile,
    boolean openInBackground)
  {
    WaitCursor.wait(this);

    new NewFileComparisonPanel(originalFile, mineFile, openInBackground)
    .execute();
  }

  public void openDirectoryComparison(
    File originalFile,
    File mineFile)
  {
    WaitCursor.wait(this);

    new NewDirectoryComparisonPanel(originalFile, mineFile).execute();
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

    builder.addSpring();

    button = WidgetFactory.getToolBarButton(
        actionHandler.get(CONFIGURATION_ACTION));
    builder.addButton(button);

    button = WidgetFactory.getToolBarButton(actionHandler.get(HELP_ACTION));
    builder.addButton(button);

    button = WidgetFactory.getToolBarButton(actionHandler.get(ABOUT_ACTION));
    builder.addButton(button);

    return toolBar;
  }

  private JComponent getBar()
  {
    CellConstraints cc;

    cc = new CellConstraints();

    bar = new JPanel(new FormLayout("0:grow", "pref, pref, pref"));
    bar.add(
      new JSeparator(),
      cc.xy(1, 2));
    bar.add(
      StatusBar.getInstance(),
      cc.xy(1, 3));

    return bar;
  }

  private SearchBar getSearchBar()
  {
    if (searchBar == null)
    {
      searchBar = new SearchBar(this);
    }

    return searchBar;
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
    installKey("LEFT", action);
    installKey("alt LEFT", action);
    installKey("alt KP_LEFT", action);

    action = actionHandler.createAction(this, RIGHT_ACTION);
    installKey("RIGHT", action);
    installKey("alt RIGHT", action);
    installKey("alt KP_RIGHT", action);

    action = actionHandler.createAction(this, UP_ACTION);
    installKey("UP", action);
    installKey("alt UP", action);
    installKey("alt KP_UP", action);
    installKey("F7", action);

    action = actionHandler.createAction(this, DOWN_ACTION);
    installKey("DOWN", action);
    installKey("alt DOWN", action);
    installKey("alt KP_DOWN", action);
    installKey("F8", action);

    action = actionHandler.createAction(this, ZOOMPLUS_ACTION);
    installKey("alt EQUALS", action);
    installKey("shift alt EQUALS", action);
    installKey("alt ADD", action);

    action = actionHandler.createAction(this, ZOOMMIN_ACTION);
    installKey("alt MINUS", action);
    installKey("shift alt MINUS", action);
    installKey("alt SUBTRACT", action);

    action = actionHandler.createAction(this, GOTOSELECTED_ACTION);
    installKey("alt ENTER", action);

    action = actionHandler.createAction(this, GOTOFIRST_ACTION);
    installKey("alt HOME", action);

    action = actionHandler.createAction(this, GOTOLAST_ACTION);
    installKey("alt END", action);

    action = actionHandler.createAction(this, STARTSEARCH_ACTION);
    installKey("ctrl F", action);

    action = actionHandler.createAction(this, NEXTSEARCH_ACTION);
    installKey("F3", action);
    installKey("ctrl G", action);

    action = actionHandler.createAction(this, PREVIOUSSEARCH_ACTION);
    installKey("shift F3", action);

    action = actionHandler.createAction(this, REFRESH_ACTION);
    installKey("F5", action);

    action = actionHandler.createAction(this, MERGEMODE_ACTION);
    installKey("F9", action);

    action = actionHandler.createAction(this, HELP_ACTION);
    action.setIcon("stock_help-agent");
    installKey("F1", action);

    action = actionHandler.createAction(this, ABOUT_ACTION);
    action.setIcon("stock_about");

    action = actionHandler.createAction(this, CONFIGURATION_ACTION);
    action.setIcon("stock_preferences");
    action.setToolTip("Configuration");

    action = actionHandler.createAction(this, EXIT_ACTION);
    installKey("ESCAPE", action);
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
        new File(dialog.getOriginalFileName()),
        new File(dialog.getMineFileName()),
        false);
    }
    else if (dialog.getValue() == NewPanelDialog.DIRECTORY_COMPARISON)
    {
      openDirectoryComparison(
        new File(dialog.getOriginalDirectoryName()),
        new File(dialog.getMineDirectoryName()));
    }
  }

  public void doSave(ActionEvent ae)
  {
    getCurrentContentPanel().doSave();
  }

  public boolean isSaveEnabled()
  {
    JMeldContentPanelIF panel;

    panel = getCurrentContentPanel();
    if (panel == null)
    {
      return false;
    }

    return panel.isSaveEnabled();
  }

  public void doUndo(ActionEvent ae)
  {
    getCurrentContentPanel().doUndo();
  }

  public boolean isUndoEnabled()
  {
    JMeldContentPanelIF panel;

    panel = getCurrentContentPanel();
    if (panel == null)
    {
      return false;
    }

    return panel.isUndoEnabled();
  }

  public void doRedo(ActionEvent ae)
  {
    getCurrentContentPanel().doRedo();
  }

  public boolean isRedoEnabled()
  {
    JMeldContentPanelIF panel;

    panel = getCurrentContentPanel();
    if (panel == null)
    {
      return false;
    }

    return panel.isRedoEnabled();
  }

  public void doLeft(ActionEvent ae)
  {
    getCurrentContentPanel().doLeft();
    repaint();
  }

  public void doRight(ActionEvent ae)
  {
    getCurrentContentPanel().doRight();
    repaint();
  }

  public void doUp(ActionEvent ae)
  {
    getCurrentContentPanel().doUp();
    repaint();
  }

  public void doDown(ActionEvent ae)
  {
    getCurrentContentPanel().doDown();
    repaint();
  }

  public void doZoomPlus(ActionEvent ae)
  {
    getCurrentContentPanel().doZoom(true);
    repaint();
  }

  public void doZoomMin(ActionEvent ae)
  {
    getCurrentContentPanel().doZoom(false);
    repaint();
  }

  public void doGoToSelected(ActionEvent ae)
  {
    getCurrentContentPanel().doGoToSelected();
    repaint();
  }

  public void doGoToFirst(ActionEvent ae)
  {
    getCurrentContentPanel().doGoToFirst();
    repaint();
  }

  public void doGoToLast(ActionEvent ae)
  {
    getCurrentContentPanel().doGoToLast();
    repaint();
  }

  public void doStartSearch(ActionEvent ae)
  {
    CellConstraints cc;
    SearchBar       sb;

    sb = getSearchBar();

    cc = new CellConstraints();
    bar.add(
      sb,
      cc.xy(1, 1));
    sb.activate();
    bar.revalidate();
  }

  public void doStopSearch(ActionEvent ae)
  {
    CellConstraints cc;

    cc = new CellConstraints();
    bar.remove(getSearchBar());
    bar.revalidate();
  }

  public SearchHits doSearch(ActionEvent ae)
  {
    return getCurrentContentPanel().doSearch(searchBar.getCommand());
  }

  public void doNextSearch(ActionEvent ae)
  {
    getCurrentContentPanel().doNextSearch();
  }

  public void doPreviousSearch(ActionEvent ae)
  {
    getCurrentContentPanel().doPreviousSearch();
  }

  public void doRefresh(ActionEvent ae)
  {
    getCurrentContentPanel().doRefresh();
  }

  public void doMergeMode(ActionEvent ae)
  {
    MeldAction action;

    mergeMode = !mergeMode;

    action = actionHandler.get(LEFT_ACTION);
    installKey(mergeMode, "LEFT", action);

    action = actionHandler.get(RIGHT_ACTION);
    installKey(mergeMode, "RIGHT", action);

    action = actionHandler.get(UP_ACTION);
    installKey(mergeMode, "UP", action);

    action = actionHandler.get(DOWN_ACTION);
    installKey(mergeMode, "DOWN", action);

    getCurrentContentPanel().doMergeMode(mergeMode);
    requestFocus();

    if (mergeMode)
    {
      StatusBar.setNotification(
        MERGEMODE_ACTION,
        ImageUtil.getSmallImageIcon("jmeld_mergemode-on"));
    }
    else
    {
      StatusBar.removeNotification(MERGEMODE_ACTION);
    }
  }

  public void doHelp(ActionEvent ae)
  {
    try
    {
      JPanel               panel;
      AbstractContentPanel content;
      URL                  url;
      HelpSet              helpSet;
      JHelpContentViewer   viewer;
      JHelpNavigator       navigator;
      NavigatorView        navigatorView;
      JSplitPane           splitPane;

      url = HelpSet.findHelpSet(
          getClass().getClassLoader(),
          "jmeld");
      helpSet = new HelpSet(
          getClass().getClassLoader(),
          url);
      viewer = new JHelpContentViewer(helpSet);

      navigatorView = helpSet.getNavigatorView("TOC");
      navigator = (JHelpNavigator) navigatorView.createNavigator(
          viewer.getModel());

      splitPane = new JSplitPane();
      splitPane.setLeftComponent(navigator);
      splitPane.setRightComponent(viewer);

      content = new AbstractContentPanel();
      content.setLayout(new BorderLayout());
      content.add(splitPane, BorderLayout.CENTER);

      tabbedPane.add(
        content,
        getTabIcon("stock_help-agent", "Help"));
      tabbedPane.setSelectedComponent(content);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public void doAbout(ActionEvent ae)
  {
    AbstractContentPanel content;

    content = new AbstractContentPanel();
    content.setLayout(new BorderLayout());
    content.add(
      new JButton("JMeld version: " + Version.getVersion()),
      BorderLayout.CENTER);

    tabbedPane.add(
      content,
      getTabIcon("stock_about", "About"));
    tabbedPane.setSelectedComponent(content);
  }

  public void doExit(ActionEvent ae)
  {
    // Stop the searchBar if it is showing.
    if (getSearchBar().getParent() != null)
    {
      doStopSearch(ae);
      return;
    }

    // Exit a tab!
    doExitTab((Component) getCurrentContentPanel());
  }

  public void doConfiguration(ActionEvent ae)
  {
    AbstractContentPanel content;

    content = new ConfigurationPanel();

    tabbedPane.add(
      content,
      getTabIcon("stock_preferences", "Configuration"));
    tabbedPane.setSelectedComponent(content);
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

  public WindowListener getWindowListener()
  {
    return new WindowAdapter()
      {
        public void windowClosing(WindowEvent we)
        {
          JMeldContentPanelIF contentPanel;

          for (int i = 0; i < tabbedPane.getTabCount(); i++)
          {
            contentPanel = (JMeldContentPanelIF) tabbedPane.getComponentAt(i);
            if (!contentPanel.checkSave())
            {
              return;
            }
          }

          System.exit(1);
        }
      };
  }

  private JMeldContentPanelIF getCurrentContentPanel()
  {
    return (JMeldContentPanelIF) tabbedPane.getSelectedComponent();
  }

  class NewFileComparisonPanel
         extends SwingWorker<String, Object>
  {
    private File             originalFile;
    private File             mineFile;
    private boolean          openInBackground;
    private BufferDocumentIF bd1;
    private BufferDocumentIF bd2;
    private JMDiff           diff;
    private JMRevision       revision;

    NewFileComparisonPanel(
      File    originalFile,
      File    mineFile,
      boolean openInBackground)
    {
      this.originalFile = originalFile;
      this.mineFile = mineFile;
      this.openInBackground = openInBackground;
    }

    public String doInBackground()
    {
      if (StringUtil.isEmpty(originalFile.getName()))
      {
        return "original filename is empty";
      }

      if (!originalFile.exists())
      {
        return "original filename(" + originalFile.getName()
        + ") doesn't exist";
      }

      if (StringUtil.isEmpty(mineFile.getName()))
      {
        return "mine filename is empty";
      }

      if (!mineFile.exists())
      {
        return "mine filename(" + mineFile.getName() + ") doesn't exist";
      }

      try
      {
        StatusBar.start();
        StatusBar.setStatus("Reading file: " + originalFile.getName());
        bd1 = new FileDocument(originalFile);
        bd1.read();

        StatusBar.setStatus("Reading file: " + mineFile.getName());
        bd2 = new FileDocument(mineFile);
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
            getTabIcon(
              "stock_new",
              panel.getTitle()));
          if (!openInBackground)
          {
            tabbedPane.setSelectedComponent(panel);

            // Goto the first delta:
            // This should be invoked after the panel is displayed!
            SwingUtilities.invokeLater(
              new Runnable()
              {
                public void run()
                {
                  doGoToFirst(null);
                }
              });
          }
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
    private File          originalFile;
    private File          mineFile;
    private DirectoryDiff diff;

    NewDirectoryComparisonPanel(
      File originalFile,
      File mineFile)
    {
      this.originalFile = originalFile;
      this.mineFile = mineFile;
    }

    public String doInBackground()
    {
      if (StringUtil.isEmpty(originalFile.getName()))
      {
        return "original directoryName is empty";
      }

      if (!originalFile.exists())
      {
        return "original directoryName(" + originalFile.getName()
        + ") doesn't exist";
      }

      if (!originalFile.isDirectory())
      {
        return "original directoryName(" + originalFile.getName()
        + ") is not a directory";
      }

      if (StringUtil.isEmpty(mineFile.getName()))
      {
        return "mine directoryName is empty";
      }

      if (!mineFile.exists())
      {
        return "mine directoryName(" + mineFile.getName() + ") doesn't exist";
      }

      if (!mineFile.isDirectory())
      {
        return "mine directoryName(" + mineFile.getName()
        + ") is not a directory";
      }

      diff = new DirectoryDiff(originalFile, mineFile);
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
          getTabIcon(
            "stock_folder",
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
    boolean    enabled,
    String     key,
    MeldAction action)
  {
    if (!enabled)
    {
      deInstallKey(key, action);
    }
    else
    {
      installKey(key, action);
    }
  }

  private void installKey(
    String     key,
    MeldAction action)
  {
    InputMap  inputMap;
    ActionMap actionMap;
    KeyStroke stroke;

    stroke = KeyStroke.getKeyStroke(key);

    inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    if (inputMap.get(stroke) != action.getName())
    {
      inputMap.put(
        stroke,
        action.getName());
    }

    actionMap = getActionMap();
    if (actionMap.get(action.getName()) != action)
    {
      actionMap.put(
        action.getName(),
        action);
    }
  }

  private void deInstallKey(
    String     key,
    MeldAction action)
  {
    InputMap  inputMap;
    ActionMap actionMap;
    KeyStroke stroke;

    stroke = KeyStroke.getKeyStroke(key);
    inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    inputMap.get(stroke);
    inputMap.remove(stroke);

    // Do not deinstall the action because I don't know how many other
    //   inputmap residents will call the action.
  }

  private TabIcon getTabIcon(
    String iconName,
    String text)
  {
    TabIcon icon;

    icon = new TabIcon(
        ImageUtil.getSmallImageIcon(iconName),
        text);
    icon.addExitListener(getTabExitListener());

    return icon;
  }

  private TabExitListenerIF getTabExitListener()
  {
    return new TabExitListenerIF()
      {
        public boolean doExit(TabExitEvent te)
        {
          int                  tabIndex;
          Component            component;
          AbstractContentPanel content;

          tabIndex = te.getTabIndex();
          if (tabIndex == -1)
          {
            return false;
          }

          return doExitTab(tabbedPane.getComponentAt(tabIndex));
        }
      };
  }

  private boolean doExitTab(Component component)
  {
    AbstractContentPanel content;
    Icon                 icon;
    int                  index;

    if (component == null)
    {
      return false;
    }

    index = tabbedPane.indexOfComponent(component);
    if (index == -1)
    {
      return false;
    }

    if (component instanceof AbstractContentPanel)
    {
      content = (AbstractContentPanel) component;
      if (!content.checkSave())
      {
        return false;
      }
    }

    icon = tabbedPane.getIconAt(index);
    if (icon != null && icon instanceof TabIcon)
    {
      ((TabIcon) icon).exit();
    }

    tabbedPane.remove(component);

    return true;
  }
}
