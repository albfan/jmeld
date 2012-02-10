package org.jmeld.ui.settings;

import org.jmeld.ui.util.ImageUtil;

import javax.swing.*;
import java.awt.*;

class SettingCellRenderer
    extends JLabel
    implements ListCellRenderer
{
  public SettingCellRenderer()
  {
    setOpaque(true);
    setBackground(Color.white);
    setForeground(Color.black);
    setHorizontalAlignment(JLabel.CENTER);
    setVerticalAlignment(JLabel.CENTER);
    setVerticalTextPosition(JLabel.BOTTOM);
    setHorizontalTextPosition(JLabel.CENTER);
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    setPreferredSize(new Dimension(70, 70));
  }

  public Component getListCellRendererComponent(JList list, Object value,
      int index, boolean isSelected, boolean cellHasFocus)
  {
    Settings settings;
    JPanel panel;

    settings = (Settings) value;

    setText(settings.getName());
    setIcon(ImageUtil.getImageIcon(settings.getIconName()));

    if (isSelected)
    {
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
    }
    else
    {
      setBackground(Color.white);
      setForeground(Color.black);
    }

    setEnabled(list.isEnabled());
    setFont(list.getFont());

    return this;
  }
}
