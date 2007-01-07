package org.jmeld.conf;

public class Configuration
{
  private EditorConfiguration editor = new EditorConfiguration();
  
  private Configuration()
  {
  }

  public EditorConfiguration getEditor()
  {
    return editor;
  }
}
