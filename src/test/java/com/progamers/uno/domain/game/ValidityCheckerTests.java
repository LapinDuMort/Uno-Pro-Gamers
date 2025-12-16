package com.progamers.uno.domain.game;

import com.progamers.uno.domain.cards.Card;
import com.progamers.uno.domain.cards.Colour;
import com.progamers.uno.domain.cards.Value;
import com.progamers.uno.domain.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ValidityCheckerTests {

    Game game;
    Player player;
    DiscardPile testDiscardPile;

    @BeforeEach
    void setup() {
        game = new Game();
        player = new Player(1,"Aryn");
        testDiscardPile = new DiscardPile();
    }

    @Test
    public void checkTwoRedIsValid(){
        Card redTwo = Card.builder().colour(Colour.Red).value(Value.Two).build();
        player.addCardToHand(redTwo);
        testDiscardPile.addToPile(redTwo);
        boolean result = game.isValidMove(testDiscardPile, player.playCard(0));
        assert result;
    }
    @Test
    public void checkFourYellowIsNotValid() {
        Card redTwo = Card.builder().colour(Colour.Red).value(Value.Two).build();
        Card fourYellow = Card.builder().colour(Colour.Yellow).value(Value.Four).build();
        player.addCardToHand(fourYellow);
        testDiscardPile.addToPile(redTwo);
        boolean result = game.isValidMove(testDiscardPile, player.playCard(0));
        assert !result;
    }
    @Test
    public void checkTwoValidCardsInHand(){
        Card redTwo = Card.builder().colour(Colour.Red).value(Value.Two).build();
        Card blueTwo = Card.builder().colour(Colour.Blue).value(Value.Two).build();
        Card fourYellow = Card.builder().colour(Colour.Yellow).value(Value.Four).build();
        player.addCardToHand(fourYellow);
        player.addCardToHand(redTwo);
        player.addCardToHand(blueTwo);
        testDiscardPile.addToPile(redTwo);
        List<Card> validCards = ValidityChecker.validCardList(player.getPlayerHand(), testDiscardPile);
        assert validCards.size() == 2;
        assert validCards.getFirst().equals(redTwo);
        assert validCards.getLast().equals(blueTwo);
    }
}
