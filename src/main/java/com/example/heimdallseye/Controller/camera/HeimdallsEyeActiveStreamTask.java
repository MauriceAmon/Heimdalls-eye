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
package com.example.heimdallseye.Controller.camera;


import com.example.heimdallseye.Model.Frame;
import com.example.heimdallseye.Model.entities.IpCameraEntity;
import com.example.heimdallseye.heimdallseye.observer.Observable;
import com.example.heimdallseye.heimdallseye.observer.Observer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.runtime.x.LibXUtil;


/**
 *
 * @author Maurice Amon
 */
public class HeimdallsEyeActiveStreamTask extends Task implements Observable {

    private final List<Observer> OBSERVER_LIST = new ArrayList<>();

    private IpCameraEntity camera;

    private String cameraUri;

    private WritableImage image;

    private DirectMediaPlayerComponent mediaPlayerComponent;

    private WritableImage writableImage;

    private WritablePixelFormat<ByteBuffer> pixelFormat;

    private Integer counter = 0;

    private Boolean isClosing = false;

    public Boolean isActive = false;
    
    public Frame frame = null;

    public HeimdallsEyeActiveStreamTask() {
        System.setProperty("VLC_PLUGIN_PATH", "C:/Program Files/VideoLAN/VLC/plugins");
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files\\VideoLAN\\VLC");
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        LibXUtil.initialise();
        pixelFormat = PixelFormat.getByteBgraPreInstance();
        mediaPlayerComponent = new CanvasPlayerComponent();
        ((CanvasPlayerComponent) mediaPlayerComponent).setReference(this);
    }

    @Override
    public void addListener(Observer observer) {
        OBSERVER_LIST.add(observer);
    }

    @Override
    public void removeListener(Observer observer) {
        OBSERVER_LIST.remove(observer);
    }

    public void setIpCamera(IpCameraEntity camera) {
        this.camera = camera;
    }

    public IpCameraEntity getIpCamera() {
        return this.camera;
    }

    public void setClosingStream() {
        isClosing = true;
    }

    @Override
    public void run() {
        try {
            call();
        } catch (Exception ex) {
            Logger.getLogger(HeimdallsEyeActiveStreamTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected Object call() throws Exception {
        writableImage = new WritableImage(1920, 1080);
        cameraUri = camera.getStreamURI().substring(0, 7) + camera.getUsername() + ":" + camera.getPassword() + "@" + camera.getStreamURI().substring(7);
        mediaPlayerComponent.getMediaPlayer().prepareMedia(cameraUri);
        mediaPlayerComponent.getMediaPlayer().start();
        return null;
    }

    @Override
    public void notifyObservers() {
        OBSERVER_LIST.stream().forEach((Observer observer) -> {
            observer.update(new ImageView(image));
        });
        frame.addFrame(image);
    }

    private class CanvasPlayerComponent extends DirectMediaPlayerComponent {

        public Task thread;

        public CanvasPlayerComponent() {
            super((BufferFormatCallback) new CanvasBufferFormatCallback());
        }

        public void setReference(Task thread) {
            this.thread = thread;
        }

        PixelWriter pixelWriter = null;

        private PixelWriter getPW() {
            if (pixelWriter == null) {
                pixelWriter = writableImage.getPixelWriter();
            }
            return pixelWriter;
        }

        @Override
        public void display(DirectMediaPlayer mediaPlayer, Memory[] nativeBuffers, BufferFormat bufferFormat) {
            if (writableImage == null) {
                return;
            }
            Platform.runLater(() -> {
                if (isClosing == true) {
                    mediaPlayer.stop();
                    thread.cancel();
                } else {
                    Memory nativeBuffer = mediaPlayer.lock()[0];
                    try {
                        image = new WritableImage(1920, 1080);
                        PixelWriter writer = image.getPixelWriter();
                        ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
                        writer.setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
                        notifyObservers();
                        image = null;
                    } finally {
                        mediaPlayer.unlock();
                    }
                }
            });
        }
    }

    private class CanvasBufferFormatCallback implements BufferFormatCallback {

        @Override
        public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
            return new RV32BufferFormat(1920, 1080);
        }
    }
}
