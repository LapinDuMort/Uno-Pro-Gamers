package com.progamers.uno.domain.game;

import com.progamers.uno.domain.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DiscardPileTest {
    Game game;
    Player player;
    DiscardPile testDiscardPile;

    @BeforeEach
    void setup(){
        game = new Game();
        player = new Player();
        testDiscardPile = new DiscardPile();
    }

    @Test
    void testDiscardPileUpdates(){
        game.drawCards(this.player, 7);
        testDiscardPile.addToPile(this.player.getPlayerHand().getFirst());
        String lastCard = testDiscardPile.getTopCard().toString();
        testDiscardPile.addToPile(this.player.getPlayerHand().get(1));
        String newCard = testDiscardPile.getTopCard().toString();
        assertNotEquals(lastCard, newCard);
    }

    @Test
    void testDiscardPileRefillDeck(){
        game.drawCards(this.player, 107);
        game.getDiscardPile().addToPile(this.player.playCard(0));
        game.getDiscardPile().addToPile(this.player.playCard(0));
        game.getDiscardPile().addToPile(this.player.playCard(0));
        game.getDiscardPile().addToPile(this.player.playCard(0));
        game.drawCards(this.player, 2);
        assert game.getCardDeck().getDeckSize() == 2;
    }

    /* --- setWildColour Tests --- */

    @Test
    void testSetWildColour_withValidColour_thenSetsWildColour() throws Exception {
        testDiscardPile.setWildColour("Red");
        assert testDiscardPile.WildColour.equals("Red");
    }

    @Test
    void testSetWildColour_withInvalidColour_thenThrowsException() {
        Exception exception = assertThrows(Exception.class, () ->
            testDiscardPile.setWildColour("Purple")
        );
        assertEquals("Invalid Colour Chosen for Wild Card", exception.getMessage());
    }

    @Test
    void testSetWildColour_withGreen_thenSetsWildColour() throws Exception {
        testDiscardPile.setWildColour("Green");
        assertEquals("Green", testDiscardPile.WildColour);
    }

    @Test
    void testSetWildColour_withBlue_thenSetsWildColour() throws Exception {
        testDiscardPile.setWildColour("Blue");
        assertEquals("Blue", testDiscardPile.WildColour);
    }

    @Test
    void testSetWildColour_withYellow_thenSetsWildColour() throws Exception {
        testDiscardPile.setWildColour("Yellow");
        assertEquals("Yellow", testDiscardPile.WildColour);
    }


}
