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
import com.example.heimdallseye.View.factory.ViewClient;
import com.example.heimdallseye.View.factory.widgets.RectangleMarker;
import com.example.heimdallseye.View.factory.windows.VideoStreamWindow;
import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * THIS CLASS IS A PART OF THE OBJECT-TRACKING-MODULE
 *
 * THIS CONTROLLER SERVES AS THE PARENT-NODE-CONTROLLER OF THE TWO DIFFERENT
 * TASKS OBJECT-TRACKING AND FRAME-SEQUENCE SAVING - THE CONTROLLER IS ABLE TO
 * EXECUTE AND FINISH THE TASKS - TAKE A LOOK AT THE DOCUMENTATION FOR MORE
 * INFORMATIONS.
 *
 * @author Maurice Amon
 * @version 1.1
 */
public class ObjectTrackingModuleCommand extends Command implements EventHandler<KeyEvent> {

    @Override
    public void executeCommand() {
        ViewClient client = new ViewClient();
        VideoStreamWindow window = (VideoStreamWindow) client.createViewComponent("VideoStreamWindow", null);
        window.getStage().getScene().addEventHandler(KeyEvent.KEY_PRESSED, this);
        if (window.rectangleMarkerController != null) {
            window.getStage().getScene().removeEventHandler(MouseEvent.MOUSE_PRESSED, window.rectangleMarkerController);
            window.getStage().getScene().removeEventHandler(MouseEvent.MOUSE_DRAGGED, window.rectangleMarkerController);
        }
        RectangleMarker.getInstance().setPositioned(Boolean.TRUE);
        CONTROLLER_LIST.stream().forEach((command) -> {
            command.executeCommand();
        });
    }

    /* The Controller is listening to the scene of the video-player and terminate 
     * the execution of the controllers, if the user press the KeyCode.BACK_SPACE Key ...  
     * 
     * @param event KeyEvent
     */
    @Override
    public void handle(KeyEvent event) {
        switch (event.getCode()) {
            case BACK_SPACE:
                ViewClient client = new ViewClient();
                VideoStreamWindow window = (VideoStreamWindow) client.createViewComponent("VideoStreamWindow", null);
                if (window.rectangleMarkerController != null) {
                    Tooltip.uninstall(window.widget, new Tooltip("Objekt-Verfolgungsmodus ist Aktiv"));
                    window.getStage().getScene().removeEventHandler(KeyEvent.KEY_PRESSED, this);
                    window.root.getChildren().remove(RectangleMarker.getInstance());
                }
                CONTROLLER_LIST.stream().forEach((command) -> {
                    if (command instanceof SaveSequencesCommand) {
                        ((SaveSequencesCommand) command).stopRecording();
                    } else if (command instanceof ObjectTrackerCommand) {
                        ((ObjectTrackerCommand) command).stopObjectTracking();
                    }
                });
                break;
        }
    }

}
