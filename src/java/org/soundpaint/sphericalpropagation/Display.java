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

public class Display extends JFrame
{
  private final int sizeX, sizeY;
  private Color[][] colors;

  private class DrawArea extends JPanel
  {
    public Dimension getPreferredSize()
    {
      return new Dimension(sizeX, sizeY);
    }

    public void paint(final Graphics g)
    {
      if (colors == null) return; // nothing yet available to draw
      final Dimension size = getSize();
      final Graphics2D g2 = (Graphics2D)g;
      final int gWidth = (int)(0.5 + size.getWidth());
      final int gHeight = (int)(0.5 + size.getHeight());
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

  private Display() { throw new UnsupportedOperationException(); }

  public Display(final int sizeX, final int sizeY)
  {
    super("Circular Spreading Simulator");
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    final DrawArea drawArea = new DrawArea();
    add("Center", drawArea);
  }

  public void update(final Color[][] colors)
  {
    this.colors = colors;
    repaint();
  }
}
