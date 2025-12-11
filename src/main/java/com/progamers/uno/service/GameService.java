package com.progamers.uno.service;

import com.progamers.uno.PlayerController;
import com.progamers.uno.domain.Card;
import com.progamers.uno.domain.game.Game;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Getter
public class GameService {

    private final Game game;
    private final PlayerController player;
    private boolean gameOver;

    public GameService() {
        this.game = new Game();
        this.game.getCardDeck().shuffle();
        this.game.getDiscardPile().addToPile(
                this.game.getCardDeck().drawCard()
        );
        this.player = new PlayerController();
        this.game.drawCards(this.player, 7);
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
        this.player.DeclareUno();
    }

    public void drawCard() {
        if (gameOver) return;
        this.game.drawCards(this.player, 1);
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
                this.player.playCard(index)
        );

        if (this.player.getHandSize() == 0) {
            this.gameOver = true;
        }
    }
}
