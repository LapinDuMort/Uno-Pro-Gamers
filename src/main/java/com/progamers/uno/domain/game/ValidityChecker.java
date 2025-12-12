package com.progamers.uno.domain.game;


import com.progamers.uno.domain.cards.Card;
import com.progamers.uno.domain.cards.Colour;
import com.progamers.uno.domain.cards.Value;

public class ValidityChecker {
    public static boolean isValid(Card topCard, Card selectedCard) {
        Colour playableColour = topCard.getColour();
        Value playableValue = topCard.getValue();
        if(playableColour == selectedCard.getColour()) {return true;}
        else if(playableValue == selectedCard.getValue()) {return true;}
        else if(selectedCard.getColour() == Colour.Wild) {return true;}
        return false;
}

public static Colour pickColour(int userChoice) {
        if (userChoice == 1){return Colour.Red;}
        else if (userChoice == 2){return Colour.Blue;}
        else if (userChoice == 3){return Colour.Green;}
        else if (userChoice == 4){return Colour.Yellow;}
        else {System.out.println("Invalid colour");}
        return Colour.Wild;
}
}
