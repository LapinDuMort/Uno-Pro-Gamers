package com.progamers.uno.service;

import com.progamers.uno.domain.multiplayer.lobby.LobbySnapshot;
import com.progamers.uno.domain.multiplayer.lobby.LobbyState;
import com.progamers.uno.domain.multiplayer.lobby.exception.InvalidTokenException;
import com.progamers.uno.domain.multiplayer.lobby.exception.LobbyFullException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyServiceTests {

    @Autowired
    private LobbyService lobbyService;

    @BeforeEach
    void setUp() {
        lobbyService = new LobbyService();
    }

    @Test
    void testOpenLobby_thenOpensLobby() {
        // create sim token
        String token = UUID.randomUUID().toString();
        // grab a lobby snapshot of newly opened lobby
        LobbySnapshot snap = lobbyService.openLobby(token);
        // lobby should now be open
        assertEquals(LobbyState.OPEN, snap.getLobbyState());
    }

    @Test
    void testJoinLobby_withValidToken_thenAddsPlayer() {
        // create a sim token
        String token = UUID.randomUUID().toString();
        // open new lobby with token
        lobbyService.openLobby(token);
        // attempt to join lobby as player
        LobbySnapshot snap = lobbyService.joinLobby(token, "p1", "Player1", "s1");
        // lobby snapshot should contain player
        assertTrue(snap.getPlayerNames().contains("Player1"));
        // 1 player in lobby
        assertEquals(1, snap.getPlayerNames().size());
    }

    @Test
    void testJoinLobby_withWrongToken_thenThrowsInvalidToken() {
        // create a sim token
        String token = UUID.randomUUID().toString();
        // open a new lobby
        lobbyService.openLobby(token);
        // reject invalid token
        assertThrows(InvalidTokenException.class,
                () -> lobbyService.joinLobby(UUID.randomUUID().toString(), "p1", "Player2", "s1"));
    }

    @Test
    void testJoinLobby_thenRejectsWhenLobbyFull() {
        // create a sim token
        String token = UUID.randomUUID().toString();
        // open new lobby
        lobbyService.openLobby(token);
        // join 8 players to lobby
        for (int i = 1; i <= 8; i++) {
            lobbyService.joinLobby(token, "p" + i, "player" + i, "s" + i);
        }
        // attempt to join 9th player
        // throws exception
        assertThrows(LobbyFullException.class,
                () -> lobbyService.joinLobby(token, "p9", "player9", "s9"));
    }

}
