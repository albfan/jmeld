package org.jmeld;

import junit.framework.Assert;
import org.jmeld.ui.JMeldPanel;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;

import javax.swing.*;
import java.util.Arrays;

public class JMeldTest {

    @Before
    public void before() {
        Assume.assumeTrue(!java.awt.GraphicsEnvironment.isHeadless());
    }

    @After
    public void after() {
        //Looking for the best way to close frame after every test
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JFrameOperator frameOperator = new JFrameOperator("JMeld");
        frameOperator.setVisible(false);
        frameOperator.dispose();
    }

    /**
     * Open and close main window
     */
    @Test
    public void runAlone() {
        try {
            new ClassReference("org.jmeld.JMeld").startApplication();

            JFrameOperator frameOperator = new JFrameOperator("JMeld");
            Assert.assertTrue(new JButtonOperator(frameOperator, "New").isEnabled());
            Assert.assertFalse(new JButtonOperator(frameOperator, "Save").isEnabled());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Open and close main window
     */
    @Test
    public void runFileComparison() {
        try {
            new ClassReference("org.jmeld.JMeld").startApplication(new String[]{"src/test/resources/file1", "src/test/resources/file2"});

            JFrameOperator frameOperator = new JFrameOperator("JMeld");
            JTabbedPaneOperator tabbedPaneOperator = new JTabbedPaneOperator(frameOperator);
            Assert.assertEquals(tabbedPaneOperator.getTabCount(), 1);
            Assert.assertEquals(tabbedPaneOperator.getTitleAt(0), "file1-file2");
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Open and close main window
     */
    @Test
    public void runDirectoryComparison() {
        try {
            new ClassReference("org.jmeld.JMeld").startApplication(new String[] { "src/test/resources/dir1", "src/test/resources/dir2"});

            JFrameOperator frameOperator = new JFrameOperator("JMeld");
            JTabbedPaneOperator tabbedPaneOperator = new JTabbedPaneOperator(frameOperator);
            Assert.assertEquals(tabbedPaneOperator.getTabCount(), 1);
            Assert.assertEquals(tabbedPaneOperator.getTitleAt(0), "dir1 - dir2");
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Open and close main window
     */
    @Test
    public void runVcsComparison() {
        try {
            ClassReference classReference = new ClassReference("org.jmeld.JMeld");
            classReference.startApplication();

            SwingUtilities.invokeLater(new Runnable() {
                                           @Override
                                           public void run() {
                                               JMeldPanel jMeldPanel = JMeld.getJMeldPanel();
                                               jMeldPanel.openComparison(Arrays.asList("src/test/resources/vcs/git/file1"));
                                               jMeldPanel.openComparison(Arrays.asList("src/test/resources/vcs/git/file2"));
                                               jMeldPanel.openComparison(Arrays.asList("src/test/resources/vcs/git/file3"));
                                               jMeldPanel.openComparison(Arrays.asList("src/test/resources/vcs/git"));
                                           }
                                       });
            JFrameOperator frameOperator = new JFrameOperator("JMeld");
            JTabbedPaneOperator tabbedPaneOperator = new JTabbedPaneOperator(frameOperator);
            Assert.assertEquals(tabbedPaneOperator.getTabCount(), 3);
            Assert.assertEquals(tabbedPaneOperator.getTitleAt(0), "VCS Comparation");
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

}