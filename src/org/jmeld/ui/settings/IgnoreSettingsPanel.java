/*
 * EditorPreferencePanel.java
 *
 * Created on January 10, 2007, 6:31 PM
 */
package org.jmeld.ui.settings;

import com.l2fprod.common.swing.*;
import org.jmeld.settings.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.conf.*;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author  kees
 */
public class IgnoreSettingsPanel
    extends IgnoreSettingsForm
    implements ConfigurationListenerIF
{
  public IgnoreSettingsPanel()
  {
    init();
    initConfiguration();
  }

  private void init()
  {
    ignoreWhitespaceAtBeginCheckBox
        .addActionListener(getIgnoreWhitespaceAtBeginAction());
    ignoreWhitespaceInBetweenCheckBox
        .addActionListener(getIgnoreWhitespaceInBetweenAction());
    ignoreWhitespaceAtEndCheckBox
        .addActionListener(getIgnoreWhitespaceAtEndAction());
    ignoreEOLCheckBox.addActionListener(getIgnoreEOLAction());
    ignoreBlankLinesCheckBox.addActionListener(getIgnoreBlankLinesAction());
    ignoreCaseCheckBox.addActionListener(getIgnoreCaseAction());
  }

  private ActionListener getIgnoreWhitespaceAtBeginAction()
  {
    return new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        getEditorSettings().setIgnoreWhitespaceAtBegin(
                                                       ignoreWhitespaceAtBeginCheckBox
                                                           .isSelected());
      }
    };
  }

  private ActionListener getIgnoreWhitespaceInBetweenAction()
  {
    return new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        getEditorSettings().setIgnoreWhitespaceInBetween(
                                                         ignoreWhitespaceInBetweenCheckBox
                                                             .isSelected());
      }
    };
  }

  private ActionListener getIgnoreWhitespaceAtEndAction()
  {
    return new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        getEditorSettings().setIgnoreWhitespaceAtEnd(
                                                     ignoreWhitespaceAtEndCheckBox
                                                         .isSelected());
      }
    };
  }

  private ActionListener getIgnoreEOLAction()
  {
    return new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        getEditorSettings().setIgnoreEOL(ignoreEOLCheckBox.isSelected());
      }
    };
  }

  private ActionListener getIgnoreBlankLinesAction()
  {
    return new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        getEditorSettings().setIgnoreBlankLines(
                                                ignoreBlankLinesCheckBox
                                                    .isSelected());
      }
    };
  }

  private ActionListener getIgnoreCaseAction()
  {
    return new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        getEditorSettings().setIgnoreCase(ignoreCaseCheckBox.isSelected());
      }
    };
  }

  public void configurationChanged()
  {
    initConfiguration();
  }

  private void initConfiguration()
  {
    EditorSettings settings;
    Font font;

    settings = getEditorSettings();
    ignoreWhitespaceAtBeginCheckBox
        .setSelected(settings.getIgnore().ignoreWhitespaceAtBegin);
    ignoreWhitespaceInBetweenCheckBox
        .setSelected(settings.getIgnore().ignoreWhitespaceInBetween);
    ignoreWhitespaceAtEndCheckBox
        .setSelected(settings.getIgnore().ignoreWhitespaceAtEnd);
    ignoreEOLCheckBox.setSelected(settings.getIgnore().ignoreEOL);
    ignoreBlankLinesCheckBox.setSelected(settings.getIgnore().ignoreBlankLines);
    ignoreCaseCheckBox.setSelected(settings.getIgnore().ignoreCase);

    revalidate();
  }

  private EditorSettings getEditorSettings()
  {
    return JMeldSettings.getInstance().getEditor();
  }
}
