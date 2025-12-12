package com.progamers.uno.domain.game;

import com.progamers.uno.domain.cards.Colour;
import com.progamers.uno.domain.cards.Value;
import com.progamers.uno.domain.player.Player;

public class SpecialCards {
    public Colour checkForWild(Colour playableColour) {
        if(playableColour == Colour.Wild) {
            //TODO: currently always returns red, pending hookup to user input
            playableColour = ValidityChecker.pickColour(1);
            return playableColour;
        }
        return playableColour;}
    public int checkForDraw(Value playableValue, Player targetPlayer) {
        //Adds 4 cards to player on WildFour
        if (playableValue == Value.WildFour) {
            return 4;
        }
        //Adds 2 cards to player on DrawTwo
        else if (playableValue == Value.DrawTwo) {
            return 2;
        }
        return 0;
    }
    public int checkForSkip(int playerTurn, Value playableValue){
        if(playableValue == Value.Skip){
            //may need to add a decrement if reverse is true
            return playerTurn++;
        }
        return playerTurn;
    }
    public boolean checkForReverse(Value playableValue, boolean isReverse){
        if(playableValue == Value.Reverse){
            if(isReverse){return false;}
            else{return true;}
        }
        return isReverse;
    }
}
