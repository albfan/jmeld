package org.jmeld.vc.svn;

import org.jmeld.vc.*;

import java.io.*;
import java.util.*;

public class SubversionVersionControl implements VersionControlIF {
    private Boolean installed;
    private String reference;

    public SubversionVersionControl() {
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
        return "subversion";
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
    /*
    StatusCmd cmd;
    StatusResult statusResult;

    // Don't check for existence of '.svn' because an installations
    //   can change that default.
    // Don't use the info command because it will fail for unversioned
    //   files that ARE in a versioned directory.

    cmd = new StatusCmd(file, false);
    if (!cmd.execute().isTrue())
    {
      return false;
    }

    // Subversion has a bug until 1.5.1.
    // It will return an invalid xmldocument on a file that is not
    //   in a working copy.
    statusResult = cmd.getStatusResult();
    if (statusResult == null)
    {
      return false;
    }

    return statusResult.getEntryList().size() >= 1;
    */
    }

    public BlameIF executeBlame(File file)
    {
        BlameCmd cmd;

        cmd = new BlameCmd(file);
        cmd.execute();
        return cmd.getResultData();
    }

    public DiffIF executeDiff(File file, boolean recursive)
    {
        DiffCmd cmd;

        cmd = new DiffCmd(file, recursive);
        cmd.execute();
        return cmd.getResultData();
    }

    public StatusResult executeStatus(File file)
    {
        StatusCmd cmd;

        cmd = new StatusCmd(file, true);
        cmd.execute();
        return cmd.getStatusResult();
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

    public String toString()
    {
        return getName();
    }
}
