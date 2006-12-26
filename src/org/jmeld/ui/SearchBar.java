package org.jmeld.ui;

import org.jmeld.ui.swing.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;

public class SearchBar
       extends JPanel
{
  // class variables:
  private static final String CP_FOREGROUND = "JMeld.foreground";
  private static final String CP_BACKGROUND = "JMeld.background";

  // Instance variables:
  private JMeldPanel    meldPanel;
  private JTextField    searchField;
  private JLabel        searchResult;
  private JToggleButton highlightAllButton;

  public SearchBar(JMeldPanel meldPanel)
  {
    this.meldPanel = meldPanel;
    init();
  }

  private void init()
  {
    JButton closeButton;
    JButton previousButton;
    JButton nextButton;

    setLayout(new FlowLayout(FlowLayout.LEADING));

    // Close the search dialog:
    closeButton = new JButton(ImageUtil.getImageIcon("jmeld_close"));
    closeButton.setRolloverIcon(
      ImageUtil.getImageIcon("jmeld_close-rollover"));
    closeButton.setPressedIcon(ImageUtil.getImageIcon("jmeld_close-pressed"));
    closeButton.addActionListener(getCloseAction());
    initButton(closeButton);
    closeButton.setBorder(null);

    // Incremental search:
    searchField = new JTextField(15);
    searchField.getDocument().addDocumentListener(getSearchAction());
    searchField.addKeyListener(getSearchKeyAction());

    // Find previous match:
    previousButton = new JButton(
        "Previous",
        ImageUtil.getImageIcon("stock_data-previous"));
    previousButton.addActionListener(getPreviousAction());
    initButton(previousButton);

    // Find next match:
    nextButton = new JButton(
        "Next",
        ImageUtil.getImageIcon("stock_data-next"));
    nextButton.addActionListener(getNextAction());
    initButton(nextButton);

    // Highlight all:
    highlightAllButton = new JToggleButton(
        "Highlight all",
        ImageUtil.getImageIcon("highlight-all"));
    highlightAllButton.setDisabledIcon(
      ImageUtil.getImageIcon("highlight-all-disabled"));
    highlightAllButton.setSelectedIcon(
      ImageUtil.getImageIcon("highlight-all-selected"));
    highlightAllButton.setEnabled(false);
    //highlightAllButton.addActionListener(getNextAction());
    initButton(highlightAllButton);
     highlightAllButton.setContentAreaFilled(false);

    searchResult = new JLabel();

    initButton(previousButton);
    add(closeButton);
    add(Box.createHorizontalStrut(5));
    add(new JLabel("Find:"));
    add(searchField);
    add(previousButton);
    add(nextButton);
    add(highlightAllButton);
    add(Box.createHorizontalStrut(10));
    add(searchResult);
  }

  private void initButton(AbstractButton button)
  {
    button.setFocusable(false);
    button.setBorderPainted(false);
    button.setBorder(new EmptyBorder(0, 5, 0, 5));
  }

  public String getText()
  {
    return searchField.getText();
  }

  public void activate()
  {
    searchField.requestFocus();
    searchField.selectAll();
  }

  private DocumentListener getSearchAction()
  {
    return new DocumentListener()
      {
        public void changedUpdate(DocumentEvent e)
        {
          doSearch();
        }

        public void insertUpdate(DocumentEvent e)
        {
          doSearch();
        }

        public void removeUpdate(DocumentEvent e)
        {
          doSearch();
        }

        private void doSearch()
        {
          boolean notFound;
          Color   color;
          String  searchText;

          searchText = searchField.getText();

          notFound = (searchText.length() > 2);

          highlightAllButton.setEnabled(searchText.length() > 0);
          if(!highlightAllButton.isEnabled())
          {
            highlightAllButton.setSelected(false);
          }

          if (notFound)
          {
            // I would love to set the background to red and foreground
            //   to white but the jdk won't let me set the background if
            //   GTK look&feel is chosen.
            if (searchField.getForeground() != Color.red)
            {
              // Remember the original colors:
              searchField.putClientProperty(
                CP_FOREGROUND,
                searchField.getForeground());

              // Set the new colors:
              searchField.setForeground(Color.red);
            }

            searchResult.setIcon(ImageUtil.getImageIcon("bullet-warning"));
            searchResult.setText("Phrase not found");
          }
          else
          {
            // Set the original colors:
            color = (Color) searchField.getClientProperty(CP_FOREGROUND);
            if (color != null)
            {
              searchField.setForeground(color);
              searchField.putClientProperty(CP_FOREGROUND, null);
            }

            if (!StringUtil.isEmpty(searchResult.getText()))
            {
              searchResult.setIcon(null);
              searchResult.setText("");
            }
          }

          meldPanel.doSearch(null);
        }
      };
  }

  private KeyListener getSearchKeyAction()
  {
    return new KeyAdapter()
      {
        public void keyReleased(KeyEvent e)
        {
          if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
          {
            meldPanel.doStopSearch(null);
          }
        }
      };
  }

  private ActionListener getCloseAction()
  {
    return new ActionListener()
      {
        public void actionPerformed(ActionEvent ae)
        {
          meldPanel.doStopSearch(null);
        }
      };
  }

  private ActionListener getPreviousAction()
  {
    return new ActionListener()
      {
        public void actionPerformed(ActionEvent ae)
        {
          meldPanel.doPreviousSearch(null);
        }
      };
  }

  private ActionListener getNextAction()
  {
    return new ActionListener()
      {
        public void actionPerformed(ActionEvent ae)
        {
          meldPanel.doNextSearch(null);
        }
      };
  }
}