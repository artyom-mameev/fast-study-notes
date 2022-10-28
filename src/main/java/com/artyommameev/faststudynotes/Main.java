package com.artyommameev.faststudynotes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.val;

import java.awt.*;

/**
 * The main application class.
 *
 * @author Artyom Mameev
 */
public class Main extends javafx.application.Application {

    public final static String APP_NAME = "Fast Study Notes";

    /**
     * The main entry point of the application.
     *
     * @param args the input arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * JavaFX entry point of the application.
     *
     * @param primaryStage a primary stage of the application.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(APP_NAME);

        val graphicsDevice = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getDefaultScreenDevice();

        Parent rootParent = FXMLLoader.load(getClass().getResource(
                "/fxml/Main.fxml"));

        //create a full-screen window depending on the screen resolution
        primaryStage.setScene(new Scene(rootParent,
                graphicsDevice.getDisplayMode().getWidth(),
                graphicsDevice.getDisplayMode().getHeight()));
        primaryStage.setMaximized(true);
        primaryStage.getIcons().add(new Image(
                Main.class.getResourceAsStream(
                        "/icon/icon.png")));
        primaryStage.show();
    }
}
