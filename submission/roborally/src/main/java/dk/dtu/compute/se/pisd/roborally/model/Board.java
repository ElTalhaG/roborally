/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * Represents one RoboRally game board and stores the full game state.
 * Note that the terms board and game are used almost interchangeably.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    public final String boardName;

    private Integer gameId;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    private Player current;

    private Player winner;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;

    private int moveCounter = 0;

    public Board(int width, int height, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        this.stepMode = false;
    }

    public Board(int width, int height) {
        this(width, height, "defaultboard");
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    public int getPlayersNumber() {
        return players.size();
    }

    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    /**
     * Returns the player whose turn it currently is.
     *
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return current;
    }

    /**
     * Sets the current player.
     *
     * @param player the player whose turn it should be
     */
    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    /**
     * Returns the winner of the game.
     *
     * @return the winning player, or null if the game has no winner yet
     */
    public Player getWinner() {
        // Here we give back the player who won the game.
        return winner;
    }

    /**
     * Stores the winner of the game.
     *
     * @param winner the player who won the game
     */
    public void setWinner(Player winner) {
        // Here we only save the winner if it changed.
        if (winner != this.winner) {
            // Here we store the winner on the board.
            this.winner = winner;
            // Here we tell the views that the board changed.
            notifyChange();
        }
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    public boolean isStepMode() {
        return stepMode;
    }

    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    /**
     * Returns how many manual moves have been made with the click-move helper.
     *
     * @return the move counter
     */
    public int getMoveCounter() {
        // Here we just give back the move counter value.
        return moveCounter;
    }

    /**
     * Updates the move counter.
     *
     * @param moveCounter the new move counter value
     */
    public void setMoveCounter(int moveCounter) {
        // Here we only update the value if it is different from before.
        if (moveCounter != this.moveCounter) {
            // Here we save the new move counter in the board.
            this.moveCounter = moveCounter;
            // Here we tell the views that something changed.
            notifyChange();
        }
    }

    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned (this needs to be implemented for Assignment 6c).
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = y + 1;
                break;
            case WEST:
                x = x - 1;
                break;
            case NORTH:
                y = y - 1;
                break;
            case EAST:
                x = x + 1;
                break;
        }

        // Here we stop if there is a wall on the space we start from.
        if (space.getWalls().contains(heading)) {
            return null;
        }

        // Here we get the neighbour space in the chosen direction.
        Space neighbour = getSpace(x, y);

        // Here we stop if the neighbour is outside the board.
        if (neighbour == null) {
            return null;
        }

        // Here we also stop if the neighbour has a wall on the opposite side.
        if (neighbour.getWalls().contains(getOppositeHeading(heading))) {
            return null;
        }

        // Here we give back the reachable neighbour space.
        return neighbour;
    }

    /**
     * Builds the status text shown below the board.
     *
     * @return the current status text
     */
    public String getStatusMessage() {
        // this is actually a view aspect, but for making assignment V1 easy for
        // the students, this method gives a string representation of the current
        // status of the game

        // Here we start with a default text in case no player is selected.
        String playerName = "<none>";
        // Here we check if there is a current player.
        if (getCurrentPlayer() != null) {
            // Here we take the name from the current player and store it in the string.
            playerName = getCurrentPlayer().getName();
        }
        // Here we start with a simple register text.
        String registerText = "-";
        // Here we show the current register when the game is in activation.
        if (getPhase() == Phase.ACTIVATION) {
            registerText = String.valueOf(getStep() + 1);
        }
        // Here we start with the normal status text.
        String status = "Phase = " + getPhase() + ", Player = " + playerName + ", Register = " + registerText
                + ", Moves = " + getMoveCounter();
        // Here we add winner information when someone has won the game.
        if (getWinner() != null) {
            status = status + ", Winner = " + getWinner().getName();
        }
        // Here we give back the full status text.
        return status;
    }

    /**
     * Returns the opposite direction of the given heading.
     *
     * @param heading the heading to invert
     * @return the opposite heading
     */
    private Heading getOppositeHeading(Heading heading) {
        // Here we return the opposite direction of the heading we got.
        switch (heading) {
            case NORTH:
                return Heading.SOUTH;
            case SOUTH:
                return Heading.NORTH;
            case EAST:
                return Heading.WEST;
            case WEST:
                return Heading.EAST;
            default:
                return heading;
        }
    }

}
