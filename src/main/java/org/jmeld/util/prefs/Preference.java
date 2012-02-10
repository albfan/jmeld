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

import org.jmeld.util.*;

import java.util.*;
import java.util.prefs.*;

public abstract class Preference {
    private String preferenceName;

    public Preference(String preferenceName) {
        this.preferenceName = preferenceName;
    }

    protected String getPreferenceName() {
        return preferenceName;
    }

    protected String getString(String name, String defaultValue) {
        return getPreferences().get(getKey(name), defaultValue);
    }

    protected void putString(String name, String value) {
        getPreferences().put(getKey(name), value);
    }

    protected List<String> getListOfString(String name, int maxItems) {
        List<String> list;
        String element;

        list = new ArrayList<String>(maxItems);
        for (int index = 0; index < maxItems; index++) {
            element = getString(name + index, null);
            if (StringUtil.isEmpty(element)) {
                continue;
            }

            list.add(element);
        }

        return list;
    }

    protected void putListOfString(String name, int maxItems, List<String> list) {
        String element;

        for (int index = 0; index < maxItems; index++) {
            element = "";
            if (index < list.size()) {
                element = list.get(index);
                if (StringUtil.isEmpty(element)) {
                    element = "";
                }
            }

            putString(name + index, element);
        }
    }

    protected int getInt(String name, int defaultValue) {
        return getPreferences().getInt(getKey(name), defaultValue);
    }

    protected void putInt(String name, int value) {
        getPreferences().putInt(getKey(name), value);
    }

    protected Preferences getPreferences() {
        return AppPreferences.getPreferences(getClass());
    }

    private String getKey(String name) {
        return preferenceName + "-" + name;
    }

    private String getKey(String name, int index) {
        return preferenceName + "-" + name + "-" + index;
    }
}
