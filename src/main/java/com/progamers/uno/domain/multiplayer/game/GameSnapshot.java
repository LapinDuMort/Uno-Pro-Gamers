package com.progamers.uno.domain.multiplayer.game;

import com.progamers.uno.domain.cards.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Snapshot of game state for clients
 */
@Getter
@AllArgsConstructor
public class GameSnapshot {
    private final boolean gameOver;
    private final String currentPlayerId;
    private final Card topDiscard;
    private final String wildColour;
    private final List<PlayerPublic> players;

    @Getter
    @AllArgsConstructor
    public static class PlayerPublic {
        private final String playerId;
        private final String playerName;
        private final int handSize;
        private final boolean hasUno;
    }
}
