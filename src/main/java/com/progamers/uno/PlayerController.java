package com.progamers.uno;

import com.progamers.uno.domain.Card;
import com.progamers.uno.domain.Colour;
import com.progamers.uno.domain.Value;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class PlayerController {

    private Boolean hasUno;
    private ArrayList<Card> playerHand;
    private int currentSelected;

    public PlayerController() {
        this.hasUno = false;
        this.playerHand = new ArrayList<Card>();
        this.currentSelected = 0;
    }
    public Card getCurrentSelectedCard(int index) throws Exception {
        // Check if the player's hand is empty
        if (playerHand.isEmpty()) {
            throw new Exception("Player hand is empty");
        }
        // Check if the index is within bounds
        if (index < 0 || index >= playerHand.size()) {
            throw new Exception("Index is out of bounds");
        }
        return(playerHand.get(index));
    }

    public Card playCard(int index){
        // Implementation for playing a card from the player's hand
        //get gamestate
        //use gamestate to check card against ruleset
        //if valid pop card from hand and gamesate ends player turn
        return playerHand.remove(index);
    }

    @Deprecated
    public boolean drawStartingHand(int HandSize){
        // Drawing the starting hand
        // Will be removed later and drawCard will be called externally to pass
        // a card from a deck to the player
        for(int i = 0; i < HandSize; i++)
        {
            addCardToHand(Card.builder().colour(Colour.Red).value(Value.getValue(i)).build());
        }
        return true;
    }

    public void addCardToHand (Card card) {
        // Adds a given card to the player's hand
        // Done this way so that hasUno can be unset
        playerHand.add(card);
        if (playerHand.size() > 1) {
            hasUno = false;
        }
    }

    public void DeclareUno() {
        // Implementation for declaring UNO
        if (playerHand.size() == 1)
        {
            hasUno = true;
        }
        else
        {
            hasUno = false;
        }
    }

    @Override
    public String toString() {
        return "PlayerController{" + "playerHand=" + playerHand + ", currentSelected=" + currentSelected + '}';
    }

    public void showHand() {
        for (int i = 0; i < playerHand.size(); i++) {
            System.out.printf("%d: %s %s%n", i + 1, playerHand.get(i).getColour(), playerHand.get(i).getValue());
        }
    }

    public int getHandSize() {
        return playerHand.size();
    }
}