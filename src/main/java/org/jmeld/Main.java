/*
   JMeld is a visual diff and merge tool.
   Copyright (C) 2007  Kees Kuip
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA  02110-1301  USA
 */
package org.jmeld;

import org.jmeld.util.file.DirectoryDiff;

public class Main
{
  public static void main(String[] args)
  {
    JMeld.main(new String[] { "test/resources/files1", "test/resources/files2"} );
    JMeld.main(new String[] { "test/resources/dir1", "test/resources/dir2"} );
    JMeld.main(new String[]{ "filesLeft", "filesRight"});
    DirectoryDiff.main(new String[]{"test/resources/dir1", "test/resources/dir2"});
  }
}
