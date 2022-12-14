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
package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import commons.entities.MultiplayerUser;
import commons.entities.SoloUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserControllerTest {

    @SuppressWarnings("serial")
    public class MyRandom extends Random {

        public boolean wasCalled = false;

        @Override
        public int nextInt(int bound) {
            wasCalled = true;
            return nextInt;
        }
    }

    public int nextInt;
    private MyRandom random;
    private TestWaitingUserRepository waitingRepo;
    private TestSoloUserRepository soloRepo;

    private UserController sut;

    @BeforeEach
    public void setup() {
        random = new MyRandom();
        waitingRepo = new TestWaitingUserRepository();
        soloRepo = new TestSoloUserRepository();
        sut = new UserController(random, waitingRepo, soloRepo);
    }

    @Test
    public void cannotAddNullPerson() {
        var actual = sut.addMultiplayerUser(getMultiplayerUser(null));
        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotPutNullPerson() {
        MultiplayerUser user = getMultiplayerUser("q1");
        sut.addMultiplayerUser(user);
        user.username = null;
        var actual = sut.update(user);
        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void getAllReturnsAllUsers() {
        MultiplayerUser user = getMultiplayerUser("q1");
        var actual = sut.addMultiplayerUser(user);
        assertTrue(sut.getAll().contains(actual.getBody()));
    }

    @Test
    public void getByIdReturnsUserById() {
        MultiplayerUser user = getMultiplayerUser("q1");
        var actual = sut.addMultiplayerUser(user);
        assertEquals(
                actual.getBody(),
                sut.getById(actual.getBody().id).getBody().get()
        );
    }

    @Test
    public void putUpdatesDatabase() {
        MultiplayerUser user = getMultiplayerUser("q1");
        var added = sut.addMultiplayerUser(user);
        user.username = "q2";
        sut.update(user);
        for(MultiplayerUser u : waitingRepo.users) {
            if(u.id == Objects.requireNonNull(added.getBody()).id) {
                assertEquals("q2", u.username);
                break;
            }
        }
    }

    @Test
    public void randomSelection() {
        sut.addMultiplayerUser(getMultiplayerUser("q1"));
        sut.addMultiplayerUser(getMultiplayerUser("q2"));
        nextInt = 1;
        var actual = sut.getRandom();

        assertTrue(random.wasCalled);
        assertEquals("q2", actual.getBody().username);
    }

    @Test
    public void duplicateUsername() {
        sut.addMultiplayerUser(getMultiplayerUser("q1"));
        var actual = sut.addMultiplayerUser(getMultiplayerUser("q1"));
        assertEquals(UNAUTHORIZED, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsed() {
        sut.addMultiplayerUser(getMultiplayerUser("q1"));
        waitingRepo.calledMethods.contains("save");
    }
    
    @Test
    public void cannotDeleteNegativeID() {
        var actual = sut.deleteWaitingUser(-1);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }
    
    @Test
    public void cannotDeleteNonExistingPerson() {
        var actual = sut.deleteWaitingUser(getMultiplayerUser("q1").id);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }
    
    @Test
    public void deleteRightPerson() {
        var savedUser = sut.addMultiplayerUser(getMultiplayerUser("q1"));
        var actual = sut.deleteWaitingUser(savedUser.getBody().id);
        assertTrue(actual.getStatusCode().is2xxSuccessful());
        assertFalse(waitingRepo.existsById(savedUser.getBody().id));
    }

    @Test
    public void cannotAddNullPersonSolo() {
        var actual = sut.addSoloUser(getSoloUser(null));
        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void addCorrectPersonSolo() {
        var actual = sut.addSoloUser(getSoloUser("q1"));
        var found = soloRepo.getById((long) soloRepo.users.size()-1);
        assertEquals(actual.getBody(), found);
        assertTrue(soloRepo.calledMethods.contains("save"));
        assertTrue(soloRepo.calledMethods.contains("getById"));
    }

    @Test
    public void getAllScoresReturnslist(){
        var user1 = getSoloUser("q1");
        user1.points = 0L;
        var user2 = getSoloUser("q2");
        user2.points = 2L;
        sut.addSoloUser(user1);
        sut.addSoloUser(user2);
        ArrayList<SoloUser> users = new ArrayList<>();
        users.add(user2);
        users.add(user1);
        assertEquals(users, sut.getAllScores());

    }

    private static MultiplayerUser getMultiplayerUser(String q) {
        return new MultiplayerUser(q);
    }

    private static SoloUser getSoloUser(String q) {
        return new SoloUser(q);
    }
}