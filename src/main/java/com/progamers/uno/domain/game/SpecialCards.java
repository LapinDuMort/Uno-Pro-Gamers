package com.progamers.uno.domain.game;

import com.progamers.uno.domain.cards.Value;

public class SpecialCards {
    public static int checkForDraw(Value playableValue) {
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
    public static boolean checkForSkip(Value playableValue){
        if(playableValue == Value.Skip){
            return true;
        }
        return false;
    }
    public static boolean checkForReverse(Value playableValue, boolean isReverse){
        if(playableValue == Value.Reverse){
            if(isReverse){return false;}
            else{return true;}
        }
        return isReverse;
    }
}