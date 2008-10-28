package org.jmeld.util;

import org.jmeld.settings.*;

import java.io.*;
import java.util.*;
import java.nio.charset.*;

import com.ibm.icu.text.*;

public class CharsetDetector
{
  // Class variables:
  // Singleton:
  private static CharsetDetector instance = new CharsetDetector();

  // Instance variables:
  private Map<String, Charset> charsetMap;

  private CharsetDetector()
  {
    charsetMap = Charset.availableCharsets();
  }

  public static CharsetDetector getInstance()
  {
    return instance;
  }

  public Charset getCharset(BufferedInputStream bis)
  {
    Charset charset;
    EditorSettings settings;

    settings = JMeldSettings.getInstance().getEditor();

    charset = null;
    if (settings.getDefaultFileEncodingEnabled())
    {
      charset = getDefaultCharset();
    }
    else if (settings.getSpecificFileEncodingEnabled())
    {
      charset = charsetMap.get(settings.getSpecificFileEncodingName());
    }
    else if (settings.getDetectFileEncodingEnabled())
    {
      charset = detectCharset(bis);
    }

    if (charset == null)
    {
      charset = getDefaultCharset();
    }

    return charset;
  }

  private Charset detectCharset(BufferedInputStream bis)
  {
    try
    {
      com.ibm.icu.text.CharsetDetector detector;
      CharsetMatch match;
      Charset foundCharset;

      detector = new com.ibm.icu.text.CharsetDetector();
      detector.setText(bis);

      match = detector.detect();
      if (match != null)
      {
        foundCharset = charsetMap.get(match.getName());
        if (foundCharset != null)
        {
          return foundCharset;
        }
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    return null;
  }

  public Charset getDefaultCharset()
  {
    return Charset.defaultCharset();
  }

  public List<String> getCharsetNameList()
  {
    List<String> charsetNameList;

    charsetNameList = new ArrayList<String>();
    for (String name : charsetMap.keySet())
    {
      charsetNameList.add(name);
    }

    return charsetNameList;
  }
}
