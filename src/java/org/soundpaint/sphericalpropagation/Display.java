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
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.imageio.ImageIO;
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
  private final transient BufferedImage image;
  private final transient Graphics2D imageGraphics;

  private int imageIndex;

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

  private void save() {
    paint(imageGraphics);
    final Properties props = System.getProperties();
    final String template = props.get("image-filename").toString();
    final String dir = props.get("rundir").toString();
    final String filename = String.format(template, imageIndex++);
    try {
      ImageIO.write(image, "PNG", new File(dir, filename));
    } catch (final IOException exc) {
      System.err.println("failed saving image: " + exc);
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
    image = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_3BYTE_BGR);
    imageGraphics = image.createGraphics();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    final DrawArea drawArea = new DrawArea();
    add("Center", drawArea);
  }

  private static Color getColor(final double forceX, final double forceY)
  {
    final double brightness = 1.0;
    final double hue = 1000.0 * Math.sqrt(forceX * forceX + forceY * forceY);
    return Color.getHSBColor((float)(hue), 1.0f, 1.0f);
  }

  /**
   * Update the display for the specified cells.
   *
   * @param state the array of the simulator's cells, representing the
   * latest state of the simulation
   */
  public void update(final Simulator.Cell[][] state)
  {
    final int sizeX = size.width;
    final int sizeY = size.height;
    for (int x = 0; x < sizeX; x++) {
      for (int y = 0; y < sizeY; y++) {
        final Simulator.Cell cell = state[x][y];
        final double forceX = cell.forceX;
        final double forceY = cell.forceY;
        colors[x][y] = getColor(forceX, forceY);
      }
    }
    repaint();
    save();
  }

  /**
   * Close the window of this frame.
   */
  public void close() {
    dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
  }
}
