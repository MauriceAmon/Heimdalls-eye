/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.heimdallseye.heimdallseye;

import com.example.heimdallseye.Controller.*;
import com.example.heimdallseye.Model.entities.IpCameraEntity;
import com.example.heimdallseye.View.factory.*;
import com.example.heimdallseye.View.factory.widgets.*;
import com.example.heimdallseye.View.factory.windows.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maurice
 */
public class HeimdallsEye extends Application {

    private final DatabaseConnection con = DatabaseConnection.getInstance();

    private AlertInformationWidget alert;

    @Override
    public void start(Stage primaryStage) {
        ViewClient client = new ViewClient();
        MainWindow main = (MainWindow) client.createViewComponent("MainWindow", null);
        main.prepareView();
        main.initComponents();
        main.showView();
        alert = new AlertInformationWidget(Alert.AlertType.INFORMATION);
        alert.setAlertContent("Bitte haben Sie einen Moment Geduld.", "Die konfigurierten Geräte werden initialisiert.", new ImageView("/View/images/ajax-loader.gif"));
        Thread thread = new Thread(() -> {
            try {
                PreparedStatement stmt = con.getConnection().prepareStatement("SELECT * FROM APP.IPCAMERA");
                ResultSet set = stmt.executeQuery();
                while (set.next()) {
                    IpCameraEntity camera = new IpCameraEntity(set.getInt("id"), set.getString("ipaddress"), set.getInt("port"),
                            set.getString("username"), set.getString("password"));
                    camera.isAvaiable();
                    IpCameraEntity.addIpCamera(camera);
                }
            } catch (SQLException ex) {
                Logger.getLogger(HeimdallsEye.class.getName()).log(Level.SEVERE, null, ex);
            }
            Platform.runLater(() -> {
                ((Stage) alert.getDialogPane().getScene().getWindow()).close();
            });
        });
        thread.start();
    }
}
