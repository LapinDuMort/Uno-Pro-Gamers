package com.progamers.uno.domain.multiplayer.lobby;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to hold mutable state for the active session
 * Keeps session state out of service class for clearer responsibilities
 */
@Getter
public class LobbySession {

    private final int maxPlayers;
    private final Map<String, LobbyPlayer> playerMap = new LinkedHashMap<>();

    @Setter
    private LobbyState lobbyState;

    @Setter
    private String activeToken;

    public LobbySession(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.lobbyState = LobbyState.CLOSED;
        this.activeToken = null;
    }

    public boolean hasToken() { return this.activeToken != null; }

    public boolean containsPlayer(String playerId) {
        return this.playerMap.containsKey(playerId);
    }

    public int playerCount() { return this.playerMap.size(); }

    public List<String> getPlayerIdsInOrder() { return new ArrayList<>(this.playerMap.keySet()); }

    public void putPlayerIfAbsent(String playerId, String sessionId) {
        playerMap.putIfAbsent(playerId, new LobbyPlayer(playerId, sessionId));
    }

    public void reset() {
        this.playerMap.clear();
        this.activeToken = null;
        this.lobbyState = LobbyState.CLOSED;
    }
}
