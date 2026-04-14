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
 * Represents a conveyor belt field action that moves a player by one space.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
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
     * Executes the conveyor belt action on the given space.
     *
     * @param gameController the controller of the current game
     * @param space the space whose conveyor action should run
     * @return true if a player was moved, false otherwise
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

        // Here we ask the controller to move the player in the conveyor direction.
        return gameController.movePlayer(space.getPlayer(), heading);
    }

}
