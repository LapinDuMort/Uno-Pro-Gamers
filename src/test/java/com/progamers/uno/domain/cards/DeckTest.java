package com.progamers.uno.domain.cards;

import com.progamers.uno.domain.cards.factory.DeckFactory;
import com.progamers.uno.domain.cards.factory.StandardDeckFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class DeckTest {
    @Test
    public void testPopCardFromDeck(){
        List<Card> cards = new ArrayList<Card>(Arrays.asList(Card.builder().build()));
        Deck newDeck = new Deck(cards);
        Integer deckLength = newDeck.getDeckSize();
        Card result = newDeck.drawCard();
        assertNotNull(result);
        assert deckLength.equals(1);
    }

    @Test
    public void testDeckShuffles(){
        DeckFactory deckFactory = new StandardDeckFactory();
        Deck newDeck = deckFactory.createDeck();
        Deck newDeckTwo = deckFactory.createDeck();
        newDeck.shuffle();
        Card cardOne = newDeck.drawCard();
        Card cardTwo = newDeckTwo.drawCard();
        Card cardThree = newDeck.drawCard();
        Card cardFour = newDeckTwo.drawCard();
        //One in ~11,000 chance, if this fails, run it again and buy a lottery ticket
        assertNotEquals(true, cardOne.equals(cardTwo) || cardThree.equals(cardFour));

    }
}
