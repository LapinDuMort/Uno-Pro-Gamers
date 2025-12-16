package com.progamers.uno.domain.player;

import com.progamers.uno.domain.cards.Card;
import com.progamers.uno.domain.game.ValidityChecker;

import java.util.Collections;
import java.util.List;

public class Bots {
    public static String botPlay(Player botPlayer, Card topcard){
        List<Card> botHand = botPlayer.getPlayerHand();
        List<Card> validPlays = ValidityChecker.validCardList(botHand, topcard);
        if (validPlays.isEmpty()){
            return "none";
        }
        Collections.shuffle(validPlays);
        return validPlays.getFirst().toString();
    }
}
