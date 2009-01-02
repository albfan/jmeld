package com.jgoodies.looks.plastic.theme;

import javax.swing.plaf.ColorUIResource;

public class MySkyBluer
       extends SkyBluer
{
  /** The original color looks to much to the default Colors.CHANGED color.
   *  (Feature request [ 2414586 ] text selection visibility)
   */
  protected ColorUIResource getPrimary3()
  {
    return new ColorUIResource(153, 179, 205);
  }
}
