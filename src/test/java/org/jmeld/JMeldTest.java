package org.jmeld;

import junit.framework.Assert;
import org.jmeld.ui.JMeldPanel;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.operators.JFrameOperator;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class JMeldTest {

    @Before
    public void before() {
        Assume.assumeTrue(!java.awt.GraphicsEnvironment.isHeadless());
    }

    /**
     * Open and close main window
     */
    @Test
    public void runAlone() {
        try {
            new ClassReference("org.jmeld.JMeld").startApplication();

            JFrameOperator frameOperator = new JFrameOperator("JMeld");
            frameOperator.pushKey(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK);
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
            frameOperator.pushKey(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK);
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
            frameOperator.pushKey(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK);
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
            Thread.sleep(50000);
//            frameOperator.pushKey(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

}