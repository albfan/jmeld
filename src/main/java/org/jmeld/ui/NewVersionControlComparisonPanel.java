package org.jmeld.ui;

import org.jdesktop.swingworker.SwingWorker;
import org.jmeld.ui.util.ImageUtil;
import org.jmeld.util.StringUtil;
import org.jmeld.util.file.DirectoryDiff;
import org.jmeld.util.file.VersionControlDiff;

import javax.swing.*;
import java.io.File;

/**
* User: alberto
* Date: 16/03/12
* Time: 0:30
*/
public class NewVersionControlComparisonPanel extends SwingWorker<String, Object> {
    private JMeldPanel mainPanel;
    private File file;
    private VersionControlDiff diff;
    private AbstractContentPanel contentPanel;
    private String contentId;

    public NewVersionControlComparisonPanel(JMeldPanel mainPanel, File file) {
        this.mainPanel = mainPanel;
        this.file = file;
    }

    @Override
    public String doInBackground() {
        if (StringUtil.isEmpty(file.getName())) {
            return "file is empty";
        }

        if (!file.exists()) {
            return "file(" + file.getAbsolutePath() + ") doesn't exist";
        }

/*
  if (!directory.isDirectory())
  {
    return "directoryName(" + directory.getName() + ") is not a directory";
  }
  */

        contentId = "VersionControlDiffPanel:" + file.getName();
        contentPanel = JMeldPanel.getAlreadyOpen(mainPanel.getTabbedPane(), contentId);
        if (contentPanel == null) {
            diff = new VersionControlDiff(file, DirectoryDiff.Mode.TWO_WAY);
            diff.diff();
        }

        return null;
    }

    @Override
    protected void done() {
        try {
            String result;
            VersionControlPanel panel;

            result = get();

            if (result != null) {
                JOptionPane.showMessageDialog(mainPanel, result,
                        "Error opening file",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                if (contentPanel != null) {
                    // Already opened!
                    mainPanel.getTabbedPane().setSelectedComponent(contentPanel);
                } else {
                    //panel = new FolderDiffPanel(JMeldPanel.this, diff);
                    panel = new VersionControlPanel(mainPanel, diff);
                    panel.setId(contentId);

                    mainPanel.getTabbedPane().addTab("TODO: Think of title!", ImageUtil
                            .getSmallImageIcon("stock_folder"), panel);
                    mainPanel.getTabbedPane().setSelectedComponent(panel);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
