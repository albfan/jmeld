package org.jmeld.ui;

import org.jmeld.ui.util.*;

import javax.swing.*;
import javax.swing.text.*;

public class FilePanelBar
       extends JPanel
{
  private FilePanel filePanel;
  private JLabel    selected;
  private JLabel    lineNumber;
  private JLabel    columnNumber;
  private ImageIcon iconSelected;
  private ImageIcon iconNotSelected;

  public FilePanelBar(FilePanel filePanel)
  {
    this.filePanel = filePanel;

    init();
  }

  private void init()
  {
    ToolBarBuilder builder;

    selected = new JLabel();
    lineNumber = new JLabel();
    columnNumber = new JLabel();

    builder = new ToolBarBuilder(this);
    builder.addComponent(selected);
    builder.addSpring();
    builder.addComponent(lineNumber);
    builder.addSeparator();
    builder.addComponent(columnNumber);

    iconSelected = ImageUtil.getImageIcon("panel-selected");
    iconNotSelected = ImageUtil.createTransparentIcon(iconSelected);

    update();
  }

  public void update()
  {
    String    iconName;
    Icon      icon;
    JTextArea editor;
    int       caretPosition;
    String    text;
    int       line;
    int       column;

    icon = filePanel.isSelected() ? iconSelected : iconNotSelected;
    if (selected.getIcon() != icon)
    {
      selected.setIcon(icon);
    }

    editor = filePanel.getEditor();
    caretPosition = editor.getCaretPosition();
    try
    {
      line = editor.getLineOfOffset(caretPosition);
    }
    catch (Exception ex)
    {
      line = -1;
    }

    try
    {
      column = caretPosition - editor.getLineStartOffset(line);
    }
    catch (Exception ex)
    {
      column = -1;
    }

    text = String.format(
        "Line: %05d/%05d",
        line,
        editor.getLineCount());
    lineNumber.setText(text);

    text = String.format("Column: %03d", column);
    columnNumber.setText(text);
  }
}
