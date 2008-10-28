package org.jmeld.util.file;

import java.io.*;
import java.util.*;
import java.nio.channels.*;

public class FileUtil
{
  public static List<File> getParentFiles(File file)
  {
    List<File> result;
    String parentName;

    result = new ArrayList<File>();
    while ((parentName = file.getParent()) != null)
    {
      file = new File(parentName);
      result.add(file);
    }

    return result;
  }

  public static void copy(File src, File dst)
      throws IOException
  {
    FileChannel inChannel;
    FileChannel outChannel;

    inChannel = new FileInputStream(src).getChannel();
    outChannel = new FileOutputStream(dst).getChannel();

    outChannel.transferFrom(inChannel, 0, inChannel.size());

    inChannel.close();
    outChannel.close();
  }

  public static void copy2(File src, File dst)
      throws IOException
  {
    InputStream in;
    OutputStream out;
    byte[] buf;
    int len;

    in = new FileInputStream(src);
    out = new FileOutputStream(dst);

    // Transfer bytes from in to out
    buf = new byte[1024];
    while ((len = in.read(buf)) > 0)
    {
      out.write(buf, 0, len);
    }

    in.close();
    out.close();
  }

  public static File createTempFile(String prefix, String suffix)
      throws IOException
  {
    File file;

    file = File.createTempFile(prefix, suffix);
    file.deleteOnExit();

    return file;
  }
}
