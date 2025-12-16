package com.progamers.uno.service;

import com.progamers.uno.domain.multiplayer.lobby.LobbyPlayer;
import com.progamers.uno.domain.multiplayer.lobby.LobbySnapshot;
import com.progamers.uno.domain.multiplayer.lobby.LobbyState;
import com.progamers.uno.domain.multiplayer.lobby.exception.InvalidTokenException;
import com.progamers.uno.domain.multiplayer.lobby.exception.LobbyException;
import com.progamers.uno.domain.multiplayer.lobby.exception.LobbyFullException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyServiceTests {

    private LobbyService lobbyService;

    @BeforeEach
    void setup() {
        lobbyService = new LobbyService();
    }

    /* --- openLobby tests --- */

    @Test
    void testOpenLobby_thenOpensLobby() {
        // random token
        String token = UUID.randomUUID().toString();
        // open a new lobby with token
        LobbySnapshot snap = lobbyService.openLobby(token);
        // assert lobby is open
        assertEquals(LobbyState.OPEN, snap.getLobbyState());
        assertTrue(snap.isTokenPresent());
        assertEquals(8, snap.getMaxPlayers());
        assertTrue(snap.getPlayerNames().isEmpty());
    }

    @Test
    void testOpenLobby_withInvalidToken_thenThrowsInvalidTokenException() {
        // attempt to open lobby with invalid token
        // assert exception thrown
        assertThrows(InvalidTokenException.class,
                () -> lobbyService.openLobby("invalid-token"));
    }

    @Test
    void testOpenLobby_whenAlreadyOpen_thenThrowsLobbyException() {
        // open a new lobby
        lobbyService.openLobby(UUID.randomUUID().toString());
        // attempt to open another lobby
        // assert exception thrown
        assertThrows(LobbyException.class,
                () -> lobbyService.openLobby(UUID.randomUUID().toString()));
    }

    /* --- joinLobby tests --- */

    @Test
    void testJoinLobby_withValidToken_thenAddsPlayerId() {
        // random token
        String token = UUID.randomUUID().toString();
        // open new lobby
        lobbyService.openLobby(token);
        // take a new snapshot when player joins
        LobbySnapshot snap = lobbyService.joinLobby(token, "player1", "Player One", "session1");
        // assert player added
        assertEquals(1, snap.getPlayerNames().size());
    }

    @Test
    void testJoinLobby_withInvalidToken_thenThrowsInvalidTokenException() {
        // random token
        String token = UUID.randomUUID().toString();
        // open new lobby
        lobbyService.openLobby(token);
        // attempt to join lobby with invalid token
        // assert exception thrown
        assertThrows(InvalidTokenException.class,
                () -> lobbyService.joinLobby("invalid-token", "player1", "Player One", "session1"));
    }

    @Test
    void testJoinLobby_withFullLobby_thenThrowsLobbyFullException() {
        // random token
        // and open lobby
        String token = UUID.randomUUID().toString();
        lobbyService.openLobby(token);
        // join 8 players
        for (int i = 1; i <= 8; i++) {
            lobbyService.joinLobby(token, "p" + i, "player" + i, "s" + i);
        }
        // attempt to join 9th player
        // assert exception thrown
        assertThrows(LobbyFullException.class,
                () -> lobbyService.joinLobby(token, "p9", "player9", "s9"));
    }

    /* --- snapshot tests --- */

    @Test
    void testSnapshot_thenReturnsCurrentSessionState() {
        // lobby is initially closed
        LobbySnapshot snap0 = lobbyService.snapshot();
        assertEquals(LobbyState.CLOSED, snap0.getLobbyState());
        assertFalse(snap0.isTokenPresent());
        assertTrue(snap0.getPlayerNames().isEmpty());

        // after open + join
        String token = UUID.randomUUID().toString();
        lobbyService.openLobby(token);
        lobbyService.joinLobby(token, "p1", "Player1", "s1");

        LobbySnapshot snap1 = lobbyService.snapshot();
        assertEquals(LobbyState.OPEN, snap1.getLobbyState());
        assertTrue(snap1.isTokenPresent());
        assertEquals(List.of("p1"), snap1.getPlayerNames());
    }

    @Test
    void testSnapshot_whenGetLobbyStatus_thenMatchesSnapshot() {
        // random token
        // open lobby
        // join player
        String token = UUID.randomUUID().toString();
        lobbyService.openLobby(token);
        lobbyService.joinLobby(token, "p1", "Alice", "s1");
        // take lobby snapshot
        // take snapshot of lobby status
        LobbySnapshot a = lobbyService.snapshot();
        LobbySnapshot b = lobbyService.getLobbyStatus();
        // snapshots should match
        assertEquals(a.getLobbyState(), b.getLobbyState());
        assertEquals(a.getMaxPlayers(), b.getMaxPlayers());
        assertEquals(a.getPlayerNames(), b.getPlayerNames());
        assertEquals(a.isTokenPresent(), b.isTokenPresent());
    }

    /* --- startGame tests --- */

    @Test
    void testStartGame_withInvalidToken_thenThrowsInvalidTokenException() {
        assertThrows(InvalidTokenException.class,
                () -> lobbyService.startGame("not-a-uuid"));
    }

    @Test
    void testStartGame_whenLobbyNotOpen_thenThrowsLobbyException() {
        String token = UUID.randomUUID().toString();
        assertThrows(LobbyException.class,
                () -> lobbyService.startGame(token));
    }

    @Test
    void testStartGame_withWrongToken_thenThrowsInvalidTokenException() {
        // random token + open lobby
        String token = UUID.randomUUID().toString();
        lobbyService.openLobby(token);
        // new random token
        String wrongToken = UUID.randomUUID().toString();
        // assert exception with invalid token
        assertThrows(InvalidTokenException.class,
                () -> lobbyService.startGame(wrongToken));
    }

    @Test
    void testStartGame_withLessThanTwoPlayers_thenThrowsLobbyException() {
        // random token
        // open lobby + join player
        String token = UUID.randomUUID().toString();
        lobbyService.openLobby(token);
        lobbyService.joinLobby(token, "p1", "Alice", "s1");
        // attempt to start game
        assertThrows(LobbyException.class,
                () -> lobbyService.startGame(token));
    }

    @Test
    void testStartGame_withTwoPlayers_thenTransitionsToInProgressAndClearsToken() {
        // random token
        // open lobby
        // join two players
        String token = UUID.randomUUID().toString();
        lobbyService.openLobby(token);
        lobbyService.joinLobby(token, "p1", "Alice", "s1");
        lobbyService.joinLobby(token, "p2", "Bob", "s2");
        // take snapshot of game start
        LobbySnapshot snap = lobbyService.startGame(token);
        // assert lobby state is IN_PROGRESS
        assertEquals(LobbyState.IN_PROGRESS, snap.getLobbyState());
        // assert token cleared
        assertFalse(snap.isTokenPresent());
        // assert players in lobby
        assertEquals(List.of("p1", "p2"), snap.getPlayerNames());
    }

    /* --- getPlayersInOrder tests --- */

    @Test
    void testGetPlayersInOrder_thenReturnsDefensiveCopyOfPlayers() {
        // random token
        // open lobby
        // join two players
        String token = UUID.randomUUID().toString();
        lobbyService.openLobby(token);
        lobbyService.joinLobby(token, "p1", "Alice", "s1");
        lobbyService.joinLobby(token, "p2", "Bob", "s2");
        // get players in order
        // expect 2
        List<LobbyPlayer> list1 = lobbyService.getPlayersInOrder();
        assertEquals(2, list1.size());
        // mutate returned list
        // should not affect service state
        list1.clear();
        // get players in order again
        List<LobbyPlayer> list2 = lobbyService.getPlayersInOrder();
        // assert still 2
        assertEquals(2, list2.size());
    }

}
