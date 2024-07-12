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

import com.example.heimdallseye.Controller.Command;
import com.example.heimdallseye.Controller.ExecutorCommandService;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;

/**
 *
 * @author Maurice Amon
 */
public class MenuCommandClient implements EventHandler<ActionEvent> {

    private ExecutorCommandService executor;

    private Command command;

    @Override
    public void handle(ActionEvent event) {
        switch (getItemId(event)) {
            case "actualize":
                command = new HeimdallsEyeActualizeCommand();
                break;
            case "exit":
                command = new HeimdallsEyeExitCommand();
                break;
            case "addCamera":
                command = new DeviceConfigWindowCommand();
                break;
            case "editCamera":
                command = new DeviceEditWindowCommand();
                break;
            case "deleteCamera":
                command = new DeviceDeleteWindowCommand();
                break;
            case "info":
                command = new HeimdallsEyeInfoDialogCommand();
                break;
        }

        executor = new ExecutorCommandService(command);
        executor.runExecution();
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

}
