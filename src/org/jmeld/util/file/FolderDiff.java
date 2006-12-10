package org.jmeld.util.file;

import org.jmeld.util.node.*;

import java.util.*;

public abstract class FolderDiff
{
  private String mineFolderShortName;
  private String originalFolderShortName;
  private String mineFolderName;
  private String originalFolderName;

  public FolderDiff()
  {
  }

  protected void setOriginalFolderShortName(String originalFolderShortName)
  {
    this.originalFolderShortName = originalFolderShortName;
  }

  public String getOriginalFolderShortName()
  {
    return originalFolderShortName;
  }

  protected void setMineFolderShortName(String mineFolderShortName)
  {
    this.mineFolderShortName = mineFolderShortName;
  }

  public String getMineFolderShortName()
  {
    return mineFolderShortName;
  }

  protected void setOriginalFolderName(String originalFolderName)
  {
    this.originalFolderName = originalFolderName;
  }

  public String getOriginalFolderName()
  {
    return originalFolderName;
  }

  protected void setMineFolderName(String mineFolderName)
  {
    this.mineFolderName = mineFolderName;
  }

  public String getMineFolderName()
  {
    return mineFolderName;
  }

  public abstract String getMineNodeName(int index);

  public abstract List<JMeldNode> getMineNodes();

  public abstract String getOriginalNodeName(int index);

  public abstract List<JMeldNode> getOriginalNodes();

  public abstract void diff();
}
