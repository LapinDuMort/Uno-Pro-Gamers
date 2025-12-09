package com.progamers.uno.domaintests;

import com.progamers.uno.domain.Card;

import com.progamers.uno.domain.Deck;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
}
