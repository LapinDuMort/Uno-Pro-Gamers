package com.progamers.uno;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerControllerTests {

    @Test
    void startingHandIsCorrectSize() {
        PlayerController player = new PlayerController();
        player.drawStartingHand(7);

        assertEquals(7, player.getHandSize());
    }

    @Test
    void AddingOneElements() {
        PlayerController player = new PlayerController();
        player.getPlayerHand().add("Red 5");

        assertEquals(1, player.getPlayerHand().size());
    }

    @Test
    void AddingFiveElements() {
        PlayerController player = new PlayerController();
        player.getPlayerHand().add("Red 5");
        player.getPlayerHand().add("Blue 5");
        player.getPlayerHand().add("Yellow 5");
        player.getPlayerHand().add("Green 5");
        player.getPlayerHand().add("Red 9");

        assertEquals(5, player.getPlayerHand().size());
    }

    @Test
    void HandIsEmpty() {
        PlayerController player = new PlayerController();
        assertEquals(0, player.getPlayerHand().size());
    }

    @Test
    void invalidIndexIsHandled() {
        PlayerController player = new PlayerController();
        player.drawStartingHand(3);

        assertEquals("Index is out of bounds", player.getCurrentSelectedCard(-1));
        assertEquals("Index is out of bounds", player.getCurrentSelectedCard(99));
    }

    @Test
    void AddAndGetCertainCards() {
        PlayerController player = new PlayerController();
        player.getPlayerHand().add("Red 5");
        player.getPlayerHand().add("Blue 5");
        player.getPlayerHand().add("Yellow 5");
        player.getPlayerHand().add("Green 5");
        player.getPlayerHand().add("Red 9");

        assertEquals("Red 5", player.getCurrentSelectedCard(0));
        assertEquals("Red 9", player.getCurrentSelectedCard(4));
    }

    @Test
    void TestToString() {
        PlayerController player = new PlayerController();
        player.getPlayerHand().add("Red 5");
        player.getPlayerHand().add("Blue 5");
        player.setCurrentSelected(2);

        String expected = "PlayerController{playerHand=[Red 5, Blue 5], currentSelected=2}";
        assertEquals(expected, player.toString());
        assertEquals("Index is out of bounds", player.getCurrentSelectedCard(player.getCurrentSelected()));
    }

    @Test
    void TestUnoFalse() {
        PlayerController player = new PlayerController();
        player.getPlayerHand().add("Red 5");
        player.getPlayerHand().add("Blue 5");
        player.DeclareUno();

        assertEquals(false, player.getHasUno());
    }

    @Test
    void TestUnoTrue() {
        PlayerController player = new PlayerController();
        player.getPlayerHand().add("Red 5");
        player.DeclareUno();

        assertEquals(true, player.getHasUno());
    }
}