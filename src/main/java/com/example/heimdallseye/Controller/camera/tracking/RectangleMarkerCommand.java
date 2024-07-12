/*
 * The MIT License
 *
 * Copyright 2016 Maurice Amon.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.example.heimdallseye.Controller.camera.tracking;

import com.example.heimdallseye.Controller.Command;
import com.example.heimdallseye.View.factory.widgets.RectangleMarker;
import com.example.heimdallseye.View.factory.windows.VideoStreamWindow;
import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

/** THIS CLASS IS A PART OF THE OBJECT-TRACKING MODULE
 *
 * @author Maurice Amon
 * @version 1.1
 */
public class RectangleMarkerCommand extends Command implements EventHandler<MouseEvent> {

    private RectangleMarker rectangleMarker = RectangleMarker.getInstance();

    private final Tooltip ACTIVE_HINT = new Tooltip("Objekt-Verfolgungsmodus ist Aktiv");

    private VideoStreamWindow window;

    private Double startX;

    private Double startY;

    @Override
    public void executeCommand() {
        window = VideoStreamWindow.getInstance(null, null);
        window.rectangleMarkerController = this;
        window.getStage().getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, window.rectangleMarkerController);
        window.getStage().getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, window.rectangleMarkerController);
        ACTIVE_HINT.setFont(new Font(16));
        Tooltip.install(
                window.widget,
                ACTIVE_HINT
        );

    }

    @Override
    public void handle(MouseEvent event) {
        if (!window.root.getChildren().contains(rectangleMarker)) {
            window.root.getChildren().add(rectangleMarker);
        }
        switch (event.getEventType().getName()) {
            case "MOUSE_PRESSED":
                if (window.root.getChildren().contains(rectangleMarker)) {
                    window.root.getChildren().remove(rectangleMarker);
                }
                rectangleMarker = RectangleMarker.getInstance();
                startX = event.getSceneX();
                startY = event.getSceneY();
                rectangleMarker.setTranslateX(startX);
                rectangleMarker.setTranslateY(startY);
                break;
            case "MOUSE_DRAGGED":
                rectangleMarker.setWidth(event.getSceneX() - startX);
                rectangleMarker.setHeight(event.getSceneY() - startY);
                break;
        }

    }

}
