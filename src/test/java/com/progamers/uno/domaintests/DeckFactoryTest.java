package com.progamers.uno.domaintests;

import com.progamers.uno.domain.Deck;
import com.progamers.uno.domain.factory.DeckFactory;
import com.progamers.uno.domain.factory.StandardDeckFactory;
import org.junit.jupiter.api.Test;

public class DeckFactoryTest {
    @Test
    public void deckConstructionTest(){
        DeckFactory deckFactory = new StandardDeckFactory();
        Deck newDeck = deckFactory.createDeck();
        Integer deckSize = newDeck.getDeckSize();
        String result = newDeck.drawCard().toString();
        assert result.equals("Card(colour=Red, value=Zero)");
        assert deckSize.equals(108);
    }
}
