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

import java.nio.IntBuffer;
import java.util.ArrayList;

import com.example.heimdallseye.View.factory.widgets.RectangleMarker;
import com.example.heimdallseye.View.factory.windows.VideoStreamWindow;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;

/**
 * THIS CLASS IS A PART OF THE OBJECT-TRACKING-MODULE
 *
 * THE THREAD IS RESPONSIBLE FOR CALCULATING THE ACTUAL POSITION OF THE OBJECT
 * AND INVOKE A CALLBACK-METHOD TO INFORM THE CONTROLLER ABOUT THE STATE.
 *
 * @author Maurice Amon
 * @version 1.1
 */
public class ObjectTrackingCalculator implements Runnable {

    // The injected controller ...
    private final ObjectTrackerCommand CALLBACK_CONTROLLER;

    private final Integer DISTANCE_TO_RIM = 120;
    // the template frame 
    private int[] templateFrame;

    private Image firstFrame;

    private Image currentFrame;

    private final ArrayList<Long[]> SSD_CONTAINER = new ArrayList();

    private final Integer MARKER_POSITION_X;

    private final Integer MARKER_POSITION_Y;

    private final Integer MARKER_WIDTH;

    private final Integer MARKER_HEIGHT;

    private final Boolean isFirstCalculation;

    private final Double POSITION_FACTOR;

    /**
     * This class execute the SSD-Calculations and invoke a callback method of
     * the controller to give the results back ...
     *
     * @param callbackController The controller that offer a callback method ...
     * @param templateFrame An (pixel-)Array that represents the observed object
     * ..
     * @param oldFrame The first frame for the comparison ..
     * @param newFrame The actual frame that is received from the
     * RTSP-Stream ..
     * @param isFirstCalculation A boolean-value that represents wheter this is
     * the first calculation or not ...
     */
    public ObjectTrackingCalculator(ObjectTrackerCommand callbackController, int[] templateFrame, Image oldFrame, Image newFrame, Boolean isFirstCalculation) {
        CALLBACK_CONTROLLER = callbackController;
        this.firstFrame = oldFrame;
        this.currentFrame = newFrame;
        this.templateFrame = templateFrame;
        POSITION_FACTOR = (newFrame.getWidth() / VideoStreamWindow.getInstance(null, null).getStage().getWidth());
        MARKER_POSITION_X = ((Double) (RectangleMarker.getInstance().getTranslateX() * POSITION_FACTOR)).intValue();
        MARKER_POSITION_Y = ((Double) (RectangleMarker.getInstance().getTranslateY() * POSITION_FACTOR)).intValue();
        MARKER_WIDTH = ((Double) (RectangleMarker.getInstance().getWidth() * POSITION_FACTOR)).intValue();
        MARKER_HEIGHT = ((Double) (RectangleMarker.getInstance().getHeight() * POSITION_FACTOR)).intValue();
        this.isFirstCalculation = isFirstCalculation;

    }

    @Override
    public void run() {
        if (isFirstCalculation) {
            templateFrame = new int[MARKER_HEIGHT * MARKER_WIDTH];
            CALLBACK_CONTROLLER.receiveCalculationResult(detectSelectedObject(templateFrame, 2), 0, 0, 0, POSITION_FACTOR);
        } else {
            findCurrentObjectPosition();
        }
        /**
         * It's important to assign a nullable to these properties after the
         * calculations to avoid a memory OVER-HEAP - the garbage collector 
         * will destroy the objects automatically.
         */
        firstFrame = null;
        currentFrame = null;
        templateFrame = null;
    }

