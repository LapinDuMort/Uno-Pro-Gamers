package com.progamers.uno.domain;

import java.util.ArrayList;
import java.util.List;

public class DiscardPile {
    List<Card> playedCards;

    public DiscardPile(){
        this.playedCards  = new ArrayList<Card>();
    }
    public void addToPile(Card card){
        playedCards.add(card);
    }
    public Card getTopCard(){
        return playedCards.getLast();
    }
    public List<Card> refillDeck(){
        Card cardInPlay = this.playedCards.removeLast();
        List<Card> returningCards = this.playedCards;
        this.playedCards.clear();
        this.playedCards.add(cardInPlay);
        return returningCards;
    }
}
