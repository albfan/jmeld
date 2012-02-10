package org.jmeld.ui;

/**
* User: alberto
* Date: 16/03/12
* Time: 0:38
*/
public class Option {
    private boolean enabled;
    private JMeldPanel mainPanel;

    public Option(JMeldPanel mainPanel, boolean enabled) {
        this.mainPanel = mainPanel;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDisabled() {
        return !isEnabled();
    }

    public void enable() {
        if (mainPanel.isStarted()) {
            System.out.println("Cannot change an option after start!");
            return;
        }

        this.enabled = true;
    }

    public void disable() {
        if (mainPanel.isStarted()) {
            System.out.println("Cannot change an option after start!");
            return;
        }

        this.enabled = false;
    }
}
