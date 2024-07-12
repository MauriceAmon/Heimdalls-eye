/*
 * The MIT License
 *
 * Copyright 2016 Maurice.
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
package com.example.heimdallseye.heimdallseye;

import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Dimension2D;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;

/**
 *
 * @author Maurice
 */
public class VideoTest extends VBox {

    public ImageView view = null;

    public Dimension2D videoSize;

    public VideoTest(Dimension2D dimension) {
        videoSize = dimension;
        view = new ImageView();
        super.getChildren().addAll(view);
        super.setId("videotest");
    }

    public void setImage(BufferedImage image) {
        WritableImage img = SwingFXUtils.toFXImage(image, null);
        view.setImage(img);
        view.setFitHeight(videoSize.getHeight());
        view.setFitWidth(videoSize.getWidth());
    }

    public void setBoxSize(Dimension2D dimension2D) {
        view.setFitHeight(videoSize.getHeight());
        view.setFitWidth(videoSize.getWidth());
    }
}
