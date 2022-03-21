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
package client.utils;

import commons.entities.MultiplayerUser;
import commons.entities.Quote;
import commons.entities.SoloUser;
import commons.entities.User;
import commons.models.ChoiceQuestion;
import commons.models.ComparisonQuestion;
import commons.models.ConsumptionQuestion;
import commons.models.EstimationQuestion;
import commons.models.Question;
import commons.models.SoloGame;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.ClientConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {

    private static final String SERVER = "http://localhost:8080/";
    private static final long MAGICNUMBER = 42;
    private static final int QUESTIONS_PER_GAME = 20;

    public void getQuotesTheHardWay() throws IOException {
        var url = new URL("http://localhost:8080/api/quotes");
        var is = url.openConnection().getInputStream();
        var br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }
    public String getURL(){
        return SERVER;
    }
    public List<Quote> getQuotes() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Quote>>() {});
    }

    public Quote addQuote(Quote quote) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
    }

    public List<MultiplayerUser> getUsers(String serverUrl) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path("api/users")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<MultiplayerUser>>() {});
    }

    /**
     * Starts a game on the server and returns the index
     * of the game object
     * @param serverUrl the server to start a game on
     * @return the index of the game object
     */
    public Integer startGame(String serverUrl) {
        String path = String.format("/api/games/start/%d", QUESTIONS_PER_GAME);
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Integer.class);
    }

    public MultiplayerUser addUserMultiplayer(String serverUrl, MultiplayerUser user) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path("api/users")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(user, APPLICATION_JSON), MultiplayerUser.class);
    }

    /**
     * Returns the index of the game a user participates in
     * @param serverUrl the server URL
     * @param userId the user id
     * @return the index of the game
     */
    public Integer findGameIndex(String serverUrl, long userId) {
        String path = String.format("/api/games/find/%d", userId);
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Integer.class);
    }

    /**
     * A getter for a given question
     * @param serverUrl the server url
     * @param gameIndex the game index
     * @param questionIndex the index of the question inside the game
     * @return a question
     */
    public Question getQuestion(String serverUrl, int gameIndex, int questionIndex) {
        String questionType = getQuestionType(serverUrl, gameIndex, questionIndex);

        switch(questionType) {
            case "CONSUMPTION":
                return getConsumptionQuestion(serverUrl, gameIndex, questionIndex);
            case "ESTIMATION":
                return getEstimationQuestion(serverUrl, gameIndex, questionIndex);
            case "CHOICE":
                return getChoiceQuestion(serverUrl, gameIndex, questionIndex);
            case "COMPARISON":
                return getComparisonQuestion(serverUrl, gameIndex, questionIndex);
            default:
                return null;
        }
    }

    /**
     * A getter for the type of a given question
     * @param serverUrl the server url
     * @param gameIndex the game index
     * @param questionIndex the index of the question inside the game
     * @return a question
     */
    public String getQuestionType(String serverUrl, int gameIndex, int questionIndex) {
        String path = String.format("/api/games/%d/questionType/%d", gameIndex, questionIndex);
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(String.class);
    }

    /**
     * A getter for a given consumption question
     * @param serverUrl the server url
     * @param gameIndex the game index
     * @param questionIndex the index of the question inside the game
     * @return a question
     */
    public ConsumptionQuestion getConsumptionQuestion(String serverUrl, int gameIndex, int questionIndex) {
        String path = String.format("/api/games/%d/consumption/%d", gameIndex, questionIndex);
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(ConsumptionQuestion.class);
    }

    /**
     * A getter for a given estimation question
     * @param serverUrl the server url
     * @param gameIndex the game index
     * @param questionIndex the index of the question inside the game
     * @return a question
     */
    public EstimationQuestion getEstimationQuestion(String serverUrl, int gameIndex, int questionIndex) {
        String path = String.format("/api/games/%d/estimation/%d", gameIndex, questionIndex);
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(EstimationQuestion.class);
    }

    /**
     * A getter for a given choice question
     * @param serverUrl the server url
     * @param gameIndex the game index
     * @param questionIndex the index of the question inside the game
     * @return a question
     */
    public ChoiceQuestion getChoiceQuestion(String serverUrl, int gameIndex, int questionIndex) {
        String path = String.format("/api/games/%d/choice/%d", gameIndex, questionIndex);
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(ChoiceQuestion.class);
    }

    /**
     * A getter for a given comparison question
     * @param serverUrl the server url
     * @param gameIndex the game index
     * @param questionIndex the index of the question inside the game
     * @return a question
     */
    public ComparisonQuestion getComparisonQuestion(String serverUrl, int gameIndex, int questionIndex) {
        String path = String.format("/api/games/%d/comparison/%d", gameIndex, questionIndex);
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(ComparisonQuestion.class);
    }

    /**
     * Adds a user to the user repository for solo games.
     * @param serverUrl The server URL of the game the user is in.
     * @param user The user that has to be saved in the repository.
     * @return The user that has been saved in the repository.
     */
    public SoloUser addUserSolo(String serverUrl, SoloUser user) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path("api/users/solo")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(user, APPLICATION_JSON), SoloUser.class);
    }

    /**
     * A method that removes a multiplayer user from the repository
     * @param serverUrl
     * @param user
     * @return the user that was removed
     */
    public MultiplayerUser removeMultiplayerUser(String serverUrl, User user) {
        MultiplayerUser mu = (MultiplayerUser) user;
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path("api/users/"+mu.id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(MultiplayerUser.class);
    }

    public List<MultiplayerUser> answerQuestion(String serverUrl, int gameIndex,
                                                long userId, int questionIndex, Question question) {
        String path = String.format(
                "api/games/%d/user/%d/%s/%d",
                gameIndex,
                userId,
                question.getType().name().toLowerCase(Locale.ROOT),
                questionIndex
        );

        System.out.println("Posting on " + path);

        switch(question.getType()) {
            case CONSUMPTION:
                return postConsumptionAnswer(serverUrl, path, (ConsumptionQuestion) question);
            case ESTIMATION:
                return postEstimationAnswer(serverUrl, path, (EstimationQuestion) question);
            case CHOICE:
                return postChoiceQuestion(serverUrl, path, (ChoiceQuestion) question);
            case COMPARISON:
                return postComparisonQuestion(serverUrl, path, (ComparisonQuestion) question);
            default:
                return new ArrayList<>();
        }
    }

    private List<MultiplayerUser> postConsumptionAnswer(String serverUrl, String path,
                                                        ConsumptionQuestion answeredQuestion) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(answeredQuestion, APPLICATION_JSON), new GenericType<List<MultiplayerUser>>() {});
    }

    private List<MultiplayerUser> postEstimationAnswer(String serverUrl, String path,
                                                        EstimationQuestion answeredQuestion) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(answeredQuestion, APPLICATION_JSON), new GenericType<List<MultiplayerUser>>() {});
    }

    private List<MultiplayerUser> postChoiceQuestion(String serverUrl, String path,
                                                        ChoiceQuestion answeredQuestion) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(answeredQuestion, APPLICATION_JSON), new GenericType<List<MultiplayerUser>>() {});
    }

    private List<MultiplayerUser> postComparisonQuestion(String serverUrl, String path,
                                                        ComparisonQuestion answeredQuestion) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(answeredQuestion, APPLICATION_JSON), new GenericType<List<MultiplayerUser>>() {});
    }

    /**
     * Returns a new (solo) game instance with the given number of questions
     * @param serverUrl the server url
     * @param count the number of questions
     * @return a new (solo) game instance
     */
    public SoloGame getSoloGame(String serverUrl, int count) {

        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path("api/games/startSolo/" + count)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(SoloGame.class);
    }

    /**
     * Returns an arraylist of solo users with their corresponding scores in descending order
     * @param serverUrl
     * @return Arraylist of solo users
     */
    public List<SoloUser> getAllUsersByScore(String serverUrl){
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path("api/users/solo/leaderboard")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<SoloUser>>() {});
    }
}