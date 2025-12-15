package com.progamers.uno.domain.multiplayer.lobby.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Client sends this when starting game
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartGameRequestDTO {
    /**
     * playerId of player requesting to start game
     */
    private String playerId;
    private String token;
}
