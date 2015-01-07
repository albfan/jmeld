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
                        //TODO: interpret worktree status
                        char worktree = text.charAt(1);
                        switch (indextree) {
                            case 'M':
                                status = StatusResult.Status.modified;
                                break;
                            case 'A':
                                status = StatusResult.Status.added;
                                break;
                            case 'D':
                                status = StatusResult.Status.removed;
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
