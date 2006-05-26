/*
 * $Id: JXGroupableTableHeader.java,v 1.7 2006/03/31 06:51:23 evickroy Exp $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.util.ArrayList;
import java.util.List;
//import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.event.TableColumnModelEvent;

import org.jdesktop.swingx.table.ColumnGroup;
import org.jdesktop.swingx.JXTableHeader;
import org.jdesktop.swingx.plaf.JXGroupableTableHeaderAddon;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;

import org.jdesktop.swingx.plaf.GroupableTableHeaderUI;
import javax.swing.UIManager;

/**  JXGroupableTableHeader
@version .01 05/23/05
@author Erik Vickroy 
*/
public class JXGroupableTableHeader extends JXTableHeader {
    
    protected List<ColumnGroup> groups = null;
    public final static String muiClassID = "swingx/GroupableTableHeaderUI";
    boolean updateUI = false;
    
// ensure at least the default ui is registered
    static {
        LookAndFeelAddons.contribute(new JXGroupableTableHeaderAddon());
    }
  
    public JXGroupableTableHeader(TableColumnModel columnModel) {
        super(columnModel);
        updateUI = true;
        updateUI();
    }


    public ColumnGroup addColumnGroup(ColumnGroup columnGroup) {
        if( groups == null ) {
            groups = new ArrayList<ColumnGroup>();
        }

        groups.add(columnGroup);
        
        return columnGroup;
    }

    public ColumnGroup addColumnGroup(Object headerValue, TableColumn[] columns) {
        ColumnGroup group = new ColumnGroup(headerValue);
        
        for( TableColumn column : columns ) {
            group.add(column);
        }

        return addColumnGroup(group);
    }

    public ColumnGroup addColumnGroup(Object headerValue, int[] columns) {
        TableColumnModel columnModel = getColumnModel();
        TableColumn[] tableColumns = new TableColumn[columns.length];
        
        for( int index = 0; index < columns.length; index++ ) {
            tableColumns[index] = columnModel.getColumn(columns[index]);
        }

        return addColumnGroup(headerValue, tableColumns);
    }

    public ColumnGroup addColumnGroup(Object headerValue, int columnStart, int columnEnd) {
        int[] columns = new int[(columnEnd - columnStart) + 1];
        
        for( int columnIndex = columnStart, index = 0; columnIndex <= columnEnd; columnIndex++, index++ ) {
            columns[index] = columnIndex;
        }

        return addColumnGroup(headerValue, columns);
    }

    public List<ColumnGroup> getGroupsForColumn(TableColumn column) {
        ArrayList<ColumnGroup> groupList = new ArrayList<ColumnGroup>();

        if( groups != null) {
            for(ColumnGroup group : groups) {
                group.getGroupsForColumn(column, groupList);

                if( !groupList.isEmpty() ) {
                    break;
                }
            }
        }

        return groupList;
    }
    
    public void setColumnMargin() {
        if( groups != null ) {
            int columnMargin = getColumnModel().getColumnMargin();
            for(ColumnGroup group : groups) {
                group.setColumnMargin(columnMargin);
            }
        }
    }

  /**
   * Notification from the <code>UIManager</code> that the L&F has changed.
   * Replaces the current UI object with the latest version from the <code>UIManager</code>.
   * 
   * @see javax.swing.JComponent#updateUI
   */
    public void updateUI() {
        // collapsePane is null when updateUI() is called by the "super()"
        // constructor
//        if( !updateUI ) {
//          return;
//        }

//        setUI((GroupableTableHeaderUI)LookAndFeelAddons.getUI(this, GroupableTableHeaderUI.class));
	setUI((GroupableTableHeaderUI)UIManager.getUI(this));
	resizeAndRepaint();
	invalidate();//PENDING        
    }

/**
   * Sets the L&F object that renders this component.
   * 
   * @param ui the <code>TaskPaneUI</code> L&F object
   * @see javax.swing.UIDefaults#getUI
   * 
   * @beaninfo bound: true hidden: true description: The UI object that
   * implements the taskpane group's LookAndFeel.
   */
    public void setUI(GroupableTableHeaderUI ui) {
        super.setUI(ui);
    }
    
  /**
   * Returns the name of the L&F class that renders this component.
   * 
   * @return the string {@link #uiClassID}
   * @see javax.swing.JComponent#getUIClassID
   * @see javax.swing.UIDefaults#getUI
   */
  public String getUIClassID() {
    return muiClassID;
  }

}
