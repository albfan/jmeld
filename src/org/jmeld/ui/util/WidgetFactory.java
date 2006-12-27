package org.jmeld.ui.util;

import org.jmeld.ui.action.*;

import javax.swing.*;

import java.awt.*;

public class WidgetFactory
{
  public static JMenuItem getMenuItem(Action action)
  {
    JMenuItem item;
    ImageIcon icon;

    item = new JMenuItem(action);

    icon = (ImageIcon) action.getValue(MeldAction.SMALL_ICON);
    if (icon != null)
    {
      item.setDisabledIcon(ImageUtil.createTransparentIcon(icon));
    }

    return item;
  }

  public static JButton getToolBarButton(Action action)
  {
    JButton   button;
    ImageIcon icon;
    Dimension size;

    button = new JButton(action);
    button.setVerticalTextPosition(AbstractButton.BOTTOM);
    button.setHorizontalTextPosition(AbstractButton.CENTER);
    button.setFocusable(false);

    icon = (ImageIcon) action.getValue(MeldAction.LARGE_ICON_KEY);
    if (icon != null)
    {
      button.setIcon(icon);
      button.setDisabledIcon(ImageUtil.createTransparentIcon(icon));
    }

    size = button.getPreferredSize();
    if (size.height > size.width)
    {
      size.width = size.height;
    }

    button.setPreferredSize(size);

    return button;
  }
}
