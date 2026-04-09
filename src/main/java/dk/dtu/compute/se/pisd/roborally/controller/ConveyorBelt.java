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

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents a conveyor belt on a space.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
// XXX A6b this class might give you some inspiration for
//         implementing the class CheckPoint
// XXX A6d remember to also implement the doAction method for the
//         class CheckPoint you added in Assignment 6b
public class ConveyorBelt extends FieldAction {

    private Heading heading;

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    /**
     * Implementation of the action of a conveyor belt. Needs to be implemented for A3.
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        // TODO A6d: needs to be implemented
        // Here we stop if there is no player on this space.
        if (space.getPlayer() == null) {
            return false;
        }

        // Here we stop if the conveyor belt has no heading yet.
        if (heading == null) {
            return false;
        }

        // Here we ask the board for the next space in the conveyor direction.
        Space nextSpace = space.board.getNeighbour(space, heading);

        // Here we stop if the next space cannot be reached.
        if (nextSpace == null) {
            return false;
        }

        // Here we stop if another player is already standing on the next space.
        if (nextSpace.getPlayer() != null) {
            return false;
        }

        // Here we move the player one step in the conveyor direction.
        space.getPlayer().setSpace(nextSpace);

        // Here we say that the action was carried out.
        return true;
    }

}
