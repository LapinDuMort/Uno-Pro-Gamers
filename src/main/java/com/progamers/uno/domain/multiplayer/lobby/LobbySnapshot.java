package com.progamers.uno.domain.multiplayer.lobby;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Class for snapshot of lobby state
 * Will be broadcasted to clients
 * <bold>
 *     NO TOKEN VALUES TO AVOID LEAKS
 * </bold>
 */
@Getter
@AllArgsConstructor
public class LobbySnapshot {
    private final LobbyState lobbyState;
    private final int maxPlayers;
    private final List<String> playerIds;
    private final boolean tokenPresent;
}
