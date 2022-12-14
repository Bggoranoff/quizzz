package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public abstract class AbstractRankingCtrl extends QuestionNumController{
    protected final ServerUtils server;

    protected MultiplayerGameCtrl gameCtrl;

    @FXML
    protected Text scoreTableUserName;
    @FXML
    protected Text scoreTableUserScore;
    @FXML
    protected Text personalBest;

    @FXML
    protected Button quitButton;

    @FXML
    protected Text ranking1stPlayer;
    @FXML
    protected Text ranking2ndPlayer;
    @FXML
    protected Text ranking3rdPlayer;

    private static final double TIMER_LENGTH = 10.0;

    protected AbstractRankingCtrl(ServerUtils server, MainCtrl mainCtrl){
        super(mainCtrl);
        this.server = server;
    }

    /**
     * Sets the game controller.
     * @param gameCtrl
     */
    public void setGameCtrl(MultiplayerGameCtrl gameCtrl) {
        this.gameCtrl = gameCtrl;
    }

    /**
     * Returns the length of the timer in this scene
     * @return the length of the timer in this scene
     */
    @Override
    public double getTimerLength(){
        return TIMER_LENGTH;
    }
}
