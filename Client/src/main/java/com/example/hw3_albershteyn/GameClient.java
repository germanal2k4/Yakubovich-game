package com.example.hw3_albershteyn;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * widget that is shown when user starts a user game
 */
public class GameClient extends Application {
    @Override
    public void start(Stage primaryStage) {
        ChangeListener<String> numberOnlyListener = new UniversalTextFieldListener.RestrictionListener(UniversalTextFieldListener.RestrictionType.NUMBERS_ONLY);

        Label hostLabel = new Label("Host:");
        hostLabel.setId("hostLabel");
        hostField = new TextField("localhost");
        hostField.setId("hostField");

        Label portLabel = new Label("Port:");
        portLabel.setId("portLabel");
        portField = new TextField("1234");
        portField.setId("portField");
        portField.textProperty().addListener(numberOnlyListener);

        Label nameLabel = new Label("Player Name:");
        nameLabel.setId("nameLabel");
        nameField = new TextField();
        nameField.setId("nameField");
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z]*")) {
                nameField.setText(newValue.replaceAll("[^a-zA-Z]", ""));
            }
        });
        nameField.setPromptText("Введите никнейм на английском языке");

        Button aboutButton = new Button("About");
        aboutButton.getStyleClass().add("button");
        Button gameButton = new Button("Game");
        gameButton.getStyleClass().add("button");

        hostLabel.getStyleClass().add("label");
        portLabel.getStyleClass().add("label");
        nameLabel.getStyleClass().add("label");
        hostField.getStyleClass().add("text-field");
        portField.getStyleClass().add("text-field");
        nameField.getStyleClass().add("text-field");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(hostLabel, 0, 0);
        gridPane.add(hostField, 1, 0);
        gridPane.add(portLabel, 0, 1);
        gridPane.add(portField, 1, 1);
        gridPane.add(nameLabel, 0, 2);
        gridPane.add(nameField, 1, 2);

        HBox buttonBox = new HBox(10, aboutButton, gameButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        VBox root = new VBox(gridPane, buttonBox);
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        VBox.setVgrow(buttonBox, Priority.NEVER);


        GridPane.setHgrow(hostField, Priority.ALWAYS);
        GridPane.setHgrow(portField, Priority.ALWAYS);
        GridPane.setHgrow(nameField, Priority.ALWAYS);
        hostField.setMaxWidth(Double.MAX_VALUE);
        portField.setMaxWidth(Double.MAX_VALUE);
        nameField.setMaxWidth(Double.MAX_VALUE);


        Scene scene = new Scene(root, 400, 300);
        String css = this.getClass().getResource("styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Game Client");
        primaryStage.show();


        aboutButton.setOnAction(e -> showAboutInfo());

        gameButton.setOnAction(e -> {
            if (nameField.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Player name is required!");
                alert.showAndWait();
            } else {
                try {
                    openServerConfigWindow();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                Stage stage = (Stage) gameButton.getScene().getWindow();
                stage.close();
            }
        });


        nameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                gameButton.fire();
            }
        });
    }

    /**
     * about info message box for user
     */
    private void showAboutInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setContentText("This game is developed by German Albershteyn for HW3 programming in Java.");
        alert.showAndWait();
    }

    /**
     * when button start game is pressed it tries to open a server window
     * if it cannot open be opened a alert window is shown
     * @throws InterruptedException - due to functions of threads
     */
    private void openServerConfigWindow() throws InterruptedException {
        Thread.sleep(300);
        if(names.contains(nameField.getText())){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("This name is already taken");
            alert.setHeaderText(null);
            alert.setContentText("Change nickname");
            alert.showAndWait();
            return;
        }
        Client client = new Client(Integer.parseInt(portField.getText()), nameField.getText(), hostField.getText());
        client.start();
        Thread.sleep(200);
        Stage stage = new Stage();
        ClientWidget clientWidget = new ClientWidget();
        if(client.getSuccessfullylaunched()){
            clientWidget.start(stage);
            clientWidget.setClient(client);
            names.add(nameField.getText());
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error 404");
            alert.setHeaderText(null);
            alert.setContentText("Server wasn't found");
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private TextField portField;
    private TextField nameField;
    private TextField hostField;
    private static Set<String> names = new HashSet<>();
}