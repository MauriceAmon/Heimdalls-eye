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
package com.example.heimdallseye.Controller.camera;


import com.example.heimdallseye.Controller.Command;
import com.example.heimdallseye.Controller.ExecutorCommandService;
import com.example.heimdallseye.Controller.camera.tracking.ObjectTrackerCommand;
import com.example.heimdallseye.Controller.camera.tracking.ObjectTrackingModuleCommand;
import com.example.heimdallseye.Controller.camera.tracking.RectangleMarkerCommand;
import com.example.heimdallseye.Controller.camera.tracking.SaveSequencesCommand;
import com.example.heimdallseye.Model.entities.IpCameraEntity;
import com.example.heimdallseye.View.factory.windows.VideoStreamWindow;
import javafx.event.EventHandler;
import static javafx.scene.input.KeyCode.ENTER;
import javafx.scene.input.KeyEvent;
import org.onvif.ver10.schema.DeviceEntity;


/**
 * THIS CLASS SERVES AS A CLIENT FOR THE OBJECT-TRACKING-MODULE
 *
 * @author Maurice Amon
 *
 */
public class PtzCommandClientController implements EventHandler<KeyEvent> {

    private ExecutorCommandService executor;

    private Command command;

    private IpCameraEntity camera;

    private Boolean isSelectionActive = false;

    @Override
    public void handle(KeyEvent event) {
        VideoStreamWindow window = VideoStreamWindow.getInstance(null, null);
        camera = window.widget.getIpCamera();
        System.out.println("Event: " + event.getCode());
        switch (event.getCode()) {
            case LEFT:
                command = new IpCameraActiveCommand(-1);
                break;
            case RIGHT:
                command = new IpCameraActiveCommand(+1);
                break;
            case SPACE:
                command = new HeimdallsEyeFullscreenCommand();
                break;
            case A:
                command = new IpCameraPositioningCommand(camera, (float) -0.1, (float) 0);
                break;
            case D:
                command = new IpCameraPositioningCommand(camera, (float) 0.1, (float) 0);
                break;
            case W:
                command = new IpCameraPositioningCommand(camera, (float) 0, (float) 0.1);
                break;
            case S:
                command = new IpCameraPositioningCommand(camera, (float) 0, (float) -0.1);
                break;
            case SHIFT:
                command = new IpCameraZoomCommand(camera, 5);
                break;
            case CONTROL:
                command = new IpCameraZoomCommand(camera, -5);
                break;
            case ALT:
                command = new RectangleMarkerCommand();
                isSelectionActive = true;
                break;
            // A new case to initiate the object-tracking-module ...
            case ENTER:
                command = new ObjectTrackingModuleCommand();
                if (isSelectionActive) {
                    command.addCommand(new ObjectTrackerCommand());
                }
                break;
            case R:
                command = new ObjectTrackingModuleCommand();
                command.addCommand(new SaveSequencesCommand());
                break;
        }

        executor = new ExecutorCommandService(command);
        executor.runExecution();
    }

    private DeviceEntity getActiveDevice() {
        return new DeviceEntity();
    }

}
