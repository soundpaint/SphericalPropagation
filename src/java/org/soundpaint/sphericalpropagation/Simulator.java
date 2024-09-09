/*
 * @(#)Simulator.java 1.00 18/07/21
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
 * The core simulation.
 */
public class Simulator
{
  /**
   * Each object instance of this class represents a single cell of
   * the cellular automaton.
   */
  public class Cell
  {
    /**
     * "Force" values for xand y direction.
     */
    public double forceX, forceY;
  }

  private final int sizeX, sizeY;
  private final Display display;
  private Cell[][] frontBuffer, backBuffer;

  private Simulator() {
    throw new UnsupportedOperationException();
  }

  /**
   * Create a new instance of thecellular automaton simulation on a
   * cellular grid with the specified dimension.
   *
   * @param sizeX the width of the grid of the cellular automaton
   * @param sizeY the height of the grid of the cellular automaton
   * @param display the output to use for displaying the automaton's
   * current state
   */
  public Simulator(final int sizeX, final int sizeY,
                   final Display display)
  {
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    this.display = display;
    frontBuffer = new Cell[sizeX][sizeY];
    backBuffer = new Cell[sizeX][sizeY];
  }

  private void updateCell(final int x, final int y, final Cell cell,
                          final Cell[][] buffer)
  {
    final Cell neighbourSouth = buffer[x][(y + 1) % sizeY];
    final Cell neighbourNorth = buffer[x][(y - 1 + sizeY) % sizeY];
    final Cell neighbourWest = buffer[(x - 1 + sizeX) % sizeX][y];
    final Cell neighbourEast = buffer[(x + 1) % sizeX][y];
    cell.forceX =
      0.25 * neighbourSouth.forceX -
      0.25 * neighbourNorth.forceX +
      0.25 * neighbourWest.forceY -
      0.25 * neighbourEast.forceY;
    cell.forceY =
      0.25 * neighbourSouth.forceY -
      0.25 * neighbourNorth.forceY +
      0.25 * neighbourWest.forceX -
      0.25 * neighbourEast.forceX;
  }

  private void setupStartConfiguration()
  {
    for (int x = 0; x < sizeX; x++) {
      for (int y = 0; y < sizeY; y++) {
        frontBuffer[x][y] = new Cell();
        backBuffer[x][y] = new Cell();
      }
    }
    final Cell cell = frontBuffer[sizeX / 2][sizeY / 2];
    cell.forceX = 100.0;
    cell.forceY = -100.0;
  }

  /**
   * Start the simulation.
   */
  public void run()
  {
    setupStartConfiguration();
    int count = 0;
    while (true) {
      try {
        Thread.sleep(100);
      } catch (final InterruptedException exc) {
        return; // abort simulation
      }
      for (int x = 0; x < sizeX; x++)
        for (int y = 0; y < sizeY; y++)
          updateCell(x, y, backBuffer[x][y], frontBuffer);
      final Cell swapBuffer[][] = frontBuffer;
      frontBuffer = backBuffer;
      backBuffer = swapBuffer;
      count--;
      if (count < 0) {
        display.update(frontBuffer);
        count = 3;
      }
    }
  }
}
