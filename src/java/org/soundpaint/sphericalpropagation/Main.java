/*
 * @(#)Main.java 1.00 18/07/21
 *
 * Copyright (C) 1986, 2008, 2018 Jürgen Reuter
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.soundpaint.sphericalpropagation;

/**
 * Provide <code>main</code> method for the spherical propagation
 * application.
 */
public class Main
{
  private static final int X_SIZE = 300;
  private static final int Y_SIZE = 300;

  /**
   * Main entry method.  Starts the application.
   *
   * @param argv the array of command-line arguments for the
   * application; currently, this argument is ignored
   */
  public static void main(final String argv[])
  {
    final Display display = new Display(X_SIZE, Y_SIZE);
    display.pack();
    display.setVisible(true);
    final Simulator simulator = new Simulator(X_SIZE, Y_SIZE, display);
    simulator.run();
  }
}
