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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * Controls the game flow of RoboRally, including movement, phases,
 * command execution, field actions, and interactive cards.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class GameController {

    final public Board board;

    private Command pendingInteractiveCommand;

    /**
     * Creates a controller for the given board.
     *
     * @param board the board that this controller manages
     */
    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
        // Here we get the player whose turn it is right now.
        Player currentPlayer = board.getCurrentPlayer();

        // Here we only continue if there is a player, the space is on this board, and the space is empty.
        if (currentPlayer != null && space.board == board && space.getPlayer() == null) {
            // Here we move the player to the space the user clicked on.
            currentPlayer.setSpace(space);
            // Here we take the old move count and add one more move to it.
            board.setMoveCounter(board.getMoveCounter() + 1);

            // Here we find the number of the player we just moved.
            int currentPlayerNumber = board.getPlayerNumber(currentPlayer);
            // Here we start by saying the next player is one step after that player.
            int nextPlayerNumber = currentPlayerNumber + 1;

            // Here we go back to player 0 if we were at the end of the list.
            if (nextPlayerNumber >= board.getPlayersNumber()) {
                nextPlayerNumber = 0;
            }

            // Here we change the turn to the next player.
            board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
        }
    }

    /**
     * Starts the programming phase and gives all players a new hand of cards.
     */
    public void startProgrammingPhase() {
        pendingInteractiveCommand = null;
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    /**
     * Creates one random command card.
     *
     * @return the generated command card
     */
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    /**
     * Ends the programming phase and starts the activation phase.
     */
    public void finishProgrammingPhase() {
        pendingInteractiveCommand = null;
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    /**
     * Makes one register visible for all players.
     *
     * @param register the register index that should be shown
     */
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    /**
     * Hides all program fields for all players.
     */
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    /**
     * Executes all remaining program steps until the phase changes or the game ends.
     */
    public void executePrograms() {
        if (board.getWinner() != null) {
            return;
        }
        board.setStepMode(false);
        continuePrograms();
    }

    /**
     * Executes exactly one step of the current program.
     */
    public void executeStep() {
        if (board.getWinner() != null) {
            return;
        }
        board.setStepMode(true);
        continuePrograms();
    }

    /**
     * Continues program execution while automatic execution is enabled.
     */
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode() && board.getWinner() == null);
    }

    /**
     * Executes the next step for the current player in the activation phase.
     */
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null && board.getWinner() == null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    // Here we stop and ask the player when the card is interactive.
                    if (command.isInteractive()) {
                        pendingInteractiveCommand = command;
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    }
                    executeCommand(currentPlayer, command);
                }
                finishCurrentPlayerStep(currentPlayer);
            } else {
                return;
            }
        } else {
            return;
        }
    }

    /**
     * Executes one command for one player.
     *
     * @param player the player that should execute the command
     * @param command the command to execute
     */
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                case BACK:
                    this.moveBack(player);
                    break;
                case UTURN:
                    this.turnAround(player);
                    break;
                case MOVE_1_OR_2:
                case TURN_LEFT_OR_RIGHT:
                    // DO NOTHING here because these commands are handled via player interaction.
                    break;
                default:
                    // DO NOTHING (for now)//
            }
        }
    }

    /**
     * Moves a player one space forward, pushing other players if possible.
     *
     * @param player the player to move
     */
    public void moveForward(@NotNull Player player) {
        // Here we stop if the player is not on this board.
        if (player.board != board || player.getSpace() == null) {
            return;
        }

        // Here we try to move in the direction the player is facing.
        movePlayer(player, player.getHeading());
    }

    /**
     * Moves a player two spaces forward.
     *
     * @param player the player to move
     */
    public void fastForward(@NotNull Player player) {
        // Here we move one step forward the first time.
        moveForward(player);
        // Here we try to move one more step forward after that.
        moveForward(player);
    }

    /**
     * Turns a player one step to the right.
     *
     * @param player the player to turn
     */
    public void turnRight(@NotNull Player player) {
        // Here we stop if the player is not on this board.
        if (player.board != board) {
            return;
        }

        // Here we change the heading to the next direction on the right.
        player.setHeading(player.getHeading().next());
    }

    /**
     * Turns a player one step to the left.
     *
     * @param player the player to turn
     */
    public void turnLeft(@NotNull Player player) {
        // Here we stop if the player is not on this board.
        if (player.board != board) {
            return;
        }

        // Here we change the heading to the next direction on the left.
        player.setHeading(player.getHeading().prev());
    }

    /**
     * Moves a player one space backwards, pushing other players if possible.
     *
     * @param player the player to move
     */
    public void moveBack(@NotNull Player player) {
        // Here we stop if the player is not on this board.
        if (player.board != board || player.getSpace() == null) {
            return;
        }

        // Here we find the direction behind the player.
        Heading backHeading = player.getHeading().next().next();
        // Here we try to move one step in the direction behind the player.
        movePlayer(player, backHeading);
    }

    /**
     * Turns a player around.
     *
     * @param player the player to turn
     */
    public void turnAround(@NotNull Player player) {
        // Here we stop if the player is not on this board.
        if (player.board != board) {
            return;
        }

        // Here we turn the player two times so it faces the opposite way.
        player.setHeading(player.getHeading().next().next());
    }

    /**
     * Executes all field actions on the given space.
     *
     * @param space the space whose actions should be executed
     */
    private void executeFieldActions(@NotNull Space space) {
        // Here we go through all actions on the space one by one.
        for (FieldAction action : space.getActions()) {
            // Here we run each action with this game controller and the current space.
            action.doAction(this, space);
        }
    }

    /**
     * Returns the interactive command that is currently waiting for a choice.
     *
     * @return the pending interactive command, or null if none is waiting
     */
    public Command getPendingInteractiveCommand() {
        // Here we give back the interactive command that is waiting for a choice.
        return pendingInteractiveCommand;
    }

    /**
     * Executes one of the available options of the pending interactive command.
     *
     * @param command the chosen command option
     */
    public void executeInteractiveCommandOption(@NotNull Command command) {
        // Here we stop if the game is not waiting for an interactive choice.
        if (board.getPhase() != Phase.PLAYER_INTERACTION) {
            return;
        }

        // Here we stop if there is no current player.
        Player currentPlayer = board.getCurrentPlayer();
        if (currentPlayer == null) {
            return;
        }

        // Here we stop if there is no pending interactive command.
        if (pendingInteractiveCommand == null) {
            return;
        }

        // Here we stop if the chosen command is not one of the allowed options.
        if (!pendingInteractiveCommand.getOptions().contains(command)) {
            return;
        }

        // Here we switch back to activation because the player made a choice.
        board.setPhase(Phase.ACTIVATION);
        // Here we run the command option the player selected.
        executeCommand(currentPlayer, command);
        // Here we clear the pending interactive command because it is done now.
        pendingInteractiveCommand = null;
        // Here we finish the rest of this player's step.
        finishCurrentPlayerStep(currentPlayer);
        // Here we continue automatically when we are not in step mode.
        if (!board.isStepMode()) {
            continuePrograms();
        }
    }

    /**
     * Finishes the current player's step by executing field actions and
     * advancing to the next player or next register.
     *
     * @param currentPlayer the player whose step has just been completed
     */
    private void finishCurrentPlayerStep(@NotNull Player currentPlayer) {
        // Here we run the field actions on the space where the player ends up.
        if (currentPlayer.getSpace() != null) {
            executeFieldActions(currentPlayer.getSpace());
        }

        // Here we stop early if the player already won the game.
        if (board.getWinner() != null) {
            return;
        }

        // Here we find the next player number.
        int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
        if (nextPlayerNumber < board.getPlayersNumber()) {
            // Here we switch to the next player in the same register.
            board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
        } else {
            int step = board.getStep() + 1;
            if (step < Player.NO_REGISTERS) {
                // Here we move on to the next register and show it.
                makeProgramFieldsVisible(step);
                board.setStep(step);
                board.setCurrentPlayer(board.getPlayer(0));
            } else {
                // Here we start a new programming phase after all registers are done.
                startProgrammingPhase();
            }
        }
    }

    /**
     * Tries to move a player one space in the given direction and pushes blocking
     * players in the same direction when possible.
     *
     * @param player the player to move
     * @param heading the direction of the move
     * @return true if the player was moved successfully
     */
    boolean movePlayer(@NotNull Player player, @NotNull Heading heading) {
        // Here we stop if the player is not placed on this board.
        if (player.board != board || player.getSpace() == null) {
            return false;
        }

        // Here we ask the board for the next space in the chosen direction.
        Space nextSpace = board.getNeighbour(player.getSpace(), heading);

        // Here we stop if there is no reachable next space.
        if (nextSpace == null) {
            return false;
        }

        // Here we push the blocking player first if the next space is occupied.
        if (nextSpace.getPlayer() != null) {
            boolean moved = movePlayer(nextSpace.getPlayer(), heading);
            if (!moved) {
                return false;
            }
        }

        // Here we move the player into the next space.
        player.setSpace(nextSpace);
        return true;
    }

}
