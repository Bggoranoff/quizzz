/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import client.utils.ServerUtils;
import commons.entities.MultiplayerUser;
import commons.entities.User;
import commons.models.Question;
import commons.models.SoloGame;
import commons.utils.QuestionType;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainCtrl {

    public static final String STYLES_PATH = "client/stylesheets/pixelart.css";
    public static final double WIDTH = 1024.0;
    public static final double HEIGHT = 704.0;
    private static final double START_TIME_DELAY = 0.05;
    private static final double INTERVAL = 0.05;
    private static final int MILLIS = 50;
    private static final int POLLING_DELAY = 0;
    private static final int POLLING_INTERVAL = 500;
    private static final int QUESTIONS_PER_GAME = 20;
    private static final double ALERT_POSITION_Y = 250;
    private static final double ALERT_POSITION_X = 517;
    private static final double ALERT_SIZE = 50;


    //These are the variables used in the streak calculation formula
    private static final long X1 = 1;
    private static final long X2 = 4;
    private static final long X3 = 17;
    private static final long X4 = 20;
    private static final long FACTOR = 300;

    private Image pointerCursor;
    private Image handCursor;

    private String serverUrl;
    private Timer waitingTimer;
    private Stage primaryStage;
    private ServerUtils server;

    private MultiplayerAnswerCtrl multiplayerAnswerCtrl;
    private Scene multiplayerAnswer;

    private HomeCtrl homeCtrl;
    private Scene home;

    private MultiplayerQuestionCtrl multiplayerQuestionCtrl;
    private Scene multiplayerQuestion;

    private WaitingCtrl waitingCtrl;
    private Scene waiting;

    private RankingCtrl rankingCtrl;
    private Scene ranking;

    private MultiplayerEstimationQuestionCtrl multiplayerEstimationCtrl;
    private Scene multiplayerEstimation;

    private SoloEstimationQuestionCtrl soloEstimationCtrl;
    private Scene soloEstimation;

    private SoloQuestionCtrl soloQuestionCtrl;
    private Scene soloQuestion;

    private SoloAnswerCtrl soloAnswerCtrl;
    private Scene soloAnswer;

    private SoloResultsCtrl soloResultsCtrl;
    private Scene soloResults;

    private MultiplayerGameCtrl multiplayerCtrl;

    private MultiplayerResultsCtrl multiplayerResultsCtrl;
    private Scene multiplayerResults;

    private AdminPanelCtrl adminPanelCtrl;
    private Scene adminPanel;

    private AddActivityCtrl addActivityCtrl;
    private Scene addActivity;

    private DeleteActivityCtrl deleteActivityCtrl;
    private Scene deleteActivity;

    private HelpCtrl helpCtrl;
    private Scene help;

    private DialogPane dialogPane;

    private User user;
    private int gameIndex;
    private List<Color> colors;
    private Thread timerThread;
    private double countdown;
    private int answerCount = 0;

    private long streak = 0;
    private long streakScore=0;

    public void initialize(Stage primaryStage, Pair<HomeCtrl, Parent> home,
                           Pair<WaitingCtrl, Parent> waiting, Pair<MultiplayerQuestionCtrl, Parent> multiplayerQuestion,
                           Pair<MultiplayerAnswerCtrl, Parent> multiplayerAnswer, Pair<RankingCtrl, Parent> ranking,
                           Pair<MultiplayerEstimationQuestionCtrl, Parent> multiplayerEstimation,
                           Pair<SoloEstimationQuestionCtrl, Parent> soloEstimation,
                           Pair<SoloQuestionCtrl, Parent> soloQuestion,
                           Pair<SoloAnswerCtrl, Parent> soloAnswer,
                           Pair<SoloResultsCtrl, Parent> soloResults,
                           Pair<MultiplayerResultsCtrl, Parent> multiplayerResults,
                           Pair<AdminPanelCtrl, Parent> adminPanel,
                           Pair<AddActivityCtrl, Parent> addActivity,
                           Pair<DeleteActivityCtrl, Parent> deleteActivity,
                           Pair<HelpCtrl, Parent> help) {
        this.primaryStage = primaryStage;
        primaryStage.setMinHeight(HEIGHT);
        primaryStage.setMinWidth(WIDTH);
        primaryStage.setMaxHeight(HEIGHT);
        primaryStage.setMaxWidth(WIDTH);
        primaryStage.setResizable(false);

        pointerCursor = new Image("client/images/arrowcursor.png");
        handCursor = new Image("client/images/handcursor.png");

        this.multiplayerAnswerCtrl = multiplayerAnswer.getKey();
        this.multiplayerAnswer = new Scene(multiplayerAnswer.getValue());
        this.multiplayerAnswer.getStylesheets().add(STYLES_PATH);
        this.multiplayerAnswer.setCursor(new ImageCursor(pointerCursor));

        this.homeCtrl = home.getKey();
        this.home = new Scene(home.getValue());
        this.home.getStylesheets().add(STYLES_PATH);
        this.home.setCursor(new ImageCursor(pointerCursor));

        this.server = homeCtrl.getServer();

        this.multiplayerQuestionCtrl = multiplayerQuestion.getKey();
        this.multiplayerQuestion = new Scene(multiplayerQuestion.getValue());
        this.multiplayerQuestion.getStylesheets().add(STYLES_PATH);
        this.multiplayerQuestion.setCursor(new ImageCursor(pointerCursor));

        this.waitingCtrl = waiting.getKey();
        this.waiting = new Scene(waiting.getValue());
        this.waiting.getStylesheets().add(STYLES_PATH);
        this.waiting.setCursor(new ImageCursor(pointerCursor));
        this.waitingCtrl.setupHoverCursor();

        this.rankingCtrl = ranking.getKey();
        this.ranking = new Scene(ranking.getValue());
        this.ranking.getStylesheets().add(STYLES_PATH);
        this.ranking.setCursor(new ImageCursor(pointerCursor));

        this.multiplayerEstimationCtrl = multiplayerEstimation.getKey();
        this.multiplayerEstimation = new Scene(multiplayerEstimation.getValue());
        this.multiplayerEstimation.getStylesheets().add(STYLES_PATH);
        this.multiplayerEstimation.setCursor(new ImageCursor(pointerCursor));

        this.soloEstimationCtrl = soloEstimation.getKey();
        this.soloEstimation = new Scene(soloEstimation.getValue());
        this.soloEstimation.getStylesheets().add(STYLES_PATH);
        this.soloEstimation.setCursor(new ImageCursor(pointerCursor));

        this.soloQuestionCtrl = soloQuestion.getKey();
        this.soloQuestion = new Scene(soloQuestion.getValue());
        this.soloQuestion.getStylesheets().add(STYLES_PATH);
        this.soloQuestion.setCursor(new ImageCursor(pointerCursor));

        this.soloAnswerCtrl = soloAnswer.getKey();
        this.soloAnswer = new Scene(soloAnswer.getValue());
        this.soloAnswer.getStylesheets().add(STYLES_PATH);
        this.soloAnswer.setCursor(new ImageCursor(pointerCursor));

        this.soloResultsCtrl = soloResults.getKey();
        this.soloResults = new Scene(soloResults.getValue());
        this.soloResults.getStylesheets().add(STYLES_PATH);
        this.soloResults.setCursor(new ImageCursor(pointerCursor));

        this.multiplayerResultsCtrl = multiplayerResults.getKey();
        this.multiplayerResults = new Scene(multiplayerResults.getValue());
        this.multiplayerResults.getStylesheets().add(STYLES_PATH);
        this.multiplayerResults.setCursor(new ImageCursor(pointerCursor));

        this.adminPanelCtrl = adminPanel.getKey();
        this.adminPanel = new Scene( adminPanel.getValue() );

        this.addActivityCtrl = addActivity.getKey();
        this.addActivity = new Scene( addActivity.getValue() );

        this.deleteActivityCtrl = deleteActivity.getKey();
        this.deleteActivity = new Scene( deleteActivity.getValue() );

        this.helpCtrl = help.getKey();
        this.help = new Scene(help.getValue());

        countdown = multiplayerQuestionCtrl.getTimerLength();

        showHome();
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {

            quitGame(true, true);

            event.consume();
        });
    }

    /**
     * Binder for the User in the client side
     *
     * @param user
     */
    public void bindUser(User user) {
        this.user = user;
    }

    /**
     * Getter for the user
     *
     * @return user
     */
    public User getUser() {
        return this.user;
    }

    /**
     * This method resets the streak when an answer is incorrect.
     * Since it is called after the postAnswers method, it also disables the isActiveDoublePoints
     */
    public void resetStreak(){
        streak=0;
    }

    /**
     * Sets the streak score to this value
     * @param score
     */
    public void setStreakScore(Long score){
        streakScore=score;
    }

    /**
     * Gets the streak score
     * @return  streakScore
     */
    public Long getStreakScore(){
        return this.streakScore;
    }

    /**
     * This method increments the streak
     */
    public void incrementStreak(){
        streak++;
    }

    /**
     * This methods add the calculated score of the previous question to the user object
     * @param user
     * @param answeredQuestion
     */
    public void addScore(User user, Question answeredQuestion){
        if(answeredQuestion.hasCorrectUserAnswer()){
            incrementStreak();
        }
        else{
            resetStreak();
        }
        int correctFactor = answeredQuestion.hasCorrectUserAnswer() ? 1 : 0;
        int multiplyingFactor = (multiplayerCtrl!=null && multiplayerCtrl.getIsActiveDoublePoints()) ? 2 : 1;

        if(streak<X2){
             setStreakScore(multiplyingFactor * correctFactor * Math.round(Math.pow(FACTOR,((double)(streak+X1)/X2))));
        }
        else{
             setStreakScore(multiplyingFactor * correctFactor * Math.round(Math.pow(FACTOR,((double)(streak+X3)/X4))));
        }

        user.incrementScore((multiplyingFactor * (answeredQuestion.calculatePoints())) +
                getStreakScore());

    }

    /**
     * Returns the app primary stage
     * @return the app primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Returns the solo game points
     * @return the solo game points
     */
    public long getSoloScore() {
        return getUser().getPoints();
    }

    /**
     * Returns the addActivityScene
     * @return the addActivityScene
     */

    public Scene getAddActivityScene() {
        return this.addActivity;
    }

    /**
     * Returns the deleteActivityScene
     * @return the deleteActivityScene
     */

    public Scene getDeleteActivityScene() {
        return this.deleteActivity;
    }

    /**
     * Shows the home page of the quiz application on the primary
     * stage
     */
    public void showHome() {
        primaryStage.setTitle("Quizzz");
        primaryStage.setScene(home);
        homeCtrl.setFonts();
    }

    public void showAdminPanel() {
        try {
            adminPanelCtrl.refreshActivities();
        } catch (Exception e) {
            invalidURL();
            return;
        }
        primaryStage.setTitle( "Admin Panel");
        primaryStage.setScene(adminPanel);
        primaryStage.show();
    }

    /**
     * Displays the waiting page of the quiz application
     * Resets the colorList and the answerCount to 0 every time someone enters the waiting room.
     */
    public void showWaiting() {
        primaryStage.setTitle("Quizzz: Waiting");
        primaryStage.setScene(waiting);

        colors = new ArrayList<>();
        answerCount = 0;
        multiplayerQuestionCtrl.resetCircleColor();
        multiplayerAnswerCtrl.resetCircleColor();
        rankingCtrl.resetCircleColor();

        waitingCtrl.scaleButton();
        waitingTimer = new Timer();
        waitingTimer.schedule(
                new TimerTask() {

                    @Override
                    public void run() {
                        Platform.runLater(() -> waitingCtrl.fetchUsers());
                    }
                }, POLLING_DELAY, POLLING_INTERVAL);
    }

    /**
     * Initialises the multiplayer game controller and starts
     * a multiplayer game
     * @param gameIndex the index of the multiplayer game
     */
    public void startMultiplayerGame(int gameIndex) {
        multiplayerCtrl = new MultiplayerGameCtrl( this, server,
                new Pair<>(this.multiplayerQuestionCtrl, this.multiplayerQuestion),
                new Pair<>(this.multiplayerEstimationCtrl, this.multiplayerEstimation),
                new Pair<>(this.multiplayerAnswerCtrl, this.multiplayerAnswer),
                new Pair<>(this.rankingCtrl, this.ranking),
                new Pair<>(this.multiplayerResultsCtrl, this.multiplayerResults)
        );
        multiplayerCtrl.startGame();
    }

    /**
     * Stops the waiting room timer for continuous
     * user polling
     */
    public void stopWaitingTimer() {
        waitingTimer.cancel();
    }

    /**
     * Sets the multiplayer answer screen as the scene in the primary stage
     * and gives the primary stage a corresponding title.
     * Furthermore, it increments the answerCount and first sets up the answer page.
     *
     * @param correctUsers the users that have answered correctly
     * @param prevQuestion The question that has just been asked to the players.
     */
    public void showAnswerPage(Question prevQuestion, List<MultiplayerUser> correctUsers) {
        updateQuestionCounters(multiplayerAnswerCtrl, colors);
        //Adds the color of the answer correctness to a list of answers
        if (prevQuestion.hasCorrectUserAnswer()) {
            colors.add(Color.LIGHTGREEN);
        } else {
            colors.add(Color.INDIANRED);
        }
        answerCount++;
        multiplayerAnswerCtrl.setup(prevQuestion, correctUsers);
        primaryStage.setTitle("Answer screen");
        primaryStage.setScene(multiplayerAnswer);
    }

    /**
     * Mock method to create a simple list of strings that should later be replaced by players that
     * answered correctly.
     *
     * @return A list of Strings that represent players that answered the previous question correctly.
     */
    public List<MultiplayerUser> getCorrectPlayersMock() {
        List<MultiplayerUser> correctPlayers = new ArrayList<>();
        correctPlayers.add(new MultiplayerUser("Patrik"));
        correctPlayers.add(new MultiplayerUser("Bink"));
        correctPlayers.add(new MultiplayerUser("Boris"));
        return correctPlayers;
    }

    /**
     * Halves the remaining timer for the user.
     * @param user the user that used the joker
     */
    public void halfTime ( MultiplayerUser user ) {
        if ( !user.username.equals(this.user.username) ) {
            countdown = countdown / 2;
        }
    }

    /**
     * A getter for the number of the current question
     *
     * @return questionCount, which is the count of the number of questions that have already been shown.
     */
    public int getAnswerCount() {
        return answerCount;
    }

    /**
     * Starts a particular countdown timer and initiates the
     * timer animation with a new thread
     *
     * @param countdownCircle the circle to perform the
     *                        animation on
     * @param sceneController the scene controller instance that will redirect to the next scene,
     *                        once the timer is up
     */
    public void startTimer(ProgressIndicator countdownCircle, QuestionNumController sceneController) {
        countdownCircle.applyCss();
        Text text = (Text) countdownCircle.lookup(".text.percentage");
        if (timerThread != null && timerThread.isAlive()) {
            timerThread.interrupt();
        }
        timerThread = new Thread(() -> {
            countdown = sceneController.getTimerLength() - START_TIME_DELAY;
            while (countdown >= 0.0) {
                try {
                    double finalCountdown = countdown;
                    Platform.runLater(() -> {
                        countdownCircle.setProgress(finalCountdown / sceneController.getTimerLength());
                        if (text != null) {
                            text.setText(Math.round(finalCountdown) + "s");
                        }
                    });

                    Thread.sleep(MILLIS);
                    countdown -= INTERVAL;
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                    //This kills the current running thread
                    return;
                }
            }
            Platform.runLater(
                    () -> {
                        sceneController.redirect();
                        if (text != null) {
                            text.setText("Waiting...");
                        }
                    });
        });
        timerThread.start();
    }

    /**
     * Kills the thread that is running the timer
     */
    public void killThread() {
        timerThread.interrupt();
    }

    /**
     * Called once, initializes a solo game and shows the first question screen
     * Resets the state of the solo game
     */
    public void startSoloGame() {
        try {
            answerCount = 0;
            getUser().resetScore();
            colors = new ArrayList<>();

            soloQuestionCtrl.resetCircleColor();
            soloAnswerCtrl.resetCircleColor();
            resetStreak();
            setStreakScore(0L);

            SoloGame soloGame = server.getSoloGame(serverUrl, QUESTIONS_PER_GAME);
            primaryStage.setTitle("Solo game");

            if(soloGame.loadCurrentQuestion().getType() == QuestionType.ESTIMATION){
                showSoloEstimationQuestion(soloGame);
            }
            else {
                showSoloQuestion(soloGame);
            }
        } catch (Exception e) {
            invalidURL();
            return;
        }
    }

    /**
     * Alerts the user about the invalid URL
     */
    protected void invalidURL() {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert = setAlertStyle(alert);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText("Invalid server URL!");
        alert.showAndWait();
        return;
    }

    /**
     * Shows the relevant answer screen for the given solo game instance
     *
     * @param game the solo game instance
     */
    public void showSoloAnswerPage(SoloGame game) {
        Question prevQuestion = game.loadCurrentQuestion();
        if (prevQuestion.hasCorrectUserAnswer()) {
            colors.add(Color.LIGHTGREEN);
        } else {
            colors.add(Color.INDIANRED);
        }
        soloAnswerCtrl.setup(game);
        updateQuestionCounters(soloAnswerCtrl, colors);
        primaryStage.setScene(soloAnswer);
    }

    /**
     * Shows the Help page dialog component on the home page.
     */

    public void showHelp(){
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(help);
        stage.show();
    }

    /**
     * Getter for the number of questions per game
     *
     * @return QUESTIONS_PER_GAME
     */
    public int getQuestionsPerGame() {
        return QUESTIONS_PER_GAME;
    }

    /**
     * Shows the relevant question screen for the given solo game instance
     *
     * @param game the solo game instance
     */
    public void showSoloQuestion(SoloGame game) {
        soloQuestionCtrl.setup(game);
        updateQuestionCounters(soloQuestionCtrl, colors);
        primaryStage.setScene(soloQuestion);
        soloQuestionCtrl.startTimer();
        soloQuestionCtrl.setStartTime();
    }

    /**
     * Shows the solo estimation question page relevant to the given solo game
     * @param game the solo game instance
     */
    public void showSoloEstimationQuestion(SoloGame game) {
        soloEstimationCtrl.setup(game);
        updateQuestionCounters(soloEstimationCtrl, colors);
        primaryStage.setScene(soloEstimation);
        soloEstimationCtrl.startTimer();
        soloEstimationCtrl.setStartTime();
    }

    /**
     * Returns the server URL the application makes requests
     * to
     *
     * @return the app server URL
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * Sets the server URL for the application
     *
     * @param serverUrl the new server URL
     */
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * THIS STILL NEEDS TO BE IMPLEMENTED
     * Called after the last answer screen's timer is up, shows the solo results page
     *
     * @param game
     */
    public void showSoloResults(SoloGame game) {
        soloResultsCtrl.setup(game);
        updateQuestionCounters(soloResultsCtrl, colors);
        primaryStage.setScene(soloResults);
    }

    /**
     * Shows a pop up on screen to confirm quitting the game
     * @param quitApp is used to decide whether the application should be closed or not
     *                  If quitApp is true: the application is closed
     *                  If quitApp is false: the user is redirected to home page
     * @param isMultiplayer is used to decide whether the user is a multiplayer user
     *                      If so, it deletes the user from the database.
     */
    public void quitGame(boolean quitApp, boolean isMultiplayer){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert = setAlertStyle(alert);
        alert.setTitle("Quit solo game");
        alert.setContentText("Are you sure you want to quit?");
        ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getDialogPane().setCursor(new ImageCursor(new Image("client/images/arrowcursor.png")));
        alert.getButtonTypes().setAll(okButton, noButton);
        alert.showAndWait().ifPresent(type -> {
            if (type == okButton) {
                if(isMultiplayer) {
                    if(multiplayerCtrl != null) {
                        multiplayerCtrl.unregisterForEmojis();
                        multiplayerCtrl.unregisterForHalfTime();
                        multiplayerCtrl.hideEmojis();
                        multiplayerCtrl.setIsActiveDoublePoints(false);
                    }

                    if(server.getSession() != null && server.getSession().isConnected()) {
                        server.getSession().disconnect();
                    }

                    try {
                        if(quitApp) {
                            server.removeMultiplayerUser(serverUrl, gameIndex, (MultiplayerUser) user);
                        } else {
                            server.removeMultiplayerUserFromGame(serverUrl, gameIndex, user.id);
                        }
                        bindUser(null);
                        multiplayerEstimationCtrl.resetDoublePoints();
                        multiplayerQuestionCtrl.resetDoublePoints();
                        multiplayerQuestionCtrl.resetRemoveIncorrect();

                    } catch(WebApplicationException e) {
                        System.out.println("User to remove not found!");
                    } finally {
                        if(quitApp) {
                            System.exit(0);
                        }
                    }
                }
                killThread();
                showHome();
            }
        });
    }

    /**
     * Updates the little circles and the question counter in the given controller
     * @param controller the controller of the scene
     * @param colors the list of colors corresponding to answers to past questions
     */
    public void updateQuestionCounters(QuestionNumController controller, List<Color> colors){
        controller.resetCircleColor();
        controller.updateCircleColor(colors);
        controller.updateQuestionNumber();
        if(controller instanceof MultiplayerQuestionCtrl ||
                controller instanceof MultiplayerEstimationQuestionCtrl ||
                controller instanceof SoloQuestionCtrl ||
                controller instanceof SoloEstimationQuestionCtrl){
            controller.resetHighlight();
            controller.highlightCurrentCircle();
        }
    }

    /**
     * Sets the given alert's style in the way described in the Dialog.css file.
     * @param alert
     * @return alert with style
     */

    public Alert setAlertStyle(Alert alert){
        alert.initStyle(StageStyle.UNDECORATED);
        dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/client/Stylesheets/Dialog.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setY(ALERT_POSITION_Y);
        alert.setX(ALERT_POSITION_X);
        switch(alert.getAlertType()){
            case ERROR:
                ImageView error =
                        new ImageView(this.getClass().getResource("/client/images/thonk.png").toString());
                error.setFitHeight(ALERT_SIZE);
                error.setFitWidth(ALERT_SIZE);
                alert.setGraphic(error);
                break;
            case WARNING:
                ImageView warning =
                        new ImageView(this.getClass().getResource("/client/images/warning_alert.png").toString());
                warning.setFitHeight(ALERT_SIZE);
                warning.setFitWidth(ALERT_SIZE);
                alert.setGraphic(warning);
                break;
            case CONFIRMATION:
                ImageView confirmation =
                        new ImageView(this.getClass().getResource("/client/images/confirmation.png").toString());
                confirmation.setFitHeight(ALERT_SIZE);
                confirmation.setFitWidth(ALERT_SIZE);
                alert.setGraphic(confirmation);
                break;
            case INFORMATION:
                ImageView information =
                        new ImageView(this.getClass().getResource("/client/images/info.png").toString());
                information.setFitHeight(ALERT_SIZE);
                information.setFitWidth(ALERT_SIZE);
                alert.setGraphic(information);
                break;
        }
        return alert;
    }

    /**
     * Resets the colors of the circles in the main
     * controller
     */
    public void resetMainCtrl() {
        multiplayerQuestionCtrl.resetCircleColor();
        multiplayerAnswerCtrl.resetCircleColor();
        rankingCtrl.resetCircleColor();
        multiplayerResultsCtrl.resetCircleColor();
        this.colors = new ArrayList<>();
        this.answerCount = 0;
        this.user.resetScore();
    }

    /**
     * A getter for the game index
     * @return the game index
     */
    public int getGameIndex() {
        return gameIndex;
    }

    /**
     * A setter for the game index
     * @param gameIndex the game index to set
     */
    public void setGameIndex(int gameIndex) {
        this.gameIndex = gameIndex;
    }

    /**
     * Returns the application hand cursor
     * @return the hand cursor
     */
    public Image getHandCursorImage() {
        return handCursor;
    }
}