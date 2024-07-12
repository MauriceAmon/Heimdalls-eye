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
package com.example.heimdallseye.Controller.menu;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.heimdallseye.Controller.Command;
import com.example.heimdallseye.Model.entities.IpCameraEntity;
import com.example.heimdallseye.View.factory.widgets.AlertInformationWidget;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 *
 * @author Maurice Amon
 */
public class HeimdallsEyeActualizeCommand extends Command {

    private AlertInformationWidget alert;

    @Override
    public void executeCommand() {
        alert = new AlertInformationWidget(Alert.AlertType.INFORMATION);
        alert.setAlertContent("Bitte haben Sie einen Moment Geduld.", "Die Liste wird aktualisiert.", new ImageView("/View/images/ajax-loader.gif"));
        Thread thread = new Thread(() -> {
            ObservableList<IpCameraEntity> list = FXCollections.observableArrayList(IpCameraEntity.IP_CAMERA_LIST);
            IpCameraEntity.IP_CAMERA_LIST.clear();
            try {
                Thread.sleep(150);
            } catch (InterruptedException ex) {
                Logger.getLogger(HeimdallsEyeActualizeCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
            list.stream().forEach((camera) -> {
                camera.isAvaiable();
                IpCameraEntity.addIpCamera(camera);
            });
            Platform.runLater(() -> {
                ((Stage) alert.getDialogPane().getScene().getWindow()).close();
            });
        });
        thread.start();
    }

}
