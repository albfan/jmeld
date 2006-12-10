package org.jmeld.ui.util;

import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.layout.*;

import javax.swing.*;

public class ToolBarBuilder
       extends AbstractFormBuilder
{
  public ToolBarBuilder(JToolBar toolBar)
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

  public void addSeparator()
  {
    appendColumn("pref:none");
    add(new ToolBarSeparator());
    nextColumn();
  }
}
