/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jmeld.ui;

import org.jmeld.ui.action.Actions;
import org.jmeld.vc.util.VcCmd;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 *
 * @author kees
 */
public class JMeldComponent extends Container {
    private JMeldPanel meldPanel;

    public JMeldComponent() {
        meldPanel = new JMeldPanel();

        meldPanel.SHOW_TABBEDPANE_OPTION.disable();
        meldPanel.SHOW_TOOLBAR_OPTION.disable();
        meldPanel.SHOW_STATUSBAR_OPTION.disable();
        meldPanel.SHOW_FILE_TOOLBAR_OPTION.disable();
        meldPanel.SHOW_FILE_STATUSBAR_OPTION.disable();

        setLayout(new BorderLayout());
        add(meldPanel, BorderLayout.CENTER);
    }

    public Actions getActions() {
        return meldPanel.actions;
    }

    public Action getAction(Actions.Action action)
    {
        return meldPanel.getAction(action);
    }

    static public void main(String args[]) {
        JFrame         frame;
        JMeldComponent jmc;
        JPanel         panel;
        Actions        actions;

        File file = VcCmd.parseFile(args, 0);
        if (file == null) {
            return;
        }

        File file2 = VcCmd.parseFile(args, 1);
        if (file2 == null) {
            return;
        }

        jmc = new JMeldComponent();

        panel = new JPanel(new BorderLayout());
        panel.add(jmc, BorderLayout.CENTER);

        actions = jmc.getActions();
        actions.SAVE.option.disable();

        frame = new JFrame("Standalone JMeld");
        frame.add(jmc);
        frame.setSize(400, 200);
        jmc.openComparison(file, file2);
        frame.setVisible(true);
    }

    private void openComparison(File file, File file2) {
        FileComparisonPanel fileComparisonPanel = new FileComparisonPanel(meldPanel, file, file2);
        fileComparisonPanel.setOpenInBackground(false);
        fileComparisonPanel.setShowTree(true);
        fileComparisonPanel.execute();
    }
}
