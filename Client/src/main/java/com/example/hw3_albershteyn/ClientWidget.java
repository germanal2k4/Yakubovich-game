package com.example.hw3_albershteyn;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.*;

/**
 * widget for the game processes
 */
public class ClientWidget extends Application {
    public void setClient(Client client) {

        this.client = client;
    }

    /**
     * method that draws a start widget
     *
     * @param primaryStage - stage
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                client.stopGame();
            }
        });
        players = new HashMap<>();
        instance = this;
        primaryStage.setTitle("Game in words");

        resultsBoxLeft = createResultsBox("Current State of all Players");
        resultsBoxRight = createResultsBox("Your previous turns");

        ScrollPane scrollPaneLeft = new ScrollPane(resultsBoxLeft);
        scrollPaneLeft.setFitToWidth(true);
        scrollPaneLeft.setPadding(new Insets(10));
        scrollPaneLeft.getStylesheets().add(Objects.requireNonNull(getClass().getResource("clientStyles.css")).toExternalForm());

        ScrollPane scrollPaneRight = new ScrollPane(resultsBoxRight);
        scrollPaneRight.setFitToWidth(true);
        scrollPaneRight.setPadding(new Insets(10));
        scrollPaneLeft.getStylesheets().add(Objects.requireNonNull(getClass().getResource("clientStyles.css")).toExternalForm());


        VBox centralPane = new VBox(10);
        centralPane.setPadding(new Insets(10));
        centralPane.setAlignment(Pos.CENTER);


        idLabel = new Label("ID cессии");
        idLabel.setVisible(false);
        timerLabel = new Label("00:00:00");
        timerLabel.setVisible(false);

        wordLabel = new Label("Слово");
        wordLabel.setVisible(false);
        wordLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        numberField = new TextField();
        numberField.setPromptText("Enter numbers only");
        numberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,2}")) {
                numberField.setText(oldValue);
            }
        });
        numberField.setEditable(false);

        cyrillicField = new TextField();
        cyrillicField.setPromptText("Введите только русские буквы");
        cyrillicField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[а-яА-ЯёЁ]{0,1}")) {
                cyrillicField.setText(oldValue);
            }
        });
        cyrillicField.setEditable(false);

        submitButton = new Button("Submit");
        submitButton.setDisable(true);
        submitButton.setOnAction(e -> {
            if (cyrillicField.getText().isEmpty() || numberField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("You have to write digit and russian letter");
                alert.showAndWait();
            } else {
                client.changeFlag();
            }
        });
        centralPane.getChildren().addAll(idLabel, timerLabel, wordLabel, numberField, cyrillicField, submitButton);


        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(scrollPaneLeft);
        mainLayout.setRight(scrollPaneRight);
        mainLayout.setCenter(centralPane);

        Scene scene = new Scene(mainLayout, 1000, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("clientStyles.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * set not editable fields to not allow a user to press buttons
     * on not his turn
     *
     * @param b - editing fields param
     */
    public void setEditableFields(boolean b) {
        cyrillicField.setEditable(b);
        numberField.setEditable(b);
        submitButton.setDisable(!b);
    }

    /**
     * setter for current word
     *
     * @param text - word label text
     */
    public void setWordLabel(String text) {
        wordLabel.setText(text);
        wordLabel.setVisible(true);
    }

    /**
     * result box for guesses of user
     *
     * @param string - text on box
     * @return - - special box for the results
     */
    private VBox createResultsBox(String string) {
        Label letterLabel = new Label(string);
        letterLabel.setStyle("-fx-background-color: blue; -fx-padding: 1px;-fx-margin: 1px; -fx-border-color: grey; -fx-border-width: 1px;");
        VBox resultsBox = new VBox(80);
        resultsBox.setPadding(new Insets(80));
        resultsBox.getChildren().add(letterLabel);
        return resultsBox;
    }

    /**
     * setting a guessed letter in the word
     *
     * @param position - guessed pos
     * @param character - guessed character
     */
    public void setCharacterAtPosition(int position, String character) {
            String text = wordLabel.getText();
            if (position >= 0 && position < text.length()) {
                StringBuilder newText = new StringBuilder(text);
                newText.setCharAt(position, character.charAt(0));
                wordLabel.setText(newText.toString());
                System.out.println(client.getName().trim() + " " + wordLabel.getText());
                updateCurrentWord(client.getName().trim());
            }
    }

    public void updateCurrentWord(String name) {
        resultsBoxLeft.getChildren().remove(players.get(name).getContainer());
        players.remove(name);
        addPlayerMoveToLeft(name, wordLabel.getText());
    }

    /**
     * class for the current condition of the game
     */
    private static class PlayerMoveWidget {
        private final HBox container;

        /**
         * drawing the frame
         *
         * @param playerName - name of player
         * @param currentWord - current word
         */
        public PlayerMoveWidget(String playerName, String currentWord) {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("result.png")));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(30);
            imageView.setFitHeight(40);

