package com.progamers.uno.domain.multiplayer.lobby.exception;

public class LobbyFullException extends LobbyException {
    public LobbyFullException() {
        super("Lobby is full");
    }
}
