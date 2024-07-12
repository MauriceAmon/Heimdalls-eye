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
package com.example.heimdallseye.View.factory.components.toolbar;

import com.example.heimdallseye.Controller.menu.MenuCommandClient;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

/**
 *
 * @author Maurice
 */
public class HeimdallToolBar extends ToolBar {
    
    private final Button ACTUALIZE_TOOL = new Button("", new ImageView("/reload.png"));
    
    private final MenuCommandClient MENU_CLIENT = new MenuCommandClient();

    public HeimdallToolBar() {
        initComponents();
        setGraphics();
        setActions();
        super.getItems().addAll(ACTUALIZE_TOOL);
        super.setStyle("-fx-background-color: linear-gradient(to bottom, #fcfff4 0%,#dfe5d7 40%,#c1beb2 100%);");
    }

    private void initComponents() {
        ACTUALIZE_TOOL.setCursor(Cursor.HAND);
        ACTUALIZE_TOOL.setTooltip(new Tooltip("Aktualisieren"));
    }

    private void setGraphics() {
        ACTUALIZE_TOOL.setGraphic(new ImageView("/reload.png"));
    }

    private void setActions() {
        ACTUALIZE_TOOL.setId("actualize");
        ACTUALIZE_TOOL.setOnAction(MENU_CLIENT);
    }
}
