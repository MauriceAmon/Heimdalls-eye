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

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.heimdallseye.Controller.Command;
import com.example.heimdallseye.Model.Frame;
import com.example.heimdallseye.View.factory.widgets.RectangleMarker;
import com.example.heimdallseye.View.factory.widgets.VideoStreamThumbnail;
import com.example.heimdallseye.View.factory.windows.VideoStreamWindow;
import com.example.heimdallseye.heimdallseye.observer.Observer;
import javafx.scene.image.Image;

/**
 * THIS CLASS IS A PART OF THE OBJECT-TRACKING-MODULE
 *
 * THE CLASS IS RESPONSIBLE FOR THE OBJECT-TRACKING AND USES A COMMON PATTERN IN
 * NETWORK-PROGRAMMING FOR CALCULATING THE ACTUAL OBJECT-POSITION.
 *
 * @author Maurice Amon
 * @version 1.1
 */
public class ObjectTrackerCommand extends Command implements Observer {

    private final int PAN_LEFT_ACTION = -2;

    private final int PAN_RIGHT_ACTION = 2;

    private final int TILT_DOWN_ACTION = -1;

    private final int TILT_UP_ACTION = 1;

    // The template for the next comparison ...
    private int[] templateFrame = null;
    // Collect the frames for the calculations ...
    private final ArrayList<Image> FRAMES = new ArrayList();

    private Boolean isFirstCalculation = true;

    private Frame frame = null;

    public ObjectTrackerCommand() {
        Integer active = 0;
        VideoStreamWindow window = VideoStreamWindow.getInstance(null, null);
        for (VideoStreamThumbnail thumbnail : window.getThumbnailElements()) {
            if (thumbnail.isActive()) {
                active = window.getThumbnailElements().indexOf(thumbnail);
            }
        }
        this.frame = window.getThumbnailElements().get(active).getStream().frame;
        this.frame.addListener(this);
    }

    @Override
    public void executeCommand() {

    }

    public void stopObjectTracking() {
        this.frame.removeListener(this);
    }

    /** This Callback-Method receive the results from the Thread an execute an action, if necessary ..
     *  The Method initiates the actions with a (virtually) pressed key ..
     *
     *  @param templateFrame  An int-array which contains the rgba-values of each pixel ..
     *  @param ptzAction      Integer (-2 = pan left, -1 = tilt down, 0 = Do nothing, 1 = Tilt up, 2 = pan right)
     *  @param x              Integer that represents the x-position of the object ..
     *  @param y              Integer that represents the y-position of the object ...
     *  @param factor         Double-value for transforming the coordinates (relation of frame-size an window-size)
     */
    public void receiveCalculationResult(int[] templateFrame, Integer ptzAction, Integer x, Integer y, Double factor) {
        if (templateFrame != null) {
            this.templateFrame = templateFrame;
        }
        try {
            Robot robot = new Robot();
            VideoStreamWindow window = VideoStreamWindow.getInstance(null, null);
            switch (ptzAction) {
                case PAN_LEFT_ACTION:
                    robot.keyPress(KeyEvent.VK_A);
                    x = ((Double) RectangleMarker.getInstance().getTranslateX()).intValue() + (((Double) window.getStage().getWidth()).intValue() / 5);
                    break;
                case PAN_RIGHT_ACTION:
                    robot.keyPress(KeyEvent.VK_D);
                    x = ((Double) RectangleMarker.getInstance().getTranslateX()).intValue() - (((Double) window.getStage().getWidth()).intValue() / 5);
                    break;
                case TILT_DOWN_ACTION:
                    robot.keyPress(KeyEvent.VK_S);
                    y = ((Double) RectangleMarker.getInstance().getTranslateY()).intValue() - (((Double) window.getStage().getHeight()).intValue() / 3);
                    break;
                case TILT_UP_ACTION:
                    robot.keyPress(KeyEvent.VK_W);
                    y = ((Double) RectangleMarker.getInstance().getTranslateY()).intValue() + (((Double) window.getStage().getHeight()).intValue() / 3);
                    break;
            }
            if (x != 0 && y != 0) {
                RectangleMarker.getInstance().setTranslateX(x);
                RectangleMarker.getInstance().setTranslateY(y);
            }
        } catch (AWTException ex) {
            Logger.getLogger(ObjectTrackerCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /** This method collects the received frames and inform the observers ..
     *
     *  @param object Image
     */
    @Override
    public void update(Object object) {
        Image image = (Image) object;
        if (isFirstCalculation) {
            FRAMES.add(image);
            if (FRAMES.size() == 3) {
                ObjectTrackingCalculator calculator = new ObjectTrackingCalculator(this, null, FRAMES.get(FRAMES.size() - 3), FRAMES.get(FRAMES.size() - 1), true);
                Thread thread = new Thread(calculator);
                thread.start();
                isFirstCalculation = false;
                FRAMES.clear();
                image = null;
                templateFrame = null;
            }
        } else if (isFirstCalculation == false && templateFrame != null) {
            FRAMES.add(image);
            if (FRAMES.size() == 2) {
                Thread thread = new Thread(new ObjectTrackingCalculator(this, templateFrame, FRAMES.get(0), FRAMES.get(1), false));
                thread.start();
                FRAMES.clear();
                image = null;
                templateFrame = null;
            }
        }
    }

}
