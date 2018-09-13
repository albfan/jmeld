package org.jmeld;

import javafx.scene.input.KeyCode;
import junit.framework.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.operators.JFrameOperator;

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
            frameOperator.pushKey(KeyCode.F4.ordinal(), KeyCode.ALT.ordinal());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}