package com.progamers.uno.domain.multiplayer.game;

import com.progamers.uno.domain.player.Player;
import lombok.Getter;

/**
 * Per-player "seat" in the game
 */
@Getter
public class GamePlayer {
    private final String playerId;
    private final String playerName;
    private final Player player;

    public GamePlayer(String playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.player = new Player();
    }
}