    private int[] detectSelectedObject(int[] templateFrame, Integer difference) {
        // Get the pixel-reader of the first frame ...
        PixelReader reader = firstFrame.getPixelReader();
        // Get the pixel-reader of the second frame ...
        PixelReader reader2 = currentFrame.getPixelReader();
        WritablePixelFormat<IntBuffer> format = WritablePixelFormat.getIntArgbInstance();
        int[] pixels = new int[((int) firstFrame.getWidth() * (int) firstFrame.getHeight())]; //  pixel range
        int[] pixels2 = new int[((int) firstFrame.getWidth() * (int) firstFrame.getHeight())];
        reader.getPixels(0, 0, (int) firstFrame.getWidth(), (int) firstFrame.getHeight(), format, pixels, 0, (int) firstFrame.getWidth()); // 
        reader2.getPixels(0, 0, (int) firstFrame.getWidth(), (int) firstFrame.getHeight(), format, pixels2, 0, (int) firstFrame.getWidth()); // get all pixels by argb format
        for (int x = MARKER_POSITION_X; x < MARKER_POSITION_X + MARKER_WIDTH; x++) {
            for (int y = MARKER_POSITION_Y; y < MARKER_POSITION_Y + MARKER_HEIGHT; y++) {
                /**
                 * Assign the RGB-Values of each pixel to new vars to make the
                 * comparison easier. The datatype of each RGB-Value is long
                 * because in further releases the software should also support
                 * UHD-IP-Cameras which means that a higher value-domain is
                 * requierd.
                 */
                long red = ((pixels[x + (y * 1920)] >> 16) & 0xff);
                long green = ((pixels[x + (y * 1920)] >> 8) & 0xff);
                long blue = ((pixels[x + (y * 1920)]) & 0xff);
                long red2 = ((pixels2[x + (y * 1920)] >> 16) & 0xff);
                long green2 = ((pixels2[x + (y * 1920)] >> 8) & 0xff);
                long blue2 = ((pixels2[x + (y * 1920)]) & 0xff);
                if ((red <= red2 - difference || red >= red2 + difference) || (blue <= blue2 - difference || blue >= blue + difference) || (green <= green2 - difference || green >= green2 + difference)) {
                    templateFrame[(x - MARKER_POSITION_X) + ((y - MARKER_POSITION_Y) * MARKER_WIDTH)] = pixels[x + (y * 1920)];
                    //templateFrame[(x - markerPositionX.intValue()) + ((y - markerPositionY.intValue()) * markerWidth.intValue())] = pixels2[x + (y * 1920)];
                    //System.out.println("0");
                } else {
                    templateFrame[(x - MARKER_POSITION_X) + ((y - MARKER_POSITION_Y) * MARKER_WIDTH)] = 0;
                }
            }
        }
        return templateFrame;
    }

    private void findCurrentObjectPosition() {
        for (int x = MARKER_POSITION_X - 80; x < MARKER_POSITION_X + 80; x += 20) {
            for (int y = MARKER_POSITION_Y - 80; y < MARKER_POSITION_Y + 80; y += 20) {
                calculatePixelBlockSSD(x, y);
            }
        }
        Long[] smallestSSD = {((Integer) 0).longValue()};
        for (Long[] pixelBlock : SSD_CONTAINER) {
            if (pixelBlock[0] < smallestSSD[0] || smallestSSD[0] == 0) {
                smallestSSD = pixelBlock;
            }
        }
        int[] template = detectSelectedObject(new int[MARKER_HEIGHT * MARKER_WIDTH], 2);
        compareTemplateFrames(templateFrame, template);
        if (smallestSSD[1] + MARKER_WIDTH > 1920 - DISTANCE_TO_RIM) {
            CALLBACK_CONTROLLER.receiveCalculationResult(templateFrame, 2, ((Double) (smallestSSD[1].doubleValue() / POSITION_FACTOR)).intValue(),
                    ((Double) (smallestSSD[2].doubleValue() / POSITION_FACTOR)).intValue(), POSITION_FACTOR);
        } else if (smallestSSD[1] < DISTANCE_TO_RIM) {
            CALLBACK_CONTROLLER.receiveCalculationResult(templateFrame, -2, ((Double) (smallestSSD[1].doubleValue() / POSITION_FACTOR)).intValue(),
                    ((Double) (smallestSSD[2].doubleValue() / POSITION_FACTOR)).intValue(), POSITION_FACTOR);
        } else if (smallestSSD[2] + MARKER_HEIGHT > 1080 - DISTANCE_TO_RIM) {
            CALLBACK_CONTROLLER.receiveCalculationResult(templateFrame, -1, ((Double) (smallestSSD[1].doubleValue() / POSITION_FACTOR)).intValue(),
                    ((Double) (smallestSSD[2].doubleValue() / POSITION_FACTOR)).intValue(), POSITION_FACTOR);
        } else if (smallestSSD[2] < DISTANCE_TO_RIM) {
            CALLBACK_CONTROLLER.receiveCalculationResult(templateFrame, 1, ((Double) (smallestSSD[1].doubleValue() / POSITION_FACTOR)).intValue(),
                    ((Double) (smallestSSD[2].doubleValue() / POSITION_FACTOR)).intValue(), POSITION_FACTOR);
        } else {
            CALLBACK_CONTROLLER.receiveCalculationResult(templateFrame, 0, ((Double) (smallestSSD[1].doubleValue() / POSITION_FACTOR)).intValue(),
                    ((Double) (smallestSSD[2].doubleValue() / POSITION_FACTOR)).intValue(), POSITION_FACTOR);
        }
    }

