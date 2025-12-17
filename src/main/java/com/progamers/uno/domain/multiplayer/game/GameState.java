package com.progamers.uno.domain.multiplayer.game;

import com.progamers.uno.domain.game.Game;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mutable state for active game session
 * State holder only no business rules
 */
@Getter
public class GameState {

    private final Map<String, GamePlayer> playerMap = new LinkedHashMap<>();
    private final List<String> turnOrder = new ArrayList<>();
    private final Game game;

    @Setter
    private boolean gameOver;

    @Setter
    private int currentTurnIndex;

    @Setter
    private int direction;

    public GameState(Game game) {
        this.game = game;
        this.gameOver = false;
        this.currentTurnIndex = 0;
        this.direction = 1;
    }
}
