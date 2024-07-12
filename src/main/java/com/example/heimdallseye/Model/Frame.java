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
package com.example.heimdallseye.Model;

import java.util.ArrayList;

import com.example.heimdallseye.heimdallseye.observer.Observable;
import com.example.heimdallseye.heimdallseye.observer.Observer;
import javafx.application.Platform;
import javafx.scene.image.Image;

/**
 * THIS CLASS IS A PART OF THE OBJECT-TRACKING-MODULE
 *
 * THE CLASS INFORM THE UI-WIDGETS AND THE OT-MODULE, WHEN A NEW FRAME IS
 * RECEIVED.
 *
 * @author Maurice Amon
 * @version 1.1
 */
public class Frame implements Observable {

    // This list collect the observers ...
    private final ArrayList<Observer> OBSERVER_LIST = new ArrayList<>();
    // This list collect the frames ...
    private final ArrayList<Image> FRAME_LIST = new ArrayList<>();

    /**
     * This method collects the received frames and inform the observers ..
     *
     * @param image An image-object that is received by the RTSP-Stream ..
     */
    public void addFrame(Image image) {
        if (FRAME_LIST.size() > 10) {
            FRAME_LIST.clear();
        }
        FRAME_LIST.add(image);
        notifyObservers();
    }

    /**
     * This method add an observer to the collection ..
     *
     * @param observer A registered Observer ..
     */
    @Override
    public void addListener(Observer observer) {
        OBSERVER_LIST.add(observer);
    }

    /**
     * This method remove an observer from the collection ...
     *
     * @param observer A registered Observer ..
     */
    @Override
    public void removeListener(Observer observer) {
        OBSERVER_LIST.remove(observer);
    }

    /**
     * This method notify the observers about the new frame ..
     */
    @Override
    public void notifyObservers() {
        OBSERVER_LIST.stream().forEach((Observer observer) -> {
            Platform.runLater(() -> {
                observer.update(FRAME_LIST.get(FRAME_LIST.size() - 1));
            });
        });
    }
}