    private void calculatePixelBlockSSD(Integer x, Integer y) {
        long ssdBlock = 0;
        PixelReader reader = currentFrame.getPixelReader();
        WritablePixelFormat<IntBuffer> format = WritablePixelFormat.getIntArgbInstance();
        int[] pixels = new int[((int) currentFrame.getWidth() * (int) currentFrame.getHeight())]; //  pixel range
        reader.getPixels(0, 0, (int) currentFrame.getWidth(), (int) currentFrame.getHeight(), format, pixels, 0, (int) currentFrame.getWidth()); // 
        for (int positionX = 0; positionX < MARKER_WIDTH; positionX++) {
            for (int positionY = 0; positionY < MARKER_HEIGHT; positionY++) {
                /**
                 * Assign the RGB-Values of each pixel to new vars to make the
                 * comparison easier. The datatype of each RGB-Value is long
                 * because in further releases the software should also support
                 * UHD-IP-Cameras (which means that a higher value-domain is
                 * requierd).
                 */
                long red = ((pixels[(x + positionX) + (y + positionY) * 1920] >> 16) & 0xff);
                long green = ((pixels[(x + positionX) + (y + positionY) * 1920] >> 8) & 0xff);
                long blue = (pixels[(x + positionX) + ((y + positionY) * 1920)] & 0xff);
                long red2 = ((templateFrame[(positionX) + ((positionY) * MARKER_WIDTH)] >> 16) & 0xff);
                long green2 = ((templateFrame[(positionX) + ((positionY) * MARKER_WIDTH)] >> 8) & 0xff);
                long blue2 = ((templateFrame[(positionX) + ((positionY) * MARKER_WIDTH)]) & 0xff);
                // Calculate the SSD-Value for each pixel of the block ....

                if (templateFrame[(positionX) + ((positionY) * MARKER_WIDTH)] != 0) {
                    // Calculate the SSD of each pixel ....
                    ssdBlock += ((red - red2) * (red - red2)) + ((green - green2) * (green - green2)) + ((blue - blue2) * (blue - blue2));
                }
            }
        }
        Long[] ssdData = {ssdBlock, x.longValue(), y.longValue()};
        SSD_CONTAINER.add(ssdData);

    }

    private void compareTemplateFrames(int[] oldTemplate, int[] newTemplate) {
        long ssdBlock = 0;
        Integer nullableCounter = 0;
        for (int x = 0; x < MARKER_WIDTH; x++) {
            for (int y = 0; y < MARKER_HEIGHT; y++) {
                /**
                 * Assign the RGB-Values of each pixel to new vars to make the
                 * comparison easier. The datatype of each RGB-Value is long
                 * because in further releases the software should also support
                 * UHD-IP-Cameras which means that a higher value-domain is
                 * requierd.
                 */
                long red = ((oldTemplate[x + (y * MARKER_WIDTH)] >> 16) & 0xff);
                long green = ((oldTemplate[x + (y * MARKER_WIDTH)] >> 8) & 0xff);
                long blue = ((oldTemplate[x + (y * MARKER_WIDTH)]) & 0xff);
                long red2 = ((newTemplate[x + (y * MARKER_WIDTH)] >> 16) & 0xff);
                long green2 = ((newTemplate[x + (y * MARKER_WIDTH)] >> 8) & 0xff);
                long blue2 = ((newTemplate[x + (y * MARKER_WIDTH)]) & 0xff);
                if (newTemplate[x + (y * MARKER_WIDTH)] != 0) {
                    ssdBlock += ((red - red2) * (red - red2)) + ((green - green2) * (green - green2)) + ((blue - blue2) * (blue - blue2));
                } else {
                    nullableCounter++;
                }
            }
        }
        /**
         * 2000 is the limitation of the average squared difference of two pixel
         * values ... the other condition makes sure that there are more of 80%
         * of the pixels not NULL. If both conditions are true the template will
         * be replaced.
         */
        if (2000 > (ssdBlock / (MARKER_HEIGHT * MARKER_WIDTH)) && ((MARKER_WIDTH * MARKER_HEIGHT) / nullableCounter) > 4) {
            templateFrame = newTemplate;
        }
    }
}
