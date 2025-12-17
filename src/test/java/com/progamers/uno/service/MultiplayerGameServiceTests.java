package com.progamers.uno.service;

import com.progamers.uno.domain.cards.Card;
import com.progamers.uno.domain.cards.Value;
import com.progamers.uno.domain.game.Game;
import com.progamers.uno.domain.multiplayer.lobby.LobbyPlayer;
import com.progamers.uno.domain.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test suite for {@link MultiplayerGameService}
 * Tests are mostly AI generated, though many have been altered/corrected
 * where AI did NOT have full context of the codebase
 * 3 Tests are disabled due to AI error - Will need fixed
 * Coverage is ~98% of lines and ~94% of branches
 */
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

    @Disabled
    @Test
    void testStartFromLobby_whenCalledTwice_thenResetsState() {
        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("p1", "Player1", "s1"),
                new LobbyPlayer("p2", "Player2", "s1")
        ));

        gameService.startFromLobby("t1");
        assertTrue(gameService.isActive());
        assertEquals(List.of("p1", "p2"), gameService.getTurnOrder());

        gameService.drawCard("p1");
        assertEquals("p2", gameService.getCurrentPlayerId());

        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("session", "x1", "Xena")
        ));

        gameService.startFromLobby("t2");

        assertTrue(gameService.isActive());
        assertFalse(gameService.isGameOver());
        assertEquals(List.of("x1"), gameService.getTurnOrder());
        assertEquals("x1", gameService.getCurrentPlayerId());
        assertEquals(7, gameService.getHand("x1").size());
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

    @Test
    void testGetTurnOrder_whenActiveGame_thenReturnsDefensiveCopy() {
        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("p1", "Player1", "s1"),
                new LobbyPlayer("p2", "Player2", "s1")
        ));
        gameService.startFromLobby("t");

        List<String> order1 = gameService.getTurnOrder();
        assertEquals(List.of("p1", "p2"), order1);

        order1.clear();

        List<String> order2 = gameService.getTurnOrder();
        assertEquals(List.of("p1", "p2"), order2);
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
    void testHasUno_withInvalidPlayerId_thenThrows() {
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
    void testDeclareUno_whenNotPlayerTurn_thenThrows() {
        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("p1", "Player1", "s1"),
                new LobbyPlayer("p2", "Player2", "s1")
        ));

        gameService.startFromLobby("t");

        assertEquals("p1", gameService.getCurrentPlayerId());
        assertThrows(IllegalStateException.class, () -> gameService.declareUno("p2"));
    }

    @Disabled
    @Test
    void testDeclareUno_withMyTurn_whenHandSizeTwo_thenSetsHasUnoTrue() {
        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("p1", "Player1", "s1"),
                new LobbyPlayer("p2", "Player2", "s1")
        ));
        gameService.startFromLobby("t");

        Player p1 = gameService.getPlayersById().get("p1");
        assertNotNull(p1);

        // If Player.declareUno() has conditions, make them true (common: only allow declaring at 2 cards).
        while (p1.getPlayerHand().size() > 2) {
            p1.getPlayerHand().remove(p1.getPlayerHand().size() - 1);
        }
        assertEquals(2, p1.getHandSize());

        assertFalse(gameService.hasUno("p1"));
        gameService.declareUno("p1");
        assertTrue(gameService.hasUno("p1"));
    }

    /* --- drawCard Tests --- */

    @Test
    void testDrawCard_whenNotActive_thenThrows() {
        assertThrows(IllegalStateException.class, () -> gameService.drawCard("p1"));
    }

    @Test
    void testDrawCard_withNotMyTurn_thenThrows() {
        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("p1", "Player1", "s1"),
                new LobbyPlayer("p2", "Player2", "s1")
        ));
        gameService.startFromLobby("t");

        assertEquals("p1", gameService.getCurrentPlayerId());
        assertThrows(IllegalStateException.class, () -> gameService.drawCard("p2"));
    }

    @Test
    void testDrawCard_whenPlayerTurn_thenDrawsOneAndAdvancesTurn() {
        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("p1", "Player1", "s1"),
                new LobbyPlayer("p2", "Player2", "s1")
        ));
        gameService.startFromLobby("t");

        int before = gameService.getHand("p1").size();
        assertEquals("p1", gameService.getCurrentPlayerId());

        gameService.drawCard("p1");

        assertEquals(before + 1, gameService.getHand("p1").size());
        assertEquals("p2", gameService.getCurrentPlayerId());
    }

    @Disabled
    @Test
    void testDrawCard_whenGameOver_thenNoOp() throws Exception {
        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("p1", "Player1", "s1"),
                new LobbyPlayer("p2", "Player2", "s1")
        ));
        gameService.startFromLobby("t");

        Game g = gameService.getGame();
        Player p1 = gameService.getPlayersById().get("p1");
        assertNotNull(p1);

        while (p1.getPlayerHand().size() > 1) {
            p1.getPlayerHand().removeLast();
        }
        // Ensure hasUno true to avoid penalty when playing last card.
        p1.getPlayerHand().add(g.getCardDeck().drawCard());
        while (p1.getPlayerHand().size() > 2) {
            p1.getPlayerHand().removeLast();
        }
        p1.declareUno();
        while (p1.getPlayerHand().size() > 1) {
            p1.getPlayerHand().removeLast();
        }
        assertTrue(p1.getHasUno());

        Card lastCard = p1.getCurrentSelectedCard(0);
        Card validTop = null;
        for (int i = 0; i < 300; i++) {
            Card candidate = g.getCardDeck().drawCard();
            if (candidate == null) break;
            if (g.isValidMove(candidate, lastCard)) {
                validTop = candidate;
                break;
            }
        }
        assertNotNull(validTop);
        g.getDiscardPile().addToPile(validTop);

        gameService.playCard("p1", 0, null);
        assertTrue(gameService.isGameOver());

        int handBefore = p1.getHandSize();
        String currentBefore = gameService.getCurrentPlayerId();

        gameService.drawCard("p1");

        assertEquals(handBefore, p1.getHandSize());
        assertEquals(currentBefore, gameService.getCurrentPlayerId());
    }

    /* --- playCard Tests --- */

    @Test
    void testPlayCard_whenNotActive_thenThrows() {
        assertThrows(IllegalStateException.class, () -> gameService.playCard("p1", 0, null));
    }

    @Test
    void testPlayCard_withNotPlayerTurn_thenThrows() {
        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("p1", "Player1", "s1"),
                new LobbyPlayer("p2", "Player2", "s1")
        ));
        gameService.startFromLobby("t");

        assertEquals("p1", gameService.getCurrentPlayerId());
        assertThrows(IllegalStateException.class, () -> gameService.playCard("p2", 0, null));
    }

    @Test
    void testPlayCard_whenInvalidMove_thenDoesNotAdvanceTurnOrChangeDiscardTopOrHand() throws Exception {
        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("p1", "Player1", "s1"),
                new LobbyPlayer("p2", "Player2", "s1")
        ));
        gameService.startFromLobby("t");

        Game g = gameService.getGame();
        Player p1 = gameService.getPlayersById().get("p1");
        assertNotNull(p1);

        int index = 0;
        Card selected = p1.getCurrentSelectedCard(index);

        Card invalidTop = null;
        for (int i = 0; i < 300; i++) {
            Card candidate = g.getCardDeck().drawCard();
            if (candidate == null) break;
            if (!g.isValidMove(candidate, selected)) {
                invalidTop = candidate;
                break;
            }
        }
        assertNotNull(invalidTop, "Could not find an invalid top card to set up invalid-move test.");
        g.getDiscardPile().addToPile(invalidTop);

        String currentBefore = gameService.getCurrentPlayerId();
        Card topBefore = gameService.getTopDiscard();
        int handBefore = p1.getHandSize();

        gameService.playCard("p1", index, null);

        assertEquals(currentBefore, gameService.getCurrentPlayerId(), "Invalid move should not advance turn.");
        assertSame(topBefore, gameService.getTopDiscard(), "Invalid move should not change discard top.");
        assertEquals(handBefore, p1.getHandSize(), "Invalid move should not remove a card from hand.");
    }

    @Test
    void testPlayCard_whenValidMove_andWildColourProvided_thenSetsWildColour() throws Exception {
        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("p1", "Player1", "s1"),
                new LobbyPlayer("p2", "Player2", "s1")
        ));
        gameService.startFromLobby("t");

        Game g = gameService.getGame();
        Player p1 = gameService.getPlayersById().get("p1");
        assertNotNull(p1);

        int index = 0;
        Card selected = p1.getCurrentSelectedCard(index);

        Card validTop = null;
        for (int i = 0; i < 300; i++) {
            Card candidate = g.getCardDeck().drawCard();
            if (candidate == null) break;
            if (g.isValidMove(candidate, selected)) {
                validTop = candidate;
                break;
            }
        }
        assertNotNull(validTop, "Could not find a valid top card to set up valid-move test.");
        g.getDiscardPile().addToPile(validTop);

        assertEquals("p1", gameService.getCurrentPlayerId());

        gameService.playCard("p1", index, "Red");

        assertEquals("Red", gameService.getWildColourOrNone());
    }

    @Test
    void testPlayCard_whenUnoPenalty_thenDrawsTwoAndAdvancesTurnWithoutPlaying() throws Exception {
        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("p1", "Player1", "s1"),
                new LobbyPlayer("p2", "Player2", "s1")
        ));
        gameService.startFromLobby("t");

        Game g = gameService.getGame();
        Player p1 = gameService.getPlayersById().get("p1");
        assertNotNull(p1);

        // update: force exactly 2 cards now
        while (p1.getPlayerHand().size() > 2) {
            p1.getPlayerHand().removeLast();
        }
        assertEquals(2, p1.getHandSize());
        assertFalse(p1.getHasUno());

        Card onlyCard = p1.getCurrentSelectedCard(0);

        Card validTop = null;
        for (int i = 0; i < 300; i++) {
            Card candidate = g.getCardDeck().drawCard();
            if (candidate == null) break;
            if (g.isValidMove(candidate, onlyCard)) {
                validTop = candidate;
                break;
            }
        }
        assertNotNull(validTop, "Could not find a valid top card to set up UNO-penalty test.");
        g.getDiscardPile().addToPile(validTop);

        Card topBefore = gameService.getTopDiscard();
        assertEquals("p1", gameService.getCurrentPlayerId());

        gameService.playCard("p1", 0, null);

        assertEquals("p2", gameService.getCurrentPlayerId());
        assertEquals(3, p1.getHandSize());
        assertNotSame(topBefore, gameService.getTopDiscard(), "UNO penalty should not play the card.");
    }

    @Test
    void testPlayCard_whenSkip_thenSkipsNextPlayer() throws Exception {
        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("p1", "Player1", "s1"),
                new LobbyPlayer("p2", "Player2", "s1"),
                new LobbyPlayer("p3", "Player3", "s1")
        ));
        gameService.startFromLobby("t");

        Game g = gameService.getGame();
        Player p1 = gameService.getPlayersById().get("p1");
        assertNotNull(p1);

        int skipIndex = -1;

        for (int i = 0; i < p1.getPlayerHand().size(); i++) {
            if (p1.getCurrentSelectedCard(i).getValue().equals(Value.Skip)) {
                skipIndex = i;
                break;
            }
        }

        // If not found in initial hand, draw from deck and add to hand until we get one.
        for (int guard = 0; skipIndex < 0 && guard < 150; guard++) {
            Card c = g.getCardDeck().drawCard();
            if (c == null) break;
            p1.getPlayerHand().add(c);
            if (c.getValue().equals(Value.Skip)) {
                skipIndex = p1.getPlayerHand().size() - 1;
                break;
            }
        }
        assertTrue(skipIndex >= 0, "Could not acquire a Skip card for test.");

        Card skipCard = p1.getCurrentSelectedCard(skipIndex);

        Card validTop = null;
        for (int i = 0; i < 300; i++) {
            Card candidate = g.getCardDeck().drawCard();
            if (candidate == null) break;
            if (g.isValidMove(candidate, skipCard)) {
                validTop = candidate;
                break;
            }
        }
        assertNotNull(validTop, "Could not find a valid top card for Skip play.");
        g.getDiscardPile().addToPile(validTop);

        assertEquals("p1", gameService.getCurrentPlayerId());

        gameService.playCard("p1", skipIndex, null);

        // Skip advances twice overall => p1 -> p3 (skips p2)
        assertEquals("p3", gameService.getCurrentPlayerId());
    }

    @Test
    void testPlayCard_withReverse_inThreePlayers_thenDirectionFlipsAndNextIsPreviousPlayer() throws Exception {
        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("p1", "Player1", "s1"),
                new LobbyPlayer("p2", "Player2", "s1"),
                new LobbyPlayer("p3", "Player3", "s1")
        ));
        gameService.startFromLobby("t");

        Game g = gameService.getGame();
        Player p1 = gameService.getPlayersById().get("p1");
        assertNotNull(p1);

        int reverseIndex = -1;

        for (int i = 0; i < p1.getPlayerHand().size(); i++) {
            if (p1.getCurrentSelectedCard(i).getValue().equals(Value.Reverse)) {
                reverseIndex = i;
                break;
            }
        }

        for (int guard = 0; reverseIndex < 0 && guard < 150; guard++) {
            Card c = g.getCardDeck().drawCard();
            if (c == null) break;
            p1.getPlayerHand().add(c);
            if (c.getValue().equals(Value.Reverse)) {
                reverseIndex = p1.getPlayerHand().size() - 1;
                break;
            }
        }
        assertTrue(reverseIndex >= 0, "Could not acquire a Reverse card for test.");

        Card reverseCard = p1.getCurrentSelectedCard(reverseIndex);

        Card validTop = null;
        for (int i = 0; i < 300; i++) {
            Card candidate = g.getCardDeck().drawCard();
            if (candidate == null) break;
            if (g.isValidMove(candidate, reverseCard)) {
                validTop = candidate;
                break;
            }
        }
        assertNotNull(validTop, "Could not find a valid top card for Reverse play.");
        g.getDiscardPile().addToPile(validTop);

        assertEquals("p1", gameService.getCurrentPlayerId());

        gameService.playCard("p1", reverseIndex, null);

        // Reverse flips direction then advances once => p1 -> p3 (in 3-player game)
        assertEquals("p3", gameService.getCurrentPlayerId());
    }

    @Test
    void testPlayCard_withDrawTwo_thenNextPlayerDrawsTwoAndIsSkipped() throws Exception {
        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("p1", "Player1", "s1"),
                new LobbyPlayer("p2", "Player2", "s1"),
                new LobbyPlayer("p3", "Player3", "s1")
        ));
        gameService.startFromLobby("t");

        Game g = gameService.getGame();
        Player p1 = gameService.getPlayersById().get("p1");
        Player p2 = gameService.getPlayersById().get("p2");
        assertNotNull(p1);
        assertNotNull(p2);

        int drawTwoIndex = -1;

        for (int i = 0; i < p1.getPlayerHand().size(); i++) {
            if (p1.getCurrentSelectedCard(i).getValue().equals(Value.DrawTwo)) {
                drawTwoIndex = i;
                break;
            }
        }

        for (int guard = 0; drawTwoIndex < 0 && guard < 150; guard++) {
            Card c = g.getCardDeck().drawCard();
            if (c == null) break;
            p1.getPlayerHand().add(c);
            if (c.getValue().equals(Value.DrawTwo)) {
                drawTwoIndex = p1.getPlayerHand().size() - 1;
                break;
            }
        }
        assertTrue(drawTwoIndex >= 0, "Could not acquire a DrawTwo card for test.");

        Card drawTwoCard = p1.getCurrentSelectedCard(drawTwoIndex);

        Card validTop = null;
        for (int i = 0; i < 300; i++) {
            Card candidate = g.getCardDeck().drawCard();
            if (candidate == null) break;
            if (g.isValidMove(candidate, drawTwoCard)) {
                validTop = candidate;
                break;
            }
        }
        assertNotNull(validTop, "Could not find a valid top card for DrawTwo play.");
        g.getDiscardPile().addToPile(validTop);

        int p2Before = p2.getHandSize();
        assertEquals("p1", gameService.getCurrentPlayerId());

        gameService.playCard("p1", drawTwoIndex, null);

        assertEquals(p2Before + 2, p2.getHandSize(), "Next player should draw 2.");
        // DrawTwo advances to p2, draws, then advances again => p1 -> p3 (skips p2 turn)
        assertEquals("p3", gameService.getCurrentPlayerId());
    }

    @Test
    void testPlayCard_whenLastCardPlayed_thenGameOverTrueAndTurnDoesNotAdvance() throws Exception {
        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("p1", "Player1", "s1"),
                new LobbyPlayer("p2", "Player2", "s1")
        ));
        gameService.startFromLobby("t");

        Game g = gameService.getGame();
        Player p1 = gameService.getPlayersById().get("p1");
        assertNotNull(p1);

        // Make p1 have 1 card and avoid UNO-penalty by setting hasUno true.
        while (p1.getPlayerHand().size() > 1) {
            p1.getPlayerHand().removeLast();
        }
        p1.declareUno(); // may or may not matter at size 1, but we’ll enforce hasUno for penalty guard
        // If declareUno doesn't set at size 1, force it by going to size 2 first then declare, then back to 1.
        if (!p1.getHasUno()) {
            p1.getPlayerHand().add(g.getCardDeck().drawCard());
            while (p1.getPlayerHand().size() > 2) {
                p1.getPlayerHand().removeLast();
            }
            p1.declareUno();
            while (p1.getPlayerHand().size() > 1) {
                p1.getPlayerHand().removeLast();
            }
        }
        assertEquals(1, p1.getHandSize());
        assertTrue(p1.getHasUno(), "Could not ensure hasUno=true to bypass UNO-penalty guard for win test.");

        Card lastCard = p1.getCurrentSelectedCard(0);

        Card validTop = null;
        for (int i = 0; i < 300; i++) {
            Card candidate = g.getCardDeck().drawCard();
            if (candidate == null) break;
            if (g.isValidMove(candidate, lastCard)) {
                validTop = candidate;
                break;
            }
        }
        assertNotNull(validTop, "Could not find a valid top card for last-card win play.");
        g.getDiscardPile().addToPile(validTop);

        assertEquals("p1", gameService.getCurrentPlayerId());

        gameService.playCard("p1", 0, null);

        assertTrue(gameService.isGameOver());
        assertEquals("p1", gameService.getCurrentPlayerId(), "Service returns early on win; should not advance turn.");
    }

    /* --- publicSnapshot Tests --- */

    @Test
    void testPublicSnapshot_whenNotActive_thenThrows() {
        assertThrows(IllegalStateException.class, () -> gameService.publicSnapshot());
    }

    @Test
    void testPublicSnapshot_withActiveGame_thenContainsExpectedFields() {
        when(lobbyService.getPlayersInOrder()).thenReturn(List.of(
                new LobbyPlayer("p1", "Player1", "s1"),
                new LobbyPlayer("p2", "Player2", "s1")
        ));
        gameService.startFromLobby("t");

        Map<String, Object> snap = gameService.publicSnapshot();

        assertNotNull(snap);
        assertTrue(snap.containsKey("gameOver"));
        assertTrue(snap.containsKey("currentPlayerId"));
        assertTrue(snap.containsKey("topDiscard"));
        assertTrue(snap.containsKey("wildColour"));
        assertTrue(snap.containsKey("players"));

        assertEquals(false, snap.get("gameOver"));
        assertEquals("p1", snap.get("currentPlayerId"));
        assertEquals("None", snap.get("wildColour"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> players = (List<Map<String, Object>>) snap.get("players");
        assertEquals(2, players.size());

        assertEquals("p1", players.getFirst().get("playerId"));
        assertEquals("Player1", players.getFirst().get("playerName"));
        assertEquals(7, players.get(0).get("handSize"));
        assertEquals(false, players.get(0).get("hasUno"));

        assertEquals("p2", players.get(1).get("playerId"));
        assertEquals("Player2", players.get(1).get("playerName"));
        assertEquals(7, players.get(1).get("handSize"));
        assertEquals(false, players.get(1).get("hasUno"));
    }
}
