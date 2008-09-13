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

import com.jgoodies.forms.layout.*;

import org.jdesktop.swingworker.SwingWorker;
import org.jmeld.*;
import org.jmeld.settings.*;
import org.jmeld.settings.util.*;
import org.jmeld.ui.action.*;
import org.jmeld.ui.search.*;
import org.jmeld.ui.settings.SettingsPanel;
import org.jmeld.ui.util.*;
import org.jmeld.util.*;
import org.jmeld.util.conf.*;
import org.jmeld.util.file.*;
import org.jmeld.util.node.*;

import javax.help.*;
import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.List;

public class JMeldPanel
       extends JPanel
       implements ConfigurationListenerIF
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
  private static final String SETTINGS_ACTION = "Settings";
  private static final String EXIT_ACTION = "Exit";

  // instance variables:
  private ActionHandler actionHandler;
  private JTabbedPane   tabbedPane;
  private JPanel        bar;
  private SearchBar     searchBar;
  private boolean       mergeMode;

  public JMeldPanel()
  {
    setFocusable(true);

    tabbedPane = new JTabbedPane();
    tabbedPane.setFocusable(false);
    //tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
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

    JMeldSettings.getInstance().addConfigurationListener(this);
  }

  public void openComparison(List<String> fileNameList)
  {
    if (fileNameList.size() < 2)
    {
      return;
    }

    // Possibilities;
    // 1. <fileName>, <fileName>
    // 2. <directory>, <directory>
    // 3. <directory>, <fileName>, <fileName> ...
    // ad 3:
    //   The fileNames are relative and are also available in 
    //   the <directory>. So this enables you to do:
    // jmeld ../branches/branch1 src/lala.java src/haha.java
    //   This results in 2 compares:
    //   1. ../branches/branch1/src/lala.java with ./src/lala.java  
    //   2. ../branches/branch1/src/haha.java with ./src/haha.java  

    for (int i = 1; i < fileNameList.size(); i++)
    {
      openComparison(
        fileNameList.get(0),
        fileNameList.get(i));
    }
  }

  public void openComparison(
    String leftName,
    String rightName)
  {
    File leftFile;
    File rightFile;

    if (!StringUtil.isEmpty(leftName) && !StringUtil.isEmpty(rightName))
    {
      leftFile = new File(leftName);
      rightFile = new File(rightName);
      if (leftFile.isDirectory())
      {
        if (rightFile.isDirectory())
        {
          openDirectoryComparison(
            leftFile,
            rightFile,
            JMeldSettings.getInstance().getFilter().getFilter("default"));
        }
        else
        {
          openFileComparison(
            new File(leftFile, rightName),
            rightFile,
            false);
        }
      }
      else
      {
        openFileComparison(leftFile, rightFile, false);
      }
    }
  }

  public void openFileComparison(
    File    leftFile,
    File    rightFile,
    boolean openInBackground)
  {
    new NewFileComparisonPanel(leftFile, rightFile, openInBackground).execute();
  }

  public void openFileComparison(
    JMDiffNode diffNode,
    boolean    openInBackground)
  {
    new NewFileComparisonPanel(diffNode, openInBackground).execute();
  }

  public void openDirectoryComparison(
    File   leftFile,
    File   rightFile,
    Filter filter)
  {
    new NewDirectoryComparisonPanel(leftFile, rightFile, filter).execute();
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
        actionHandler.get(SETTINGS_ACTION));
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
    installKey("ctrl S", action);

    action = actionHandler.createAction(this, UNDO_ACTION);
    action.setIcon("stock_undo");
    action.setToolTip("Undo the latest change");
    installKey("control Z", action);
    installKey("control Y", action);

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

    action = actionHandler.createAction(this, SETTINGS_ACTION);
    action.setIcon("stock_preferences");
    action.setToolTip("Settings");

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

    dialog = new NewPanelDialog(this);
    dialog.show();

    if (dialog.getValue().equals(NewPanelDialog.FILE_COMPARISON))
    {
      openFileComparison(
        new File(dialog.getLeftFileName()),
        new File(dialog.getRightFileName()),
        false);
    }
    else if (dialog.getValue().equals(NewPanelDialog.DIRECTORY_COMPARISON))
    {
      openDirectoryComparison(
        new File(dialog.getLeftDirectoryName()),
        new File(dialog.getRightDirectoryName()),
        dialog.getFilter());
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

  public boolean isLeftEnabled()
  {
    return !JMeldSettings.getInstance().getEditor().getLeftsideReadonly();
  }

  public void doLeft(ActionEvent ae)
  {
    getCurrentContentPanel().doLeft();
    repaint();
  }

  public boolean isRightEnabled()
  {
    return !JMeldSettings.getInstance().getEditor().getRightsideReadonly();
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
    String          selectedText;

    sb = getSearchBar();

    cc = new CellConstraints();
    bar.add(
      sb,
      cc.xy(1, 1));
    sb.setSearchText(getSelectedSearchText());
    sb.activate();
    bar.revalidate();
  }

  public void doStopSearch(ActionEvent ae)
  {
    bar.remove(getSearchBar());
    bar.revalidate();

    getCurrentContentPanel().doStopSearch();
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

  private String getSelectedSearchText()
  {
    return getCurrentContentPanel().getSelectedText();
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
      StatusBar.getInstance().setNotification(
        MERGEMODE_ACTION,
        ImageUtil.getSmallImageIcon("jmeld_mergemode-on"));
    }
    else
    {
      StatusBar.getInstance().removeNotification(MERGEMODE_ACTION);
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
    JMeldContentPanelIF cp;

    // Stop the searchBar if it is showing.
    if (getSearchBar().getParent() != null)
    {
      doStopSearch(ae);
      return;
    }

    cp = getCurrentContentPanel();
    if(cp == null)
    {
      return;
    }

    if (!cp.checkExit())
    {
      return;
    }

    // Exit a tab!
    doExitTab((Component) getCurrentContentPanel());
  }

  public void doSettings(ActionEvent ae)
  {
    AbstractContentPanel content;

    content = new SettingsPanel(this);

    tabbedPane.add(
      content,
      getTabIcon("stock_preferences", "Settings"));
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
        @Override
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
    private JMDiffNode      diffNode;
    private File            leftFile;
    private File            rightFile;
    private boolean         openInBackground;
    private BufferDiffPanel panel;

    NewFileComparisonPanel(
      JMDiffNode diffNode,
      boolean    openInBackground)
    {
      this.diffNode = diffNode;
      this.openInBackground = openInBackground;
    }

    NewFileComparisonPanel(
      File    leftFile,
      File    rightFile,
      boolean openInBackground)
    {
      this.leftFile = leftFile;
      this.rightFile = rightFile;
      this.openInBackground = openInBackground;
    }

    public String doInBackground()
    {
      try
      {
        if (diffNode == null)
        {
          if (StringUtil.isEmpty(leftFile.getName()))
          {
            return "left filename is empty";
          }

          if (!leftFile.exists())
          {
            return "left filename(" + leftFile.getName() + ") doesn't exist";
          }

          if (StringUtil.isEmpty(rightFile.getName()))
          {
            return "right filename is empty";
          }

          if (!rightFile.exists())
          {
            return "right filename(" + rightFile.getName() + ") doesn't exist";
          }

          diffNode = JMDiffNodeFactory.create(
              leftFile.getName(),
              leftFile,
              rightFile.getName(),
              rightFile);
        }

        diffNode.diff();
      }
      catch (Exception ex)
      {
        ex.printStackTrace();

        return ex.getMessage();
      }

      return null;
    }

    @Override
    protected void done()
    {
      try
      {
        String result;

        result = get();

        if (result != null)
        {
          JOptionPane.showMessageDialog(JMeldPanel.this, result,
            "Error opening file", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
          panel = new BufferDiffPanel(JMeldPanel.this);
          panel.setDiffNode(diffNode);
          tabbedPane.add(
            panel,
            getTabIcon(
              "stock_new",
              panel.getTitle()));
          if (!openInBackground)
          {
            tabbedPane.setSelectedComponent(panel);
          }

          panel.doGoToFirst();
          panel.repaint();

          // Goto the first delta:
          // This should be invoked after the panel is displayed!
          SwingUtilities.invokeLater(getDoGoToFirst());
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }

    private Runnable getDoGoToFirst()
    {
      return new Runnable()
        {
          public void run()
          {
            panel.doGoToFirst();
            panel.repaint();
          }
        };
    }
  }

  class NewDirectoryComparisonPanel
         extends SwingWorker<String, Object>
  {
    private File          leftFile;
    private File          rightFile;
    private Filter        filter;
    private DirectoryDiff diff;

    NewDirectoryComparisonPanel(
      File   leftFile,
      File   rightFile,
      Filter filter)
    {
      this.leftFile = leftFile;
      this.rightFile = rightFile;
      this.filter = filter;
    }

    public String doInBackground()
    {
      if (StringUtil.isEmpty(leftFile.getName()))
      {
        return "left directoryName is empty";
      }

      if (!leftFile.exists())
      {
        return "left directoryName(" + leftFile.getName() + ") doesn't exist";
      }

      if (!leftFile.isDirectory())
      {
        return "left directoryName(" + leftFile.getName()
        + ") is not a directory";
      }

      if (StringUtil.isEmpty(rightFile.getName()))
      {
        return "right directoryName is empty";
      }

      if (!rightFile.exists())
      {
        return "right directoryName(" + rightFile.getName()
        + ") doesn't exist";
      }

      if (!rightFile.isDirectory())
      {
        return "right directoryName(" + rightFile.getName()
        + ") is not a directory";
      }

      diff = new DirectoryDiff(leftFile, rightFile, filter,
          DirectoryDiff.Mode.TWO_WAY);
      diff.diff();

      return null;
    }

    @Override
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
    SwingUtil.installKey(this, key, action);
  }

  private void deInstallKey(
    String     key,
    MeldAction action)
  {
    SwingUtil.deInstallKey(this, key, action);
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

  public void configurationChanged()
  {
    checkActions();
  }
}
