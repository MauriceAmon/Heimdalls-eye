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
package com.example.heimdallseye.View.factory.dialogs.config;

import com.example.heimdallseye.View.View;
import com.example.heimdallseye.View.factory.GuiFactory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

/**
 *
 * @author Maurice Amon
 */
public class DeleteDeviceDialog extends View {

    public final Dialog<Pair<String, String>> DELETE_IP_CAMERA_DIALOG = new Dialog<>();

    private final GridPane DIALOG_LAYOUT = new GridPane();

    private final GuiFactory factory;

    private final Button CONFIRM_BUTTON = new Button("Bestätigen");

    private final Button CANCEL_BUTTON = new Button("Abbrechen");

    public DeleteDeviceDialog(GuiFactory factory) {
        this.factory = factory;
        ((Stage) DELETE_IP_CAMERA_DIALOG.getDialogPane().getScene().getWindow()).setOnCloseRequest((WindowEvent event1) -> {
            DELETE_IP_CAMERA_DIALOG.close();
            ((Stage) DELETE_IP_CAMERA_DIALOG.getDialogPane().getScene().getWindow()).close();
        });
    }

    public void initializeActions(EventHandler<ActionEvent> event) {
        CONFIRM_BUTTON.setId("deleteIpCamera");
        CONFIRM_BUTTON.setOnAction(event);
        CANCEL_BUTTON.setId("cancel");
        CANCEL_BUTTON.setOnAction(event);
    }

    @Override
    public void prepareView() {
        this.textField = factory.createTextField();

    }

    @Override
    public void initComponents() {
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(CONFIRM_BUTTON, CANCEL_BUTTON);
        DIALOG_LAYOUT.add(buttonBox, 0, 0);
        DELETE_IP_CAMERA_DIALOG.setTitle("Heimdall's Eye: Geräte-Konfiguration wirklich löschen?");
        DELETE_IP_CAMERA_DIALOG.setHeaderText("Warnmeldung: Sind Sie sich sicher?");
        DELETE_IP_CAMERA_DIALOG.setGraphic(new ImageView("/delete.png"));
        DELETE_IP_CAMERA_DIALOG.getDialogPane().setContent(DIALOG_LAYOUT);
        Stage stage = (Stage) DELETE_IP_CAMERA_DIALOG.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource("/delete.png").toString()));
    }

    @Override
    public void showView() {
        DELETE_IP_CAMERA_DIALOG.show();
    }

}
