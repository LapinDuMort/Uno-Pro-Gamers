package com.progamers.uno.dto;

import com.progamers.uno.domain.cards.Card;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class GameStateDto {
    private List<Card> playerHand;
    private Card discardCard;
    private String wildColour;
    private boolean gameOver;
    private boolean hasUno;
}
