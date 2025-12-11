package com.progamers.uno.domaintests;


import com.progamers.uno.domain.*;
import com.progamers.uno.domain.game.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.fail;

public class GameTests {

    Game game;
    Player player;

    @BeforeEach
    void setup() {
        game = new Game();
        player = new Player();
    }

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
}
