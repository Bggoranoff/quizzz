package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.MultiplayerUser;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.ImageCursor;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class MultiplayerResultsCtrl extends AbstractRankingCtrl {

    private boolean rematch;

    @FXML
    private Button rematchButton;
    @FXML
    private ProgressIndicator countdownCircle;

    @FXML
    private TableView<MultiplayerUser> scoreTable;
    @FXML
    private TableColumn<MultiplayerUser, String> usernameColumn;
    @FXML
    private TableColumn<MultiplayerUser, Long> pointsColumn;

    /**
     * Creates a controller for the multiplayer results page screen, with the given server and mainCtrl parameters.
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public MultiplayerResultsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Setups the page quit button that redirects to the main page, and fills in the score and personal best
     * Populates the ranking table with the users in the game according
     * to their points
     * @param users the users to populate
     */
    public void setup(List<MultiplayerUser> users) {

        resetRematchButton();

        enableRematchButton();
        scoreTableUserName.setText(String.format("%s", gameCtrl.getUser().username));
        scoreTableUserScore.setText(String.format("%d", gameCtrl.getUser().points));

        ranking1stPlayer.setText(users.size() > 0 ? users.get(0).username : "");
        ranking2ndPlayer.setText(users.size() > 1 ? users.get(1).username : "");
        ranking3rdPlayer.setText(users.size() > 2 ? users.get(2).username : "");

        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));

        gameCtrl.populateRanking(scoreTable, users);
        scoreTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * This method resets the rematch button to look unselected
     */
    public void resetRematchButton(){
        rematchButton.setStyle(
                "    -fx-font-family: ARCADECLASSIC;\n" +
                        "    -fx-background-size: stretch;\n" +
                        "    -fx-background-radius: 30;\n" +
                        "    -fx-background-color: #D6EAF8;\n" +
                        "    -fx-background-insets: 0,1,2,3,0;\n" +
                        "    -fx-font-size: 36;\n" +
                        "    -fx-min-width: 128;\n" +
                        "    -fx-padding: 10 20 10 20;");
    }

    /**
     * Sets the current game controller
     *
     * @param gameCtrl the current game controller
     */
    public void setGameCtrl(MultiplayerGameCtrl gameCtrl) {
        this.gameCtrl = gameCtrl;
    }

    /**
     * Indicates that the player wants (or doesn't want) to rematch the players from the last game.
     */
    @FXML
    protected void onRematchButton() {
        rematch = !rematch;
        if (rematch) {
            server.addRestartUserID(gameCtrl.getServerUrl(), gameCtrl.getGameIndex(), gameCtrl.getUser().id);
            rematchButton.setStyle("-fx-background-radius: 30;\n" +
                    "    -fx-background-insets: 0,1,2,3,0;\n" +
                    "    -fx-padding: 10 20 10 20;\n" +
                    "    -fx-background-color: #5e8f7b;\n" +
                    "    -fx-scale-y: -1;");
        } else {
            server.removeRestartUserID(gameCtrl.getServerUrl(), gameCtrl.getGameIndex(), gameCtrl.getUser().id);
            resetRematchButton();
        }
    }

    /**
     * Redirects the user to the home page when the quit button is clicked.
     */
    @Override
    @FXML
    public void onQuit() {
        if (rematch) {
            rematch = false;
            server.removeRestartUserID(gameCtrl.getServerUrl(), gameCtrl.getGameIndex(), gameCtrl.getUser().id);
        }
        mainCtrl.quitGame(false, true);
    }

    /**
     * Redirects the user to the question page if the user clicked rematch
     * and after a new game has started, otherwise, the user is redirected to the home page.
     */
    @Override
    public void redirect() {
        if (rematch) {
            gameCtrl.resetGameCtrl();
            mainCtrl.resetStreak();
            mainCtrl.setStreakScore(0L);
            rematch = false;
            String serverUrl = mainCtrl.getServerUrl();
            gameCtrl.showQuestion(server.restartGame(serverUrl, gameCtrl.getGameIndex(),
                    gameCtrl.getUser().id));
        } else {
            disableRematchButton();
        }
    }

    /**
     * Disables the rematch button.
     */
    public void disableRematchButton() {
        rematchButton.setOnAction(null);
        rematchButton.setDisable(true);
    }

    /**
     * Enables the rematch button.
     */
    public void enableRematchButton() {
        rematchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                onRematchButton();
            }
        });
        rematchButton.setDisable(false);
    }

    /**
     * A general method for setting a joker button's background color upon the cursor enters it,
     * according to whether it is already used.
     */
    public void enterRematch() {
        if(!rematch){
            rematchButton.setStyle("-fx-background-radius: 30;\n" +
                    "    -fx-background-insets: 0,1,2,3,0;\n" +
                    "    -fx-padding: 10 20 10 20;\n" +
                    "    -fx-background-color: #85C1E9;\n" +
                    "    -fx-scale-y: -1;");
        }
    }

    public void exitRematch(){
        if(!rematch){
            resetRematchButton();
        }
    }
    /**
     * Initiates the timer countdown and animation
     */
    public void startTimer() {
        mainCtrl.startTimer(countdownCircle, this);
    }

    /**
     * Updates the number of the current question
     */
    @Override
    public void updateQuestionNumber() {
        questionNum.setText("" + gameCtrl.getAnswerCount());
    }

    /**
     * Highlights current question so the user is aware which circle corresponds to his current question
     */
    @Override
    public void highlightCurrentCircle() {
        highlightCurrentCircle(gameCtrl.getAnswerCount());
    }

    /**
     * Sets the hover cursors to all buttons to hand
     */
    @Override
    public void setupHoverCursor() {
        quitButton.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));
        rematchButton.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));
    }
}
