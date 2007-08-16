package org.jmeld.ui.util;

import javax.swing.*;

import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;

public class FileDropHandler
       extends TransferHandler
{
  public boolean canImport(JComponent comp, DataFlavor[] transferFlavors)
  {
    return true;
  }

  public boolean importData(JComponent comp, Transferable t)
  {
    Object       data;
    List<File>   fileList;
    DataFlavor   dataFlavors[];

    dataFlavors =  t.getTransferDataFlavors();
    System.out.println("importData: " + dataFlavors);
    if(dataFlavors == null)
    {
      return false;
    }

    try
    {
      for(int i=0; i<dataFlavors.length; i++)
      {
      /* fetch the data from the Transferable */
      data = t.getTransferData(dataFlavors[i]);

      String flavor = "unknown";
      if(dataFlavors[i] .equals( DataFlavor.imageFlavor))
      {
        flavor = "imageflavor";
      }
      else if(dataFlavors[i] .equals( DataFlavor.javaFileListFlavor))
      {
        flavor = "javaFileListFlavor";
      }
      else if(dataFlavors[i] .equals( DataFlavor.plainTextFlavor))
      {
        flavor = "plainTextFlavor";
      }
      else if(dataFlavors[i] .equals( DataFlavor.stringFlavor))
      {
        flavor = "stringFlavor";
      }

      System.out.println("  [" + i + "] = " + data + " (" + flavor + ")" + dataFlavors[i].getHumanPresentableName() + " (" + data.getClass() + ")");

      if(data.getClass() == String[].class)
      {
        for(String s : (String[]) data)
        {
System.out.println("    " + s);
        }
      }
      }

    if(t.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
    {
    data = t.getTransferData(DataFlavor.javaFileListFlavor);
    System.out.println("files=" + data);
    }
    }
    catch (UnsupportedFlavorException e)
    {
      e.printStackTrace();
      return false;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return false;
    }

    return true;
  }
}
