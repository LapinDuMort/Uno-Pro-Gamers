package com.progamers.uno.domain.game;

import com.progamers.uno.PlayerController;
import com.progamers.uno.domain.Deck;
import com.progamers.uno.domain.factory.DeckFactory;
import com.progamers.uno.domain.factory.StandardDeckFactory;

public class Game {

    private final Deck cardDeck;

    public Game() {
        DeckFactory deckFactory = new StandardDeckFactory();
        cardDeck = deckFactory.createDeck();
    }

    public Deck getCardDeck() { return this.cardDeck; }

    public void drawCards(PlayerController player, int numCards) {
        for (int i=0; i < numCards; i++) {
            player.addCardToHand(cardDeck.drawCard());
        }
    }
}
