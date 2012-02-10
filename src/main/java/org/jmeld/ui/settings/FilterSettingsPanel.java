/*
 * FilterPreferencePanel.java
 *
 * Created on January 10, 2007, 6:31 PM
 */
package org.jmeld.ui.settings;

import org.jmeld.settings.FilterSettings;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.settings.util.Filter;
import org.jmeld.settings.util.FilterRule;
import org.jmeld.ui.swing.table.JMTableModel;
import org.jmeld.ui.swing.table.util.JMComboBoxEditor;
import org.jmeld.ui.swing.table.util.JMComboBoxRenderer;
import org.jmeld.util.conf.ConfigurationListenerIF;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author  kees
 */
public class FilterSettingsPanel
       extends FilterSettingsForm
       implements ConfigurationListenerIF
{
  JMTableModel filterTableModel;
  JMTableModel filterRuleTableModel;

  public FilterSettingsPanel()
  {
    initConfiguration();
    init();

    JMeldSettings.getInstance().addConfigurationListener(this);
  }

  private void init()
  {
    ListSelectionModel selectionModel;

    filterTableModel = getFilterTableModel();
    filterTable.setModel(filterTableModel);

    filterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    selectionModel = filterTable.getSelectionModel();
    selectionModel.addListSelectionListener(getFilterSelectionAction());

    filterRuleTableModel = getFilterRuleTableModel(0);
    filterRuleTable.setModel(filterRuleTableModel);
    filterRuleTable.setDefaultEditor(
      Filter.class,
      new JMComboBoxEditor(getFilters()));
    filterRuleTable.setDefaultRenderer(
      Filter.class,
      new JMComboBoxRenderer(getFilters()));
    filterRuleTable.setDefaultEditor(
      FilterRule.Rule.class,
      new JMComboBoxEditor(FilterRule.Rule.values()));
    filterRuleTable.setDefaultRenderer(
      FilterRule.Rule.class,
      new JMComboBoxRenderer(FilterRule.Rule.values()));
    filterRuleTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

    newFilterButton.addActionListener(getNewFilterAction());
    deleteFilterButton.addActionListener(getDeleteFilterAction());
    newFilterRuleButton.addActionListener(getNewFilterRuleAction());
    deleteFilterRuleButton.addActionListener(getDeleteFilterRuleAction());

    if (filterTable.getRowCount() > 0)
    {
      filterTable.addRowSelectionInterval(0, 0);
      ;
    }
  }

  private JMTableModel getFilterTableModel()
  {
    return new FilterTableModel();
  }

  private JMTableModel getFilterRuleTableModel(int filterIndex)
  {
    return new FilterRuleTableModel(filterIndex);
  }

  private ListSelectionListener getFilterSelectionAction()
  {
    return new ListSelectionListener()
      {
        public void valueChanged(ListSelectionEvent e)
        {
          int    rowIndex;
          Object value;

          if (e.getValueIsAdjusting())
          {
            return;
          }

          rowIndex = filterTable.getSelectedRow();
          value = filterTableModel.getValueAt(rowIndex, 0);

          filterNameLabel.setText(value.toString());
          filterRuleTableModel = getFilterRuleTableModel(rowIndex);
          filterRuleTable.setModel(filterRuleTableModel);
          filterRuleTable.doLayout();
        }
      };
  }

  private ActionListener getNewFilterAction()
  {
    return new ActionListener()
      {
        public void actionPerformed(ActionEvent ae)
        {
          getFilterSettings().addFilter(new Filter("Untitled"));
          filterTableModel.fireTableDataChanged();
        }
      };
  }

  private ActionListener getDeleteFilterAction()
  {
    return new ActionListener()
      {
        public void actionPerformed(ActionEvent ae)
        {
          getFilterSettings().removeFilter(getSelectedFilter());
          filterTableModel.fireTableDataChanged();
        }
      };
  }

  private ActionListener getNewFilterRuleAction()
  {
    return new ActionListener()
      {
        public void actionPerformed(ActionEvent ae)
        {
          Filter     filter;
          FilterRule newRule;
          FilterRule selectedFilterRule;

          filter = getSelectedFilter();
          if (filter == null)
          {
            return;
          }

          newRule = new FilterRule("Untitled", FilterRule.Rule.excludes, "",
              true);

          selectedFilterRule = getSelectedFilterRule();
          if (selectedFilterRule != null)
          {
            newRule.setDescription(selectedFilterRule.getDescription());
            newRule.setRule(selectedFilterRule.getRule());
            filter.insertRule(selectedFilterRule, newRule);
          }
          else
          {
            filter.addRule(newRule);
          }

          filterRuleTableModel.fireTableDataChanged();
        }
      };
  }

  private ActionListener getDeleteFilterRuleAction()
  {
    return new ActionListener()
      {
        public void actionPerformed(ActionEvent ae)
        {
          Filter     filter;
          FilterRule rule;

          filter = getSelectedFilter();
          if (filter == null)
          {
            return;
          }

          rule = getSelectedFilterRule();
          if (rule == null)
          {
            return;
          }

          filter.removeRule(rule);
          filterRuleTableModel.fireTableDataChanged();
        }
      };
  }

  public void configurationChanged()
  {
    initConfiguration();
  }

  private void initConfiguration()
  {
  }

  private class FilterTableModel
         extends JMTableModel
  {
    Column nameColumn;

    FilterTableModel()
    {
      nameColumn = addColumn("name", null, "Name", String.class, -1, true);
    }

    public int getRowCount()
    {
      return getFilterSettings().getFilters().size();
    }

    public void setValueAt(
      Object value,
      int    rowIndex,
      Column column)
    {
      Filter filter;

      filter = getFilter(rowIndex);
      if (filter != null)
      {
        if (column == nameColumn)
        {
          filter.setName((String) value);
        }
      }
    }

    public Object getValueAt(
      int    rowIndex,
      Column column)
    {
      Filter filter;

      filter = getFilter(rowIndex);
      if (filter != null)
      {
        if (column == nameColumn)
        {
          return filter.getName();
        }
      }

      return "";
    }

    private Filter getFilter(int rowIndex)
    {
      return getFilters().get(rowIndex);
    }
  }

  private class FilterRuleTableModel
         extends JMTableModel
  {
    private int    filterIndex;
    private Column activeColumn;
    private Column descriptionColumn;
    private Column ruleColumn;
    private Column patternColumn;

    public FilterRuleTableModel(int filterIndex)
    {
      this.filterIndex = filterIndex;

      init();
    }

    private void init()
    {
      activeColumn = addColumn("active", null, "Active", Boolean.class, 5, true);
      descriptionColumn = addColumn("description", null, "Description",
          String.class, 15, true);
      ruleColumn = addColumn("rule", null, "Rule", FilterRule.Rule.class, 10,
          true);
      patternColumn = addColumn("pattern", null, "Pattern", String.class, -1,
          true);
    }

    public int getRowCount()
    {
      return getRules(filterIndex).size();
    }

    public void setValueAt(
      Object value,
      int    rowIndex,
      Column column)
    {
      FilterRule rule;

      rule = getRule(rowIndex);
      if (rule != null)
      {
        if (column == activeColumn)
        {
          rule.setActive((Boolean) value);
        }

        if (column == descriptionColumn)
        {
          rule.setDescription((String) value);
        }

        if (column == ruleColumn)
        {
          rule.setRule((FilterRule.Rule) value);
          fireTableCellUpdated(
            rowIndex,
            column.getColumnIndex());
        }

        if (column == patternColumn)
        {
          if (value instanceof Filter)
          {
            value = ((Filter) value).getName();
          }

          rule.setPattern((String) value);
        }
      }
    }

    public Object getValueAt(
      int    rowIndex,
      Column column)
    {
      FilterRule rule;

      rule = getRule(rowIndex);
      if (rule != null)
      {
        if (column == activeColumn)
        {
          return rule.isActive();
        }

        if (column == descriptionColumn)
        {
          return rule.getDescription();
        }

        if (column == ruleColumn)
        {
          return rule.getRule();
        }

        if (column == patternColumn)
        {
          if (rule.getRule() == FilterRule.Rule.importFilter)
          {
            return getFilterSettings().getFilter(rule.getPattern());
          }

          return rule.getPattern();
        }
      }

      return "??";
    }

    public Class getColumnClass(
      int    rowIndex,
      Column column)
    {
      FilterRule rule;

      if (column == patternColumn)
      {
        rule = getRule(rowIndex);
        if (rule != null && rule.getRule() == FilterRule.Rule.importFilter)
        {
          return Filter.class;
        }
      }

      return null;
    }

    private FilterRule getRule(int rowIndex)
    {
      return getRules(filterIndex).get(rowIndex);
    }
  }

  private Filter getSelectedFilter()
  {
    int rowIndex;

    rowIndex = filterTable.getSelectedRow();
    if (rowIndex < 0 || rowIndex > getFilters().size())
    {
      return null;
    }

    return getFilters().get(rowIndex);
  }

  private FilterRule getSelectedFilterRule()
  {
    Filter filter;
    int    rowIndex;

    filter = getSelectedFilter();
    if (filter == null)
    {
      return null;
    }

    rowIndex = filterRuleTable.getSelectedRow();
    if (rowIndex < 0 || rowIndex > filter.getRules().size())
    {
      return null;
    }

    return filter.getRules().get(rowIndex);
  }

  private List<FilterRule> getRules(int filterIndex)
  {
    int size;

    size = getFilters().size();
    if (filterIndex < 0 || filterIndex >= size)
    {
      return Collections.emptyList();
    }

    return getFilters().get(filterIndex).getRules();
  }

  private List<Filter> getFilters()
  {
    return getFilterSettings().getFilters();
  }

  private FilterSettings getFilterSettings()
  {
    return JMeldSettings.getInstance().getFilter();
  }
}
