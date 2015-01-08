package org.jmeld.vc.git;

import org.jmeld.util.Result;
import org.jmeld.vc.StatusResult;
import org.jmeld.vc.util.VcCmd;

import java.io.*;

public class StatusCmd
        extends VcCmd<StatusResult>
{

    StatusResult statusResult;

    private enum Phase {
        state
    }

    private File file;
    private Phase phase;
    private File actualfile;

    public StatusCmd(File file) {
        this.file = file;

        initWorkingDirectory(file);
    }

    public Result execute() {
        phase = Phase.state;

        if (phase == Phase.state) {
            statusResult = new StatusResult(workingDirectory);
        } else {
            statusResult = getResultData();
        }

        File[] files;
        if (file.isDirectory()) {
            files = file.listFiles();
        } else {
            files = new File[]{file};
        }

        for(File file:files) {
            actualfile = file;
            processFile();
        }

        return getResult();
    }

    private Result processFile() {
        return _execute("git", "status", "--porcelain", actualfile.getAbsolutePath());
    }

    /*
     This parse is far from perfect. Comes from this documentation of git status

          For paths with merge conflicts, X and Y show the modification states of each side of the merge. For
       paths that do not have merge conflicts, X shows the status of the index, and Y shows the status of the work
       tree. For untracked paths, XY are ??. Other status codes can be interpreted as follows:

       ·   ' ' = unmodified
       ·   M = modified
       ·   A = added
       ·   D = deleted
       ·   R = renamed
       ·   C = copied
       ·   U = updated but unmerged

       Ignored files are not listed, unless --ignored option is in effect, in which case XY are !!.

           X          Y     Meaning
           -------------------------------------------------
                     [MD]   not updated
           M        [ MD]   updated in index
           A        [ MD]   added to index
           D         [ M]   deleted from index
           R        [ MD]   renamed in index
           C        [ MD]   copied in index
           [MARC]           index and work tree matches
           [ MARC]     M    work tree changed since index
           [ MARC]     D    deleted in work tree
           -------------------------------------------------
           D           D    unmerged, both deleted
           A           U    unmerged, added by us
           U           D    unmerged, deleted by them
           U           A    unmerged, added by them
           D           U    unmerged, deleted by us
           A           A    unmerged, both added
           U           U    unmerged, both modified
           -------------------------------------------------
     */
    protected void build(byte[] data) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
        try {
            boolean changes = false;
            String text;
            while ((text = reader.readLine()) != null) {
                changes = true;
                if (phase == Phase.state) {
                    StatusResult.Status status;

                    if (text.isEmpty()) {
                        status = StatusResult.Status.unmodified;
                        statusResult.addEntry(actualfile.getName(), status);
                    } else {
                        char indextree = text.charAt(0);
                        char worktree = text.charAt(1);
                        switch (indextree) {
                            case 'M':
                                status = StatusResult.Status.index_modified;
                                break;
                            case 'A':
                                status = StatusResult.Status.index_added;
                                break;
                            case 'D':
                                status = StatusResult.Status.index_removed;
                                break;
                            case 'R':
                                status = StatusResult.Status.renamed;
                                break;
                            case 'U':
                                status = StatusResult.Status.updated;
                                break;
                            case 'C':
                                status = StatusResult.Status.copied;
                                break;
                            case '!':
                                status = StatusResult.Status.ignored;
                                break;
                            case '?':
                                status = StatusResult.Status.unversioned;
                                break;
                            case ' ':
                                status = StatusResult.Status.unversioned;
                                switch (worktree) {
                                    case 'M':
                                        status = StatusResult.Status.modified;
                                        break;
                                    case 'D':
                                        status = StatusResult.Status.removed;
                                        break;
                                }
                                break;
                            default:
                                status = StatusResult.Status.unmodified;
                                break;
                        }
                        statusResult.addEntry(text.substring(3), status);
                    }

                }
            }
            if (!changes) {
                statusResult.addEntry(actualfile.getName(), StatusResult.Status.unmodified);
            }
        } catch (IOException ex) {
            // This cannot happen! We are reading from a byte array.
        }

        setResultData(statusResult);
    }

    public static void main(String[] args) {
        StatusCmd cmd;
        StatusResult result;

        File file = parseFile(args);
        if (file == null) {
            return;
        }

        result = new GitVersionControl().executeStatus(file);
        if (result != null) {
            for (StatusResult.Entry entry : result.getEntryList()) {
                System.out.println(entry.getStatus().getShortText() + " "
                        + entry.getName());
            }
        }
    }
}
