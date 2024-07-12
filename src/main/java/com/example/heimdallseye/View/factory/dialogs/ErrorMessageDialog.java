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

import java.io.PrintWriter;
import java.io.StringWriter;

import com.example.heimdallseye.View.View;
import com.example.heimdallseye.View.factory.GuiFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 *
 * @author Maurice Amon
 */
public class ErrorMessageDialog extends View {

    private final Alert ERROR_DIALOG = new Alert(Alert.AlertType.ERROR);

    private final GuiFactory factory;

    private final Exception EXCEPTION;

    private final GridPane ERROR_DIALOG_LAYOUT = new GridPane();

    private final String ERROR_MESSAGE = "Die angegebene Konfiguration passt zu keinem, in ihrem Netzwerk verfügbaren, Gerät. "
            + "Bitte überprüfen Sie Ihre Angaben und/oder wenden Sie sich an den Entwickler, indem Sie "
            + "die Exception in der Box in die Mail kopieren oder als Anhang hinzufügen.";
    private TextArea exceptionArea;

    public ErrorMessageDialog(GuiFactory factory, Exception ex) {
        this.factory = factory;
        this.EXCEPTION = ex;
    }

    @Override
    public void prepareView() {

    }

    @Override
    public void initComponents() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        EXCEPTION.printStackTrace(pw);
        exceptionArea = new TextArea(sw.toString());
        exceptionArea.setEditable(false);
        exceptionArea.setWrapText(true);
        ERROR_DIALOG_LAYOUT.add(new Label(EXCEPTION.getMessage()), 0, 0);
        ERROR_DIALOG_LAYOUT.add(exceptionArea, 0, 2);
        ERROR_DIALOG.setTitle("Heimdall's Eye: Fehlermeldung");
        ERROR_DIALOG.setHeaderText("Das Gerät konnte im Netzwerk nicht gefunden werden.");
        ERROR_DIALOG.setContentText(ERROR_MESSAGE);
        ERROR_DIALOG.getDialogPane().setExpandableContent(ERROR_DIALOG_LAYOUT);
        Stage stage = (Stage) ERROR_DIALOG.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource("/error.png").toString()));
    }

    @Override
    public void showView() {
        ERROR_DIALOG.showAndWait();
    }
}
