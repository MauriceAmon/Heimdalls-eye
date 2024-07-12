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
package com.example.heimdallseye.View.factory.dialogs.config;

import com.example.heimdallseye.Model.entities.IpCameraEntity;
import com.example.heimdallseye.View.View;
import com.example.heimdallseye.View.factory.GuiFactory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

/**
 *
 * @author Maurice Amon
 */
public class EditDeviceDialog extends View {

    private final TabPane EDIT_DIALOG_TAB_PANE = new TabPane();

    private final Tab DEVICE_CONNECT_DATA_TAB = new Tab("Allgemein");

    private final Tab DEVICE_NETWORK_STREAM = new Tab("Netzwerk");

    private final GuiFactory factory;

    private final IpCameraEntity camera;

    public final Dialog<Pair<String, String>> EDIT_IP_CAMERA_DIALOG = new Dialog<>();

    public TextField uriField = new TextField("Beispiel: 192.168.1.44");

    public TextField portField = new TextField();

    public TextField userField = new TextField();

    private TextField streamUriField = new TextField();

    private final Label URI_LABEL = new Label("IP-Adresse: ");

    private final Label PORT_LABEL = new Label("Port: ");

    private final Label NAME_LABEL = new Label("Benutzername: ");

    private final Label PASS_LABEL = new Label("Passwort: ");

    private final Label INFO_NETWORK_TAB_LABEL = new Label("Ändern Sie die URI des Livestreams nur dann,\nwenn sich das Gerät nicht\nim Intranet befindet.");

    private final Label STREAM_URI = new Label("URI des Livestreams: ");

    public PasswordField passField = new PasswordField();

    private final GridPane DIALOG_LAYOUT = new GridPane();

    private final GridPane INFO_DIALOG_LAYOUT = new GridPane();

    private final Button CONFIRM_BUTTON = new Button("Bestätigen");

    private final Button CANCEL_BUTTON = new Button("Abbrechen");

    public EditDeviceDialog(GuiFactory factory, IpCameraEntity camera) {
        this.factory = factory;
        this.camera = camera;
        uriField = this.factory.createTextField();
        portField = this.factory.createTextField();
        userField = this.factory.createTextField();
        passField = this.factory.createPasswordField();
    }

    public void initializeActions(EventHandler<ActionEvent> event) {
        CONFIRM_BUTTON.setId("editIpCamera");
        CONFIRM_BUTTON.setOnAction(event);
        CANCEL_BUTTON.setId("cancel");
        CANCEL_BUTTON.setOnAction(event);
        ((Stage) EDIT_IP_CAMERA_DIALOG.getDialogPane().getScene().getWindow()).setOnCloseRequest((WindowEvent event1) -> {
            EDIT_IP_CAMERA_DIALOG.close();
            ((Stage) EDIT_IP_CAMERA_DIALOG.getDialogPane().getScene().getWindow()).close();
        });
    }

    @Override
    public void prepareView() {
        this.textField = factory.createTextField();

    }

    @Override
    public void initComponents() {
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(CONFIRM_BUTTON, CANCEL_BUTTON);
        uriField.setPrefWidth(250);
        uriField.setText(camera.getIpAddress());
        portField.setMaxWidth(50);
        portField.setText(camera.getPort().toString());
        userField.setPrefWidth(250);
        userField.setText(camera.getUsername());
        passField.setPrefWidth(250);
        passField.setText(camera.getPassword());
        streamUriField.setPrefWidth(250);
        URI_LABEL.setFont(Font.font(16));
        PORT_LABEL.setFont(Font.font(16));
        NAME_LABEL.setFont(Font.font(16));
        PASS_LABEL.setFont(Font.font(16));
        STREAM_URI.setFont(Font.font(16));
        EDIT_IP_CAMERA_DIALOG.setTitle("Heimdall's Eye: Konfiguration einer ONVIF-IP-Kamera editieren");
        EDIT_IP_CAMERA_DIALOG.setHeaderText("Editieren Sie die Konfiguration nach Ihren Wünschen.");
        EDIT_IP_CAMERA_DIALOG.setGraphic(new ImageView("/config.png"));
        DIALOG_LAYOUT.setVgap(10);
        DIALOG_LAYOUT.setHgap(10);
        DIALOG_LAYOUT.setPadding(new Insets(20, 10, 10, 10));
        INFO_DIALOG_LAYOUT.setVgap(10);
        INFO_DIALOG_LAYOUT.setHgap(10);
        INFO_DIALOG_LAYOUT.setPadding(new Insets(20, 10, 10, 10));
        DIALOG_LAYOUT.add(URI_LABEL, 0, 0);
        DIALOG_LAYOUT.add(uriField, 1, 0);
        DIALOG_LAYOUT.add(PORT_LABEL, 0, 1);
        DIALOG_LAYOUT.add(portField, 1, 1);
        DIALOG_LAYOUT.add(NAME_LABEL, 0, 2);
        DIALOG_LAYOUT.add(userField, 1, 2);
        DIALOG_LAYOUT.add(PASS_LABEL, 0, 3);
        DIALOG_LAYOUT.add(passField, 1, 3);
        DIALOG_LAYOUT.add(buttonBox, 1, 5);
        INFO_DIALOG_LAYOUT.add(INFO_NETWORK_TAB_LABEL, 1, 2);
        INFO_DIALOG_LAYOUT.add(STREAM_URI, 0, 1);
        INFO_DIALOG_LAYOUT.add(streamUriField, 1, 1);
        DEVICE_CONNECT_DATA_TAB.setClosable(false);
        DEVICE_CONNECT_DATA_TAB.setContent(DIALOG_LAYOUT);
        DEVICE_NETWORK_STREAM.setClosable(false);
        DEVICE_NETWORK_STREAM.setContent(INFO_DIALOG_LAYOUT);
        EDIT_DIALOG_TAB_PANE.getTabs().addAll(DEVICE_CONNECT_DATA_TAB, DEVICE_NETWORK_STREAM);
        EDIT_IP_CAMERA_DIALOG.getDialogPane().setContent(EDIT_DIALOG_TAB_PANE);
        Stage stage = (Stage) EDIT_IP_CAMERA_DIALOG.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource("/config.png").toString()));
    }

    @Override
    public void showView() {
        EDIT_IP_CAMERA_DIALOG.show();
    }

    public TextField getIpAddressField() {
        return this.uriField;
    }

    public TextField getPortField() {
        return this.portField;
    }

    public TextField getUsernameField() {
        return this.userField;
    }

    public TextField getPasswordField() {
        return this.passField;
    }
    
    public TextField getStreamUriField() {
        return this.streamUriField;
    }

}
