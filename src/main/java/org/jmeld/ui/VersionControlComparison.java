package org.jmeld.ui;

import com.jidesoft.swing.JideTabbedPane;
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
public class VersionControlComparison extends SwingWorker<String, Object> {
    private JMeldPanel mainPanel;
    private File file;
    private VersionControlDiff diff;
    private AbstractContentPanel contentPanel;
    private String contentId;

    public VersionControlComparison(JMeldPanel mainPanel, File file) {
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
            String result = get();

            if (result != null) {
                JOptionPane.showMessageDialog(mainPanel, result,
                        "Error opening file",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JideTabbedPane tabbedPane = mainPanel.getTabbedPane();
                if (tabbedPane != null) {
                    if (contentPanel != null) {
                        tabbedPane.setSelectedComponent(contentPanel);
                    } else {
                        VersionControlPanel panel = new VersionControlPanel(mainPanel, diff);
                        panel.setId(contentId);

                        tabbedPane.addTab("VCS Comparation", ImageUtil.getSmallImageIcon("stock_folder"), panel);
                        tabbedPane.setSelectedComponent(panel);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
