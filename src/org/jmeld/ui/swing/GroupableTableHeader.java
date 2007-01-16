package org.jmeld.ui.swing;

import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;
import java.util.*;

public class GroupableTableHeader
       extends JTableHeader
{
  private static final String uiClassID = "GroupableTableHeaderUI";
  protected Vector            columnGroups = null;

  public GroupableTableHeader(TableColumnModel model)
  {
    super(model);
    setUI(new GroupableTableHeaderUI());
    setReorderingAllowed(false);
  }

  public void updateUI()
  {
    setUI(new GroupableTableHeaderUI());
    invalidate();
    resizeAndRepaint();
  }

  public void setReorderingAllowed(boolean b)
  {
    reorderingAllowed = false;
  }

  public void addColumnGroup(ColumnGroup g)
  {
    if (columnGroups == null)
    {
      columnGroups = new Vector();
    }
    columnGroups.addElement(g);
  }

  public Enumeration getColumnGroups(TableColumn col)
  {
    if (columnGroups == null)
    {
      return null;
    }
    Enumeration enumerate = columnGroups.elements();

    while (enumerate.hasMoreElements())
    {
      ColumnGroup cGroup = (ColumnGroup) enumerate.nextElement();
      Vector      v_ret = (Vector) cGroup.getColumnGroups(
          col,
          new Vector());

      if (v_ret != null)
      {
        return v_ret.elements();
      }
    }
    return null;
  }

  private static final long serialVersionUID = -9076551117122862611L;
}
