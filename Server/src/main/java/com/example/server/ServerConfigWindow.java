package com.example.server;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;


public class ServerConfigWindow extends Application {

    /**
     * main window of application that is respondent for drawing a GUI
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        ChangeListener<String> numberOnlyListener = new UniversalTextFieldListener.RestrictionListener(UniversalTextFieldListener.RestrictionType.NUMBERS_ONLY);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                try {
                    if(server != null){
                        server.setFlag(true);
                        server.killGame();
                    }
                    System.exit(0);
                    primaryStage.close();
                } catch (IOException e) {
                    return;
                }
            }
        });
        Label portLabel = new Label("Port:");
        portLabel.setId("portLabel");
        portField = new TextField("1234");
        portField.setId("portField");
        portField.textProperty().addListener(numberOnlyListener);

        Label playersLabel = new Label("Number of Players:");
        playersLabel.setId("playersLabel");
        playersField = new TextField("3");
        playersField.setId("playersField");
        playersField.textProperty().addListener(numberOnlyListener);

        Label prepTimeLabel = new Label("Preparation Time (tp):");
        prepTimeLabel.setId("prepTimeLabel");
        prepTimeField = new TextField("30");
        prepTimeField.setId("prepTimeField");
        prepTimeField.textProperty().addListener(numberOnlyListener);

        Label sessionTimeLabel = new Label("Session Time (ts):");
        sessionTimeLabel.setId("sessionTimeLabel");
        sessionTimeField = new TextField("300");
        sessionTimeField.setId("sessionTimeField");
        sessionTimeField.textProperty().addListener(numberOnlyListener);

        Label pauseTimeLabel = new Label("Pause Time (tb):");
        pauseTimeLabel.setId("pauseTimeLabel");
        pauseTimeField = new TextField("5");
        pauseTimeField.setId("pauseTimeField");
        pauseTimeField.textProperty().addListener(numberOnlyListener);

        Label notifyPeriodLabel = new Label("Notify Period (tn):");
        notifyPeriodLabel.setId("notifyPeriodLabel");
        notifyPeriodField = new TextField("1");
        notifyPeriodField.setId("notifyPeriodField");
        notifyPeriodField.textProperty().addListener(numberOnlyListener);

        Label wordOptionLabel = new Label("Word Option:");
        wordOptionLabel.setId("wordOptionLabel");
        wordOptionComboBox = new ComboBox<>();
        wordOptionComboBox.getItems().addAll("Length of Word", "Custom Word");
        wordOptionComboBox.setValue("Length of Word");


        Label wordLengthLabel = new Label("Number of Letters in Word (n):");
        wordLengthLabel.setId("wordLengthLabel");
        wordLengthField = new TextField("5");
        wordLengthField.setId("wordLengthField");
        wordLengthField.textProperty().addListener(numberOnlyListener);

        Label customWordLabel = new Label("Custom Word:");
        customWordLabel.setId("customWordLabel");
        customWordLabel.setVisible(false);
        customWordField = new TextField();
        customWordField.setId("customWordField");
        customWordField.setVisible(false);
        customWordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[а-яА-ЯёЁ]*")) {
                customWordField.setText(newValue.replaceAll("[^а-яА-ЯёЁ]", ""));
            }
        });
        customWordField.setPromptText("Введите только осмысленные русские слова");

        Label progressBarLabel = new Label("Server is status");
        serverProgressBar = new ProgressBar(0);
        serverProgressBar.setPrefWidth(300);
        serverProgressBar.setProgress(0.1);
        serverProgressBar.getStyleClass().add("progress-bar-red");

        wordOptionComboBox.setOnAction(e -> {
            if (wordOptionComboBox.getValue().equals("Length of Word")) {
                wordLengthLabel.setVisible(true);
                wordLengthField.setVisible(true);
                customWordLabel.setVisible(false);
                customWordField.setVisible(false);
            } else {
                wordLengthLabel.setVisible(false);
                wordLengthField.setVisible(false);
                customWordLabel.setVisible(true);
                customWordField.setVisible(true);
            }
        });


        fileChooserButton = new Button("Select File");
        selectedFileLabel = new Label("russian_nouns.txt");

        fileChooserButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                selectedFileLabel.setText(file.getAbsolutePath());
            }
        });

        Button startServerButton = new Button("Start Server");
        startServerButton.getStyleClass().add("button");

        Button stopServerButton = new Button("Stop Server");
        stopServerButton.getStyleClass().add("button");

        portLabel.getStyleClass().add("label");
        playersLabel.getStyleClass().add("label");
        prepTimeLabel.getStyleClass().add("label");
        sessionTimeLabel.getStyleClass().add("label");
        pauseTimeLabel.getStyleClass().add("label");
        notifyPeriodLabel.getStyleClass().add("label");
        wordOptionLabel.getStyleClass().add("label");
        wordLengthLabel.getStyleClass().add("label");
        customWordLabel.getStyleClass().add("label");
        selectedFileLabel.getStyleClass().add("label");
        portField.getStyleClass().add("text-field");
        playersField.getStyleClass().add("text-field");
        prepTimeField.getStyleClass().add("text-field");
        sessionTimeField.getStyleClass().add("text-field");
        pauseTimeField.getStyleClass().add("text-field");
        notifyPeriodField.getStyleClass().add("text-field");
        wordLengthField.getStyleClass().add("text-field");
        customWordField.getStyleClass().add("text-field");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(portLabel, 0, 0);
        gridPane.add(portField, 1, 0);
        gridPane.add(playersLabel, 0, 1);
        gridPane.add(playersField, 1, 1);
        gridPane.add(prepTimeLabel, 0, 2);
        gridPane.add(prepTimeField, 1, 2);
        gridPane.add(sessionTimeLabel, 0, 3);
        gridPane.add(sessionTimeField, 1, 3);
        gridPane.add(pauseTimeLabel, 0, 4);
        gridPane.add(pauseTimeField, 1, 4);
        gridPane.add(notifyPeriodLabel, 0, 5);
        gridPane.add(notifyPeriodField, 1, 5);
        gridPane.add(wordOptionLabel, 0, 6);
        gridPane.add(wordOptionComboBox, 1, 6);
        gridPane.add(wordLengthLabel, 0, 7);
        gridPane.add(wordLengthField, 1, 7);
        gridPane.add(customWordLabel, 0, 7);
        gridPane.add(customWordField, 1, 7);
        gridPane.add(fileChooserButton, 0, 8);
        gridPane.add(selectedFileLabel, 1, 8);
        gridPane.add(progressBarLabel, 0, 10);
        gridPane.add(serverProgressBar, 1, 10);

        HBox buttonBox = new HBox(10, stopServerButton, startServerButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        VBox root = new VBox(gridPane, buttonBox);
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        VBox.setVgrow(buttonBox, Priority.ALWAYS);

        GridPane.setHgrow(portField, Priority.ALWAYS);
        GridPane.setHgrow(playersField, Priority.ALWAYS);
        GridPane.setHgrow(prepTimeField, Priority.ALWAYS);
        GridPane.setHgrow(sessionTimeField, Priority.ALWAYS);
        GridPane.setHgrow(pauseTimeField, Priority.ALWAYS);
        GridPane.setHgrow(notifyPeriodField, Priority.ALWAYS);
        GridPane.setHgrow(wordLengthField, Priority.ALWAYS);
        GridPane.setHgrow(customWordField, Priority.ALWAYS);
        GridPane.setHgrow(selectedFileLabel, Priority.ALWAYS);

        portField.setMaxWidth(Double.MAX_VALUE);
        playersField.setMaxWidth(Double.MAX_VALUE);
        prepTimeField.setMaxWidth(Double.MAX_VALUE);
        sessionTimeField.setMaxWidth(Double.MAX_VALUE);
        pauseTimeField.setMaxWidth(Double.MAX_VALUE);
        notifyPeriodField.setMaxWidth(Double.MAX_VALUE);
        wordLengthField.setMaxWidth(Double.MAX_VALUE);
        customWordField.setMaxWidth(Double.MAX_VALUE);
        selectedFileLabel.setMaxWidth(Double.MAX_VALUE);


        Scene scene = new Scene(root, 600, 800);
        String css = this.getClass().getResource("styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Server Configuration");
        primaryStage.show();

        startServerButton.setOnAction(e -> {
            try {
                startServer();
            } catch (InterruptedException ex) {
                return;
            }
        });
        stopServerButton.setOnAction(e -> {
            try {
                stopServer();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        wordLengthField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                server.setN(Integer.parseInt(wordLengthField.getText()));
            }
        });
        customWordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                server.setWord(customWordField.getText());
            }
        });
    }

    /**
     * this method launches server
     */
    private void startServer() throws InterruptedException {
        if(!flag){
            portField.setEditable(false);
            playersField.setEditable(false);
            prepTimeField.setEditable(false);
            sessionTimeField.setEditable(false);
            pauseTimeField.setEditable(false);
            notifyPeriodField.setEditable(false);
            fileChooserButton.setDisable(true);
            server = setServerConfig();
            server.start();
            Thread.sleep(1000);
            if(!server.isStarted()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error 404");
                alert.setHeaderText(null);
                alert.setContentText("Server cannot be started try to insert some other values next time");
                alert.showAndWait();
            }
            flag = true;
            updateServerStatus(true);
        }
    }

