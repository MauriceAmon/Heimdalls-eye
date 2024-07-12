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
import java.util.Timer;

import com.example.heimdallseye.Controller.camera.HeimdallsEyeActiveStreamTask;
import com.example.heimdallseye.Model.entities.IpCameraEntity;
import com.example.heimdallseye.heimdallseye.observer.Observer;
import javafx.geometry.Dimension2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 *
 * @author Maurice Amon
 */
public class HeimdallsEyeStreamWidget extends VBox implements VideoWidget, Observer {

    public ImageView view = null;

    public Dimension2D videoSize;

    private Timer timer = new Timer();

    private HeimdallsEyeActiveStreamTask activeStream = null;

    private static HeimdallsEyeStreamWidget widget = null;

    private IpCameraEntity camera;
    
    private Integer counter = 0;

    public HeimdallsEyeStreamWidget() {
        view = new ImageView("/ajax-loader.gif");
        view.preserveRatioProperty().set(true);
        super.getChildren().addAll(view);
    }

    public static HeimdallsEyeStreamWidget getInstance() {
        if (widget == null) {
            widget = new HeimdallsEyeStreamWidget();
        }
        return widget;
    }

    public void setActualStream(HeimdallsEyeActiveStreamTask activeStream) {
        if (this.activeStream != null) {
            this.activeStream.removeListener(this);
        }
        this.activeStream = activeStream;
        this.activeStream.addListener(this);
    }

    public void setIpCamera(IpCameraEntity camera) {
        this.camera = camera;
    }

    public IpCameraEntity getIpCamera() {
        return this.camera;
    }

    public void setStreamURI(String uri) {
        activeStream.setClosingStream();
    }

    public void closeVideoStream() {
        activeStream.setClosingStream();
    }

    @Override
    public void setImage(BufferedImage image) {

    }

    @Override
    public void update(Object object) {
        ImageView frame = (ImageView) object;
        view.setImage(frame.getImage());
    }

}
