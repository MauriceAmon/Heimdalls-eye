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

import java.io.IOException;

import com.example.heimdallseye.Controller.Command;
import com.example.heimdallseye.Model.entities.IpCameraEntity;
import com.example.heimdallseye.Model.entities.UnitOfWorkDatabaseMapper;
import com.example.heimdallseye.View.factory.ViewClient;
import com.example.heimdallseye.View.factory.dialogs.ErrorMessageDialog;
import com.example.heimdallseye.View.factory.dialogs.config.AddDeviceDialog;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

/**
 *
 * @author Maurice Amon
 */
public class DeviceConfigWindowCommand extends Command implements EventHandler<ActionEvent> {

    private IpCameraEntity ipCamera;

    private UnitOfWorkDatabaseMapper uowMapper;

    private AddDeviceDialog dialog;

    private ViewClient client = ViewClient.getViewClientInstance();

    @Override
    public void executeCommand() {
        dialog = (AddDeviceDialog) client.createViewComponent("AddDeviceDialog", null);
        dialog.prepareView();
        dialog.initComponents();
        dialog.showView();
        dialog.initializeActions(this);
        uowMapper = UnitOfWorkDatabaseMapper.getUnitOfWorkMapperInstance();
    }

    @Override
    public void handle(ActionEvent event) {
        switch (getItemId(event)) {
            case "addIpCamera":
                persistIpCameraConfiguration();
                ((Stage) dialog.ADD_IP_CAMERA_DIALOG.getDialogPane().getScene().getWindow()).close();
                break;
            case "close":
                ((Stage) dialog.ADD_IP_CAMERA_DIALOG.getDialogPane().getScene().getWindow()).close();
                break;
        }
    }

    private String getItemId(ActionEvent event) {
        if (event.getSource() instanceof Button) {
            return ((Button) event.getSource()).getId();
        }
        if (event.getSource() instanceof MenuItem) {
            return ((MenuItem) event.getSource()).getId();
        }
        return null;
    }

    private void persistIpCameraConfiguration() {
        ipCamera = new IpCameraEntity(null, dialog.getIpAddressField().getText(), Integer.parseInt(dialog.getPortField().getText()),
                dialog.getUsernameField().getText(), dialog.getPasswordField().getText());
        if (ipCamera.isAvaiable()) {
            uowMapper.addObjectToInsertList(ipCamera);
            uowMapper.commit();
            actualizeIpCameraWidget();
        } else {
            ErrorMessageDialog error = (ErrorMessageDialog) client.createViewComponent("ErrorMessageDialog", new IOException());
            error.prepareView();
            error.initComponents();
            error.showView();
        }
    }

    private void actualizeIpCameraWidget() {
        IpCameraEntity.addIpCamera(ipCamera);
    }

}
