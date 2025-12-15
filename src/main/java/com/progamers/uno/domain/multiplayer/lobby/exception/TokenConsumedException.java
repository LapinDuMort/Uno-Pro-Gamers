package com.progamers.uno.domain.multiplayer.lobby.exception;

public class TokenConsumedException extends LobbyException {
    public TokenConsumedException() {
        super("Join token has already been used");
    }
}
