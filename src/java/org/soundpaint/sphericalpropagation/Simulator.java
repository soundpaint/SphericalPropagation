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

  private final int rounds;
  private final int sizeX, sizeY;
  private final Display display;
  private Cell[][] prevState, nextState;
  private int round;

  private Simulator() {
    throw new UnsupportedOperationException();
  }

  /**
   * Create a new instance of thecellular automaton simulation on a
   * cellular grid with the specified dimension.
   *
   * @param rounds the number of simulation steps to perform
   * @param sizeX the width of the grid of the cellular automaton
   * @param sizeY the height of the grid of the cellular automaton
   * @param display the output to use for displaying the automaton's
   * current state
   */
  public Simulator(final int rounds, final int sizeX, final int sizeY,
                   final Display display)
  {
    this.rounds = rounds;
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    this.display = display;
    prevState = new Cell[sizeX][sizeY];
    nextState = new Cell[sizeX][sizeY];
  }

  private void updateCell(final int x, final int y, final Cell cell)
  {
    final int xEast = x < sizeX - 1 ? x + 1 : x + 1 - sizeX;
    final int xWest = x > 0 ? x - 1 : sizeX - 1;
    final int ySouth = y < sizeY - 1 ? y + 1 : y + 1 - sizeY;
    final int yNorth = y > 0 ? y - 1 : sizeY - 1;
    final Cell neighbourSouth = prevState[x][ySouth];
    final Cell neighbourNorth = prevState[x][yNorth];
    final Cell neighbourWest = prevState[xWest][y];
    final Cell neighbourEast = prevState[xEast][y];
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
        prevState[x][y] = new Cell();
        nextState[x][y] = new Cell();
      }
    }
    final Cell cell = prevState[sizeX / 2][sizeY / 2];
    cell.forceX = 100.0;
    cell.forceY = -100.0;
  }

  private void swapStates() {
    final Cell swapState[][] = prevState;
    prevState = nextState;
    nextState = swapState;
  }

  private static void showProgress(final Simulator simulator) {
    System.out.println(); // ensure we start a new line
    for (;;) {
      try {
        Thread.sleep(1000);
      } catch (final InterruptedException exc) {
        break;
      }
      simulator.showProgress();
    }
    simulator.showProgress();
  }

  private void showProgress() {
    final double percent = 100.0 * round / rounds;
    final String progress =
      String.format("[% 4d/%d (%3.2f%%)]\r", round, rounds, percent);
    System.out.println(progress);
    System.out.flush();
  }

  /**
   * Start the simulation.
   */
  public void run()
  {
    final Thread progressThread = new Thread(() -> { showProgress(this); });
    setupStartConfiguration();
    progressThread.start();
    for (round = 0; round < rounds; round++) {
      for (int x = 0; x < sizeX; x++)
        for (int y = 0; y < sizeY; y++)
          updateCell(x, y, nextState[x][y]);
      swapStates();
      if ((round & 0x3) == 0) {
        display.update(prevState);
      }
    }
    progressThread.interrupt();
  }
}
