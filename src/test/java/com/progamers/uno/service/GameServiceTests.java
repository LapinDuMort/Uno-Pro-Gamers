package com.progamers.uno.service;

import com.progamers.uno.domain.cards.Card;
import com.progamers.uno.domain.cards.Colour;
import com.progamers.uno.domain.cards.Value;
import com.progamers.uno.domain.game.Game;
import com.progamers.uno.domain.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    private GameService gameService;
    private Game game;
    private Player player;

    @BeforeEach
    void setup() {
        gameService = new GameService();
        game = gameService.getGame();
        player = gameService.getActivePlayer();
    }

    /**
     * Helper method to reset player's hand to single specific card
     * to help ensure card will be a valid move on top of the current discard
     */
    private void setupSingleValidCardHand(Colour colour, Value value) {
        player.getPlayerHand().clear();

        Card topCard = Card.builder()
                .colour(colour)
                .value(value)
                .build();
        game.getDiscardPile().addToPile(topCard);

        Card lastCard = Card.builder()
                .colour(colour)
                .value(value)
                .build();
        player.getPlayerHand().add(lastCard);

        player.setHasUno(false);
    }

    /* --- drawCard tests --- */

    @Test
    void testDrawCard_whenGameNotOver_thenIncreasesHandSize() {
        // capture initial hand size
        int handSize = player.getHandSize();
        assertFalse(gameService.isGameOver());
        // draw a card
        gameService.drawCard();
        // ensure one extra card now in hand
        assertEquals(handSize+1, player.getHandSize());
    }

    @Test
    void testDrawCard_whenGameOver_thenDoesNothing() throws Exception {
        // win the game
        setupSingleValidCardHand(Colour.Red, Value.One);
        gameService.declareUno();
        gameService.playCard(0);
        assertTrue(gameService.isGameOver());
        // capture hand size after win
        int handSize = player.getHandSize();
        // attempt to draw card
        gameService.drawCard();
        // hand size should not have increased
        assertEquals(handSize, player.getHandSize());
    }

    /* --- declareUno tests --- */

    @Test
    void testDeclareUno_withSingleCard_thenSetHasUnoTrue() {
        // player has exactly one card
        setupSingleValidCardHand(Colour.Red, Value.One);
        // precondition uno should be false before declaring uno
        assertFalse(gameService.hasUno());
        // player declares uno
        gameService.declareUno();
        // hasUno should be true
        assertTrue(gameService.hasUno());
    }

    @Test
    void testDeclareUno_withMoreThanOneCars_thenClearsHasUno() {
        // player has two cards in hand
        player.getPlayerHand().clear();
        player.getPlayerHand().add(Card.builder().colour(Colour.Red).value(Value.One).build());
        player.getPlayerHand().add(Card.builder().colour(Colour.Blue).value(Value.Two).build());
        // set hasUno to true
        player.setHasUno(true);
        // declare uno through GameService
        gameService.declareUno();
        // uno should be cleared when not on last card
        assertFalse(gameService.hasUno());
    }

    /* --- playCard final card tests --- */

    @Test
    void testPlayCard_withUnoDeclaredOnFinalCard_thenSetsGameOverAndEmptiesHand() throws Exception {
        // player has exactly one card
        setupSingleValidCardHand(Colour.Red, Value.One);
        // player declares uno
        gameService.declareUno();

        assertEquals(1, player.getHandSize());
        assertTrue(gameService.hasUno());

        //play final card
        gameService.playCard(0);
        // ensure player hand is empty
        assertEquals(0, player.getHandSize());
        // ensure game has ended
        assertTrue(gameService.isGameOver());
    }

    /* -- playCard penalty tests --- */

    @Test
    void testPlayCard_withoutUnoDeclaredOnFinalCard_thenDrawTwoCardsAutomatically() throws Exception {
        // player has exactly one card
        setupSingleValidCardHand(Colour.Red, Value.One);
        assertEquals(1, player.getHandSize());
        // player has not declared uno
        assertFalse(gameService.hasUno());
        // attempt to play card
        gameService.playCard(0);
        // player should have drawn two cards
        assertEquals(3, player.getHandSize());
        // game should not have ended
        assertFalse(gameService.isGameOver());
    }

    /* --- playCard invalid move tests --- */

    @Test
    void testPlayCard_withInvalidMove_thenDoesNotEndGameOrEmptyHand() throws Exception {
        // player hand and top discard are different
        player.getPlayerHand().clear();

        Card topCard = Card.builder()
                .colour(Colour.Red)
                .value(Value.Five)
                .build();
        game.getDiscardPile().addToPile(topCard);

        Card invalidCard = Card.builder()
                .colour(Colour.Green)
                .value(Value.Nine)
                .build();
        player.getPlayerHand().add(invalidCard);

        int handSize = player.getHandSize();
        // attempt to play mismatching card
        gameService.playCard(0);
        // ensure no change to state
        assertEquals(handSize, player.getHandSize());
        assertFalse(gameService.isGameOver());
    }

}
