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
package client;

import static com.google.inject.Guice.createInjector;

import java.io.IOException;
import java.net.URISyntaxException;

import client.scenes.AddActivityCtrl;
import client.scenes.AdminPanelCtrl;
import client.scenes.DeleteActivityCtrl;
import client.scenes.HelpCtrl;
import client.scenes.HomeCtrl;
import client.scenes.MultiplayerResultsCtrl;
import client.scenes.MultiplayerQuestionCtrl;
import client.scenes.MultiplayerAnswerCtrl;
import client.scenes.SoloAnswerCtrl;
import client.scenes.SoloEstimationQuestionCtrl;
import client.scenes.SoloQuestionCtrl;
import client.scenes.SoloResultsCtrl;
import client.scenes.RankingCtrl;
import client.scenes.MultiplayerEstimationQuestionCtrl;
import client.scenes.MainCtrl;
import client.scenes.WaitingCtrl;
import client.utils.ResourceUtils;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private static final int LABEL_SIZE = 36;

    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        Font.loadFont(
                ResourceUtils.getClientResource("fonts/ARCADECLASSIC.TTF").getPath(),
                LABEL_SIZE
        );

        var adminPanel = FXML.load(
                AdminPanelCtrl.class, "client", "scenes", "AdminPanel.fxml");

        var addActivity = FXML.load (
                AddActivityCtrl.class, "client", "scenes", "AddActivity.fxml");

        var deleteActivity = FXML.load (
                DeleteActivityCtrl.class, "client", "scenes", "DeleteActivity.fxml");

        var multiplayerAnswer = FXML.load(
                MultiplayerAnswerCtrl.class, "client", "scenes", "MultiplayerAnswer.fxml");

        var home = FXML.load(HomeCtrl.class, "client", "scenes", "Home.fxml");

        var multiplayerQuestion = FXML.load(
                MultiplayerQuestionCtrl.class, "client", "scenes", "MultiplayerQuestion.fxml");

        var waiting = FXML.load(WaitingCtrl.class, "client", "scenes", "Waiting.fxml");

        var ranking = FXML.load(RankingCtrl.class, "client", "scenes", "Ranking.fxml");

        var multiplayerEstimation = FXML.load(
                MultiplayerEstimationQuestionCtrl.class, "client", "scenes", "MultiplayerEstimation.fxml");

        var soloEstimation = FXML.load(
                SoloEstimationQuestionCtrl.class, "client", "scenes", "SoloEstimation.fxml");

        var soloQuestion = FXML.load(
                SoloQuestionCtrl.class, "client", "scenes", "SoloQuestion.fxml");

        var soloAnswer = FXML.load(
                SoloAnswerCtrl.class, "client", "scenes", "SoloAnswer.fxml");

        var soloResults = FXML.load(
                SoloResultsCtrl.class, "client", "scenes", "SoloResults.fxml");

        var multiplayerResults = FXML.load(
                MultiplayerResultsCtrl.class, "client", "scenes", "MultiplayerResults.fxml");

        var help = FXML.load(HelpCtrl.class, "client", "scenes", "Help.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(primaryStage, home, waiting, multiplayerQuestion,
                multiplayerAnswer, ranking, multiplayerEstimation, soloEstimation, soloQuestion,
                soloAnswer, soloResults, multiplayerResults, adminPanel, addActivity, deleteActivity, help );
    }
}