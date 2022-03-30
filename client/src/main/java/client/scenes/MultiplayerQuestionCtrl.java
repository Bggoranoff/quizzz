package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.Activity;
import commons.entities.MultiplayerUser;
import commons.models.Answer;
import commons.models.ChoiceQuestion;
import commons.models.ComparisonQuestion;
import commons.models.ConsumptionQuestion;
import commons.models.Emoji;
import commons.models.Question;
import commons.utils.CompareType;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;


public class MultiplayerQuestionCtrl extends AbstractMultichoiceQuestionCtrl
        implements EmojiController {
    private static final int KICK_AT_X_QUESTIONS = 3;

    private MultiplayerGameCtrl gameCtrl;

    private boolean answerTopDisable = false;
    private boolean answerMidDisable = false;
    private boolean answerBotDisable = false;

    @FXML
    private GridPane emojiPane;
    @FXML
    private ImageView emojiImage;
    @FXML
    private Text emojiText;

    private List<StackPane> jokers;

    @FXML
    private StackPane doublePoints;
    @FXML
    private StackPane removeIncorrect;
    @FXML
    private StackPane reduceTime;

    @FXML
    private ImageView doublePointsImage;
    @FXML
    private ImageView removeIncorrectImage;
    @FXML
    private ImageView reduceTimeImage;

    /**
     * Creates a controller for the multiplayer question screen, with the given server and main controller.
     * Creates the list answerButtons for iterating through all of these.
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public MultiplayerQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Sets up the question page scene: <br>
     * - Sets up the question/answers according to the type of the question given <br>
     * - Fills the answerButtons list for iterations <br>
     * - Resets all buttons to their default colors
     *
     * @param question the question instance upon which the setup is based
     */
    public void setup(Question question) {
        jokers=new ArrayList<>();
        jokers.add(doublePoints);
        jokers.add(removeIncorrect);
        jokers.add(reduceTime);

        for(StackPane joker:jokers){
            if(gameCtrl.getUsedJokers().contains(joker.idProperty().getValue())){
                gameCtrl.disableJokerButton(joker);
            }
        }

        gameCtrl.setAnsweredQuestion ( false );

        super.setup(question, gameCtrl.getUser().points);

        resetAnswerClickability();
        answerTopDisable = false;
        answerMidDisable = false;
        answerBotDisable = false;
        enableEmojis();
        doublePointsImage.setVisible(false);

    }

    /**
     * Saves the answer selected last by the user, as well as the amount of time it took.
     * Changes the scene visuals accordingly.
     *
     * @param answerButton The answer button pressed.
     * @param answer       The answer corresponding to the answer button.
     */
    protected void onAnswerClicked(StackPane answerButton, Answer answer) {
        gameCtrl.setAnsweredQuestion (true);
        super.onAnswerClicked(answerButton, answer);
    }

    /**
     * The method called when the cursor enters the button answerTop. Checks if the answer is disabled by the joker.
     * Sets answerTop's background color according to whether it is selected.
     */
    @FXML
    protected void enterAnswerTop() {
        if(!answerTopDisable) {
            enterAnswer(answerTop);
        }
    }

    /**
     * The method called when the cursor enters the button answerMid. Checks if the answer is disabled by the joker.
     * Sets answerMid's background color according to whether it is selected.
     */
    @FXML
    protected void enterAnswerMid() {
        if(!answerMidDisable) {
            enterAnswer(answerMid);
        }
    }

    /**
     * The method called when the cursor enters the button answerBot. Checks if the answer is disabled by the joker.
     * Sets answerBot's background color according to whether it is selected.
     */
    @FXML
    protected void enterAnswerBot() {
        if(!answerBotDisable) {
            enterAnswer(answerBot);
        }
    }

    /**
     * The method called when the cursor exits the button answerTop. Checks if the answer is disabled by the joker.
     * Sets answerTop's background color according to whether it is selected.
     */
    @FXML
    protected void exitAnswerTop() {
        if(!answerTopDisable) {
            resetAnswerColors(answerTop);
        }
    }

    /**
     * The method called when the cursor exits the button answerMid. Checks if the button is disabled by the joker.
     * Sets answerMid's background color according to whether it is selected.
     */
    @FXML
    protected void exitAnswerMid() {
        if(!answerMidDisable) {
            resetAnswerColors(answerMid);
        }
    }

    /**
     * The method called when the cursor exits the button answerBot. Checks if the answer is disabled by the joker.
     * Sets answerBot's background color according to whether it is selected.
     */
    @FXML
    protected void exitAnswerBot() {
        if(!answerBotDisable) {
            resetAnswerColors(answerBot);
        }
    }

    /**
     * The method called when the cursor enters the button double points.
     * Sets double points' background color according to whether it is selected.
     */
    @FXML
    protected void enterDoublePoints(){
        enterJoker(doublePoints);
    }

    /**
     * The method called when the cursor enters the button remove incorrect question.
     * Sets remove incorrect question's background color according to whether it is selected.
     */
    @FXML
    protected void enterRemoveIncorrect(){
        enterJoker(removeIncorrect);
    }

    /**
     * The method called when the cursor enters the button reduce time for others.
     * Sets reduce time for others' background color according to whether it is selected.
     */
    @FXML
    protected void enterReduceTime(){
        enterJoker(reduceTime);
    }



    /**
     * The method is called upon loading the question scene, to make sure each answer button is clickable.
     * Else an answer is disabled after a user uses the Remove Incorrect Answer Joker.
     */
    private void resetAnswerClickability(){
        for(StackPane answerBtn : answerButtons){
            answerBtn.setDisable(false);
        }
    }

    /**
     * A general method for setting a joker button's background color upon the cursor enters it,
     * according to whether it is already used.
     *
     * @param jokerBtn The joker button to be recolored.
     */
    private void enterJoker(StackPane jokerBtn) {
        if (!gameCtrl.getUsedJokers().contains(jokerBtn.idProperty().getValue())) {
            jokerBtn.setBackground(new Background(
                    new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

    /**
     * The method called upon loading the question scene, and when the cursor leaves either one of the joker buttons.
     * Resets all joker buttons' background color according to whether they are already used.
     */
    @FXML
    public void resetJokerColors() {
        for (StackPane joker : jokers) {
            if (!gameCtrl.getUsedJokers().contains(joker.idProperty().getValue())) {
                joker.setBackground(new Background(
                        new BackgroundFill(Color.color(gameCtrl.RGB_VALUE,gameCtrl.RGB_VALUE,gameCtrl.RGB_VALUE),
                                CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }
    }

    /**
     * This method is called when the double points joker is clicked.
     * It gives double points for the current question if the answer is correct.
     */
    @FXML
    public void useDoublePoints(){
        gameCtrl.setIsActiveDoublePoints(true);
        gameCtrl.useJoker(doublePoints,doublePointsImage);
    }

    /**
     * This method resets the double point jokers so that it can be used again when another game starts
     */
    public void resetDoublePoints(){
        doublePoints.setOnMouseClicked(event -> useDoublePoints());
        gameCtrl.enableJoker(doublePoints);
    }

    /**
     * This method is called when the remove incorrect answer joker is clicked.
     * It removes a randomly selected incorrect answer from the multiple choice questions.
     * Disables the selected incorrect answer's stackPane.
     */
    public void useRemoveIncorrect(){
        gameCtrl.setIsActiveRemoveIncorrect(true);
        gameCtrl.useJoker(removeIncorrect, removeIncorrectImage);
        switch (currentQuestion.getType()) {
            case CONSUMPTION: {
                List<Long> incorrectAnswers = new ArrayList<>();
                incorrectAnswers.add(answerTopAnswer.getLongAnswer());
                incorrectAnswers.add(answerMidAnswer.getLongAnswer());
                incorrectAnswers.add(answerBotAnswer.getLongAnswer());
                for(Long answer:incorrectAnswers){
                    if(answer==((ConsumptionQuestion)currentQuestion).getUserAnswer().getLongAnswer()){
                        incorrectAnswers.remove(answer);
                    }
                };
                if (answerTopAnswer.getLongAnswer() == incorrectAnswers.get(0)) {
                    answerTop.setDisable(true);
                    answerTop.setBackground(new Background(
                            new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    answerTopDisable = true;

                }
                if (answerBotAnswer.getLongAnswer() == incorrectAnswers.get(0)) {
                    answerBot.setDisable(true);
                    answerBot.setBackground(new Background(
                            new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    answerBotDisable = true;
                }
                if (answerMidAnswer.getLongAnswer() == incorrectAnswers.get(0)) {
                    answerMid.setDisable(true);
                    answerMid.setBackground(new Background(
                            new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    answerMidDisable = true;

                }
                break;
            }
            case COMPARISON: {
                List<CompareType> incorrectAnswers = ((ComparisonQuestion) currentQuestion).getincorrectAnswers();
                if(answerTopAnswer.getCompareType() == incorrectAnswers.get(0)){
                    answerTop.setDisable(true);
                    answerTop.setBackground(new Background(
                            new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    answerTopDisable = true;
                }
                if(answerBotAnswer.getCompareType() == incorrectAnswers.get(0)){
                    answerBot.setDisable(true);
                    answerBot.setBackground(new Background(
                            new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    answerBotDisable = true;
                }
                if(answerMidAnswer.getCompareType() == incorrectAnswers.get(0)){
                    answerMid.setDisable(true);
                    answerMid.setBackground(new Background(
                            new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    answerMidDisable = true;
                }
                break;
            }
            case CHOICE: {
                List<Activity> incorrectAnswers = ((ChoiceQuestion) currentQuestion).getIncorrectActivities();
                if(answerTopAnswer.getActivity() == incorrectAnswers.get(0)){
                    answerTop.setDisable(true);
                    answerTop.setBackground(new Background(
                            new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    answerTopDisable = true;
                }
                if(answerBotAnswer.getActivity() == incorrectAnswers.get(0)){
                    answerBot.setDisable(true);
                    answerBot.setBackground(new Background(
                            new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    answerBotDisable = true;
                }
                if(answerMidAnswer.getActivity() == incorrectAnswers.get(0)){
                    answerMid.setDisable(true);
                    answerMid.setBackground(new Background(
                            new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    answerMidDisable = true;
                }

                break;
            }
        }

    }

    /**
     * This method resets the remove incorrect answer jokers so that it can be used when
     * another game starts.
     */
    public void resetRemoveIncorrect(){
        removeIncorrect.setOnMouseClicked(event -> useRemoveIncorrect());
        gameCtrl.enableJoker(removeIncorrect);
    }

    /**
     * Initiates the timer countdown and animation
     */
    public void startTimer() {
        mainCtrl.startTimer(countdownCircle, this);
    }

    /**
     * Disables all interaction with the answer buttons.
     */
    public void disableAnswers() {
        answerTop.setOnMouseEntered(null);
        answerMid.setOnMouseEntered(null);
        answerBot.setOnMouseEntered(null);
        answerTop.setOnMouseClicked(null);
        answerMid.setOnMouseClicked(null);
        answerBot.setOnMouseClicked(null);
    }

    public void enableAnswers() {
        answerTop.setOnMouseClicked(event -> onAnswerTopClicked());
        answerMid.setOnMouseClicked(event -> onAnswerMidClicked());
        answerBot.setOnMouseClicked(event -> onAnswerBotClicked());
    }

    @Override
    public void redirect() {
        disableAnswers();
        disableEmojis();
        MultiplayerUser user = gameCtrl.getUser();
        if (!gameCtrl.getAnsweredQuestion()) {
            user.unansweredQuestions++;
            if (user.unansweredQuestions == KICK_AT_X_QUESTIONS) {
                try {
                    server.removeMultiplayerUser(server.getURL(), user);
                } catch(WebApplicationException e) {
                    System.out.println("User to remove not found!");
                }

                mainCtrl.killThread();

                if(server.getSession() != null && server.getSession().isConnected()) {
                    gameCtrl.unregisterForEmojis();
                    server.getSession().disconnect();
                }
                gameCtrl.hideEmojis();
                mainCtrl.showHome();
                mainCtrl.bindUser(null);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle ("Kicked :(");
                alert.setHeaderText(null);
                alert.setGraphic(null);
                alert.setContentText("You've been kicked for not answering 3 question in a row!");
                alert.show();

                return;
            }
        } else {
            user.unansweredQuestions = 0;
        }

        gameCtrl.setAnsweredQuestion(false);
        gameCtrl.postAnswer(currentQuestion);
    }

    @Override
    public void onQuit() {
        mainCtrl.quitGame(false, true);
        mainCtrl.bindUser(null);
    }

    /**
     * Highlights current question so the user is aware which circle corresponds to his current question
     */
    public void highlightCurrentCircle() {
        super.highlightCurrentCircle(gameCtrl.getAnswerCount());
    }

    /**
     * Updates the question number on the top of the screen.
     */
    @Override
    public void updateQuestionNumber() {
        questionNum.setText("" + (gameCtrl.getAnswerCount() + 1));
    }


    /**
     * Send emojis to the server on emoji click
     */
    public void enableEmojis() {
        emojiPane.getChildren().forEach(n -> {
            if(n instanceof ImageView) {
                ImageView e = (ImageView) n;
                e.setOnMouseClicked(event -> gameCtrl.sendEmoji(e));
                e.setCursor(Cursor.HAND);

                String[] parts = e.getImage().getUrl().split("/");
                String emojiPath = String.valueOf(ServerUtils.class.getClassLoader().getResource(""));
                emojiPath = emojiPath.substring(
                        "file:/".length(), emojiPath.length() - "classes/java/main/".length())
                        + "resources/main/client/images/" + parts[parts.length - 1];

                e.setImage(new Image(emojiPath));
            }
        });
    }
    /**
     * Disable emoji clicks
     */
    public void disableEmojis() {
        emojiPane.getChildren().forEach(n -> {
            if(n instanceof ImageView) {
                ImageView e = (ImageView) n;
                e.setOnMouseClicked(null);
            }
        });
    }

    /**
     * Visualise emoji on the screen
     * @param emoji the emoji to visualise
     */
    @Override
    public void displayEmoji(Emoji emoji) {
        String emojiPath = String.valueOf(ServerUtils.class.getClassLoader().getResource(""));
        emojiPath = emojiPath.substring(
                "file:/".length(), emojiPath.length() - "classes/java/main/".length())
                + "resources/main/client/images/" + emoji.getImageName();
        emojiImage.setImage(new Image(emojiPath));
        emojiText.setText(emoji.getUsername());
    }

    /**
     * Removes the emoji from the image view
     */
    @Override
    public void hideEmoji() {
        emojiImage.setImage(null);
        emojiText.setText("");
    }

    /**
     * Sets the current game controller
     * @param gameCtrl the current game controller
     */
    public void setGameCtrl(MultiplayerGameCtrl gameCtrl) {
        this.gameCtrl = gameCtrl;
    }

    }
