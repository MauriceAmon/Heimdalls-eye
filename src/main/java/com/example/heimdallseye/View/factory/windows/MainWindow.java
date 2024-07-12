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
package com.example.heimdallseye.View.factory.windows;

import com.example.heimdallseye.View.View;
import com.example.heimdallseye.View.factory.GuiFactory;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Maurice Amon
 */
public class MainWindow extends View {

    private final GuiFactory factory;

    private Scene scene;

    public Stage stage;

    private final VBox LAYOUT = new VBox();

    private final VBox content = new VBox();

    private final Label label = new Label("Geladene Konfiguration");

    private static MainWindow mainWindow = null;

    private MainWindow(GuiFactory factory) {
        this.factory = factory;
    }

    public static MainWindow getInstance(GuiFactory factory) {
        if (mainWindow == null) {
            mainWindow = new MainWindow(factory);
        }
        return mainWindow;
    }

    @Override
    public void prepareView() {
        this.menuBar = factory.createMenuBar();
        this.toolBar = factory.createToolBar();
        this.deviceMenu = factory.createCameraBox();
    }

    @Override
    public void initComponents() {
        LAYOUT.getChildren().addAll(this.menuBar, this.toolBar, content);
        label.setStyle("-fx-padding: 10px 0 10px 0; -fx-text-fill: white; -fx-font-weight: bold;"
                + " -fx-font-size: 18px; -fx-background-color: linear-gradient(to bottom,#337ab7 0,#265a88 100%); -fx-alignment: center;");
        label.setMinWidth(640);
        content.getChildren().addAll(label, (VBox) deviceMenu);
        stage = new Stage();
        scene = new Scene(LAYOUT, 640, 480);
        scene.getStylesheets().addAll(this.getClass().getResource("/menubar.css").toExternalForm());
        stage.setTitle("Heimdall's Eye");
        stage.setScene(scene);
        stage.setWidth(640);
        stage.setHeight(480);
        stage.setResizable(false);
        stage.getIcons().add(new Image("/logo.png"));
    }

    @Override
    public void showView() {
        stage.show();
    }

}
