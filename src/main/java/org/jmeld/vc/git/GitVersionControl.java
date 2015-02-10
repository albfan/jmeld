package org.jmeld.vc.git;

import org.jmeld.vc.BaseFile;
import org.jmeld.vc.StatusResult;
import org.jmeld.vc.VersionControlIF;

import java.io.File;
import java.util.Vector;

public class GitVersionControl implements VersionControlIF {
    private Boolean installed;
    private String reference;

    public GitVersionControl() {
        setReference("HEAD");
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

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
        StatusCmd cmd = new StatusCmd(file, reference);
        cmd.execute();
        return cmd.getResultData();
    }

    public BaseFile getBaseFile(File file) {
        CatCmd cmd;

        cmd = new CatCmd(file, getReference());
        cmd.execute();
        return cmd.getResultData();
    }

    public static final String SEPARATOR = "SEPARATOR";

    @Override
    public Vector<String> getRevisions(File file) {
        Vector<String> revisions = new Vector<>();
        revisions.add("worktree");
        revisions.add("HEAD");
        revisions.add("index");
        revisions.add(SEPARATOR);
        BranchCmd branchCmd = new BranchCmd(file);
        branchCmd.execute();
        revisions.addAll(branchCmd.getResultData().getBranchs());
        revisions.add(SEPARATOR);
        TagCmd tagCmd = new TagCmd(file);
        tagCmd.execute();
        revisions.addAll(tagCmd.getResultData().getTags());
        revisions.add(SEPARATOR);
        LogCmd logCmd = new LogCmd(file);
        logCmd.execute();
        revisions.addAll(logCmd.getResultData().getRevisions());

        return revisions;
    }

    @Override
    public String toString() {
        return getName();
    }
}
