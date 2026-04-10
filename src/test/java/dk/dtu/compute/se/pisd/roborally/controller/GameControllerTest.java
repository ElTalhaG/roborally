package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null,"Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    /**
     * Test for Assignment 6a (can be deleted later once Assignment 6a was shown to the teacher)
     */
    @Test
    void testV1() {
        Board board = gameController.board;

        Player player1 = board.getCurrentPlayer();
        Player player2 = board.getPlayer(1);
        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should be on Space (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() +"!");
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void turnRight() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.turnRight(current);

        Assertions.assertEquals(Heading.WEST, current.getHeading(), "Player 0 should be heading WEST!");
    }

    @Test
    void moveBack() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setSpace(board.getSpace(6, 3));

        gameController.moveBack(current);

        Assertions.assertEquals(current, board.getSpace(6, 2).getPlayer(), "Player " + current.getName() + " should be on Space (6,2)!");
        Assertions.assertNull(board.getSpace(6, 3).getPlayer(), "Space (6,3) should be empty!");
    }

    @Test
    void getNeighbourStopsAtWall() {
        Board board = gameController.board;
        board.getSpace(0, 0).getWalls().add(Heading.SOUTH);

        Assertions.assertNull(board.getNeighbour(board.getSpace(0, 0), Heading.SOUTH), "There should be no reachable neighbour through a wall!");
    }

    @Test
    void getNeighbourStopsOutsideBoard() {
        Board board = gameController.board;

        Assertions.assertNull(board.getNeighbour(board.getSpace(0, 0), Heading.NORTH), "There should be no neighbour outside the board!");
    }

    @Test
    void conveyorBeltMovesPlayer() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setSpace(board.getSpace(2, 2));

        ConveyorBelt conveyorBelt = new ConveyorBelt();
        conveyorBelt.setHeading(Heading.SOUTH);
        board.getSpace(2, 2).getActions().add(conveyorBelt);

        Assertions.assertTrue(conveyorBelt.doAction(gameController, board.getSpace(2, 2)), "The conveyor belt should move the player!");
        Assertions.assertEquals(current, board.getSpace(2, 3).getPlayer(), "Player should be moved to Space (2,3)!");
    }

    @Test
    void checkpointUpdatesProgressInOrder() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        CheckPoint checkPoint = new CheckPoint();
        checkPoint.setNumber(1);
        board.getSpace(0, 0).getActions().add(checkPoint);

        Assertions.assertTrue(checkPoint.doAction(gameController, board.getSpace(0, 0)), "The checkpoint should be counted!");
        Assertions.assertEquals(1, current.getCheckPoints(), "The player should have reached checkpoint 1!");
    }

    @Test
    void executeStepRunsFieldActions() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setSpace(board.getSpace(2, 2));
        current.setHeading(Heading.SOUTH);

        board.getSpace(2, 3).getActions().clear();
        CheckPoint checkPoint = new CheckPoint();
        checkPoint.setNumber(1);
        board.getSpace(2, 3).getActions().add(checkPoint);

        current.getProgramField(0).setCard(new CommandCard(Command.FORWARD));
        board.setPhase(Phase.ACTIVATION);
        board.setStep(0);
        board.setCurrentPlayer(current);

        gameController.executeStep();

        Assertions.assertEquals(current, board.getSpace(2, 3).getPlayer(), "The player should end on Space (2,3)!");
        Assertions.assertEquals(1, current.getCheckPoints(), "The checkpoint action should have been executed!");
    }

    @Test
    void interactiveCommandChangesPhase() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        current.getProgramField(0).setCard(new CommandCard(Command.MOVE_1_OR_2));
        board.setPhase(Phase.ACTIVATION);
        board.setStep(0);
        board.setCurrentPlayer(current);

        gameController.executeStep();

        Assertions.assertEquals(Phase.PLAYER_INTERACTION, board.getPhase(), "The game should wait for the player to choose an option!");
        Assertions.assertEquals(Command.MOVE_1_OR_2, gameController.getPendingInteractiveCommand(), "The interactive command should be stored!");
    }

    @Test
    void interactiveCommandOptionExecutesChoice() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setSpace(board.getSpace(2, 2));
        current.setHeading(Heading.SOUTH);

        current.getProgramField(0).setCard(new CommandCard(Command.MOVE_1_OR_2));
        board.setPhase(Phase.ACTIVATION);
        board.setStep(0);
        board.setCurrentPlayer(current);
        board.setStepMode(true);

        gameController.executeStep();
        gameController.executeInteractiveCommandOption(Command.FAST_FORWARD);

        Assertions.assertEquals(Phase.ACTIVATION, board.getPhase(), "The game should go back to activation after the player chooses!");
        Assertions.assertEquals(current, board.getSpace(2, 4).getPlayer(), "The player should move two spaces forward!");
    }

    @Test
    void lastCheckpointSetsWinner() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        CheckPoint checkPoint = new CheckPoint();
        checkPoint.setNumber(1);
        checkPoint.setLast(true);
        board.getSpace(0, 0).getActions().add(checkPoint);

        Assertions.assertTrue(checkPoint.doAction(gameController, board.getSpace(0, 0)), "The last checkpoint should be counted!");
        Assertions.assertEquals(current, board.getWinner(), "The player should be stored as the winner!");
        Assertions.assertTrue(board.getStatusMessage().contains(current.getName()), "The status message should mention the winner name!");
    }

    // TODO and there should be more tests added for the different assignments eventually

}
