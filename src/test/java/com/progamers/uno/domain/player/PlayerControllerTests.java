package com.progamers.uno.domain.player;

import com.progamers.uno.domain.cards.Card;
import com.progamers.uno.domain.cards.Colour;
import com.progamers.uno.domain.cards.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerControllerTests {
    Player player;

    @BeforeEach
    void setUp() {
        player = new Player();
    }

    @Test
    void startingHandIsCorrectSize() {
        player.drawStartingHand(7);

        assertEquals(7, player.getHandSize());
    }

    @Test
    void addCardToHandingOneElements() {
        Card card = Card.builder().value(Value.Five).colour(Colour.Red).build();
        player.addCardToHand(card);

        assertEquals(1, player.getPlayerHand().size());
    }

    @Test
    void addCardToHandingFiveElements() {
        Card card = Card.builder().value(Value.Five).colour(Colour.Red).build();
        player.addCardToHand(card);
        player.addCardToHand(card);
        player.addCardToHand(card);
        player.addCardToHand(card);
        player.addCardToHand(card);

        assertEquals(5, player.getPlayerHand().size());
    }

    @Test
    void HandIsEmpty() {
        assertEquals(0, player.getPlayerHand().size());
    }

    @Test
    void invalidIndexIsHandled() {
        player.drawStartingHand(3);
        try {
            player.getCurrentSelectedCard(-1);
        } catch (Exception e) {
            assertEquals("Index is out of bounds", e.getMessage());
        }
        try {
            player.getCurrentSelectedCard(99);
        } catch (Exception e) {
            assertEquals("Index is out of bounds", e.getMessage());
        }
    }

    @Test
    void addCardToHandAndGetCertainCards() {
        Card cardRedFive = Card.builder().value(Value.Five).colour(Colour.Red).build();
        player.addCardToHand(cardRedFive);

        Card cardNineBlue = Card.builder().value(Value.Nine).colour(Colour.Blue).build();
        player.addCardToHand(cardNineBlue);

        assertDoesNotThrow(() -> {
            Card card = player.getCurrentSelectedCard(0);
            assertEquals(cardRedFive, card);
        });
        assertDoesNotThrow(() -> {
            Card card = player.getCurrentSelectedCard(1);
            assertEquals(cardNineBlue, card);
        });
    }

//    @Test
//    void TestToString() {
//        player.addCardToHand("Red 5");
//        player.addCardToHand("Blue 5");
//        player.setCurrentSelected(2);
//
//        String expected = "PlayerController{playerHand=[Red 5, Blue 5], currentSelected=2}";
//        assertEquals(expected, player.toString());
//        assertEquals("Index is out of bounds", player.getCurrentSelectedCard(player.getCurrentSelected()));
//    }

    @Test
    void TestUnoFalse() {
        Card cardRedFive = Card.builder().value(Value.Five).colour(Colour.Red).build();
        player.addCardToHand(cardRedFive);

        Card cardNineBlue = Card.builder().value(Value.Nine).colour(Colour.Blue).build();
        player.addCardToHand(cardNineBlue);

        assertEquals(false, player.getHasUno());
    }

    @Test
    void testDeclareUno_withHandSizeTwo_thenSetsHasUnoTrue() {
        // create two random cards
        Card cardRedFive = Card.builder().value(Value.Five).colour(Colour.Red).build();
        Card cardBlueNine = Card.builder().value(Value.Nine).colour(Colour.Blue).build();
        // add both cards to hand
        player.addCardToHand(cardRedFive);
        player.addCardToHand(cardBlueNine);
        // declare uno
        player.declareUno();
        // assert hasUno is true
        assertTrue(player.getHasUno());
    }

    @Test
    void TestUnoTrue() {
        Card cardRedFive = Card.builder().value(Value.Five).colour(Colour.Red).build();
        player.addCardToHand(cardRedFive);

        player.declareUno();

        assertFalse(player.getHasUno());
    }

    @Test
    void TestUnoResetsToFalse() {
        Card cardRedFive = Card.builder().value(Value.Five).colour(Colour.Red).build();
        player.addCardToHand(cardRedFive);

        player.declareUno();

        Card cardBlueFive = Card.builder().value(Value.Five).colour(Colour.Blue).build();
        player.addCardToHand(cardBlueFive);

        assertEquals(false, player.getHasUno());
    }

    @Test
    void testHandSortsCorrectly(){
        Card cardRedFour = Card.builder().value(Value.Five).colour(Colour.Red).build();
        Card cardWildFour = Card.builder().value(Value.WildFour).colour(Colour.Wild).build();
        Card cardRedTwo = Card.builder().value(Value.Two).colour(Colour.Red).build();
        Card cardGreenEight = Card.builder().value(Value.Eight).colour(Colour.Green).build();
        Card cardGreenFive = Card.builder().value(Value.Five).colour(Colour.Green).build();
        player.addCardToHand(cardRedTwo);
        player.addCardToHand(cardWildFour);
        player.addCardToHand(cardGreenEight);
        player.addCardToHand(cardRedFour);
        player.addCardToHand(cardGreenFive);
        player.handSort();
        assert player.getPlayerHand().toString().equals("[Card(colour=Red, value=Two), Card(colour=Red, value=Five), Card(colour=Green, value=Five), Card(colour=Green, value=Eight), Card(colour=Wild, value=WildFour)]");
    }

    @Test
    void testHandSortsCorrectlyWithDoubles(){
        Card cardRedFour = Card.builder().value(Value.Five).colour(Colour.Red).build();
        Card cardWildFour = Card.builder().value(Value.WildFour).colour(Colour.Wild).build();
        Card cardRedTwo = Card.builder().value(Value.Two).colour(Colour.Red).build();
        Card cardGreenEight = Card.builder().value(Value.Eight).colour(Colour.Green).build();
        Card cardGreenFive = Card.builder().value(Value.Five).colour(Colour.Green).build();
        player.addCardToHand(cardRedTwo);
        player.addCardToHand(cardWildFour);
        player.addCardToHand(cardGreenEight);
        player.addCardToHand(cardRedFour);
        player.addCardToHand(cardRedTwo);
        player.addCardToHand(cardGreenFive);
        player.handSort();
        assert player.getPlayerHand().toString().equals("[Card(colour=Red, value=Two), Card(colour=Red, value=Two), Card(colour=Red, value=Five), Card(colour=Green, value=Five), Card(colour=Green, value=Eight), Card(colour=Wild, value=WildFour)]");
    }
}