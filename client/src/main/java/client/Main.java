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

import client.scenes.AddQuoteCtrl;
import client.scenes.HomeCtrl;
import client.scenes.MultiplayerAnswerCtrl;
import client.scenes.QuoteOverviewCtrl;
import client.scenes.MultiplayerQuestionCtrl;
import client.scenes.WaitingCtrl;
import client.scenes.RankingCtrl;
import client.scenes.SoloResultsCtrl;
import client.scenes.EstimationQuestionCtrl;
import client.scenes.MainCtrl;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        var overview = FXML.load(QuoteOverviewCtrl.class, "client", "scenes", "QuoteOverview.fxml");
        var add = FXML.load(AddQuoteCtrl.class, "client", "scenes", "AddQuote.fxml");
        var answerPage = FXML.load(MultiplayerAnswerCtrl.class, "client", "scenes", "MultiplayerAnswer.fxml");

        var home = FXML.load(HomeCtrl.class, "client", "scenes", "Home.fxml");
        var question = FXML.load(MultiplayerQuestionCtrl.class, "client", "scenes", "MultiplayerQuestion.fxml");

        var waiting = FXML.load(WaitingCtrl.class, "client", "scenes", "Waiting.fxml");
        var ranking = FXML.load(RankingCtrl.class, "client", "scenes", "Ranking.fxml");

        var soloResults = FXML.load(SoloResultsCtrl.class, "client", "scenes", "SoloResults.fxml");

        var estimation = FXML.load(EstimationQuestionCtrl.class, "client", "scenes", "Estimation.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(primaryStage, overview, add, home, waiting, question, answerPage,
                ranking, soloResults, estimation);
    }
}