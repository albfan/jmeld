/*
   JMeld is a visual diff and merge tool.
   Copyright (C) 2007  Kees Kuip
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA  02110-1301  USA
 */
package org.jmeld.settings;

import org.jmeld.ui.util.*;
import org.jmeld.util.*;
import org.jmeld.util.conf.*;

import javax.xml.bind.annotation.*;

import java.awt.*;

@XmlAccessorType(XmlAccessType.NONE)
public class EditorSettings
    extends AbstractConfigurationElement
{
  @XmlElement
  private boolean showLineNumbers;
  @XmlElement
  private int tabSize;
  @XmlElement
  private Ignore ignore;
  @XmlElement
  private boolean leftsideReadonly;
  @XmlElement
  private boolean rightsideReadonly;
  @XmlElement
  private ColorSetting addedColor;
  @XmlElement
  private ColorSetting changedColor;
  @XmlElement
  private ColorSetting deletedColor;
  @XmlElement
  private boolean customFont;
  @XmlElement
  private FontSetting font;
  @XmlElement
  private boolean antialias;
  @XmlElement
  private boolean defaultFileEncodingEnabled;
  @XmlElement
  private boolean detectFileEncodingEnabled;
  @XmlElement
  private boolean specificFileEncodingEnabled;
  @XmlElement
  private String specificFileEncodingName;
  @XmlElement
  private String lookAndFeelName;
  @XmlElement
  private ToolbarButtonIcon toolbarButtonIcon;
  @XmlElement
  private boolean toolbarButtonTextEnabled;
  @XmlElement
  private boolean showLevenstheinEditor;
  @XmlElement
  private boolean showTreeChunks;
  @XmlElement
  private boolean showTreeRaw;

  public EditorSettings()
  {
      tabSize = 4;
      ignore = new Ignore();
      toolbarButtonIcon = ToolbarButtonIcon.LARGE;
      toolbarButtonTextEnabled = true;
      defaultFileEncodingEnabled = true;
  }

  @Override
  public void init(AbstractConfiguration configuration)
  {
    super.init(configuration);

    ignore.init(configuration);
  }

  public boolean getShowLineNumbers()
  {
    return showLineNumbers;
  }

  public void setShowLineNumbers(boolean showLineNumbers)
  {
    this.showLineNumbers = showLineNumbers;
    fireChanged();
  }

  public boolean isShowLevenstheinEditor() {
    return showLevenstheinEditor;
  }

  public void setShowLevenstheinEditor(boolean showLevenstheinEditor) {
    this.showLevenstheinEditor = showLevenstheinEditor;
    fireChanged();
  }

  public boolean isShowTreeChunks() {
    return showTreeChunks;
  }

  public void setShowTreeChunks(boolean showTreeChunks) {
    this.showTreeChunks = showTreeChunks;
    fireChanged();
  }

  public int getTabSize()
  {
    return tabSize;
  }

  public void setTabSize(int tabSize)
  {
    if (tabSize == this.tabSize)
    {
      return;
    }

    this.tabSize = tabSize;
    fireChanged();
  }

  public Ignore getIgnore()
  {
    return ignore;
  }

  public void setIgnoreWhitespaceAtBegin(boolean ignoreWhitespaceAtBegin)
  {
    if (ignore.ignoreWhitespaceAtBegin == ignoreWhitespaceAtBegin)
    {
      return;
    }

    ignore.ignoreWhitespaceAtBegin = ignoreWhitespaceAtBegin;
    fireChanged();
  }

  public void setIgnoreWhitespaceInBetween(boolean ignoreWhitespaceInBetween)
  {
    if (ignore.ignoreWhitespaceInBetween == ignoreWhitespaceInBetween)
    {
      return;
    }

    ignore.ignoreWhitespaceInBetween = ignoreWhitespaceInBetween;
    fireChanged();
  }

  public void setIgnoreWhitespaceAtEnd(boolean ignoreWhitespaceAtEnd)
  {
    if (ignore.ignoreWhitespaceAtEnd == ignoreWhitespaceAtEnd)
    {
      return;
    }

    ignore.ignoreWhitespaceAtEnd = ignoreWhitespaceAtEnd;
    fireChanged();
  }

  public void setIgnoreEOL(boolean ignoreEOL)
  {
    if (ignore.ignoreEOL == ignoreEOL)
    {
      return;
    }

    ignore.ignoreEOL = ignoreEOL;
    fireChanged();
  }

  public void setIgnoreBlankLines(boolean ignoreBlankLines)
  {
    if (ignore.ignoreBlankLines == ignoreBlankLines)
    {
      return;
    }

    ignore.ignoreBlankLines = ignoreBlankLines;
    fireChanged();
  }

  public void setIgnoreCase(boolean ignoreCase)
  {
    if (ignore.ignoreCase == ignoreCase)
    {
      return;
    }

    ignore.ignoreCase = ignoreCase;
    fireChanged();
  }

  public boolean getLeftsideReadonly()
  {
    return leftsideReadonly;
  }

  public void setLeftsideReadonly(boolean leftsideReadonly)
  {
    if (this.leftsideReadonly == leftsideReadonly)
    {
      return;
    }

    this.leftsideReadonly = leftsideReadonly;
    fireChanged();
  }

  public boolean getRightsideReadonly()
  {
    return rightsideReadonly;
  }

  public void setRightsideReadonly(boolean rightsideReadonly)
  {
    if (this.rightsideReadonly == rightsideReadonly)
    {
      return;
    }

    this.rightsideReadonly = rightsideReadonly;
    fireChanged();
  }

  public boolean getDefaultFileEncodingEnabled()
  {
    return defaultFileEncodingEnabled;
  }

  public void setDefaultFileEncodingEnabled(boolean encoding)
  {
    if (this.defaultFileEncodingEnabled == encoding)
    {
      return;
    }

    this.defaultFileEncodingEnabled = encoding;
    fireChanged();
  }

  public boolean getDetectFileEncodingEnabled()
  {
    return detectFileEncodingEnabled;
  }

  public void setDetectFileEncodingEnabled(boolean encoding)
  {
    if (this.detectFileEncodingEnabled == encoding)
    {
      return;
    }

    this.detectFileEncodingEnabled = encoding;
    fireChanged();
  }

  public boolean getSpecificFileEncodingEnabled()
  {
    return specificFileEncodingEnabled;
  }

  public void setSpecificFileEncodingEnabled(boolean encoding)
  {
    if (this.specificFileEncodingEnabled == encoding)
    {
      return;
    }

    this.specificFileEncodingEnabled = encoding;
    fireChanged();
  }

  public String getSpecificFileEncodingName()
  {
    return specificFileEncodingName;
  }

  public void setSpecificFileEncodingName(String encodingName)
  {
    if (ObjectUtil.equals(this.specificFileEncodingName, encodingName))
    {
      return;
    }

    this.specificFileEncodingName = encodingName;
    fireChanged();
  }

  public void restoreColors()
  {
    addedColor = null;
    changedColor = null;
    deletedColor = null;
    fireChanged();
  }

  public void setAddedColor(Color color)
  {
    addedColor = new ColorSetting(color);
    fireChanged();
  }

  public Color getAddedColor()
  {
    return getColor(addedColor, Colors.ADDED);
  }

  public void setChangedColor(Color color)
  {
    changedColor = new ColorSetting(color);
    fireChanged();
  }

  public Color getChangedColor()
  {
    return getColor(changedColor, Colors.CHANGED);
  }

  public void setDeletedColor(Color color)
  {
    deletedColor = new ColorSetting(color);
    fireChanged();
  }

  public Color getDeletedColor()
  {
    return getColor(deletedColor, Colors.DELETED);
  }

  public void setLookAndFeelName(String lookAndFeelName)
  {
    this.lookAndFeelName = lookAndFeelName;
    fireChanged();
  }

  public String getLookAndFeelName()
  {
    return lookAndFeelName;
  }

  public void setToolbarButtonIcon(ToolbarButtonIcon toolbarButtonIcon)
  {
    if (this.toolbarButtonIcon == toolbarButtonIcon)
    {
      return;
    }

    this.toolbarButtonIcon = toolbarButtonIcon;

    // Don't allow the buttons to disappear!
    if (toolbarButtonIcon == ToolbarButtonIcon.NO)
    {
      toolbarButtonTextEnabled = true;
    }


    fireChanged();
  }

  public ToolbarButtonIcon getToolbarButtonIcon()
  {
    return toolbarButtonIcon;
  }

  public void setToolbarButtonTextEnabled(boolean toolbarButtonTextEnabled)
  {
    if (this.toolbarButtonTextEnabled == toolbarButtonTextEnabled)
    {
      return;
    }

    this.toolbarButtonTextEnabled = toolbarButtonTextEnabled;

    // Don't allow the buttons to disappear!
    if (!toolbarButtonTextEnabled && toolbarButtonIcon == ToolbarButtonIcon.NO)
    {
      toolbarButtonIcon = ToolbarButtonIcon.LARGE;
    }

    fireChanged();
  }

  public ToolbarButtonIcon[] getToolbarButtonIcons()
  {
    return ToolbarButtonIcon.values();
  }

  public boolean isToolbarButtonTextEnabled()
  {
    return toolbarButtonTextEnabled;
  }

  public void enableCustomFont(boolean customFont)
  {
    this.customFont = customFont;
    fireChanged();
  }

  public boolean isCustomFontEnabled()
  {
    return customFont;
  }

  public void enableAntialias(boolean antialias)
  {
    this.antialias = antialias;
    fireChanged();
  }

  public boolean isAntialiasEnabled()
  {
    return antialias;
  }

  public void setFont(Font f)
  {
    font = new FontSetting(f);
    fireChanged();
  }

  public Font getFont()
  {
    return font == null ? null : font.getFont();
  }

  private Color getColor(ColorSetting cc, Color defaultColor)
  {
    Color c;

    c = null;
    if (cc != null)
    {
      c = cc.getColor();
    }

    if (c == null)
    {
      c = defaultColor;
    }

    return c;
  }

  public boolean isShowTreeRaw() {
    return showTreeRaw;
  }

  public void setShowTreeRaw(boolean showTreeRaw) {
    this.showTreeRaw = showTreeRaw;
    fireChanged();
  }

  public enum ToolbarButtonIcon
  {
    NO("no icon"),
    SMALL("small icon"),
    LARGE("large icon");

    // instance variables:
    private String text;

    private ToolbarButtonIcon(String text)
    {
      this.text = text;
    }

    public String toString()
    {
      return text;
    }
  }
}
