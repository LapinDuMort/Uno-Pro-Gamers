package com.progamers.uno.domain.game;

import com.progamers.uno.domain.cards.Card;
import java.util.ArrayList;
import java.util.List;

public class DiscardPile {
    List<Card> playedCards;

    public DiscardPile(){
        this.playedCards  = new ArrayList<Card>();
    }
    public void addToPile(Card card){
        this.playedCards.add(card);
    }
    public Card getTopCard(){
        return playedCards.getLast();
    }
    public List<Card> refillDeck(){
        Card cardInPlay = this.playedCards.removeLast();
        List<Card> returningCards = new ArrayList<>(playedCards);
        this.playedCards.clear();
        this.playedCards.add(cardInPlay);
        return returningCards;
    }
}
