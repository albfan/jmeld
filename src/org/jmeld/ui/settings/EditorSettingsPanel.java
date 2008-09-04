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
public class EditorSettingsPanel
    extends EditorSettingsForm
    implements ConfigurationListenerIF
{
  private static JDialog       colorDialog;
  private static JColorChooser colorChooser;

  public EditorSettingsPanel()
  {
    initConfiguration();
    init();

    JMeldSettings.getInstance().addConfigurationListener(this);
  }

  private void init()
  {
    tabSizeSpinner.addChangeListener(getTabSizeChangeListener());
    showLineNumbersCheckBox.addActionListener(getShowLineNumbersAction());
    ignoreWhitespaceAtBeginCheckBox
        .addActionListener(getIgnoreWhitespaceAtBeginAction());
    ignoreWhitespaceInBetweenCheckBox
        .addActionListener(getIgnoreWhitespaceInBetweenAction());
    ignoreWhitespaceAtEndCheckBox
        .addActionListener(getIgnoreWhitespaceAtEndAction());
    ignoreEOLCheckBox.addActionListener(getIgnoreEOLAction());
    ignoreBlankLinesCheckBox.addActionListener(getIgnoreBlankLinesAction());
    ignoreCaseCheckBox.addActionListener(getIgnoreCaseAction());
    leftsideReadonlyCheckBox.addActionListener(getLeftsideReadonlyAction());
    rightsideReadonlyCheckBox.addActionListener(getRightsideReadonlyAction());
    colorAddedButton.addActionListener(getColorAddedAction());
    colorDeletedButton.addActionListener(getColorDeletedAction());
    colorChangedButton.addActionListener(getColorChangedAction());
    restoreOriginalColorsButton
        .addActionListener(getRestoreOriginalColorsAction());
    defaultFontRadioButton.addActionListener(getDefaultFontAction());
    customFontRadioButton.addActionListener(getCustomFontAction());
    fontChooserButton.addActionListener(getFontChooserAction());
  }

  private ChangeListener getTabSizeChangeListener()
  {
    return new ChangeListener()
    {
      public void stateChanged(ChangeEvent evt)
      {
        getEditorSettings().setTabSize((Integer) tabSizeSpinner.getValue());
      }
    };
  }

  private ActionListener getColorAddedAction()
  {
    return new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        Color color;

        color = chooseColor(getEditorSettings().getAddedColor());
        if (color != null)
        {
          getEditorSettings().setAddedColor(color);
        }
      }
    };
  }

  private ActionListener getColorDeletedAction()
  {
    return new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        Color color;

        color = chooseColor(getEditorSettings().getDeletedColor());
        if (color != null)
        {
          getEditorSettings().setDeletedColor(color);
        }
      }
    };
  }

  private ActionListener getColorChangedAction()
  {
    return new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        Color color;

        color = chooseColor(getEditorSettings().getChangedColor());
        if (color != null)
        {
          getEditorSettings().setChangedColor(color);
        }
      }
    };
  }

  private ActionListener getShowLineNumbersAction()
  {
    return new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        getEditorSettings().setShowLineNumbers(
                                               showLineNumbersCheckBox
                                                   .isSelected());
      }
    };
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

  private ActionListener getLeftsideReadonlyAction()
  {
    return new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        getEditorSettings().setLeftsideReadonly(
                                                leftsideReadonlyCheckBox
                                                    .isSelected());
      }
    };
  }

  private ActionListener getRightsideReadonlyAction()
  {
    return new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        getEditorSettings().setRightsideReadonly(
                                                 rightsideReadonlyCheckBox
                                                     .isSelected());
      }
    };
  }

  private ActionListener getRestoreOriginalColorsAction()
  {
    return new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        getEditorSettings().restoreColors();
      }
    };
  }

  private ActionListener getDefaultFontAction()
  {
    return new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        getEditorSettings()
            .enableCustomFont(!defaultFontRadioButton.isSelected());
      }
    };
  }

  private ActionListener getCustomFontAction()
  {
    return new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        getEditorSettings()
            .enableCustomFont(customFontRadioButton.isSelected());
      }
    };
  }

  private ActionListener getFontChooserAction()
  {
    return new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        Font font;

        font = chooseFont(getEditorSettings().getFont());
        if (font != null)
        {
          getEditorSettings().setFont(font);
        }
      }
    };
  }

  private Color chooseColor(Color initialColor)
  {
    // Do not instantiate ColorChooser multiple times because it contains
    //   a memory leak.
    if (colorDialog == null)
    {
      colorChooser = new JColorChooser(initialColor);
      colorDialog = JColorChooser.createDialog(null, "Choose color", true,
                                               colorChooser, null, null);
    }

    colorChooser.setColor(initialColor);
    colorDialog.setVisible(true);

    return colorChooser.getColor();
  }

  private Font chooseFont(Font initialFont)
  {
    JFontChooser fontChooser;

    fontChooser = new JFontChooser();
    fontChooser.setSelectedFont(initialFont);

    return fontChooser.showFontDialog(this, "");
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
    colorAddedButton.setIcon(new EmptyIcon(settings.getAddedColor(), 20, 20));
    colorDeletedButton
        .setIcon(new EmptyIcon(settings.getDeletedColor(), 20, 20));
    colorChangedButton
        .setIcon(new EmptyIcon(settings.getChangedColor(), 20, 20));
    showLineNumbersCheckBox.setSelected(settings.getShowLineNumbers());
    ignoreWhitespaceAtBeginCheckBox
        .setSelected(settings.getIgnore().ignoreWhitespaceAtBegin);
    ignoreWhitespaceInBetweenCheckBox
        .setSelected(settings.getIgnore().ignoreWhitespaceInBetween);
    ignoreWhitespaceAtEndCheckBox
        .setSelected(settings.getIgnore().ignoreWhitespaceAtEnd);
    ignoreEOLCheckBox.setSelected(settings.getIgnore().ignoreEOL);
    ignoreBlankLinesCheckBox.setSelected(settings.getIgnore().ignoreBlankLines);
    ignoreCaseCheckBox.setSelected(settings.getIgnore().ignoreCase);
    leftsideReadonlyCheckBox.setSelected(settings.getLeftsideReadonly());
    rightsideReadonlyCheckBox.setSelected(settings.getRightsideReadonly());
    tabSizeSpinner.setValue(settings.getTabSize());
    font = settings.getFont();
    if (font != null)
    {
      fontChooserButton.setFont(font);
      fontChooserButton.setText(font.getName() + " (" + font.getSize() + ")");
    }
    defaultFontRadioButton.setSelected(!settings.isCustomFontEnabled());
    customFontRadioButton.setSelected(settings.isCustomFontEnabled());

    revalidate();
  }

  private EditorSettings getEditorSettings()
  {
    return JMeldSettings.getInstance().getEditor();
  }
}
