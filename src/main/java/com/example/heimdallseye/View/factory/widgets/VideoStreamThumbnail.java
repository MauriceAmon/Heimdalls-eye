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
package com.example.heimdallseye.View.factory.widgets;

import java.awt.image.BufferedImage;

import com.example.heimdallseye.Controller.camera.HeimdallsEyeActiveStreamTask;
import com.example.heimdallseye.heimdallseye.observer.Observer;
import javafx.geometry.Dimension2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 *
 * @author Maurice Amon
 */
public class VideoStreamThumbnail extends VBox implements VideoWidget, Observer {

    public ImageView view = null;

    public Dimension2D videoSize;

    private Boolean isActive = false;

    private final HeimdallsEyeActiveStreamTask stream;

    public VideoStreamThumbnail(Dimension2D dimension, HeimdallsEyeActiveStreamTask stream) {
        this.stream = stream;
        this.stream.addListener(this);
        videoSize = dimension;
        view = new ImageView("/View/images/cute.png");
        super.getChildren().addAll(view);
        super.setId("videotest");
    }

    public HeimdallsEyeActiveStreamTask getStream() {
        return this.stream;
    }

    @Override
    public void setImage(BufferedImage image) {

    }

    public void setBoxSize(Dimension2D dimension2D) {
        videoSize = dimension2D;
        view.setFitHeight(videoSize.getHeight());
        view.setFitWidth(videoSize.getWidth());
    }

    public void setActivity(Boolean isActive) {
        this.isActive = isActive;
        setActiveBorder();
    }

    public Boolean isActive() {
        return isActive;
    }

    private void setActiveBorder() {
        if (isActive) {
            super.setId("videotest-active");
        } else {
            super.setId("videotest");
        }
    }

    @Override
    public void update(Object object) {
        view.setImage(((ImageView)object).getImage());
        view.setFitHeight(videoSize.getHeight());
        view.setFitWidth(videoSize.getWidth());
    }
}
