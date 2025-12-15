package com.progamers.uno.service;

import com.progamers.uno.domain.multiplayer.lobby.LobbySession;
import com.progamers.uno.domain.multiplayer.lobby.LobbySnapshot;
import com.progamers.uno.domain.multiplayer.lobby.LobbyState;
import com.progamers.uno.domain.multiplayer.lobby.exception.InvalidTokenException;
import com.progamers.uno.domain.multiplayer.lobby.exception.LobbyException;
import com.progamers.uno.domain.multiplayer.lobby.exception.LobbyFullException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class LobbyService {

    private static final int MAX_PLAYERS = 8;
    private final LobbySession session = new LobbySession(MAX_PLAYERS);

    /**
     * Opens lobby with client generated join token
     * Separate from joinLobby so host can share token
     *
     * @param token client-generated join token (uuid string)
     * @return LobbySnapshot snapshot of opened lobby
     * @throws InvalidTokenException if token is invalid
     * @throws LobbyException if lobby is already open or in-progress
     */
    public synchronized LobbySnapshot openLobby(String token) {
        validateToken(token);

        if (session.getLobbyState() != LobbyState.CLOSED) {
            throw new LobbyException("Lobby already open or in-progress");
        }

        this.session.setActiveToken(token);
        this.session.setLobbyState(LobbyState.OPEN);

        return snapshot();
    }

    /**
     * Join a currenetly open lobby using active join token
     * @param token join token provided by lobby host
     * @param playerId unique player id of player
     * @param sessionId session identifier
     * @return LobbySnapshot snapshot of updated lobby
     * @throws InvalidTokenException if token is invalid
     * @throws LobbyException if lobby is already open or in-progress
     * @throws LobbyFullException if lobby full
     */
    public synchronized LobbySnapshot joinLobby(String token, String playerId, String playerName, String sessionId) {
        validateToken(token);

        if (this.session.getLobbyState() != LobbyState.OPEN) {
            throw new LobbyException("Lobby is not open");
        }
        if (!Objects.equals(this.session.getActiveToken(), token)) {
            throw new InvalidTokenException();
        }
        if (this.session.playerCount() >= this.session.getMaxPlayers()) {
            throw new LobbyFullException();
        }

        this.session.putPlayerIfAbsent(playerId, playerName, sessionId);

        return snapshot();
    }

    public synchronized LobbySnapshot snapshot() {
        return new LobbySnapshot(
                this.session.getLobbyState(),
                this.session.getMaxPlayers(),
                this.session.getPlayerIdsInOrder(),
                this.session.hasToken()
        );
    }

    public synchronized LobbySnapshot getLobbyStatus() { return snapshot(); }

    public synchronized LobbySnapshot startGame(String token) {
        validateToken(token);

        if (this.session.getLobbyState() != LobbyState.OPEN) {
            throw new LobbyException("Lobby is not open");
        }
        if (!Objects.equals(session.getActiveToken(), token)) {
            throw new InvalidTokenException();
        }
        if (session.playerCount() < 2) {
            throw new LobbyException("Need at least 2 players to start.");
        }

        this.session.setActiveToken(null);
        this.session.setLobbyState(LobbyState.IN_PROGRESS);

        return snapshot();
    }

    private void validateToken(String token) {
        try {
            UUID.fromString(token);
        }
        catch (Exception e) {
            throw new InvalidTokenException();
        }
    }
}
