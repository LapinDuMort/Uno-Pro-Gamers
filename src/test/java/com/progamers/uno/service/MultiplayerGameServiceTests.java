package com.progamers.uno.service;

import com.progamers.uno.domain.multiplayer.lobby.LobbyPlayer;
import com.progamers.uno.domain.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MultiplayerGameServiceTests {

    @Mock
    private LobbyService lobbyService;

    private MultiplayerGameService gameService;

    @BeforeEach
    void setup() {
        gameService = new MultiplayerGameService(lobbyService);
    }

    /* --- isActive Tests --- */

    @Test
    void testIsActive_whenNotStarted_thenFalse() {
        assertFalse(gameService.isActive());
        assertNull(gameService.getCurrentPlayerId());
    }

    /* --- startFromLobby Tests --- */

    @Test
    void testStartFromLobby_withPlayers_thenInitialisesGameAndTurnOrderAndHands() {
        String sessionId = "s1";;
        List<LobbyPlayer> lobbyPlayers = List.of(
                new LobbyPlayer("p1", "Alice", sessionId),
                new LobbyPlayer("p2", "Bob", sessionId),
                new LobbyPlayer("p3", "Cara", sessionId)
        );

        when(lobbyService.getPlayersInOrder()).thenReturn(lobbyPlayers);

        gameService.startFromLobby("token-does-not-matter-here");

        assertTrue(gameService.isActive());
        assertNotNull(gameService.getGame());
        assertFalse(gameService.isGameOver());

        assertEquals(List.of("p1", "p2", "p3"), gameService.getTurnOrder());
        assertEquals("p1", gameService.getCurrentPlayerId());

        assertEquals(7, gameService.getHand("p1").size());
        assertEquals(7, gameService.getHand("p2").size());
        assertEquals(7, gameService.getHand("p3").size());

        assertNotNull(gameService.getGame().getDiscardPile().getTopCard());
        assertEquals("None", gameService.getWildColourOrNone());
        assertFalse(gameService.hasUno("p1"));
    }

    /* --- getCurrentPlayerId Tests --- */

    @Test
    void testGetCurrentPlayerId_whenNotActive_thenNull() {
        assertNull(gameService.getCurrentPlayerId());
    }

    /* --- getTurnOrder Tests --- */

    @Test
    void testGetTurnOrder_whenNotStarted_thenEmptyCopy() {
        List<String> order = gameService.getTurnOrder();
        assertNotNull(order);
        assertTrue(order.isEmpty());
    }

    /* --- getHand Tests --- */

    @Test
    void testGetHand_whenNotActive_thenThrows() {
        assertThrows(IllegalStateException.class, () -> gameService.getHand("p1"));
    }

    @Test
    void testGetHand_withUnknownPlayerId_thenThrows() {
        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("session", "p1", "Alice")
        ));

        gameService.startFromLobby("t");
        assertThrows(IllegalArgumentException.class, () -> gameService.getHand("nope"));
    }

    /* --- getTopDiscard Tests --- */

    @Test
    void testGetTopDiscard_whenNotActive_thenThrows() {
        assertThrows(IllegalStateException.class, () -> gameService.getTopDiscard());
    }

    /* --- getWildColourOrNone Tests --- */

    @Test
    void testGetWildColourOrNone_whenNotActive_thenThrows() {
        assertThrows(IllegalStateException.class, () -> gameService.getWildColourOrNone());
    }

    /* --- hasUno + declareUno Tests --- */

    @Test
    void testHasUno_whenNotActive_thenThrows() {
        assertThrows(IllegalStateException.class, () -> gameService.hasUno("p1"));
    }

    @Test
    void testHasUno_withUnknownPlayerId_thenThrows() {
        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("p1", "Player1", "s1")
        ));

        gameService.startFromLobby("t");

        assertThrows(IllegalArgumentException.class, () -> gameService.hasUno("nope"));
    }

    @Test
    void testDeclareUno_whenNotActive_thenThrows() {
        assertThrows(IllegalStateException.class, () -> gameService.declareUno("p1"));
    }

    @Test
    void testDeclareUno_withNotMyTurn_thenThrows() {
        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("p1", "Player1", "s1"),
                new LobbyPlayer("p2", "Player2", "s1")
        ));

        gameService.startFromLobby("t");

        assertEquals("p1", gameService.getCurrentPlayerId());
        assertThrows(IllegalStateException.class, () -> gameService.declareUno("p2"));
    }

}
