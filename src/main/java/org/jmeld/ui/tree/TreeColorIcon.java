package org.jmeld.ui.tree;

import javax.swing.*;
import java.awt.*;

/**
 * Created by alberto on 16/11/14.
 */
class TreeColorIcon implements Icon {
    private static int HEIGHT = 14;
    private static int WIDTH = 14;

    private Color color;

    public TreeColorIcon(Color color) {
        this.color = color;
    }

    public int getIconHeight() {
        return HEIGHT;
    }

    public int getIconWidth() {
        return WIDTH;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color);
        g.fillRect(x, y, WIDTH - 1, HEIGHT - 1);

        g.setColor(Color.black);
        g.drawRect(x, y, WIDTH - 1, HEIGHT - 1);
    }
}
