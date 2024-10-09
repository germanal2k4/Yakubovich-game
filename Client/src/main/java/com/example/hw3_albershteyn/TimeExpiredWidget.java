package com.example.hw3_albershteyn;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * when time is expired this widget is shown
 */
public class TimeExpiredWidget extends Application {
    private static String field;

    /**
     * external launching of widget
     * @param f - a line that will be shown
     */
    public static void launchWidget(String f) {
        field = f;
        Platform.runLater(() -> {
            try {
                new TimeExpiredWidget().start(new Stage());
            } catch (Exception e) {
                return;
            }
        });
    }

    /**
     * drawing a widget
     * @param stage - stage
     */
    @Override
    public void start(Stage stage) {
        stage.setTitle("Time Expired");

        Label label = new Label(field);

        Button endGameButton = new Button("End Game");
        endGameButton.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });

        Button newGameButton = new Button("New Game");
        newGameButton.setOnAction(e -> {
            Platform.runLater(() -> {
                new GameClient().start(new Stage());
            });
            stage.close();
        });

        HBox buttonBox = new HBox(10, endGameButton, newGameButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        VBox vbox = new VBox(10, label, buttonBox);
        vbox.setId("vbox");
        VBox.setVgrow(label, Priority.ALWAYS);
        VBox.setVgrow(buttonBox, Priority.NEVER);

        Scene scene = new Scene(vbox, 300, 200);
        scene.getStylesheets().add(getClass().getResource("widgetStyles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}

