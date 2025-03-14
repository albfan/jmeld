package org.jmeld.vc;

import org.jmeld.vc.bzr.BazaarVersionControl;
import org.jmeld.vc.git.GitVersionControl;
import org.jmeld.vc.hg.MercurialVersionControl;
import org.jmeld.vc.svn.SubversionVersionControl;

import java.io.*;

import java.util.*;

public class VersionControlUtil {

    public static boolean isVersionControlled(File file) {
        return getVersionControl(file) != null;
    }

    public static List<VersionControlIF> getVersionControl(File file) {
        List<VersionControlIF> list;

        list = new ArrayList<VersionControlIF>();
        for (VersionControlIF versionControl : getVersionControlList()) {
            if (!versionControl.isInstalled()) {
                continue;
            }

            if (!versionControl.isEnabled(file)) {
                continue;
            }

            list.add(versionControl);
        }

        return list;
    }

    public static List<VersionControlIF> getVersionControlList() {
        List<VersionControlIF> versionControlList = new ArrayList<VersionControlIF>();
        versionControlList.add(new SubversionVersionControl());
        versionControlList.add(new MercurialVersionControl());
        versionControlList.add(new GitVersionControl());
        versionControlList.add(new BazaarVersionControl());

        return versionControlList;
    }

    public static VersionControlIF getFirstVersionControl(File file) {
        List<VersionControlIF> versionControlList = VersionControlUtil.getVersionControl(file);
        if (versionControlList.isEmpty()) {
            return null;
        }

        // TODO: versioncontrol should be a parameter in the constructor.
        //       The user has to decide which vc is used (popup) if there is more than one
        return versionControlList.get(0);
    }
}
