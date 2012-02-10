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
package org.jmeld.util.prefs;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * Salva las preferencias de un modo independiente al sistema (puede ser con registro, ficheros planos, etc...
 */
public class WindowPreference extends Preference {
    private static String X = "X";
    private static String Y = "Y";
    private static String WIDTH = "WIDTH";
    private static String HEIGHT = "HEIGHT";

    private Window target;

    public WindowPreference(String preferenceName, Window target) {
        super("Window-" + preferenceName);

        this.target = target;
        init();
    }

    private void init() {
        target.setLocation(getInt(X, 0), getInt(Y, 0));
        target.setSize(getInt(WIDTH, 500), getInt(HEIGHT, 400));
        target.addWindowListener(getWindowListener());
    }

    private void save() {
        putInt(X, target.getLocation().x);
        putInt(Y, target.getLocation().y);
        putInt(WIDTH, target.getSize().width);
        putInt(HEIGHT, target.getSize().height);
    }

    private WindowListener getWindowListener() {
        return new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                save();
            }
        };
    }
}
