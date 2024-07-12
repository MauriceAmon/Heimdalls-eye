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
package com.example.heimdallseye.View.factory.components.camerabox;

import com.example.heimdallseye.Controller.menu.MenuCommandClient;
import com.example.heimdallseye.Controller.stream.StreamCommandClientController;
import com.example.heimdallseye.Model.entities.IpCameraEntity;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 *
 * @author Maurice Amon
 */
public class HeimdallDeviceTableMenu extends VBox implements DeviceMenu {

    private final TableView<IpCameraEntity> TABLE_VIEW_IP_CAMERAS = new TableView<>();

    private final TableColumn IP_ADDRESS_COLUMN = new TableColumn("IP-Adresse");

    private final TableColumn USED_PORT_COLUMN = new TableColumn("Port");

    private final TableColumn DEVICE_USERNAME_COLUMN = new TableColumn("Benutzername");

    private final TableColumn DEVICE_STATE_COLUMN = new TableColumn("Status");

    private final TableColumn CHANGE_DEVICE_COLUMN = new TableColumn("Ändern");

    private final TableColumn DELETE_DEVICE_COLUMN = new TableColumn("Löschen");

    private final ObservableList<IpCameraEntity> IP_CAMERA_LIST = IpCameraEntity.IP_CAMERA_LIST;

    private final Button STREAM_BUTTON = new Button("Live-Stream starten");

    private final MenuCommandClient MENU_CLIENT = new MenuCommandClient();

    private final StreamCommandClientController STREAM_COMMAND_CLIENT = new StreamCommandClientController();

    public HeimdallDeviceTableMenu() {
        super.getChildren().addAll(TABLE_VIEW_IP_CAMERAS, STREAM_BUTTON);
        super.setPrefHeight(640);
        init();
    }

    public TableView getTable() {
        return TABLE_VIEW_IP_CAMERAS;
    }

    @Override
    public final void init() {
        initializeTableView();
        STREAM_BUTTON.setId("start");
        STREAM_BUTTON.setOnAction(STREAM_COMMAND_CLIENT);
        STREAM_BUTTON.setPrefWidth(640);
        STREAM_BUTTON.alignmentProperty().set(Pos.BOTTOM_CENTER);
    }

    private void initializeTableView() {
        TABLE_VIEW_IP_CAMERAS.setEditable(false);

        IP_ADDRESS_COLUMN.setCellValueFactory(new PropertyValueFactory<>("ipAddress"));
        USED_PORT_COLUMN.setCellValueFactory(new PropertyValueFactory<>("port"));
        DEVICE_USERNAME_COLUMN.setCellValueFactory(new PropertyValueFactory<>("username"));
        DEVICE_STATE_COLUMN.setCellValueFactory(new PropertyValueFactory<>("state"));
        initializeActionCells();
        TABLE_VIEW_IP_CAMERAS.getColumns().addAll(IP_ADDRESS_COLUMN, USED_PORT_COLUMN, DEVICE_USERNAME_COLUMN, DEVICE_STATE_COLUMN, CHANGE_DEVICE_COLUMN, DELETE_DEVICE_COLUMN);
        TABLE_VIEW_IP_CAMERAS.getColumns().stream().forEach((column) -> {
            column.setMinWidth(100);
            column.setMaxWidth(150);
        });
        TABLE_VIEW_IP_CAMERAS.setItems(IP_CAMERA_LIST);
    }

    private void initializeActionCells() {
        Callback<TableColumn<IpCameraEntity, String>, TableCell<IpCameraEntity, String>> changeCellFactory
                = (final TableColumn<IpCameraEntity, String> param) -> {
                    final TableCell<IpCameraEntity, String> cell = new TableCell<IpCameraEntity, String>() {
                final Button CHANGE_DEVICE_CONFIG_BUTTON = new Button("", new ImageView("/edit_little.png"));

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        initializeButtons(CHANGE_DEVICE_CONFIG_BUTTON);
                        CHANGE_DEVICE_CONFIG_BUTTON.setId("editCamera");
                        CHANGE_DEVICE_CONFIG_BUTTON.setOnAction(MENU_CLIENT);
                        setGraphic(CHANGE_DEVICE_CONFIG_BUTTON);
                    }

                }
            };
                    return cell;
                };
        Callback<TableColumn<IpCameraEntity, String>, TableCell<IpCameraEntity, String>> deleteCellFactory
                = (final TableColumn<IpCameraEntity, String> param) -> {
                    final TableCell<IpCameraEntity, String> cell = new TableCell<IpCameraEntity, String>() {
                final Button DELETE_DEVICE_CONFIG_BUTTON = new Button("", new ImageView("/delete_little.png"));

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        initializeButtons(DELETE_DEVICE_CONFIG_BUTTON);
                        DELETE_DEVICE_CONFIG_BUTTON.setId("deleteCamera");
                        DELETE_DEVICE_CONFIG_BUTTON.setOnAction(MENU_CLIENT);
                        setGraphic(DELETE_DEVICE_CONFIG_BUTTON);
                    }

                }
            };
                    return cell;
                };
        CHANGE_DEVICE_COLUMN.setCellFactory(changeCellFactory);
        DELETE_DEVICE_COLUMN.setCellFactory(deleteCellFactory);
    }

    private void initializeButtons(Button button) {
        button.setPrefSize(100, USE_PREF_SIZE);
        button.setStyle("-fx-cursor: hand;");
        button.setPadding(new Insets(1));

    }

}
