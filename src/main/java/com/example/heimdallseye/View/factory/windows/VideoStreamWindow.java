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
package com.example.heimdallseye.View.factory.windows;

import java.util.ArrayList;
import java.util.List;

import com.example.heimdallseye.Controller.camera.PtzCommandClientController;
import com.example.heimdallseye.Controller.camera.tracking.RectangleMarkerCommand;
import com.example.heimdallseye.View.View;
import com.example.heimdallseye.View.factory.GuiFactory;
import com.example.heimdallseye.View.factory.widgets.HeimdallsEyeStreamWidget;
import com.example.heimdallseye.View.factory.widgets.VideoStreamThumbnail;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 *
 * @author Maurice
 */
public class VideoStreamWindow extends View {

    private GuiFactory factory;
    public final Stage VIDEO_WINDOW = new Stage();
    private final String VIDEO_PLAYER_TITLE = "Heimdall's Eye | Livestream";

    public final HBox MEDIA_BOX = new HBox();
    private final Timeline SLIDER_IN = new Timeline();
    private final Timeline SLIDER_OUT = new Timeline();

    private KeyFrame slideInOne = null;
    private KeyFrame slideInTwo = null;
    private KeyFrame slideOutOne = null;
    private KeyFrame slideOutTwo = null;

    private final Double ZOOM_FACTOR = 1.1;

    private ArrayList<VideoStreamThumbnail> thumbnails;

    public Group root;

    private final PtzCommandClientController PTZ_COMMAND_CLIENT = new PtzCommandClientController();

    public RectangleMarkerCommand rectangleMarkerController = null;

    private static VideoStreamWindow streamWindow = null;

    public HeimdallsEyeStreamWidget widget = new HeimdallsEyeStreamWidget();

    private VideoStreamWindow(GuiFactory factory, ArrayList<VideoStreamThumbnail> thumbnails) {
        this.factory = factory;
        this.thumbnails = thumbnails;
        widget.view.fitWidthProperty().bind(VIDEO_WINDOW.widthProperty());
        widget.view.fitHeightProperty().bind(VIDEO_WINDOW.heightProperty());
        widget.setActualStream(thumbnails.get(0).getStream());
        widget.setIpCamera(thumbnails.get(0).getStream().getIpCamera());
        thumbnails.get(0).getStream().isActive = Boolean.TRUE;
        VIDEO_WINDOW.setResizable(false);
        VIDEO_WINDOW.setOnCloseRequest((WindowEvent event) -> {
            this.thumbnails.stream().forEach((thumbnail) -> {
                thumbnail.getStream().setClosingStream();
                streamWindow = null;
            });
        });

    }

    public static VideoStreamWindow getInstance(GuiFactory factory, ArrayList<VideoStreamThumbnail> thumbnail) {
        if (streamWindow == null) {
            streamWindow = new VideoStreamWindow(factory, thumbnail);
        }
        return streamWindow;
    }

    public List<VideoStreamThumbnail> getThumbnailElements() {
        return thumbnails;
    }

    public Stage getStage() {
        return VIDEO_WINDOW;
    }