    /**
     * method for correct configuration of server
     * @return
     */
    private Server setServerConfig(){
        return new Server(Integer.parseInt(portField.getText()),
                Integer.parseInt(playersField.getText()),
                Integer.parseInt(prepTimeField.getText()),
                Integer.parseInt(sessionTimeField.getText()),
                Integer.parseInt(pauseTimeField.getText()),
                Integer.parseInt(notifyPeriodField.getText()),
                selectedFileLabel.getText());
    }

    /**
     * updating status of progress bar that is respondent for condition of server
     * @param isRunning
     */
    private void updateServerStatus(boolean isRunning) {
        if (isRunning) {
            serverProgressBar.setProgress(1.0);
            serverProgressBar.getStyleClass().removeAll("progress-bar-red");
            serverProgressBar.getStyleClass().add("progress-bar-green");
        } else {
            serverProgressBar.setProgress(0.1);
            serverProgressBar.getStyleClass().removeAll("progress-bar-green");
            serverProgressBar.getStyleClass().add("progress-bar-red");

        }
    }

    /**
     * method that stops server
     * @throws IOException
     */
    private void stopServer() throws IOException {
        if(flag){
            portField.setEditable(true);
            playersField.setEditable(true);
            prepTimeField.setEditable(true);
            sessionTimeField.setEditable(true);
            pauseTimeField.setEditable(true);
            notifyPeriodField.setEditable(true);
            fileChooserButton.setDisable(false);
            flag = false;
            updateServerStatus(false);
            server.killGame();
        }
    }
    private TextField portField, playersField, prepTimeField, sessionTimeField, pauseTimeField, notifyPeriodField, wordLengthField, customWordField;
    private ComboBox<String> wordOptionComboBox;
    private boolean flag = false;
    private Server server;
    private ProgressBar serverProgressBar;
    private Button fileChooserButton;
    private Label selectedFileLabel;
    public static void main(String[] args) {
        launch(args);
    }
}