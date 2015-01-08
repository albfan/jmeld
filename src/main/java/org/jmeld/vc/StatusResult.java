package org.jmeld.vc;

import java.io.*;
import java.util.*;

public class StatusResult {
    private File path;
    private Set<Entry> entryList = new HashSet<Entry>();

    public StatusResult(File path) {
        this.path = path;
    }

    public File getPath() {
        return path;
    }

    public void addEntry(String name, Status status) {
        Entry entry;

        entry = new Entry(name, status);
        if (entryList.contains(entry)) {
            return;
        }

        entryList.add(entry);
    }

    public List<Entry> getEntryList() {
        List<Entry> list;

        list = new ArrayList(entryList);
        Collections.sort(list);

        return list;
    }

    public class Entry implements Comparable<Entry> {
        private String name;
        private Status status;

        Entry(String name, Status status) {
            this.name = name;
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public Status getStatus() {
            return status;
        }

        public int compareTo(Entry entry) {
            return name.compareTo(entry.name);
        }

        public String toString() {
            return name;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }

            return name.equals(((Entry) o).name);
        }

        public int hashCode() {
            return name.hashCode();
        }
    }

    public enum Status {
        modified('M', "vcModified"),
        added('A', "vcAdded"),
        removed('D', "vcRemoved"),
        clean(' ', "vcClean"),
        conflicted('C', "vcConflicted"),
        ignored('I', "vcIgnored"),
        unversioned('?', "vcUnversioned"),
        missing('!', "vcMissing"),
        dontknow('#', "vcMissing"),
        unmodified(' ', "vcUnmodified"),
        renamed('R', "vcRenamed"),
        copied('C', "vcCopied"),
        updated('C', "vcUpdated"),
        index_modified('m', "vcModified"),
        index_added('a', "vcAdded"),
        index_removed('d', "vcRemoved");

        private char shortText;
        private String iconName;

        Status(char shortText, String iconName) {
            this.shortText = shortText;
            this.iconName = iconName;
        }

        public char getShortText() {
            return shortText;
        }

        public String getIconName() {
            return iconName;
        }
    }
}
