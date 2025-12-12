package com.progamers.uno.domain.game;

import com.progamers.uno.domain.player.Player;
import com.progamers.uno.domain.cards.Card;
import com.progamers.uno.domain.cards.Colour;
import com.progamers.uno.domain.cards.Deck;
import com.progamers.uno.domain.cards.factory.DeckFactory;
import com.progamers.uno.domain.cards.factory.StandardDeckFactory;

public class Game {

    private final Deck cardDeck;
    private final DiscardPile discardPile;

    public Game() {
        DeckFactory deckFactory = new StandardDeckFactory();
        cardDeck = deckFactory.createDeck();
        discardPile = new DiscardPile();
    }

    public Deck getCardDeck() { return this.cardDeck; }

    public DiscardPile getDiscardPile() {
        return this.discardPile;
    }

    public void drawCards(Player player, int numCards) {
        for (int i=0; i < numCards; i++) {
            //Check if deck empty
            //If deck empty, call DiscardPile.RefillDeck
            //Shuffle Deck
            //THEN draw card
            if(cardDeck.getDeckSize() == 0){
                cardDeck.refillEmptyDeck(discardPile.refillDeck());
                cardDeck.shuffle();
            }
            player.addCardToHand(cardDeck.drawCard());
            player.handSort();
        }
    }

    //isValidMove compares value and colour to check move legality, and if selectedCard is a Wild type
    public boolean isValidMove(Card topCard, Card selectedCard){
        if(topCard.getColour() == Colour.Wild)return true;
        if(topCard.getColour() == selectedCard.getColour()) {return true;}
        else if(topCard.getValue() == selectedCard.getValue()) {return true;}
        else if(selectedCard.getColour() == Colour.Wild) {return true;}
        return false;
    }
}
