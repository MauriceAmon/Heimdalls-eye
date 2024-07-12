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
import com.example.heimdallseye.View.factory.ViewClient;
import com.example.heimdallseye.View.factory.widgets.RectangleMarker;
import com.example.heimdallseye.View.factory.windows.VideoStreamWindow;

/**
 *
 * @author Maurice Amon
 */
public class HeimdallsEyeFullscreenCommand extends Command {

    @Override
    public void executeCommand() {
        ViewClient client = new ViewClient();
        VideoStreamWindow window = (VideoStreamWindow) client.createViewComponent("VideoStreamWindow", null);
        Double width = window.VIDEO_WINDOW.getScene().getWidth();
        Double height = window.VIDEO_WINDOW.getScene().getHeight();
        if (window.getStage().isFullScreen() != true) {
            window.getStage().setFullScreen(true);
            window.addVideoMenu();
            positioningRectangleMarker(window.VIDEO_WINDOW.getScene().getWidth(), window.VIDEO_WINDOW.getScene().getHeight(), width, height);
        } else {
            window.getStage().setFullScreen(false);
            window.addVideoMenu();
            positioningRectangleMarker(window.VIDEO_WINDOW.getScene().getWidth(), window.VIDEO_WINDOW.getScene().getHeight(), width, height);
        }
    }

    private void positioningRectangleMarker(Double afterWidth, Double afterHeight, Double beforeWidth, Double beforeHeight) {
        RectangleMarker.getInstance().setTranslateX(RectangleMarker.getInstance().getTranslateX() * (afterWidth / beforeWidth));
        RectangleMarker.getInstance().setTranslateY(RectangleMarker.getInstance().getTranslateY() * (afterHeight / beforeHeight));
        RectangleMarker.getInstance().setWidth(RectangleMarker.getInstance().getWidth() * (afterWidth / beforeWidth));
        RectangleMarker.getInstance().setHeight(RectangleMarker.getInstance().getHeight() * (afterHeight / beforeHeight));
    }
}
