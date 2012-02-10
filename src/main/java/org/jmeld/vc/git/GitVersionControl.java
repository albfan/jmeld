package org.jmeld.vc.git;

import org.jmeld.vc.BaseFile;
import org.jmeld.vc.StatusResult;
import org.jmeld.vc.VersionControlIF;

import java.io.File;

public class GitVersionControl implements VersionControlIF {
    private Boolean installed;

    public String getName() {
        return "git";
    }

    public boolean isInstalled() {
        InstalledCmd cmd;

        if (installed == null) {
            cmd = new InstalledCmd();
            cmd.execute();
            installed = cmd.getResult().isTrue();
        }

        return installed;
    }

    public boolean isEnabled(File file) {
        ActiveCmd cmd;

        cmd = new ActiveCmd(file);
        cmd.execute();

        return cmd.getResult().isTrue();
    }

    public StatusResult executeStatus(File file) {
        StatusCmd cmd;

        cmd = new StatusCmd(file);
        cmd.execute();
        return cmd.getResultData();
    }

    public BaseFile getBaseFile(File file) {
        CatCmd cmd;

        cmd = new CatCmd(file);
        cmd.execute();
        return cmd.getResultData();
    }

    @Override
    public String toString() {
        return getName();
    }
}
