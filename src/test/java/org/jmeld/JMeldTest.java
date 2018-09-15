package org.jmeld;

import junit.framework.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.operators.JFrameOperator;

import java.awt.event.KeyEvent;

public class JMeldTest {

    @Before
    public void before() {
        Assume.assumeTrue(!java.awt.GraphicsEnvironment.isHeadless());
    }

    /**
     * Open and close main window
     */
    @Test
    public void testMain() {

        try {
            new ClassReference("org.jmeld.JMeld").startApplication();

            JFrameOperator frameOperator = new JFrameOperator("JMeld");
            frameOperator.pushKey(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}