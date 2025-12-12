package com.progamers.uno.service;

import com.progamers.uno.domain.game.SpecialCards;
import com.progamers.uno.domain.player.Player;
import com.progamers.uno.domain.cards.Card;
import com.progamers.uno.domain.game.Game;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Service
@Getter
public class GameService {

    private final Game game;
    private final Player player;
    private boolean gameOver;
    private boolean isReverse;
    private int turnTracker;
    private int numberOfPlayers;

    public GameService() {
        this.game = new Game();
        this.game.getCardDeck().shuffle();
        this.game.getDiscardPile().addToPile(
                this.game.getCardDeck().drawCard()
        );
        this.player = new Player();
        this.game.drawCards(this.player, 7);
        this.isReverse = false;
        this.turnTracker = 1;
        //currently set to 1, but will adapt to game size later
        this.numberOfPlayers = 1;
    }

    public List<Card> getPlayerHand() {
        return this.player.getPlayerHand();
    }

    public Card getTopDiscard() {
        return this.game.getDiscardPile().getTopCard();
    }

    public boolean hasUno() {
        return this.player.getHasUno();
    }

    public void declareUno() {
        this.player.declareUno();
    }

    public void drawCard() {
        if (gameOver) return;
        this.game.drawCards(this.player, 1);
    }


    public String checkTopDiscardWild() {

        if(getGame().getDiscardPile().WildColour != null){
            return getGame().getDiscardPile().WildColour;
        }
        return "None";
    }

    public void playCard(int index, String WildColor) throws Exception {
        playCard(index);
        if(WildColor != null) {
            this.game.getDiscardPile().setWildColour(WildColor);
        }
    }


    public void playCard(int index) throws Exception {
        if (gameOver) return;

        Card selectedCard = this.player.getCurrentSelectedCard(index);
        Card topCard = this.game.getDiscardPile().getTopCard();

        if (!this.game.isValidMove(topCard, selectedCard)) return;

        if (player.getHandSize() == 1 && !this.player.getHasUno()) {
            this.game.drawCards(this.player, 2);
            return;
        }

        this.game.getDiscardPile().addToPile(
                this.player.playCard(index));

        checkSpecialCard(selectedCard);

        if (this.player.getHandSize() == 0) {
            this.gameOver = true;
        }
    }
    public void checkSpecialCard(Card selectedCard) {
        //checking for SpecialCards functions
        if(SpecialCards.checkForDraw(selectedCard.getValue()) == 4){
            this.game.drawCards(this.player, 4);
            //TODO Increment turn order
        }
        else if (SpecialCards.checkForDraw(selectedCard.getValue()) == 2){
            this.game.drawCards(this.player, 2);
            //TODO Increment turn order
        }
        else if (SpecialCards.checkForSkip(selectedCard.getValue())){
            //TODO Increment turn order
        }
        this.isReverse = SpecialCards.checkForReverse(selectedCard.getValue(), this.isReverse);
    }
}
