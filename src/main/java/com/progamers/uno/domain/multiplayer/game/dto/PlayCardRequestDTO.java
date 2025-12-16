package com.progamers.uno.domain.multiplayer.game.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayCardRequestDTO extends ActionBaseDTO {
    private int cardIndex;
    private String wildColour;
}