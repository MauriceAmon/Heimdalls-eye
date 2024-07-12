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
import com.example.heimdallseye.View.factory.widgets.VideoStreamThumbnail;
import com.example.heimdallseye.View.factory.windows.VideoStreamWindow;

/**
 *
 * @author Maurice Amon
 */
public class IpCameraActiveCommand extends Command {

    private Integer selection = 0;

    private Integer actual;

    private VideoStreamThumbnail currentThumbnail;

    public IpCameraActiveCommand(Integer selection) {
        this.selection = selection;
    }

    @Override
    public void executeCommand() {
        VideoStreamWindow window = VideoStreamWindow.getInstance(null, null);
        for (VideoStreamThumbnail thumbnail : window.getThumbnailElements()) {
            if (thumbnail.isActive()) {
                actual = (window.getThumbnailElements().indexOf(thumbnail) + selection);
                currentThumbnail = thumbnail;
            }
        }
        System.out.println(actual);
        if (actual != -1 && actual < window.getThumbnailElements().size()) {
            currentThumbnail.setActivity(Boolean.FALSE);
            currentThumbnail.getStream().isActive = Boolean.FALSE;
            window.getThumbnailElements().get(actual).getStream().isActive = Boolean.TRUE;
            window.getThumbnailElements().get(actual).setActivity(Boolean.TRUE);
            window.widget.setActualStream(window.getThumbnailElements().get(actual).getStream());
            window.widget.setIpCamera(window.getThumbnailElements().get(actual).getStream().getIpCamera());
        }
    }

}