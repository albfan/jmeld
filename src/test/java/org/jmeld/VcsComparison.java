package org.jmeld;

import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.JMeldPanel;

import javax.swing.*;
import java.util.Arrays;

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
public class VcsComparison {
    public static void main(String[] args) {
        JMeld.main(new String[]{});
        SwingUtilities.invokeLater(new Runnable() {
               @Override
               public void run() {
                   JMeldPanel jMeldPanel = JMeld.getJMeldPanel();
                   jMeldPanel.openComparison(Arrays.asList(new String[] { "src/test/resources/vcs/git/file1"}) );
                   jMeldPanel.openComparison(Arrays.asList(new String[]{"src/test/resources/vcs/git/file2"}));
                   jMeldPanel.openComparison(Arrays.asList(new String[]{"src/test/resources/vcs/git/file3"}));
                   jMeldPanel.openComparison(Arrays.asList(new String[]{"src/test/resources/vcs/git"}));
               }
           });
    }
}