            Label nameLabel = new Label(playerName);
            TextFlow wordFlow = createHighlightedTextFlow(currentWord);

            VBox textContainer = new VBox(5);
            textContainer.getChildren().addAll(nameLabel, wordFlow);

            container = new HBox(10);
            container.getChildren().addAll(imageView, textContainer);
            container.setStyle("-fx-border-color: blue; -fx-border-width: 1px; -fx-padding: 10px; -fx-background-color: lightgrey;");
        }

        /**
         * container for adding to left
         *
         * @return - container for adding to vbox
         */
        public HBox getContainer() {
            return container;
        }

        /**
         * creator of text flow
         *
         * @param word - word that has to be flowed
         * @return - text flow
         */
        private TextFlow createHighlightedTextFlow(String word) {
            TextFlow textFlow = new TextFlow();

            for (char ch : word.toCharArray()) {
                Text text = new Text(String.valueOf(ch == '+' ? '*' : ch));

                if (ch == '*') {
                    text.setStyle("-fx-background-color: red; -fx-fill: red;");
                } else if (ch == '+') {
                    text.setStyle("-fx-background-color: green; -fx-fill: green;");
                } else {
                    text.setStyle("-fx-background-color: black; -fx-fill: black;");
                }

                textFlow.getChildren().add(text);
            }

            return textFlow;
        }
    }

    /**
     * adding a guess into the result box
     *
     * @param letter  -
     * @param position -
     * @param result -
     */
    public synchronized void addPlayerMoveToRight(String letter, int position, int result) {
        Platform.runLater(() -> resultsBoxRight.getChildren().add(createPlayerMoveWidget(letter, position, result)));
    }

    /**
     * adding a player in the left panel
     *
     * @param word - name of player
     * @param current - current word
     */
    public synchronized void addPlayerMoveToLeft(String word, String current) {
        PlayerMoveWidget widget = new PlayerMoveWidget(word, current);
        players.put(word, widget);
        resultsBoxLeft.getChildren().add(widget.getContainer());
    }

    /**
     * initialisation of timers and id label after the connection
     * @param n - number of session
     * @param k - time
     */
    public void initParams(int n, int k){
        idLabel.setText("ID сессии: " + n);
        idLabel.setVisible(true);
        timerLabel = new Label(formatTime(k));
        timerLabel.setVisible(true);
        startTimer(k);
    }
    private void startTimer(int k) {
        long startTime = System.nanoTime();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsedNanos = now - startTime;
                long elapsedSeconds = elapsedNanos / 1_000_000_000;

                long remainingSeconds = k - elapsedSeconds;

                if (remainingSeconds <= 0) {
                    timerLabel.setText("0:00:00.000");
                    this.stop();
                } else {
                    timerLabel.setText(formatTime(remainingSeconds));
                }
            }
        };

        timer.start();
    }
    private String formatTime(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }
    /**
     * updating a child to the left layout
     *
     * @param name - name of player
     * @param word - word for player
     */
    public synchronized void updateWord(String name, String word) {
        resultsBoxLeft.getChildren().remove(players.get(name).getContainer());
        players.remove(name);
        addPlayerMoveToLeft(name, word);
    }

    /**
     * Creating a box for the results
     *
     * @param letter - letter guessed
     * @param position - wanted position
     * @param result - result that was shown
     * @return - Box of results
     */
    private HBox createPlayerMoveWidget(String letter, int position, int result) {
        Image resultIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("guesses.png")));
        ImageView imageView = new ImageView(resultIcon);
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);

        VBox textBox = new VBox(10);
        textBox.getChildren().addAll(
                new Label("Letter: " + letter),
                new Label("Position: " + position),
                new Label("Result: " + result)
        );

        HBox playerMoveBox = new HBox(10);
        playerMoveBox.getChildren().addAll(textBox, imageView);
        playerMoveBox.setStyle("-fx-border-color: orange; -fx-border-width: 1px; -fx-padding: 1px; -fx-margin: 1px; -fx-background-color: lightblue;");

        return playerMoveBox;
    }

    /**
     * instance of object for the singleton pattern
     *
     * @return - singleton instance of widget
     */
    public static ClientWidget getInstance() {
        return instance;
    }

    /**
     * get text for do the guess
     *
     * @return
     */
    public String getFieldString() {
        return cyrillicField.getText();
    }

    /**
     * get word number
     *
     * @return - return a number
     */
    public String getNumber() {
        return numberField.getText();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private TextField cyrillicField, numberField;
    private Label wordLabel, idLabel, timerLabel;
    private Button submitButton;
    private static ClientWidget instance;
    private VBox resultsBoxLeft, resultsBoxRight;
    private Client client;
    private Map<String, PlayerMoveWidget> players;

}
