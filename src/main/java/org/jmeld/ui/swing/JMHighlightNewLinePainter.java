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

public class JMHighlightNewLinePainter extends JMHighlightPainter {

    protected JMHighlightNewLinePainter(Color color) {
        super(color);
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
            if (this == CHANGED_LIGHTER || this == SEARCH || this == CURRENT_SEARCH) {
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
                //Add One line to print empty newline
                height += r1.height;
                g.fillRect(0, r1.y, b.x + b.width, height);
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
}
