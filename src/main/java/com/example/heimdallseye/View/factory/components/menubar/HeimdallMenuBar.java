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
package com.example.heimdallseye.View.factory.components.menubar;

import com.example.heimdallseye.Controller.menu.MenuCommandClient;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

/**
 *
 * @author Maurice Amon
 */
public class HeimdallMenuBar extends MenuBar {

    private final Menu FILE_MENU = new Menu("Datei");

    private final Menu CONFIG_MENU = new Menu("Konfiguration");

    private final Menu INFO_MENU = new Menu("Info");

    private final MenuItem CLOSE_ITEM = new MenuItem("Beenden", new ImageView("/exit.png"));

    private final MenuItem ADD_IP_CAM_ITEM = new MenuItem("IP-Kamera hinzufügen", new ImageView("/add.png"));

    private final MenuItem EDIT_IP_CAM_ITEM = new MenuItem("IP-Kamera editieren", new ImageView("/edit.png"));

    private final MenuItem DELETE_IP_CAM_ITEM = new MenuItem("IP-Kamera löschen", new ImageView("/delete.png"));

    private final MenuItem INFO_ITEM = new MenuItem("Info anzeigen", new ImageView("/info_little.png"));

    private final MenuCommandClient MENU_CLIENT = new MenuCommandClient();

    public HeimdallMenuBar() {
        FILE_MENU.setId("file");
        FILE_MENU.getItems().add(CLOSE_ITEM);
        CONFIG_MENU.setId("config");
        CONFIG_MENU.getItems().addAll(ADD_IP_CAM_ITEM, EDIT_IP_CAM_ITEM, DELETE_IP_CAM_ITEM);
        INFO_MENU.setId("information");
        INFO_MENU.getItems().addAll(INFO_ITEM);
        super.getMenus().addAll(FILE_MENU, CONFIG_MENU, INFO_MENU);
        super.setHover(true);
        initializeActions();
    }

    private void initializeActions() {
        ADD_IP_CAM_ITEM.setId("addCamera");
        EDIT_IP_CAM_ITEM.setId("editCamera");
        DELETE_IP_CAM_ITEM.setId("deleteCamera");
        INFO_ITEM.setId("info");
        CLOSE_ITEM.setId("exit");
        super.getMenus().stream().forEach((menu) -> {
            menu.getItems().stream().forEach((item) -> {
                item.setOnAction(MENU_CLIENT);
            });
        });
    }

}
