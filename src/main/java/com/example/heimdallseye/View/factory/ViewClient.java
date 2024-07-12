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
package com.example.heimdallseye.View.factory;


import com.example.heimdallseye.Model.entities.IpCameraEntity;
import com.example.heimdallseye.View.View;
import com.example.heimdallseye.View.factory.dialogs.ErrorMessageDialog;
import com.example.heimdallseye.View.factory.dialogs.FailedMessageDialog;
import com.example.heimdallseye.View.factory.dialogs.InformationDialog;
import com.example.heimdallseye.View.factory.dialogs.SuccessMessageDialog;
import com.example.heimdallseye.View.factory.dialogs.config.AddDeviceDialog;
import com.example.heimdallseye.View.factory.dialogs.config.DeleteDeviceDialog;
import com.example.heimdallseye.View.factory.dialogs.config.EditDeviceDialog;
import com.example.heimdallseye.View.factory.widgets.VideoStreamThumbnail;
import com.example.heimdallseye.View.factory.windows.MainWindow;
import com.example.heimdallseye.View.factory.windows.VideoStreamWindow;

import java.util.ArrayList;

/**
 *
 * @author Maurice Amon
 */
public class ViewClient {

    private final GuiFactory HEIMDALL_FACTORY = new HeimdallStyleFactory();

    private View view;

    private static ViewClient viewClient = null;

    public static ViewClient getViewClientInstance() {
        if (viewClient == null) {
            viewClient = new ViewClient();
        }
        return viewClient;
    }

    public View createViewComponent(String view, Object data) {
        switch (view) {
            case "MainWindow":
                this.view = MainWindow.getInstance(HEIMDALL_FACTORY);
                break;
            case "AddDeviceDialog":
                this.view = new AddDeviceDialog(HEIMDALL_FACTORY);
                break;
            case "EditDeviceDialog":
                this.view = new EditDeviceDialog(HEIMDALL_FACTORY, (IpCameraEntity)data);
                break;
            case "DeleteDeviceDialog":
                this.view = new DeleteDeviceDialog(HEIMDALL_FACTORY);
                break;
            case "InformationDialog":
                this.view = new InformationDialog(HEIMDALL_FACTORY);
                break;
            case "ErrorMessageDialog":
                this.view = new ErrorMessageDialog(HEIMDALL_FACTORY, (Exception) data);
                break;
            case "FailedMessageDialog":
                this.view = new FailedMessageDialog(HEIMDALL_FACTORY, (String)data);
                break;
            case "SuccessMessageDialog":
                this.view = new SuccessMessageDialog(HEIMDALL_FACTORY);
                break;
            case "VideoStreamWindow":
                this.view = VideoStreamWindow.getInstance(HEIMDALL_FACTORY, (ArrayList<VideoStreamThumbnail>) data);
                break;
        }
        return this.view;
    }

}
