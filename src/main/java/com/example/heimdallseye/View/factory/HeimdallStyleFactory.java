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


import com.example.heimdallseye.View.factory.components.camerabox.DeviceMenu;
import com.example.heimdallseye.View.factory.components.camerabox.HeimdallDeviceTableMenu;
import com.example.heimdallseye.View.factory.components.menubar.HeimdallMenuBar;
import com.example.heimdallseye.View.factory.components.toolbar.HeimdallToolBar;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;

/**
 *
 * @author Maurice
 */
public class HeimdallStyleFactory extends GuiFactory {

    @Override
    public MenuBar createMenuBar() {
        return new HeimdallMenuBar();
    }

    @Override
    public ToolBar createToolBar() {
        return new HeimdallToolBar();
    }

    @Override
    public Button createButton() {
        return new Button();
    }

    @Override
    public DeviceMenu createCameraBox() {
        return new HeimdallDeviceTableMenu();
    }

    @Override
    public PasswordField createPasswordField() {
        return new PasswordField();
    }

    @Override
    public TextField createTextField() {
        return new TextField();
    }

}
