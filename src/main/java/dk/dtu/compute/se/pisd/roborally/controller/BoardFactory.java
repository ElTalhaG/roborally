package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.Board;

import java.util.Arrays;
import java.util.List;

/**
 * A factory for creating boards. The factory itself is implemented as a singleton.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
// XXX A3: might be used for creating a first slightly more interesting board.
public class BoardFactory {

    private static final String DEFAULT_BOARD_NAME = "defaultboard";

    /**
     * The single instance of this class, which is lazily instantiated on demand.
     */
    static private BoardFactory instance = null;

    /**
     * Constructor for BoardFactory. It is private in order to make the factory a singleton.
     */
    private BoardFactory() {
    }

    /**
     * Returns the single instance of this factory. The instance is lazily
     * instantiated when requested for the first time.
     *
     * @return the single instance of the BoardFactory
     */
    public static BoardFactory getInstance() {
        if (instance == null) {
            instance = new BoardFactory();
        }
        return instance;
    }

    /**
     * Creates a new board of given name of a board, which indicates
     * which type of board should be created. For now the name is ignored.
     *
     * @param name the given name board
     * @return the new board corresponding to that name
     */
    public Board createBoard(String name) {
        // TODO A6b: Implement this method properly as described in Assignment 6b.
        //     Dependent on the provided name, create a board accordingly and
        //     return it. In case the name is null, some default board should
        //     be returned (defensive programming).
        // Here we start with the default board name in case the input is null.
        String boardName = DEFAULT_BOARD_NAME;

        // Here we use the given name if it is not null and it is one of the known boards.
        if (name != null && getAvailableBoardNames().contains(name)) {
            boardName = name;
        }

        // Here we try to load the board from the JSON file.
        Board board = LoadBoard.loadBoard(boardName);

        // Here we return a very simple fallback board if loading did not work.
        if (board == null) {
            board = new Board(8, 8, DEFAULT_BOARD_NAME);
        }

        // Here we give back the board we created or loaded.
        return board;
    }

    // TODO A6b: add a method that returns a list (of type List<String>)
    //     of all available board names. The corresponding method
    //     createBoard(String name) must return a board for any of the
    //     names in this list. Make sure that the new method that you create
    //     here has a proper JavaDoc documentation.
    //

    /**
     * Returns the names of the boards that can be selected in the application.
     *
     * @return the list of available board names
     */
    public List<String> getAvailableBoardNames() {
        // Here we return the board names that exist in the project right now.
        return Arrays.asList(DEFAULT_BOARD_NAME);
    }

}
