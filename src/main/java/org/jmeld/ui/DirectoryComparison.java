package org.jmeld.ui;

import com.jidesoft.swing.JideTabbedPane;
import org.jdesktop.swingworker.SwingWorker;
import org.jmeld.settings.util.Filter;
import org.jmeld.ui.util.ImageUtil;
import org.jmeld.util.StringUtil;
import org.jmeld.util.file.DirectoryDiff;

import javax.swing.*;
import java.io.File;

/**
* User: alberto
* Date: 16/03/12
* Time: 0:25
*/
public class DirectoryComparison extends SwingWorker<String, Object> {
    private File leftFile;
    private File rightFile;
    private Filter filter;
    private DirectoryDiff diff;
    private AbstractContentPanel contentPanel;
    private String contentId;
    private JMeldPanel mainPanel;

    public DirectoryComparison(JMeldPanel mainPanel, File leftFile, File rightFile, Filter filter) {
        this.mainPanel = mainPanel;
        this.leftFile = leftFile;
        this.rightFile = rightFile;
        this.filter = filter;
    }

    @Override
    public String doInBackground() {
        if (StringUtil.isEmpty(leftFile.getName())) {
            return "left directoryName is empty";
        }

        if (!leftFile.exists()) {
            return "left directoryName(" + leftFile.getAbsolutePath()
                    + ") doesn't exist";
        }

        if (!leftFile.isDirectory()) {
            return "left directoryName(" + leftFile.getName()
                    + ") is not a directory";
        }

        if (StringUtil.isEmpty(rightFile.getName())) {
            return "right directoryName is empty";
        }

        if (!rightFile.exists()) {
            return "right directoryName(" + rightFile.getAbsolutePath()
                    + ") doesn't exist";
        }

        if (!rightFile.isDirectory()) {
            return "right directoryName(" + rightFile.getName()
                    + ") is not a directory";
        }

        contentId = "FolderDiffPanel:" + leftFile.getName() + "-"
                + rightFile.getName();
        contentPanel = JMeldPanel.getAlreadyOpen(mainPanel.getTabbedPane(), contentId);
        if (contentPanel == null) {
            diff = new DirectoryDiff(leftFile, rightFile, filter,
                    DirectoryDiff.Mode.TWO_WAY);
            diff.diff();
        }

        return null;
    }

    @Override
    protected void done() {
        try {
            String result = get();

            if (result != null) {
                JOptionPane.showMessageDialog(mainPanel, result, "Error opening file", JOptionPane.ERROR_MESSAGE);
            } else {
                JideTabbedPane tabbedPane = mainPanel.getTabbedPane();
                if (tabbedPane != null) {
                    if (contentPanel != null) {
                        // Already opened!
                        tabbedPane.setSelectedComponent(contentPanel);
                    } else {
                        FolderDiffPanel panel = new FolderDiffPanel(mainPanel, diff);
                        panel.setId(contentId);

                        tabbedPane.addTab(panel.getTitle(), ImageUtil.getSmallImageIcon("stock_folder"), panel);
                        tabbedPane.setSelectedComponent(panel);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
