package com.progamers.uno.domain.game;

import com.progamers.uno.domain.player.Player;
import com.progamers.uno.domain.cards.Card;
import com.progamers.uno.domain.cards.Colour;
import com.progamers.uno.domain.cards.Deck;
import com.progamers.uno.domain.cards.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTests {

    Game game;
    Player player;

    @BeforeEach
    void setup() {
        game = new Game();
        player = new Player();
    }

    /* --- feature tests --- */

    @Test
    void testDrawAStartingHand() {
        game.drawCards(this.player, 7);
        assert player.getHandSize() == 7;

        Deck deck = game.getCardDeck();
        assert deck.getDeckSize() == 101;

        Card res = Card.builder().colour(Colour.Red).value(Value.Zero).build();

        try {
            assert player.getCurrentSelectedCard(0).equals(res);
        }
        catch (Exception e) {
            fail();
        }
    }

    /* --- getDiscardPile + getCardDeck tests --- */

    @Test
    void testGetDiscardPile_whenCalled_thenReturnsNonNullDiscardPile() {
        DiscardPile discardPileOne = game.getDiscardPile();
        DiscardPile discardPileTwo = game.getDiscardPile();
        assertNotNull(discardPileOne);
        assertSame(discardPileOne, discardPileTwo);
    }

    @Test
    void testGetCardDeck_whenCalled_thenReturnsNonNullDeck() {
        Deck deckOne = game.getCardDeck();
        Deck deckTwo = game.getCardDeck();
        assertNotNull(deckOne);
        assertSame(deckOne, deckTwo);
    }

    /* --- drawCards tests --- */

    @Test
    void testDrawCards_withNonEmptyDeck_thenDrawsCard() {
        Player player = new Player();

        int initialHandSize = player.getHandSize();
        int initialDeckSize = game.getCardDeck().getDeckSize();

        assertTrue(initialDeckSize > 0);

        game.drawCards(player, 1);

        assertEquals(initialHandSize + 1, player.getHandSize());
        assertEquals(initialDeckSize - 1, game.getCardDeck().getDeckSize());
    }

    @Test
    void testDrawCards_withEmptyDeck_thenRefillsDeckFromDiscardPileAndDrawsCard() {
        Player player = new Player();

        // empty card deck
        int deckSize = game.getCardDeck().getDeckSize();
        game.drawCards(player, deckSize);
        assertEquals(0, game.getCardDeck().getDeckSize());

        // force cards onto discard pile so refillDeck() has content
        DiscardPile discardPile = game.getDiscardPile();
        discardPile.addToPile(
                Card.builder().colour(Colour.Red).value(Value.One).build()
        );
        discardPile.addToPile(
                Card.builder().colour(Colour.Red).value(Value.Two).build()
        );

        game.drawCards(player, 1);

        assertEquals(deckSize + 1, player.getHandSize());
        assertTrue(game.getCardDeck().getDeckSize() >= 0);
    }

    /* --- isValidMove tests --- */

    @Test
    void testIsValidMove_withMatchingColour_thenReturnsTrue() {
        Card top = Card.builder().colour(Colour.Red).value(Value.One).build();
        Card selected = Card.builder().colour(Colour.Red).value(Value.Two).build();

        assertTrue(game.isValidMove(top, selected));
    }

    @Test
    void testIsValidMove_withMatchingValue_thenReturnsTrue() {
        Card top = Card.builder().colour(Colour.Blue).value(Value.Five).build();
        Card selected = Card.builder().colour(Colour.Green).value(Value.Five).build();

        assertTrue(game.isValidMove(top, selected));
    }

    @Test
    void testIsValidMove_withWild_thenReturnsTrue() {
        Card top = Card.builder().colour(Colour.Yellow).value(Value.Nine).build();
        Card selected = Card.builder().colour(Colour.Wild).value(Value.Wild).build();

        assertTrue(game.isValidMove(top, selected));
    }

    @Test
    void testIsValidMove_withTopDiscardWildAndColourNull_thenReturnsFalse() {
        DiscardPile pile = game.getDiscardPile();
        pile.WildColour = null;

        Card top = Card.builder().colour(Colour.Wild).value(Value.Wild).build();
        Card selected = Card.builder().colour(Colour.Red).value(Value.Zero).build();

        assertFalse(game.isValidMove(top, selected));
    }

    @Test
    void testIsValidMove_withTopDiscardWildAndColourNone_thenReturnsFalse() {
        DiscardPile pile = game.getDiscardPile();
        pile.WildColour = "None";

        Card top = Card.builder().colour(Colour.Wild).value(Value.Wild).build();
        Card selected = Card.builder().colour(Colour.Red).value(Value.Zero).build();

        assertFalse(game.isValidMove(top, selected));
    }

    @Test
    void testIsValidMove_withTopDiscardWildAndWildColourMatches_thenReturnsTrue() {
        DiscardPile pile = game.getDiscardPile();
        pile.WildColour = "Red";

        Card top = Card.builder().colour(Colour.Wild).value(Value.Wild).build();
        Card selected = Card.builder().colour(Colour.Red).value(Value.Zero).build();

        assertTrue(game.isValidMove(top, selected));
    }

    @Test
    void testIsValidMove_withTopWildAndWildColourDoesNotMatch_thenReturnsFalse() {
        DiscardPile pile = game.getDiscardPile();
        pile.WildColour = "Red";

        Card top = Card.builder().colour(Colour.Wild).value(Value.Wild).build();
        Card selected = Card.builder().colour(Colour.Green).value(Value.Two).build();

        assertFalse(game.isValidMove(top, selected));
    }
}
