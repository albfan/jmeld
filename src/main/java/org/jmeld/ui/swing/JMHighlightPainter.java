/*
   JMeld is a visual diff and merge tool.
   Copyright (C) 2007  Kees Kuip
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA  02110-1301  USA
 */
package org.jmeld.ui.swing;

import org.jmeld.settings.EditorSettings;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.util.Colors;
import org.jmeld.util.conf.ConfigurationListenerIF;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class JMHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter implements ConfigurationListenerIF {
    public static final JMHighlightPainter ADDED;
    public static final JMHighlightPainter ADDED_LINE;
    /** Painter which adds a newline at end */
    public static final JMHighlightPainter ADDED_NEWLINE;
    public static final JMHighlightPainter CHANGED;
    /** Painter which adds a newline at end */
    public static final JMHighlightPainter CHANGED_NEWLINE;
    public static final JMHighlightPainter CHANGED_LIGHTER;
    public static final JMHighlightPainter DELETED;
    public static final JMHighlightPainter DELETED_LINE;
    /** Painter which adds a newline at end */
    public static final JMHighlightPainter DELETED_NEWLINE;
    public static final JMHighlightPainter CURRENT_SEARCH;
    public static final JMHighlightPainter SEARCH;

    static {
        ADDED = new JMHighlightPainter(Colors.ADDED);
        ADDED.initConfiguration();
        ADDED_LINE = new JMHighlightLinePainter(Colors.ADDED);
        ADDED_LINE.initConfiguration();
        ADDED_NEWLINE = new JMHighlightNewLinePainter(Colors.ADDED);
        ADDED_NEWLINE.initConfiguration();
        CHANGED = new JMHighlightPainter(Colors.CHANGED);
        CHANGED.initConfiguration();
        CHANGED_NEWLINE = new JMHighlightNewLinePainter(Colors.CHANGED);
        CHANGED_NEWLINE.initConfiguration();
        CHANGED_LIGHTER = new JMHighlightPainter(Colors.CHANGED_LIGHTER);
        CHANGED_LIGHTER.initConfiguration();
        DELETED = new JMHighlightPainter(Colors.DELETED);
        DELETED.initConfiguration();
        DELETED_LINE = new JMHighlightLinePainter(Colors.DELETED);
        DELETED_LINE.initConfiguration();
        DELETED_NEWLINE = new JMHighlightNewLinePainter(Colors.DELETED);
        DELETED_NEWLINE.initConfiguration();
        SEARCH = new JMHighlightPainter(Color.yellow);
        SEARCH.initConfiguration();
        CURRENT_SEARCH = new JMHighlightPainter(Color.yellow.darker());
        CURRENT_SEARCH.initConfiguration();
    }

    protected Color color;

    protected JMHighlightPainter(Color color) {
        super(color);

        this.color = color;

        JMeldSettings.getInstance().addConfigurationListener(this);
    }

    @Override
    public void paint(Graphics g, int p0, int p1, Shape shape, JTextComponent comp) {
        Rectangle b;
        Rectangle r1;
        Rectangle r2;
        int x;
        int y;
        int width;
        int height;
        int count;

        b = shape.getBounds();

        try {
            r1 = comp.modelToView(p0);
            r2 = comp.modelToView(p1);

            g.setColor(color);
            if (isChangeLighter() || isSearch()) {
                if (r1.y == r2.y) {
                    g.fillRect(r1.x, r1.y, r2.x - r1.x, r1.height);
                } else {
                    count = ((r2.y - r1.y) / r1.height) + 1;
                    y = r1.y;
                    for (int i = 0; i < count; i++, y += r1.height) {
                        if (i == 0) {
                            // firstline:
                            x = r1.x;
                            width = b.width - b.x;
                        }
                        else if (i == count - 1) {
                            // lastline:
                            x = b.x;
                            width = r2.x - x;
                        } else {
                            // all lines in between the first and the lastline:
                            x = b.x;
                            width = b.width - b.x;
                        }

                        g.fillRect(x, y, width, r1.height);
                    }
                }
            } else {
                height = r2.y - r1.y;
                if(height == 0) {
                    height = r1.height;
                }
                g.fillRect(0, r1.y, b.x + b.width, height);
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    private boolean isSearch() {
        return this == SEARCH || this == CURRENT_SEARCH;
    }

    public void configurationChanged() {
        initConfiguration();
    }

    private void initConfiguration() {
        if (isAdd()) {
            color = getSettings().getAddedColor();
        } else if (isDeleted()) {
            color = getSettings().getDeletedColor();
        } else if (isChange()) {
            color = getSettings().getChangedColor();
        } else if (isChangeLighter()) {
            color = Colors.getChangedLighterColor(getSettings().getChangedColor());
        }
    }

    private boolean isChangeLighter() {
        return this == CHANGED_LIGHTER;
    }

    private boolean isChange() {
        return this == CHANGED;
    }

    private boolean isDeleted() {
        return this == DELETED || this == DELETED_LINE || this == DELETED_NEWLINE;
    }

    private boolean isAdd() {
        return this == ADDED || this == ADDED_LINE || this == ADDED_NEWLINE;
    }

    private EditorSettings getSettings() {
        return JMeldSettings.getInstance().getEditor();
    }
}
