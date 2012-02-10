package org.jmeld.ui.swing.table.util;

import javax.swing.*;
import java.util.List;

public class JMComboBoxEditor
    extends DefaultCellEditor
{
  public JMComboBoxEditor(Object[] items)
  {
    super(new JComboBox(items));
  }

  public JMComboBoxEditor(List items)
  {
    this(items.toArray());
  }
}
