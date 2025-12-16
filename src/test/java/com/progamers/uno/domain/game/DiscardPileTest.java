package com.progamers.uno.domain.game;

import com.progamers.uno.domain.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class DiscardPileTest {
    Game game;
    Player player;
    DiscardPile testDiscardPile;

    @BeforeEach
    void setup(){
        game = new Game();
        player = new Player(1,"Aryn");
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
}
