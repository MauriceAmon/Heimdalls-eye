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
package com.example.heimdallseye.View.factory.dialogs;

import com.example.heimdallseye.View.View;
import com.example.heimdallseye.View.factory.GuiFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 *
 * @author Maurice Amon
 */
public class InformationDialog extends View {

    private final Alert INFO_DIALOG = new Alert(Alert.AlertType.INFORMATION);

    private final GuiFactory factory;

    private final Label AUTHOR_LABEL = new Label();

    private final Label SOFTWARE_INFO_LABEL = new Label("Heimdall's Eye wurde für die Administration von IP-Kameras entwickelt,\ndie dem ONVIF-Standard folgen.\n"
            + "Maurice Amon\nlogikextremist@gmail.com");

    private final GridPane INFO_DIALOG_LAYOUT = new GridPane();

    public InformationDialog(GuiFactory factory) {
        this.factory = factory;
    }

    @Override
    public void prepareView() {

    }

    @Override
    public void initComponents() {
        AUTHOR_LABEL.setStyle("-fx-font-weight: bold;");
        AUTHOR_LABEL.setText("Info: \n\nAutor: \nKontakt: \n");
        INFO_DIALOG_LAYOUT.add(AUTHOR_LABEL, 0, 1);
        INFO_DIALOG_LAYOUT.add(SOFTWARE_INFO_LABEL, 1, 1);
        INFO_DIALOG.setTitle("Heimdall's Eye: Information");
        INFO_DIALOG.setHeaderText("Informationen über diese Software");
        INFO_DIALOG.getDialogPane().setContent(INFO_DIALOG_LAYOUT);
        Stage stage = (Stage) INFO_DIALOG.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource("/info_little.png").toString()));
    }

    @Override
    public void showView() {
        INFO_DIALOG.showAndWait();
    }

}
