package org.jmeld.ui.util;

import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.layout.*;

import javax.swing.*;

public class ToolBarBuilder
       extends AbstractFormBuilder
{
  public ToolBarBuilder(JComponent toolBar)
  {
    super(toolBar, new FormLayout("", "fill:p"));

    initialize();
  }

  private void initialize()
  {
    getContainer().setLayout(getLayout());
  }

  public void addButton(AbstractButton button)
  {
    appendColumn("pref:none");
    getLayout().addGroupedColumn(getColumn());
    //button.putClientProperty(NARROW_KEY, Boolean.TRUE);
    add(button);
    nextColumn();
  }

  public void addComponent(JComponent component)
  {
    appendColumn("pref:none");
    add(component);
    nextColumn();
  }

  public void addSeparator()
  {
    appendColumn("pref:none");
    add(new ToolBarSeparator());
    nextColumn();
  }

  public void addSpring()
  {
    appendColumn("pref:grow");

    // Any old component will do here!
    add(Box.createHorizontalGlue());
    nextColumn();
  }
}
