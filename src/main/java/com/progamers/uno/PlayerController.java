package com.progamers.uno;

import com.progamers.uno.domain.Card;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class PlayerController {

    //private String playerName;
    //private Integer playerScore;
    private Boolean hasUno;
    private ArrayList<Card> playerHand;
    private int currentSelected;

    public PlayerController() {
        //this.playerName = name;
        //this.playerScore = 0;
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

    public void playCard(int index){

        // Implementation for playing a card from the player's hand
        //get gamestate
        //use gamestate to check card against ruleset
        //if valid pop card from hand and gamesate ends player turn
    }

    public boolean drawStartingHand(int HandSize){
        //Drawing the starting hand
        for(int i = 0; i < HandSize; i++)
        {
            // Card newCard = Deck.getInstance().drawCard();
            // if (drawnCard == null) {
            //     return false;
            // }
            // playerHand.add(drawnCard);
            playerHand.add("Sample Card " + (i + 1));
        }
        return true;
    }

    public void DeclareUno(){
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

    public int getHandSize() {
        return playerHand.size();
    }
}