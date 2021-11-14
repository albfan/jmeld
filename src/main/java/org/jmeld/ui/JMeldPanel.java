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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.swing.JideTabbedPane;
import org.jmeld.Version;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.action.ActionHandler;
import org.jmeld.ui.action.Actions;
import org.jmeld.ui.action.MeldAction;
import org.jmeld.ui.bar.LineNumberBarDialog;
import org.jmeld.ui.search.SearchBarDialog;
import org.jmeld.ui.search.SearchCommand;
import org.jmeld.ui.search.SearchHits;
import org.jmeld.ui.settings.SettingsPanel;
import org.jmeld.ui.util.*;
import org.jmeld.util.ObjectUtil;
import org.jmeld.util.StringUtil;
import org.jmeld.util.conf.ConfigurationListenerIF;
import org.jmeld.vc.VersionControlUtil;

import javax.help.HelpSet;
import javax.help.JHelpContentViewer;
import javax.help.JHelpNavigator;
import javax.help.NavigatorView;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class JMeldPanel extends JPanel implements ConfigurationListenerIF, PropertyChangeListener {
    public final Actions actions;

    // Options (enable/disable before adding this component to its container)
    public final Option SHOW_TOOLBAR_OPTION;
    public final Option SHOW_STATUSBAR_OPTION;
    public final Option SHOW_TABBEDPANE_OPTION;
    public final Option SHOW_FILE_TOOLBAR_OPTION;
    public final Option SHOW_FILE_STATUSBAR_OPTION;
    public final Option STANDALONE_INSTALLKEY_OPTION;

    private ActionHandler actionHandler;
    private JideTabbedPane tabbedPane;
    private JPanel barContainer;
    private AbstractBarDialog currentBarDialog;
    private SearchBarDialog searchBarDialog;
    private JComponent toolBar;
    private boolean mergeMode;
    private boolean started;

    public JMeldPanel() {
        setFocusable(true);

        tabbedPane = new JideTabbedPane();
        addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {
                start();
            }

            public void ancestorMoved(AncestorEvent event) {
            }

            public void ancestorRemoved(AncestorEvent event) {
            }
        });
        SHOW_TOOLBAR_OPTION = new Option(this, true);
        SHOW_STATUSBAR_OPTION = new Option(this, true);
        SHOW_TABBEDPANE_OPTION = new Option(this, true);
        SHOW_FILE_TOOLBAR_OPTION = new Option(this, true);
        SHOW_FILE_STATUSBAR_OPTION = new Option(this, true);
        STANDALONE_INSTALLKEY_OPTION = new Option(this, false);
        actions = new Actions();
    }

    public JideTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    private void start() {
        if (started) {
            return;
        }

        started = true;

        getTabbedPane().setFocusable(false);
        getTabbedPane().setShowCloseButtonOnTab(true);
        getTabbedPane().setShowCloseButtonOnSelectedTab(true);

        if (!SHOW_TABBEDPANE_OPTION.isEnabled()) {
            getTabbedPane().setShowTabArea(false);
        }

        // Pin the tabshape because the defaults do not look good
        //   on lookandfeels other than JGoodies Plastic.
        getTabbedPane().setTabShape(JideTabbedPane.SHAPE_OFFICE2003);

        // Watch out: initActions uses 'tabbedPane' so this statement should be
        //   after the instantiation of tabbedPane.
        initActions();

        if (SHOW_TABBEDPANE_OPTION.isEnabled()) {
            // Watch out: actionHandler gets initialized in 'initActions' so this
            //   statement should be AFTER initActions();
            getTabbedPane().setCloseAction(getAction(actions.EXIT));
        }

        setLayout(new BorderLayout());
        addToolBar();
        add(getTabbedPane(), BorderLayout.CENTER);
        add(getBar(), BorderLayout.PAGE_END);

        getTabbedPane().getModel().addChangeListener(getChangeListener());

        JMeldSettings.getInstance().addConfigurationListener(this);

        //setTransferHandler(getDragAndDropHandler());
    }

    public void openComparison(List<String> fileNameList) {
        String fileName1;
        String fileName2;

        if (fileNameList.size() <= 0) {
            return;
        }

/*
        Possibilities;

            1. <fileName>, <fileName>
            2. <fileName>, <fileName>, <fileName>, <fileName>
            3. <directory>, <directory>
            4. <directory>, <fileName>, <fileName> ...
            5. <directory (version controlled)>

        ad 2:
            I always assume filepairs!
            for instance: <file1> <file2> <file3> <file4>
            will open 2 filediffs "file1-file2" and "file3-file4".
        ad 4:
            The fileNames are relative and are also available in
            the <directory>. So this enables you to do:
        jmeld ../branches/branch1 src/lala.java src/haha.java
            This results in 2 compares:
                1. ../branches/branch1/src/lala.java with ./src/lala.java
                2. ../branches/branch1/src/haha.java with ./src/haha.java
*/
        if (fileNameList.size() > 1) {
            if (new File(fileNameList.get(0)).isDirectory()) {
                fileName1 = fileNameList.get(0);
                for (int i = 1; i < fileNameList.size(); i++) {
                    fileName2 = fileNameList.get(i);
                    openComparison(fileName1, fileName2);
                }
            } else {
                for (int i = 0; i < fileNameList.size(); i += 2) {
                    fileName1 = fileNameList.get(i);
                    if (i + 1 >= fileNameList.size()) {
                        continue;
                    }

                    fileName2 = fileNameList.get(i + 1);
                    openComparison(fileName1, fileName2);
                }
            }
        } else {
            openComparison(fileNameList.get(0), null);
        }
    }

    public void openComparison(String leftName,
                               String rightName) {
        File leftFile;
        File rightFile;
        File file;

        if (!StringUtil.isEmpty(leftName) && !StringUtil.isEmpty(rightName)) {
            leftFile = new File(leftName);
            rightFile = new File(rightName);
            if (leftFile.isDirectory()) {
                if (rightFile.isDirectory()) {
                    new DirectoryComparison(this, leftFile, rightFile, JMeldSettings
                                        .getInstance().getFilter().getFilter("default")).execute();
                } else {
                    FileComparison fileComparison = new FileComparison(this, new File(leftFile, rightName), rightFile);
                    fileComparison.setOpenInBackground(false);
                    fileComparison.execute();
                }
            } else {
                FileComparison fileComparison = new FileComparison(this, leftFile, rightFile);
                fileComparison.setOpenInBackground(false);
                fileComparison.execute();
            }
        } else {
            if (!StringUtil.isEmpty(leftName)) {
                file = new File(leftName);
                if (file.exists() && VersionControlUtil.isVersionControlled(file)) {
                    VersionControlComparison versionControlComparison = new VersionControlComparison(this, file);
                    versionControlComparison.execute();
                }
            }
        }
    }

    public MeldAction getAction(Actions.Action action) {
        return getActionHandler().get(action);
    }

    public void addToolBar() {
        if (SHOW_TOOLBAR_OPTION.isEnabled()) {
            if (toolBar != null) {
                remove(toolBar);
            }

            toolBar = getToolBar();
            add(toolBar, BorderLayout.PAGE_START);

            revalidate();
        }
    }

    private JComponent getToolBar() {
        JButton button;
        JToolBar tb;
        ToolBarBuilder builder;

        tb = new JToolBar();
        tb.setFloatable(false);
        tb.setRollover(true);

        builder = new ToolBarBuilder(tb);

        button = WidgetFactory.getToolBarButton(getAction(actions.NEW));
        builder.addButton(button);
        button = WidgetFactory.getToolBarButton(getAction(actions.SAVE));
        builder.addButton(button);

        builder.addSeparator();

        button = WidgetFactory.getToolBarButton(getAction(actions.UNDO));
        builder.addButton(button);
        button = WidgetFactory.getToolBarButton(getAction(actions.REDO));
        builder.addButton(button);

        builder.addSpring();

        button = WidgetFactory.getToolBarButton(getAction(actions.SETTINGS));
        builder.addButton(button);

        button = WidgetFactory.getToolBarButton(getAction(actions.HELP));
        builder.addButton(button);

        button = WidgetFactory.getToolBarButton(getAction(actions.ABOUT));
        builder.addButton(button);

        return tb;
    }

    private JComponent getBar() {
        CellConstraints cc;

        cc = new CellConstraints();

        barContainer = new JPanel(new FormLayout("0:grow", "pref, pref, pref"));
        barContainer.add(new JSeparator(), cc.xy(1, 2));
        if (SHOW_STATUSBAR_OPTION.isEnabled()) {
            barContainer.add(StatusBar.getInstance(), cc.xy(1, 3));
        }

        return barContainer;
    }

    private SearchBarDialog getSearchBarDialog() {
        if (searchBarDialog == null) {
            searchBarDialog = new SearchBarDialog(this);
        }

        return searchBarDialog;
    }

    public void initActions() {
        MeldAction action;

        actionHandler = new ActionHandler();

        action = actionHandler.createAction(this, actions.NEW);
        action.setIcon("stock_new");
        action.setToolTip("Merge 2 new files");

        action = actionHandler.createAction(this, actions.SAVE);
        action.setIcon("stock_save");
        action.setToolTip("Save the changed files");
        if (!STANDALONE_INSTALLKEY_OPTION.isEnabled()) {
            installKey("ctrl S", action);
        }

        action = actionHandler.createAction(this, actions.UNDO);
        action.setIcon("stock_undo");
        action.setToolTip("Undo the latest change");
        installKey("control Z", action);
        installKey("control Y", action);

        action = actionHandler.createAction(this, actions.REDO);
        action.setIcon("stock_redo");
        action.setToolTip("Redo the latest change");
        installKey("control R", action);

        action = actionHandler.createAction(this, actions.LEFT);
        installKey("LEFT", action);
        installKey("alt LEFT", action);
        installKey("alt KP_LEFT", action);

        action = actionHandler.createAction(this, actions.RIGHT);
        installKey("RIGHT", action);
        installKey("alt RIGHT", action);
        installKey("alt KP_RIGHT", action);

        action = actionHandler.createAction(this, actions.UP);
        installKey("UP", action);
        installKey("alt UP", action);
        installKey("alt KP_UP", action);
        installKey("F7", action);

        action = actionHandler.createAction(this, actions.DOWN);
        installKey("DOWN", action);
        installKey("alt DOWN", action);
        installKey("alt KP_DOWN", action);
        installKey("F8", action);

        action = actionHandler.createAction(this, actions.ZOOM_PLUS);
        installKey("alt EQUALS", action);
        installKey("shift alt EQUALS", action);
        installKey("alt ADD", action);

        action = actionHandler.createAction(this, actions.ZOOM_MIN);
        installKey("alt MINUS", action);
        installKey("shift alt MINUS", action);
        installKey("alt SUBTRACT", action);

        action = actionHandler.createAction(this, actions.GOTO_SELECTED);
        installKey("alt ENTER", action);

        action = actionHandler.createAction(this, actions.GOTO_FIRST);
        installKey("alt HOME", action);

        action = actionHandler.createAction(this, actions.GOTO_LAST);
        installKey("alt END", action);

        action = actionHandler.createAction(this, actions.GOTO_LINE);
        installKey("ctrl L", action);

        action = actionHandler.createAction(this, actions.START_SEARCH);
        installKey("ctrl F", action);

        action = actionHandler.createAction(this, actions.NEXT_SEARCH);
        installKey("F3", action);
        installKey("ctrl G", action);

        action = actionHandler.createAction(this, actions.PREVIOUS_SEARCH);
        installKey("shift F3", action);

        action = actionHandler.createAction(this, actions.REFRESH);
        installKey("F5", action);

        action = actionHandler.createAction(this, actions.MERGEMODE);
        installKey("F9", action);

        if (!STANDALONE_INSTALLKEY_OPTION.isEnabled()) {
            action = actionHandler.createAction(this, actions.HELP);
            action.setIcon("stock_help-agent");
            installKey("F1", action);

            action = actionHandler.createAction(this, actions.ABOUT);
            action.setIcon("stock_about");

            action = actionHandler.createAction(this, actions.SETTINGS);
            action.setIcon("stock_preferences");
            action.setToolTip("Settings");

            action = actionHandler.createAction(this, actions.EXIT);
            installKey("ESCAPE", action);
        }
    }

    public ActionHandler getActionHandler() {
        return actionHandler;
    }

    public void checkActions() {
        if (actionHandler != null) {
            actionHandler.checkActions();
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())
                && org.jdesktop.swingworker.SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
            try {
                String result = (String)((org.jdesktop.swingworker.SwingWorker) evt.getSource()).get();
                if (result != null) {
                    launchDialog();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

    }
    public void doNew(ActionEvent ae) {
        launchDialog();
    }

    private void launchDialog() {
        PanelDialog dialog;

        dialog = new PanelDialog(this);
        dialog.show();


        org.jdesktop.swingworker.SwingWorker<String, Object> worker = null;
        if (dialog.getFunction() == PanelDialog.Function.FILE_COMPARISON) {
            worker = new FileComparison(this, new File(dialog.getLeftFileName()), new File(dialog
                        .getRightFileName()));
        } else if (dialog.getFunction() == PanelDialog.Function.DIRECTORY_COMPARISON) {
            worker = new DirectoryComparison(this,
                    new File(dialog.getLeftDirectoryName()), new File(dialog.getRightDirectoryName()), dialog
                    .getFilter());
        } else if (dialog.getFunction() == PanelDialog.Function.VERSION_CONTROL) {
            worker = new VersionControlComparison(this, new File(dialog
                    .getVersionControlDirectoryName()));
        }
        worker.addPropertyChangeListener(this);
        worker.execute();

    }

    public void doSave(ActionEvent ae) {
        getCurrentContentPanel().doSave();
    }

    public boolean isSaveEnabled() {
        JMeldContentPanelIF panel;

        panel = getCurrentContentPanel();
        if (panel == null) {
            return false;
        }

        return panel.isSaveEnabled();
    }

    public void doUndo(ActionEvent ae) {
        getCurrentContentPanel().doUndo();
    }

    public boolean isUndoEnabled() {
        JMeldContentPanelIF panel;

        panel = getCurrentContentPanel();
        if (panel == null) {
            return false;
        }

        return panel.isUndoEnabled();
    }

    public void doRedo(ActionEvent ae) {
        getCurrentContentPanel().doRedo();
    }

    public boolean isRedoEnabled() {
        JMeldContentPanelIF panel;

        panel = getCurrentContentPanel();
        if (panel == null) {
            return false;
        }

        return panel.isRedoEnabled();
    }

    public void doLeft(ActionEvent ae) {
        boolean shift = false;
        int onmask = MouseEvent.SHIFT_DOWN_MASK | MouseEvent.BUTTON1_DOWN_MASK;
        int offmask = MouseEvent.CTRL_DOWN_MASK;
        if ((ae.getModifiers() & (onmask | offmask)) == onmask) {
            shift = true;
        }
        getCurrentContentPanel().doLeft(shift);
        repaint();
    }

    public void doRight(ActionEvent ae) {
        boolean shift = false;
        int onmask = MouseEvent.SHIFT_DOWN_MASK | MouseEvent.BUTTON1_DOWN_MASK;
        int offmask = MouseEvent.CTRL_DOWN_MASK;
        if ((ae.getModifiers() & (onmask | offmask)) == onmask) {
            shift = true;
        }
        getCurrentContentPanel().doRight(shift);
        repaint();
    }

    public void doUp(ActionEvent ae) {
        getCurrentContentPanel().doUp();
        repaint();
    }

    public void doDown(ActionEvent ae) {
        getCurrentContentPanel().doDown();
        repaint();
    }

    public void doZoomPlus(ActionEvent ae) {
        getCurrentContentPanel().doZoom(true);
        repaint();
    }

    public void doZoomMin(ActionEvent ae) {
        getCurrentContentPanel().doZoom(false);
        repaint();
    }

    public void doGoToSelected(ActionEvent ae) {
        getCurrentContentPanel().doGoToSelected();
        repaint();
    }

    public void doGoToFirst(ActionEvent ae) {
        getCurrentContentPanel().doGoToFirst();
        repaint();
    }

    public void doGoToLast(ActionEvent ae) {
        getCurrentContentPanel().doGoToLast();
        repaint();
    }

    public void doGoToLine(ActionEvent ae) {
        activateBarDialog(new LineNumberBarDialog(this));
    }

    public void doGoToLine(int line) {
        getCurrentContentPanel().doGoToLine(line);
        repaint();
        deactivateBarDialog();
    }

    public void doStartSearch(ActionEvent ae) {
        SearchBarDialog sbd;

        sbd = getSearchBarDialog();
        sbd.setSearchText(getSelectedSearchText());

        activateBarDialog(sbd);
    }

    public void doStopSearch(ActionEvent ae) {
        deactivateBarDialog();

        for (JMeldContentPanelIF cp : getContentPanelList(getTabbedPane())) {
            cp.doStopSearch();
        }
    }

    public SearchHits doSearch(ActionEvent ae) {
        return getCurrentContentPanel().doSearch();
    }

    SearchCommand getSearchCommand() {
        if (currentBarDialog != getSearchBarDialog()) {
            return null;
        }

        return getSearchBarDialog().getCommand();
    }

    public void doNextSearch(ActionEvent ae) {
        if (currentBarDialog != getSearchBarDialog()) {
            return;
        }

        getCurrentContentPanel().doNextSearch();
    }

    public void doPreviousSearch(ActionEvent ae) {
        if (currentBarDialog != getSearchBarDialog()) {
            return;
        }

        getCurrentContentPanel().doPreviousSearch();
    }

    private String getSelectedSearchText() {
        return getCurrentContentPanel().getSelectedText();
    }

    public void doRefresh(ActionEvent ae) {
        getCurrentContentPanel().doRefresh();
    }

    public void doMergeMode(ActionEvent ae) {
        MeldAction action;

        mergeMode = !mergeMode;

        action = getAction(actions.LEFT);
        installKey(mergeMode, "LEFT", action);

        action = getAction(actions.RIGHT);
        installKey(mergeMode, "RIGHT", action);

        action = getAction(actions.UP);
        installKey(mergeMode, "UP", action);

        action = getAction(actions.DOWN);
        installKey(mergeMode, "DOWN", action);

        getCurrentContentPanel().doMergeMode(mergeMode);
        requestFocus();

        if (mergeMode) {
            StatusBar.getInstance()
                    .setNotification(actions.MERGEMODE.getName(),
                            ImageUtil.getSmallImageIcon("jmeld_mergemode-on"));
        } else {
            StatusBar.getInstance().removeNotification(actions.MERGEMODE.getName());
        }
    }

    public void doHelp(ActionEvent ae) {
        try {
            JPanel panel;
            AbstractContentPanel content;
            URL url;
            HelpSet helpSet;
            JHelpContentViewer viewer;
            JHelpNavigator navigator;
            NavigatorView navigatorView;
            JSplitPane splitPane;
            String contentId;

            contentId = "HelpPanel";
            if (checkAlreadyOpen(contentId)) {
                return;
            }

            url = HelpSet.findHelpSet(getClass().getClassLoader(), "jmeld");
            helpSet = new HelpSet(getClass().getClassLoader(), url);
            viewer = new JHelpContentViewer(helpSet);
            navigatorView = helpSet.getNavigatorView("TOC");
            navigator = (JHelpNavigator) navigatorView.createNavigator(viewer
                    .getModel());
            splitPane = new JSplitPane();
            splitPane.setLeftComponent(navigator);
            splitPane.setRightComponent(viewer);
            content = new AbstractContentPanel();
            content.setId(contentId);
            content.setLayout(new BorderLayout());
            content.add(splitPane, BorderLayout.CENTER);

            /*
              content = new HelpPanel(this);
            */
            getTabbedPane().addTab("Help",
                    ImageUtil.getSmallImageIcon("stock_help-agent"),
                    content);
            getTabbedPane().setSelectedComponent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void doAbout(ActionEvent ae) {
        AbstractContentPanel content;
        String contentId;

        contentId = "AboutPanel";
        if (checkAlreadyOpen(contentId)) {
            return;
        }

        content = new AbstractContentPanel();
        content.setId(contentId);
        content.setLayout(new BorderLayout());
        content.add(new JButton("JMeld version: " + Version.getVersion()),
                BorderLayout.CENTER);

        getTabbedPane().addTab("About", ImageUtil.getSmallImageIcon("stock_about"),
                content);
        getTabbedPane().setSelectedComponent(content);
    }

    public void doExit(ActionEvent ae) {
        JMeldContentPanelIF cp;

        // Stop the searchBarDialog if it is showing.
        if (currentBarDialog == getSearchBarDialog()) {
            doStopSearch(ae);
            return;
        }

        if (currentBarDialog != null) {
            deactivateBarDialog();
            return;
        }

        cp = getCurrentContentPanel();
        if (cp == null) {
            return;
        }

        // Detect if this close is due to pressing ESC.
        if (ae.getSource() == this) {
            if (!cp.checkExit()) {
                return;
            }
        }

        // Exit a tab!
        doExitTab((Component) getCurrentContentPanel());
    }

    public void doSettings(ActionEvent ae) {
        AbstractContentPanel content;
        String contentId;

        contentId = "SettingsPanel";
        if (checkAlreadyOpen(contentId)) {
            return;
        }

        content = new SettingsPanel(this);
        content.setId(contentId);
        getTabbedPane().addTab("Settings", ImageUtil
                .getSmallImageIcon("stock_preferences"), content);
        getTabbedPane().setSelectedComponent(content);
    }

    private boolean checkAlreadyOpen(String contentId) {
        AbstractContentPanel contentPanel;

        contentPanel = getAlreadyOpen(getTabbedPane(), contentId);
        if (contentPanel != null) {
            getTabbedPane().setSelectedComponent(contentPanel);
            return true;
        }

        return false;
    }

    public static AbstractContentPanel getAlreadyOpen(JideTabbedPane tabbedPane, String contentId) {
        for (AbstractContentPanel contentPanel : getContentPanelList(tabbedPane)) {
            if (ObjectUtil.equals(contentPanel.getId(), contentId)) {
                System.out.println("already open: " + contentId);
                return contentPanel;
            }
        }

        return null;
    }

    private ChangeListener getChangeListener() {
        return new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                checkActions();
            }
        };
    }

    public WindowListener getWindowListener() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                for (JMeldContentPanelIF contentPanel : getContentPanelList(getTabbedPane())) {
                    if (!contentPanel.checkSave()) {
                        return;
                    }
                }

                System.exit(1);
            }
        };
    }

    private AbstractContentPanel getCurrentContentPanel() {
        return (AbstractContentPanel) getTabbedPane().getSelectedComponent();
    }

    public static  List<AbstractContentPanel> getContentPanelList(JideTabbedPane tabbedPane) {
        List<AbstractContentPanel> result;

        result = new ArrayList<AbstractContentPanel>();

        if (tabbedPane != null) {
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                result.add((AbstractContentPanel) tabbedPane.getComponentAt(i));
            }
        }

        return result;
    }

    private void installKey(boolean enabled,
                            String key,
                            MeldAction action) {
        if (!enabled) {
            deInstallKey(key, action);
        } else {
            installKey(key, action);
        }
    }

    private void installKey(String key,
                            MeldAction action) {
        SwingUtil.installKey(this, key, action);
    }

    private void deInstallKey(String key,
                              MeldAction action) {
        SwingUtil.deInstallKey(this, key, action);
    }

    /*
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
              int tabIndex;
              tabIndex = te.getTabIndex();
              if (tabIndex == -1)
              {
                return false;
              }
              return doExitTab(tabbedPane.getComponentAt(tabIndex));
            }
          };
      }
    */
    private boolean doExitTab(Component component) {
        AbstractContentPanel content;
        Icon icon;
        int index;

        if (component == null) {
            return false;
        }

        index = getTabbedPane().indexOfComponent(component);
        if (index == -1) {
            return false;
        }

        if (component instanceof AbstractContentPanel) {
            content = (AbstractContentPanel) component;
            if (!content.checkSave()) {
                return false;
            }
        }

        icon = getTabbedPane().getIconAt(index);
        if (icon != null && icon instanceof TabIcon) {
            ((TabIcon) icon).exit();
        }

        getTabbedPane().remove(component);

        return true;
    }

    public void activateBarDialog(AbstractBarDialog bar) {
        CellConstraints cc;

        deactivateBarDialog();

        cc = new CellConstraints();
        barContainer.add(bar, cc.xy(1, 1));
        bar.activate();
        currentBarDialog = bar;
        barContainer.revalidate();
    }

    public void deactivateBarDialog() {
        if (currentBarDialog != null) {
            barContainer.remove(currentBarDialog);
            barContainer.revalidate();
            currentBarDialog.deactivate();
            currentBarDialog = null;
        }
    }

    public void configurationChanged() {
        checkActions();
    }

    /* == JDK6!
      private TransferHandler getDragAndDropHandler()
      {
        return new TransferHandler()
        {
          public boolean canImport(TransferHandler.TransferSupport support)
          {
            System.out.println(support);
            return false;
          }
          public boolean importData(TransferHandler.TransferSupport support)
          {
            return true;
          }
        };
      }
    */

    public boolean isStarted() {
        return started;
    }

}
