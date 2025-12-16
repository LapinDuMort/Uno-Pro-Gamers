package com.progamers.uno.domain.game;

import com.progamers.uno.domain.cards.Card;
import com.progamers.uno.domain.cards.Colour;
import com.progamers.uno.domain.cards.Value;

import java.util.ArrayList;
import java.util.List;

import java.util.Scanner;

public class ValidityChecker {
    public static boolean isValid(DiscardPile Pile, Card selectedCard) {
        Colour playableColour = Pile.getTopCard().getColour();
        Value playableValue = Pile.getTopCard().getValue();

        if (playableColour == Colour.Wild) {return selectedCard.getColour().toString().equals(Pile.WildColour);}
        if(playableColour == selectedCard.getColour()) {return true;}
        else if(playableValue == selectedCard.getValue()) {return true;}
        else if(selectedCard.getColour() == Colour.Wild) {return true;}
        return false;
    }
    public static int userInputColour(){
        Scanner userInputColour = new Scanner(System.in);
        int number = userInputColour.nextInt();
        userInputColour.close();
        return number;
    }

    public static List<Card> validCardList(List<Card> currentHand, DiscardPile Pile){
        List<Card> validCards = new ArrayList<>();
        for (Card card : currentHand){
            if (isValid(Pile, card)){
                validCards.add(card);
            }
        }
        return validCards;
    }
}
