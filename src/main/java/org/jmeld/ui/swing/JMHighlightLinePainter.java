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

public class JMHighlightLinePainter extends JMHighlightPainter {

    public JMHighlightLinePainter(Color color) {
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
                int yLine = r1.y;
                g.drawLine(0, yLine, b.x + b.width, yLine);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
}
