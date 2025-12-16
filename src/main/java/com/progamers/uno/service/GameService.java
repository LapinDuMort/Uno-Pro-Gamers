package com.progamers.uno.service;

import com.progamers.uno.domain.player.Player;
import com.progamers.uno.domain.cards.Card;
import com.progamers.uno.domain.game.Game;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Getter
public class GameService {

    private final Game game;
    private final Player player;
    private boolean gameOver;
    private final Map<String, Player> players = new HashMap<>();

    public GameService() {
        this.game = new Game();
        this.game.getCardDeck().shuffle();
        this.game.getDiscardPile().addToPile(
                this.game.getCardDeck().drawCard()
        );
        this.player = new Player();
        this.game.drawCards(this.player, 7);
    }

    private Player getOrCreatePlayer(String playerId) {
        return players.computeIfAbsent(playerId, id -> {
            Player p = new Player();
            // Give a starting hand for new players
            this.game.drawCards(p, 7);
            return p;
        });
    }

    // Keep old methods for backwards compat, add player-specific ones

    public List<Card> getPlayerHand(String playerId) {
        return getOrCreatePlayer(playerId).getPlayerHand();
    }

    public Card getTopDiscard() {
        return this.game.getDiscardPile().getTopCard();
    }

    public boolean hasUno(String playerId) {
        return getOrCreatePlayer(playerId).getHasUno();
    }

    public void declareUno(String playerId) {
        getOrCreatePlayer(playerId).declareUno();
    }

    public void drawCard(String playerId) {
        if (gameOver) return;
        this.game.drawCards(getOrCreatePlayer(playerId), 1);
    }


    public String checkTopDiscardWild() {

        if(getGame().getDiscardPile().WildColour != null){
            return getGame().getDiscardPile().WildColour;
        }
        return "None";
    }

    public void playCard(String playerId, int index, String wildColor) throws Exception {
        if (gameOver) return;
        Player p = getOrCreatePlayer(playerId);
        Card selectedCard = p.getCurrentSelectedCard(index);
        Card topCard = this.game.getDiscardPile().getTopCard();

        if (!this.game.isValidMove(topCard, selectedCard)) return;

        if (p.getHandSize() == 1 && !p.getHasUno()) {
            this.game.drawCards(p, 2);
            return;
        }

        this.game.getDiscardPile().addToPile(p.playCard(index));
        if (wildColor != null) {
            this.game.getDiscardPile().setWildColour(wildColor);
        }

        if (p.getHandSize() == 0) {
            this.gameOver = true;
        }
    }

    public void playCard(String playerId, int index) throws Exception {
        playCard(playerId, index, null);
    }
}
