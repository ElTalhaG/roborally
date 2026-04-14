RoboRally Submission 3

Overview
This submission contains one combined solution for assignments 6a, 6b, 6c, 6d, and 6e based on the original assignment6 project.

Implementation summary
6a:
- I implemented changing between players in turn order in GameController.moveCurrentPlayerToSpace().
- I added a move counter to Board and show it in the board status message.

6b:
- I implemented board selection at game start in AppController.newGame().
- I implemented board creation by name in BoardFactory.
- I added two selectable boards: defaultboard and advancedboard.
- I implemented drawing of walls, conveyor belts, and checkpoints in SpaceView.
- I added the missing checkpoint file-access template and checkpoint model/controller class used by the board JSON.

6c:
- I implemented execution of command cards and program steps in GameController.
- I implemented FORWARD, FAST_FORWARD, LEFT, RIGHT, BACK, and UTURN.
- I connected the player-tab buttons to the real controller methods.
- I updated neighbour handling so movement stops at board edges and walls instead of wrapping around.
- I show phase, current player, current register, move count, and winner in the board status text.

6d:
- I implemented field actions for ConveyorBelt and CheckPoint.
- I implemented pushing of players when a player or conveyor movement tries to enter an occupied reachable space.
- I added checkpoint progress to Player and show it in PlayerView.
- Field actions are executed after the player's command during activation.

6e:
- I added two interactive command cards: MOVE_1_OR_2 and TURN_LEFT_OR_RIGHT.
- I implemented switching from ACTIVATION to PLAYER_INTERACTION when an interactive command is reached.
- I implemented showing the real command options in PlayerView and executing the selected option.
- I implemented winning the game when the last checkpoint is reached.

Design choices
- Board edges do not wrap around. If a move would leave the board, the move is blocked.
- Walls block movement both from the current space and from the opposite wall of the neighbour space.
- Player pushing is recursive. A player can move only if every blocking player in front can also be moved in the same direction.
- Conveyor belts use the same movement rules as normal movement, including wall checks and player pushing.
- The move counter is kept for the click-move helper from assignment 6a and is still shown in the status bar.
- The database save/load features were not extended further because the submission requirements focus on assignments 6a-6e.

Tests
- I added tests for player turn changes, command execution, neighbour blocking by walls and edges, conveyor belts, checkpoints, interactive cards, winning, second board availability, and pushing.
- The Maven test suite passes without errors.

Coverage note
- I added tests to cover the implemented behaviour in GameController, ConveyorBelt, and CheckPoint.
- I did not generate a separate coverage report file in the submission folder, but the tests were written specifically to exercise the implemented assignment paths directly.

Extras
- I added a second playable JSON board named advancedboard.
- I kept the click-to-move helper from 6a because it is useful for manual testing.
