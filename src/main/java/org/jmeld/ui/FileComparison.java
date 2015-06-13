package org.jmeld.ui;

import org.jdesktop.swingworker.SwingWorker;
import org.jmeld.ui.util.ImageUtil;
import org.jmeld.util.StringUtil;
import org.jmeld.util.node.JMDiffNode;
import org.jmeld.util.node.JMDiffNodeFactory;

import javax.swing.*;
import java.io.File;

/**
* User: alberto
* Date: 16/03/12
* Time: 0:23
*/
public class FileComparison extends SwingWorker<String, Object> {
    private JMeldPanel mainPanel;
    private JMDiffNode diffNode;
    private File leftFile;
    private File rightFile;
    private BufferDiffPanel panel;
    private AbstractContentPanel contentPanel;
    private String contentId;

    private boolean openInBackground;
    private boolean showLevenstein;
    private boolean showTree;

    public FileComparison(JMeldPanel mainPanel, JMDiffNode diffNode) {
        this.mainPanel = mainPanel;
        this.diffNode = diffNode;
    }

    public FileComparison(JMeldPanel mainPanel, File leftFile, File rightFile) {
        this.mainPanel = mainPanel;
        this.leftFile = leftFile;
        this.rightFile = rightFile;
    }

    public boolean isShowTree() {
        return showTree;
    }

    public void setShowTree(boolean showTree) {
        this.showTree = showTree;
    }

    public boolean isShowLevenstein() {
        return showLevenstein;
    }

    public void setShowLevenstein(boolean showLevenstein) {
        this.showLevenstein = showLevenstein;
    }

    public boolean isOpenInBackground() {
        return openInBackground;
    }

    public void setOpenInBackground(boolean openInBackground) {
        this.openInBackground = openInBackground;
    }

    @Override
    public String doInBackground() {
        try {
            if (diffNode == null) {
                if (StringUtil.isEmpty(leftFile.getName())) {
                    return "left filename is empty";
                }

                if (!leftFile.exists()) {
                    return "left filename(" + leftFile.getAbsolutePath()
                            + ") doesn't exist";
                }

                if (StringUtil.isEmpty(rightFile.getName())) {
                    return "right filename is empty";
                }

                if (!rightFile.exists()) {
                    return "right filename(" + rightFile.getAbsolutePath()
                            + ") doesn't exist";
                }

                diffNode = JMDiffNodeFactory.create(leftFile.getName(), leftFile,
                        rightFile.getName(), rightFile);
            }

            contentId = "BufferDiffPanel:" + diffNode.getId();
            contentPanel = JMeldPanel.getAlreadyOpen(mainPanel.getTabbedPane(), contentId);
            if (contentPanel == null) {
                diffNode.diff();
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            return ex.getMessage();
        }

        return null;
    }

    @Override
    protected void done() {
        try {
            String result;

            result = get();

            if (result != null) {
                JOptionPane.showMessageDialog(mainPanel, result, "Error opening file", JOptionPane.ERROR_MESSAGE);
            } else {
                if (contentPanel != null) {
                    // Already opened!
                    mainPanel.getTabbedPane().setSelectedComponent(contentPanel);
                } else {
                    panel = new BufferDiffPanel(mainPanel);
                    panel.setId(contentId);
                    panel.setDiffNode(diffNode);
                    mainPanel.getTabbedPane().addTab(panel.getTitle(), ImageUtil.getSmallImageIcon("stock_new"), panel);
                    if (!openInBackground) {
                        mainPanel.getTabbedPane().setSelectedComponent(panel);
                    }

                    SwingUtilities.invokeLater(doGoToFirst());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Runnable doGoToFirst() {
        return new Runnable() {
            public void run() {
                panel.doGoToFirst();
                panel.repaint();
            }
        };
    }
}