    @Override
    public void initComponents() {

        root = new Group();
        root.setOnMouseEntered((MouseEvent event) -> {
            SLIDER_IN.play();
        });
        root.setOnMouseExited((MouseEvent event) -> {
            SLIDER_OUT.play();
        });

        // Style das Menu des Players ...
        MEDIA_BOX.setSpacing(20);
        MEDIA_BOX.setPadding(new Insets(5, 15, 15, 15));
        MEDIA_BOX.setStyle("-fx-background-color: #000000;");
        MEDIA_BOX.setOpacity(0.8);

        root.getChildren().add(widget);
        root.getChildren().add(MEDIA_BOX);
        MEDIA_BOX.getChildren().addAll(thumbnails);
        thumbnails.get(0).setActivity(Boolean.TRUE);
        Scene scene = new Scene(root, 960, 540, Color.BLACK);

        scene.setOnScroll((ScrollEvent event) -> {
            event.consume();
            Double scaleFactor = (event.getDeltaY() > 0) ? ZOOM_FACTOR : 1 / ZOOM_FACTOR;
            if (root.getScaleX() * scaleFactor < 1) {
                scaleFactor = 1.0;
            }
            root.setScaleX(root.getScaleX() * scaleFactor);
            root.setScaleY(root.getScaleY() * scaleFactor);
        });
        scene.setOnKeyPressed(PTZ_COMMAND_CLIENT);
        scene.getStylesheets().addAll(this.getClass().getResource("/View/css/menubar.css").toExternalForm());
        // Setze Icon fuer den Player ...
        VIDEO_WINDOW.getIcons().add(new Image("/View/images/movie.png"));
        VIDEO_WINDOW.requestFocus();
        VIDEO_WINDOW.setTitle(VIDEO_PLAYER_TITLE);
        VIDEO_WINDOW.setScene(scene);
        VIDEO_WINDOW.show();
        addVideoMenu();
    }

    public void addVideoMenu() {
        MEDIA_BOX.setMinSize(VIDEO_WINDOW.getWidth(), 120);
        MEDIA_BOX.setTranslateY(VIDEO_WINDOW.getScene().getHeight() - 120);
        // Fuege Video-Menu hinzu ...
        SLIDER_OUT.getKeyFrames().removeAll(slideOutOne, slideOutTwo);
        SLIDER_IN.getKeyFrames().removeAll(slideInOne, slideInTwo);
        SLIDER_OUT.getKeyFrames().addAll(
                // Setze vertikale Verschiebung mit Keyframes, sobald der Muaszeiger aus dem Fenster "geht" ...
                // Die beiden Keyframes sorgen dafuer, dass das Menu (70px) innerhalb von 300 Ms aus dem Fenster vertikal nach unten slidet ..
                // Zugleich wird auch mit dem opacityProperty() die Transparenz auf 0.8 gesetzt, damit man durch das Menu ein wenig hindurch sehen kann ...
                slideOutOne = new KeyFrame(new Duration(0),
                        new KeyValue(MEDIA_BOX.translateYProperty(), VIDEO_WINDOW.getScene().getHeight() - 120),
                        new KeyValue(MEDIA_BOX.opacityProperty(), 0.8)),
                slideOutTwo = new KeyFrame(new Duration(300),
                        new KeyValue(MEDIA_BOX.translateYProperty(), VIDEO_WINDOW.getScene().getHeight()),
                        new KeyValue(MEDIA_BOX.opacityProperty(), 0.0)));

        // Setze vertikale Verschiebung mit Keyframes, sobald der Mauszeiger in das Fenster "geht" ...
        // Die beiden Keyframes sorgen dafuer, dass das Menu (70px) innerhalb von 300 Ms aus dem Fenster vertikal nach oben slidet ..
        // Zugleich wird auch mit dem opacityProperty() die Transparenz auf 0.8 gesetzt, damit man durch das Menu ein wenig hindurch sehen kann ...
        SLIDER_IN.getKeyFrames().addAll(
                slideInOne = new KeyFrame(new Duration(0),
                        new KeyValue(MEDIA_BOX.translateYProperty(), VIDEO_WINDOW.getScene().getHeight()),
                        new KeyValue(MEDIA_BOX.opacityProperty(), 0.0)),
                slideInTwo = new KeyFrame(new Duration(300),
                        new KeyValue(MEDIA_BOX.translateYProperty(), VIDEO_WINDOW.getScene().getHeight() - 120),
                        new KeyValue(MEDIA_BOX.opacityProperty(), 0.8)));
    }

    @Override
    public void prepareView() {
    }

    @Override
    public void showView() {
        VIDEO_WINDOW.show();
    }

}
