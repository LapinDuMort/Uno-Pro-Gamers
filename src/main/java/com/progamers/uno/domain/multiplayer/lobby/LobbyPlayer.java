package com.progamers.uno.domain.multiplayer.lobby;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Minimal repr of a player in the lobby
 * sessionId for disconnect/reconnect handling
 */
@Data
@AllArgsConstructor
public class LobbyPlayer {
    private final String playerId;
    private final String playerName;
    private final String sessionId;
}
