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
package com.example.heimdallseye.Controller.stream;

import java.util.ArrayList;

import com.example.heimdallseye.Controller.Command;
import com.example.heimdallseye.Controller.camera.HeimdallsEyeActiveStreamTask;
import com.example.heimdallseye.Model.Frame;
import com.example.heimdallseye.Model.entities.IpCameraEntity;
import com.example.heimdallseye.View.factory.ViewClient;
import com.example.heimdallseye.View.factory.dialogs.FailedMessageDialog;
import com.example.heimdallseye.View.factory.widgets.AlertInformationWidget;
import com.example.heimdallseye.View.factory.widgets.VideoStreamThumbnail;
import com.example.heimdallseye.View.factory.windows.VideoStreamWindow;
import javafx.application.Platform;
import javafx.geometry.Dimension2D;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 *
 * @author Maurice Amon
 */
public class StartLiveStreamCommand extends Command {

    private final ArrayList<VideoStreamThumbnail> VIDEO_LIST = new ArrayList<>();

    private AlertInformationWidget alert;

    @Override
    public void executeCommand() {
        alert = new AlertInformationWidget(Alert.AlertType.INFORMATION);
        alert.setAlertContent("Bitte haben Sie einen Moment Geduld.", "Initiiere den Livestream ...", new ImageView("/ajax-loader.gif"));
        Thread thread = new Thread(() -> {
            for (IpCameraEntity camera : IpCameraEntity.IP_CAMERA_LIST) {
                if (camera.isAvaiable()) {
                    Frame frame = new Frame();
                    HeimdallsEyeActiveStreamTask stream = new HeimdallsEyeActiveStreamTask();
                    stream.frame = frame;
                    stream.setIpCamera(camera);
                    VIDEO_LIST.add(new VideoStreamThumbnail(new Dimension2D(150, 100), stream));
                    stream.run();
                }
            }
            Platform.runLater(() -> {
                ((Stage) alert.getDialogPane().getScene().getWindow()).close();
                if (VIDEO_LIST.isEmpty()) {
                    showErrorMessage();
                } else {
                    VideoStreamWindow window = (VideoStreamWindow) new ViewClient().createViewComponent("VideoStreamWindow", VIDEO_LIST);
                    window.prepareView();
                    window.initComponents();
                    window.showView();
                }
            });
        });
        thread.start();
    }

    private void showErrorMessage() {
        FailedMessageDialog error = (FailedMessageDialog) ViewClient.getViewClientInstance().createViewComponent("FailedMessageDialog", "Sie haben noch keine ONVIF-IP-Kamera konfiguriert"
                + " oder keine Ihrer konfigurierten Geräte ist online!");
        error.prepareView();
        error.initComponents();
        error.showView();
    }

}
