package com.progamers.uno.domain.game;

import com.progamers.uno.PlayerController;
import com.progamers.uno.domain.Deck;
import com.progamers.uno.domain.DiscardPile;
import com.progamers.uno.domain.factory.DeckFactory;
import com.progamers.uno.domain.factory.StandardDeckFactory;

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

    public void drawCards(PlayerController player, int numCards) {
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
        }
    }
}
