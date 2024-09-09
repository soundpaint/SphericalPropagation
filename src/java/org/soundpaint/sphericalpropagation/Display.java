/*
 * @(#)Display.java 1.00 18/07/21
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Graphical component for displaying the state of the cellular
 * automaton's grid.
 */
public class Display extends JFrame
{
  private static final long serialVersionUID = -2133618053850796573L;

  private final Dimension size;
  private final Color[][] colors;

  private class DrawArea extends JPanel
  {
    private static final long serialVersionUID = -462532690058071262L;

    /**
     * Return the dimensions passed into the constructor of this class
     * as preferred size.
     *
     * @return the dimensions of the preferred size as passed into the
     * constructor {@link #Display} of this class
     * @see #Display()
     */
    @Override
    public Dimension getPreferredSize()
    {
      return size;
    }

    @Override
    public void paint(final Graphics g)
    {
      final Dimension size = getSize();
      final int sizeX = size.width;
      final int sizeY = size.height;
      final Graphics2D g2 = (Graphics2D)g;
      final int gWidth = (int)(0.5 + sizeX);
      final int gHeight = (int)(0.5 + sizeY);
      int yLeft;
      int yRight = 0;
      for (int y = 1; y <= sizeY; y++) {
        yLeft = yRight;
        yRight = (int)(0.5 + ((double)y / sizeY) * gHeight);
        int xLeft;
        int xRight = 0;
        for (int x = 1; x <= sizeX; x++) {
          xLeft = xRight;
          xRight = (int)(0.5 + ((double)x / sizeX) * gWidth);
          final Color color = colors[x - 1][y - 1];
          g2.setColor(color);
          g2.fillRect(xLeft, yLeft, xRight - xLeft, yRight - yLeft);
        }
      }
    }
  }

  private Display() {
    throw new UnsupportedOperationException();
  }

  /**
   * Create a new object instance for displaying the state of the
   * simulation's cellular automaton.
   *
   * @param sixeX the width of the grid to display as returned as
   * preferred size of this component
   * @param sixeY the height of the grid to display as returned as
   * preferred size of this component
   * @see #getPreferredSize
   */
  public Display(final int sizeX, final int sizeY)
  {
    super("Circular Spreading Simulator");
    size = new Dimension(sizeX, sizeY);
    colors = new Color[sizeX][sizeY];
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    final DrawArea drawArea = new DrawArea();
    add("Center", drawArea);
  }

  private static float saturize(final double value)
  {
    return Math.max(0.0f, Math.min((float)value, 1.0f));
  }

  private static Color getColor(final double forceX, final double forceY)
  {
    final double brightness =
      100000.0 * Math.sqrt(forceX * forceX + forceY * forceY);
    final double hue = Math.atan2(forceY, forceX);
    return Color.getHSBColor((float)(hue), 1.0f, saturize(brightness));
  }

  /**
   * Update the display for the specified cells.
   */
  public void update(final Simulator.Cell[][] buffer)
  {
    final int sizeX = size.width;
    final int sizeY = size.height;
    for (int x = 0; x < sizeX; x++) {
      for (int y = 0; y < sizeY; y++) {
        final Simulator.Cell cell = buffer[x][y];
        final double forceX = cell.forceX;
        final double forceY = cell.forceY;
        colors[x][y] = getColor(forceX, forceY);
      }
    }
    repaint();
  }
}
