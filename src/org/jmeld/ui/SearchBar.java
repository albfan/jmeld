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
  // Instance variables:
  private JMeldPanel meldPanel;
  private JTextField searchField;

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
    searchField = new JTextField(20);
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

    add(closeButton);
    add(new JLabel("Find:"));
    add(searchField);
    add(previousButton);
    add(nextButton);
  }

  private void initButton(JButton button)
  {
    button.setFocusable(false);
    button.setBorderPainted(false);
    button.setBorder(new EmptyBorder(0, 5, 0, 5));
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
          System.out.println("change: " + e);
        }

        public void insertUpdate(DocumentEvent e)
        {
          System.out.println("insert: " + e);
        }

        public void removeUpdate(DocumentEvent e)
        {
          System.out.println("remove: " + e);
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
