package com.progamers.uno.service;

import com.progamers.uno.domain.game.DiscardPile;
import com.progamers.uno.domain.game.SpecialCards;
import com.progamers.uno.domain.Player;
import com.progamers.uno.domain.cards.Card;
import com.progamers.uno.domain.game.Game;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Getter
public class GameService {

    private final Game game;
    private boolean gameOver;
    private boolean isReverse;
    private int turnTracker;
    private int numberOfPlayers;
    private List<Player> playerList;
    private Player activePlayer;
    private Player playerOne;
    private Player playerTwo;
    private Player playerThree;
    private Player playerFour;

    public GameService() {
        this.game = new Game();
        this.game.getCardDeck().shuffle();
        this.game.getDiscardPile().addToPile(
                this.game.getCardDeck().drawCard()
        );
        this.isReverse = false;
        this.activePlayer = null;
        this.turnTracker = 1;
        this.playerList = new ArrayList<>();
        this.playerOne = new Player(1, "test1");
        this.playerTwo = new Player(2, "test2");
        this.playerThree = new Player(3, "test3");
        this.playerFour = new Player(4, "test4");
        this.playerList.add(this.playerOne);
        this.playerList.add(this.playerTwo);
        this.playerList.add(this.playerThree);
        this.playerList.add(this.playerFour);
        this.numberOfPlayers = this.playerList.size();
        whoseTurn();
        dealStartingHands();
    }
    public void addPlayers(Player player){
        this.playerList.add(player);
    }

    public void whoseTurn(){
        for(Player player : this.playerList){
            if(this.turnTracker == player.getPlayerNumber()){
                this.activePlayer = player;
            }
        }
    }

    public void dealStartingHands(){
        for(Player player : this.playerList){
            this.game.drawCards(player, 7);
        }
    }

    public List<Card> getPlayerHand() {
        return this.activePlayer.getPlayerHand();
    }

    public Card getTopDiscard() {
        return this.game.getDiscardPile().getTopCard();
    }

    public boolean hasUno() {
        return this.activePlayer.getHasUno();
    }

    public void declareUno() {
        this.activePlayer.declareUno();
    }

    public void drawCard() {
        if (gameOver) return;
        this.game.drawCards(this.activePlayer, 1);
        this.turnTracker = this.game.incrementTurn(this.turnTracker, this.numberOfPlayers, this.isReverse);
        whoseTurn();
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

        Card selectedCard = this.activePlayer.getCurrentSelectedCard(index);
        DiscardPile pile = this.game.getDiscardPile();

        if (!this.game.isValidMove(pile, selectedCard)) return;

        if (activePlayer.getHandSize() == 2 && !this.activePlayer.getHasUno()) {
            this.game.drawCards(this.activePlayer, 2);

            this.turnTracker = this.game.incrementTurn(this.turnTracker, this.numberOfPlayers, this.isReverse);
            whoseTurn();
            return;
        }

        this.game.getDiscardPile().addToPile(
                this.activePlayer.playCard(index));

        checkSpecialCard(selectedCard);

        if (this.activePlayer.getHandSize() == 0) {
            this.gameOver = true;
        }
        this.turnTracker = this.game.incrementTurn(this.turnTracker, this.numberOfPlayers, this.isReverse);
        whoseTurn();
    }
    public void checkSpecialCard(Card selectedCard) {
        //checking for SpecialCards functions
        if(SpecialCards.checkForDraw(selectedCard.getValue()) == 4){
            this.turnTracker = this.game.incrementTurn(this.turnTracker, this.numberOfPlayers, this.isReverse);
            whoseTurn();
            this.game.drawCards(this.activePlayer, 4);
        }
        else if (SpecialCards.checkForDraw(selectedCard.getValue()) == 2){
            this.turnTracker = this.game.incrementTurn(this.turnTracker, this.numberOfPlayers, this.isReverse);
            whoseTurn();
            this.game.drawCards(this.activePlayer, 2);
        }
        else if (SpecialCards.checkForSkip(selectedCard.getValue())){
            this.turnTracker = this.game.incrementTurn(this.turnTracker, this.numberOfPlayers, this.isReverse);
        }
        this.isReverse = SpecialCards.checkForReverse(selectedCard.getValue(), this.isReverse);
    }
}