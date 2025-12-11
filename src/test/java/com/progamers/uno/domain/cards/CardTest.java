package com.progamers.uno.domain.cards;

import org.junit.jupiter.api.Test;


public class CardTest {
    @Test
    public void testCreateAGreenSeven(){
        //Using existing enum for now as there does not seem to be a neat way to mock an enum- might revisit this
        Card newCard = Card.builder().colour(Colour.Green).value(Value.Seven).build();
        Colour cardColour = newCard.getColour();
        Value cardValue = newCard.getValue();
        assert cardColour.equals(Colour.getColour(2));
        assert cardValue.equals(Value.getValue(7));
    }


}
