package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a checkpoint field action that records player progress.
 *
 * @author Ekkart Kindler, ikke@dtu.dk
 */
public class CheckPoint extends FieldAction {

    private int number;

    private boolean last;

    /**
     * Returns the number of this checkpoint.
     *
     * @return the checkpoint number
     */
    public int getNumber() {
        // Here we just give back the checkpoint number.
        return number;
    }

    /**
     * Sets the number of this checkpoint.
     *
     * @param number the checkpoint number
     */
    public void setNumber(int number) {
        // Here we save the checkpoint number in the object.
        this.number = number;
    }

    /**
     * Returns whether this is the last checkpoint.
     *
     * @return true if this is the last checkpoint
     */
    public boolean isLast() {
        // Here we tell if this is the last checkpoint or not.
        return last;
    }

    /**
     * Sets whether this is the last checkpoint.
     *
     * @param last true if this is the last checkpoint
     */
    public void setLast(boolean last) {
        // Here we save whether this checkpoint is the last one.
        this.last = last;
    }

    /**
     * Executes the checkpoint action on the given space.
     *
     * @param gameController the controller of the current game
     * @param space the space whose checkpoint action should run
     * @return true if the player's checkpoint progress changed
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        // Here we stop if there is no player standing on this checkpoint.
        if (space.getPlayer() == null) {
            return false;
        }

        // Here we get the player who stands on the checkpoint.
        int nextCheckPoint = space.getPlayer().getCheckPoints() + 1;

        // Here we only count the checkpoint if it is the next one in order.
        if (number == nextCheckPoint) {
            // Here we save the new checkpoint progress on the player.
            space.getPlayer().setCheckPoints(number);
            // Here we mark the player as winner if this was the last checkpoint.
            if (last) {
                space.board.setWinner(space.getPlayer());
            }
            // Here we say that the checkpoint action worked.
            return true;
        }

        // Here we say that nothing changed.
        return false;
    }

}
