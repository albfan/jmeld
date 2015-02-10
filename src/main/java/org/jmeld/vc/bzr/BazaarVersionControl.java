package org.jmeld.vc.bzr;

import org.jmeld.vc.*;

import java.io.*;
import java.util.Vector;

public class BazaarVersionControl implements VersionControlIF {
    private Boolean installed;
    private String reference;

    public BazaarVersionControl() {
        setReference("HEAD");
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getName()
    {
        return "bazaar";
    }

    public boolean isInstalled()
    {
        InstalledCmd cmd;

        if (installed == null)
        {
            cmd = new InstalledCmd();
            cmd.execute();
            installed = cmd.getResult().isTrue();
        }

        return installed.booleanValue();
    }

    public boolean isEnabled(File file)
    {
        ActiveCmd cmd;

        cmd = new ActiveCmd(file);
        cmd.execute();

        return cmd.getResult().isTrue();
    }

    public StatusResult executeStatus(File file)
    {
        StatusCmd cmd;

        cmd = new StatusCmd(file);
        cmd.execute();
        return cmd.getResultData();
    }

    public BaseFile getBaseFile(File file)
    {
        CatCmd cmd;

        cmd = new CatCmd(file);
        cmd.execute();
        return cmd.getResultData();
    }

    @Override
    public Vector<String> getRevisions(File file) {
        Vector<String> revisions = new Vector<>();
        revisions.add("HEAD");
        return revisions;
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
