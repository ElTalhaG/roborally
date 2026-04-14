RoboRally Submission 3

What this submission contains
This submission contains one combined solution for assignments 6a, 6b, 6c, 6d, and 6e, based on the original assignment6 project.

How the solution was made
The project was kept in the original structure from the base code. The main work was done in the model, controller, and view packages, and a few extra tests were added in the test package.

6a
For 6a, the click-to-move helper was implemented so the current player can move to a free space, the move counter is increased, and the turn changes to the next player in order.

The move counter is still kept in the project and is shown in the status text below the board.

6b
For 6b, board selection was added when starting a new game. The game now lets the user choose between a standard board and a more advanced board.

Board creation is handled through BoardFactory, and the board data is loaded from JSON files in the resources folder.

Walls, conveyor belts, and checkpoints are drawn in the board view, so the selected board can actually be seen in the GUI.

6c
For 6c, command execution and program execution were implemented.

The project now supports:
- FORWARD
- FAST_FORWARD
- LEFT
- RIGHT
- BACK
- UTURN

The buttons in the player tabs were connected to the real controller methods, so the programming and activation phases can be used directly from the GUI.

Movement no longer wraps around the board edges. If a move would go outside the board, it is blocked. Walls also block movement.

6d
For 6d, field actions were implemented.

Conveyor belts now move players when possible, and checkpoints now update the player's progress in the correct order.

Player pushing was also implemented. This means that if a player tries to move into an occupied space, the blocking player is pushed in the same direction if there is room for that move chain to happen. If the push cannot be completed, the move is blocked.

Checkpoint progress is shown in each player's tab.

6e
For 6e, interactive command cards and winning the game were added.

Two interactive cards were added:
- MOVE_1_OR_2
- TURN_LEFT_OR_RIGHT

When one of these cards is reached during activation, the game switches to PLAYER_INTERACTION. The current player then sees buttons for the real card options and can choose one of them.

Winning is handled through the last checkpoint. When a player reaches the last checkpoint in the correct order, that player is stored as the winner and the status text shows the winner as well.

Design choices
Some small design choices were made and are listed here so the behaviour is clear:

- Board edges do not wrap around. Moving outside the board is blocked.
- Walls block movement from both sides.
- Player pushing is recursive, so several players can be pushed in a line if there is space.
- Conveyor belts use the same movement rules as normal movement, including pushing and wall checks.
- The click-to-move helper from 6a was kept because it is useful for manual testing.
- The database save/load part was not extended beyond the original placeholders, because the submission requirements for this hand-in focus on assignments 6a to 6e.

Tests
Extra tests were added for the implemented behaviour in the controller and related model logic.

The tests cover:
- player turn changing
- forward and backward movement
- turning
- neighbour blocking by walls and board edges
- program execution
- conveyor belts
- checkpoints
- player pushing
- interactive command cards
- winning the game
- multiple board choices

The Maven test suite passes without errors.

Coverage note
Tests were added specifically for the implemented assignment paths in GameController, ConveyorBelt, and CheckPoint.

No separate coverage report file is included in the submission folder, but the test suite was written so the important assignment logic is executed directly.

Extras
At the end, a few extra touches were also kept:

- a second playable board called advancedboard
- the click-to-move helper for manual testing
- a slightly more informative status line showing phase, player, register, move count, and winner
