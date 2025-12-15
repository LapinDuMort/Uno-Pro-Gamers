package com.progamers.uno.domain.multiplayer.lobby.exception;

public class LobbyClosedException extends LobbyException {
    public LobbyClosedException() {
        super("Lobby is closed");
    }
}
