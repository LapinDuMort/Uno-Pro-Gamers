package com.progamers.uno.domain.multiplayer;

import com.progamers.uno.domain.multiplayer.game.GamePlayer;
import com.progamers.uno.domain.multiplayer.game.GameState;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    /* --- constructor tests --- */

    @Test
    void testConstructor_whenCreated_thenFieldsInitialized() {
        GameState state = new GameState(null);

        Map<String, ?> playerMap = state.getPlayerMap();
        List<?> turnOrder = state.getTurnOrder();
        Object game = state.getGame();
        boolean gameOver = state.isGameOver();
        int currentTurnIndex = state.getCurrentTurnIndex();
        int direction = state.getDirection();

        assertNotNull(playerMap, "playerMap should be initialized");
        assertTrue(playerMap.isEmpty(), "playerMap should start empty");

        assertNotNull(turnOrder, "turnOrder should be initialized");
        assertTrue(turnOrder.isEmpty(), "turnOrder should start empty");

        assertNull(game, "game should be stored as provided (null here)");

        assertFalse(gameOver, "gameOver should default to false");
        assertEquals(0, currentTurnIndex, "currentTurnIndex should default to 0");
        assertEquals(1, direction, "direction should default to 1");
    }

    /* --- setter tests --- */

    @Test
    void testSetters_whenCalled_thenStateUpdated() {
        GameState state = new GameState(null);

        // Use setters
        state.setGameOver(true);
        state.setCurrentTurnIndex(42);
        state.setDirection(-1);

        assertTrue(state.isGameOver(), "setGameOver should change the internal flag");
        assertEquals(42, state.getCurrentTurnIndex(), "setCurrentTurnIndex should change the internal index");
        assertEquals(-1, state.getDirection(), "setDirection should change the internal direction");
    }

    @Test
    void testCollections_whenMutated_thenPreserveOrderAndData() {
        GameState state = new GameState(null);

        Map<String, GamePlayer> playerMap = state.getPlayerMap();
        List<String> turnOrder = state.getTurnOrder();

        // Mutate turnOrder
        turnOrder.add("alice");
        turnOrder.add("bob");
        turnOrder.add("carol");

        assertEquals(3, turnOrder.size());
        assertEquals("alice", turnOrder.get(0));
        assertEquals("bob", turnOrder.get(1));
        assertEquals("carol", turnOrder.get(2));

        // Create GamePlayer instances and put them into the map
        GamePlayer alice = new GamePlayer("alice", "Alice");
        GamePlayer bob = new GamePlayer("bob", "Bob");
        GamePlayer carol = new GamePlayer("carol", "Carol");

        playerMap.put("alice", alice);
        playerMap.put("bob", bob);
        playerMap.put("carol", carol);

        assertEquals(3, playerMap.size());
        String[] keys = playerMap.keySet().toArray(new String[0]);
        assertArrayEquals(new String[] { "alice", "bob", "carol" }, keys, "LinkedHashMap should preserve insertion order");
        assertSame(bob, playerMap.get("bob"));
    }

}
