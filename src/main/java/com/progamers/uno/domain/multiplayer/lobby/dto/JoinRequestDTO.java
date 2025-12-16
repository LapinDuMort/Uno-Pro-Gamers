package com.progamers.uno.domain.multiplayer.lobby.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Client must send this when joining lobby
 * COnsumed by websocket handler later
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinRequestDTO {
    private String token;
    private String playerId;
    private String playerName;
}
