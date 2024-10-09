package com.example.hw3_albershteyn;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * when game is over this widget that allows user to renew a game is shown
 */
public class GameOverWidget extends Application {
    private static String field1;
    private static String field2;

    /**
     * external launching of widget
     * @param f1 - line that will be shown on widget
     * @param f2 - line that will be shown on widget
     */
    public static void launchWidget(String f1, String f2) {
        field1 = f1;
        field2 = f2;
        Platform.runLater(() -> {
            try {
                new GameOverWidget().start(new Stage());
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
        stage.setTitle("Game over");

        Label label1 = new Label(field1);
        Label label2 = new Label(field2);

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

        VBox vbox = new VBox(10, label1, label2, buttonBox);
        vbox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        vbox.setId("vbox");
        VBox.setVgrow(label1, Priority.ALWAYS);
        VBox.setVgrow(label2, Priority.ALWAYS);
        VBox.setVgrow(buttonBox, Priority.NEVER);

        Scene scene = new Scene(vbox, 300, 200);
        scene.getStylesheets().add(getClass().getResource("widgetStyles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}

