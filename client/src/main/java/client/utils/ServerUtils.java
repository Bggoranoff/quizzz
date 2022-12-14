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

import commons.entities.Activity;
import commons.entities.MultiplayerUser;
import commons.entities.SoloUser;
import commons.models.Question;
import commons.models.SoloGame;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import javafx.scene.image.Image;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {
    private static final int QUESTIONS_PER_GAME = 20;

    private StompSession session;

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

    /**
     * Restarts the game on the server and returns the index
     * of the game object.
     * @param serverUrl The server to start a game on.
     * @param gameIndex The index of the current game.
     * @param userId The ID of the user that wants a rematch.
     * @return The first question of the new game.
     */
    public Question restartGame(String serverUrl, Integer gameIndex, Long userId) {
        String path = String.format("/api/games/restart/%d/%d/%d", gameIndex,  QUESTIONS_PER_GAME, userId);
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Question.class);
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
        String path = String.format("/api/games/%d/question/%d", gameIndex, questionIndex);
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Question.class);
    }

    /**
     * Adds a new activity to the repo
     *
     * @param serverUrl the current server
     * @param activity the activity to be added
     * @return the added activity
     */

    public Activity addActivityToRepo ( String serverUrl, Activity activity ) {
        String path = "/api/activities";
        return ClientBuilder.newClient ( new ClientConfig() )
                .target(serverUrl).path(path)
                .request( APPLICATION_JSON )
                .accept ( APPLICATION_JSON )
                .post ( Entity.entity ( activity, APPLICATION_JSON ), Activity.class );
    }

    /**
     * Finds and returns an activity based on the id
     *
     * @param serverUrl the server of the game
     * @param id the id of the actvity
     * @return the desired activity
     */

    public Activity findActivityByID ( String serverUrl, int id ) {
        String path = String.format ( "/api/activities/%d", id );
        return ClientBuilder.newClient ( new ClientConfig() )
                .target ( serverUrl ).path ( path )
                .request ( APPLICATION_JSON )
                .accept ( APPLICATION_JSON )
                .get ( Activity.class );
    }

    /**
     * Returns a list of all activities
     *
     * @param serverUrl the server url of the game the user is in
     * @return a list of activities
     */

    public List<Activity> getActivities ( String serverUrl ) {
        String path = "/api/activities";
        return ClientBuilder.newClient ( new ClientConfig() )
                .target ( serverUrl ).path( path )
                .request( APPLICATION_JSON )
                .accept( APPLICATION_JSON )
                .get( new GenericType<List<Activity>>() {} );
    }

    /**
     * Deletes an activity from the repo
     *
     * @param serverUrl the server url
     * @param activity the activity to be deleted
     * @return the deleted activity
     */
    public Activity deleteActivityFromRepo ( String serverUrl, Activity activity ) {
        return ClientBuilder.newClient ( new ClientConfig() )
                .target(serverUrl).path("/api/activities/" + activity.id )
                .request( APPLICATION_JSON )
                .accept ( APPLICATION_JSON )
                .delete ( Activity.class );
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
     * @param gameIndex should be -1 if the user is not in a game
     * @param user
     * @return The user that has been deleted.
     */
    public MultiplayerUser removeMultiplayerUser(String serverUrl, int gameIndex, MultiplayerUser user) {
        if(gameIndex != -1){
            removeMultiplayerUserFromGame(serverUrl, gameIndex, user.id);
        }

        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path("api/users/"+user.id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(MultiplayerUser.class);
    }

    /**
     * A method that posts the answer of the user in the repository
     * @param serverUrl
     * @param gameIndex
     * @param userId
     * @param questionIndex
     * @param question
     * @param streakPoints
     * @return list of the users who got the question right
     */
    public List<MultiplayerUser> answerQuestion(String serverUrl, int gameIndex,
                                                long userId, int questionIndex, Question question,Long streakPoints) {
        String path = String.format(
                "api/games/%d/user/%d/question/%d/%d",
                gameIndex,
                userId,
                questionIndex,
                streakPoints
        );

        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(question, APPLICATION_JSON), new GenericType<List<MultiplayerUser>>() {});
    }

    /**
     * A method that posts the answer of the user in the repository when the double points joker is activated
     * @param serverUrl
     * @param gameIndex
     * @param userId
     * @param questionIndex
     * @param question
     * @param streakPoints
     * @return list of the users who got the question right
     */
    public List<MultiplayerUser> answerDoublePointsQuestion(String serverUrl, int gameIndex,
                                                long userId, int questionIndex, Question question, Long streakPoints) {
        String path = String.format(
                "api/games/%d/user/%d/question/%d/%d/doublePoints",
                gameIndex,
                userId,
                questionIndex,
                streakPoints
        );
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(question, APPLICATION_JSON), new GenericType<List<MultiplayerUser>>() {});
    }

    /**
     * Removes an ID from a player from the list of ID's of the users that are in a game.
     * @param serverUrl The URL of the server.
     * @param gameIndex The index of the game from where the user should be removed.
     * @param userId The ID of the user that should be removed.
     * @return A list with all ID's of the users that are still left in the game.
     */
    public List<Long> removeMultiplayerUserFromGame(String serverUrl, int gameIndex, Long userId) {
        String path = String.format("/api/games/%d/%d", gameIndex, userId);
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(List.class);
    }

    /**
     * Removes an ID from a player from the list of ID's of the users that want to have a rematch.
     * @param serverUrl The URL of the server.
     * @param gameIndex The index of the game from where the user should be removed.
     * @param userId The ID of the user that should be removed.
     * @return A list with all ID's of the users that want to have a rematch.
     */
    public List<Long> removeRestartUserID(String serverUrl, int gameIndex, Long userId) {
        String path = String.format("/api/games/restart/%d/%d", gameIndex, userId);
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(List.class);
    }

    /**
     * Adds an ID from a player to the list of ID's of the users that want to have a rematch.
     * @param serverUrl The URL of the server.
     * @param gameIndex The index of the game to where the user should be added.
     * @param userId The ID of the user that should be added.
     * @return A list with all ID's of the users that want to have a rematch.
     */
    public List<Long> addRestartUserID(String serverUrl, int gameIndex, Long userId) {
        String path = String.format("/api/games/restart/%d/%d", gameIndex, userId);
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(userId, APPLICATION_JSON), List.class);
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

    /**
     * Returns a list of sorted users by points
     * @param serverUrl the server url
     * @param gameIndex the game the users belong to
     * @return the list of sorted users
     */
    public List<MultiplayerUser> getRanking(String serverUrl, int gameIndex) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path("api/games/" + gameIndex + "/ranking")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<MultiplayerUser>>() {});
    }

    /**
     * Initiates the websocket connection with the server
     * and sets the current session
     * @param httpUrl the url of the http server to connect to
     */
    public void connect(String httpUrl) {
        String websocketUrl = httpUrl.replace("http", "ws");

        if(websocketUrl.charAt(websocketUrl.length() - 1) != '/') {
            websocketUrl += "/";
        }
        websocketUrl += "websocket";

        var client = new StandardWebSocketClient();
        var stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());

        try {
            session = stomp.connect(websocketUrl, new StompSessionHandlerAdapter() {}).get();
            return;
        } catch(ExecutionException ex) {
            throw new RuntimeException(ex);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        throw new IllegalStateException();
    }

    /**
     * Sends an object to the server
     * @param dest the destination to send to
     * @param o the object to send
     */
    public void send(String dest, Object o) {
        session.send(dest, o);
    }

    /**
     * Registers for websocket messages from the server
     * to the client
     * @param dest the destination url of the server to register to
     * @param type the type of the payload to expect from the server
     * @param consumer the consumer that handles the received payload
     * @param <T> the type of the payload to expect from the server
     * @return the created subscription
     */
    public <T> StompSession.Subscription registerForMessages(String dest, Class<T> type, Consumer<T> consumer) {
        //noinspection NullableProblems
        return session.subscribe(dest, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return type;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                consumer.accept((T) payload);
            }
        });
    }

    public Image fetchImage(String serverUrl, String path) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            String fetched = ClientBuilder.newClient(new ClientConfig())
                    .target(serverUrl).path("api/activities/image")
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .post(Entity.entity(path, APPLICATION_JSON), String.class);

            String[] fetchedSplit = fetched.split(" ");

            ImageIO.write(ImageIO.read(new ByteArrayInputStream(
                            Base64.getDecoder().decode(fetchedSplit[0]))),
                    fetchedSplit[1], bos);
        }
        catch(Exception e){
            String defaultPathString = String.valueOf(ServerUtils.class.getClassLoader().getResource(""));

            defaultPathString = defaultPathString.substring(
                    0, defaultPathString.length() - "classes/java/main/".length())
                    + "resources/main/client/images/lightning.jpg";

            return new Image(defaultPathString);
        }
        byte[] buffer = bos.toByteArray();

        return new Image(new ByteArrayInputStream(buffer));
    }

    /**
     * Returns the current session
     * @return the current websocket session
     */
    public StompSession getSession() {
        return session;
    }
}