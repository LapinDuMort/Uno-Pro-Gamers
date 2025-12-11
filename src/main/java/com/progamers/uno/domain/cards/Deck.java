package com.progamers.uno.domain.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards;

    public Deck(List<Card> cards){
        this.cards = new ArrayList<Card>(cards);
    }

    public Card drawCard(){
        return this.cards.removeFirst();
    }

    public Integer getDeckSize(){
        return this.cards.size();
    }

    public void refillEmptyDeck(List<Card> refillCards){
        this.cards.addAll(refillCards);
    }

    public void shuffle() {
        Collections.shuffle(this.cards); }

}
