package com.progamers.uno.domain.multiplayer.lobby.exception;

public class InvalidTokenException extends LobbyException {
    public InvalidTokenException() {
        super("Invalid join token");
    }
}
