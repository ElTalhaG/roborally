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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import org.jetbrains.annotations.NotNull;

/**
 * Visual representation of one board space, including walls, actions, and player token.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 40; // 60; // 75;
    final public static int SPACE_WIDTH = 40;  // 60; // 75;

    public final Space space;

    /**
     * Creates the view for one space.
     *
     * @param space the model space shown by this view
     */
    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    /**
     * Draws the player token on the space, if a player is present.
     */
    private void updatePlayer() {
        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            this.getChildren().clear();

            // TODO A6b: drawing the walls and the field action(s) on
            //     this space could be implemented here.
            // Here we draw the walls that belong to this space.
            drawWalls();
            // Here we draw the actions that belong to this space.
            drawActions();

            updatePlayer();
        }
    }

    /**
     * Draws all walls on this space.
     */
    private void drawWalls() {
        // Here we go through all walls on this space one by one.
        for (Heading wall : space.getWalls()) {
            // Here we make one line that we can place on the edge of the space.
            Line line = new Line();
            line.setStroke(Color.DARKRED);
            line.setStrokeWidth(3.0);

            // Here we choose where the wall line should be drawn.
            if (wall == Heading.NORTH) {
                line.setStartX(0.0);
                line.setStartY(0.0);
                line.setEndX(SPACE_WIDTH);
                line.setEndY(0.0);
            } else if (wall == Heading.SOUTH) {
                line.setStartX(0.0);
                line.setStartY(SPACE_HEIGHT);
                line.setEndX(SPACE_WIDTH);
                line.setEndY(SPACE_HEIGHT);
            } else if (wall == Heading.WEST) {
                line.setStartX(0.0);
                line.setStartY(0.0);
                line.setEndX(0.0);
                line.setEndY(SPACE_HEIGHT);
            } else if (wall == Heading.EAST) {
                line.setStartX(SPACE_WIDTH);
                line.setStartY(0.0);
                line.setEndX(SPACE_WIDTH);
                line.setEndY(SPACE_HEIGHT);
            }

            // Here we add the line to the space view.
            this.getChildren().add(line);
        }
    }

    /**
     * Draws all field actions on this space.
     */
    private void drawActions() {
        // Here we go through all field actions on this space one by one.
        for (FieldAction action : space.getActions()) {
            // Here we draw a small arrow when the action is a conveyor belt.
            if (action instanceof ConveyorBelt) {
                drawConveyorBelt((ConveyorBelt) action);
            } else if (action instanceof CheckPoint) {
                // Here we draw a checkpoint marker when the action is a checkpoint.
                drawCheckPoint((CheckPoint) action);
            }
        }
    }

    /**
     * Draws a conveyor belt marker.
     *
     * @param conveyorBelt the conveyor belt to visualise
     */
    private void drawConveyorBelt(ConveyorBelt conveyorBelt) {
        // Here we make a small triangle that shows the belt direction.
        Polygon arrow = new Polygon(0.0, -10.0,
                8.0, 6.0,
                -8.0, 6.0);
        arrow.setFill(Color.LIGHTGREEN);

        // Here we rotate the arrow so it points in the conveyor direction.
        if (conveyorBelt.getHeading() != null) {
            arrow.setRotate((90 * conveyorBelt.getHeading().ordinal()) % 360);
        }

        // Here we add the conveyor arrow to the space view.
        this.getChildren().add(arrow);
    }

    /**
     * Draws a checkpoint marker.
     *
     * @param checkPoint the checkpoint to visualise
     */
    private void drawCheckPoint(CheckPoint checkPoint) {
        // Here we make a small circle to show the checkpoint position.
        Circle circle = new Circle(10.0);
        circle.setFill(Color.GOLD);
        circle.setStroke(Color.DARKGOLDENROD);

        // Here we make a label that shows the checkpoint number.
        Label label = new Label(String.valueOf(checkPoint.getNumber()));
        label.setStyle("-fx-font-size: 9px; -fx-font-weight: bold;");

        // Here we add both the circle and the number to the space view.
        this.getChildren().add(circle);
        this.getChildren().add(label);
    }

}
