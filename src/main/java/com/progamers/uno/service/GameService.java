package com.progamers.uno.service;

import com.progamers.uno.domain.game.SpecialCards;
import com.progamers.uno.domain.player.Player;
import com.progamers.uno.domain.cards.Card;
import com.progamers.uno.domain.game.Game;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    public GameService() {
        this.game = new Game();
        this.game.getCardDeck().shuffle();
        this.game.getDiscardPile().addToPile(
                this.game.getCardDeck().drawCard()
        );
        this.isReverse = false;
        this.activePlayer = null;
        this.turnTracker = 1;
        //currently set to 1, but will adapt to game size later
        this.numberOfPlayers = 1;
        this.playerList = new ArrayList<>();
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

    public void playerSelect() {
        Scanner scanner = new Scanner(System.in);
        int playersInGame;
        while (true) {
            System.out.println("Select number of players (2-8): ");
            playersInGame = scanner.nextInt();
            if (playersInGame >= 2 && playersInGame <= 8) {
                break;
            }
        }
        for (int i = 1; i <= playersInGame; i++) {
            boolean isBot;
            while(true){
                Scanner botScanner = new Scanner(System.in);
                System.out.println("Is this player a bot? (y/n): ");
                String createBotPlayer = botScanner.next();
                if (createBotPlayer.equalsIgnoreCase("y")){
                    isBot = true;
                    break;
                }
                else if(createBotPlayer.equalsIgnoreCase("n")){
                    isBot = false;
                    break;
                }
            }
            String playerName;
            if (!isBot){
                Scanner nameScanner = new Scanner(System.in);
                System.out.println("Enter player " + (i) + "'s name: ");
                playerName = nameScanner.next();
            }
            else{
                playerName = "Bot " + i;
            }
            Player player = new Player(i, playerName, isBot);
            addPlayers(player);
        }
        whoseTurn();
        this.numberOfPlayers = playersInGame;
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

    public void playCard(int index) throws Exception {
        if (gameOver) return;

        Card selectedCard = this.activePlayer.getCurrentSelectedCard(index);
        Card topCard = this.game.getDiscardPile().getTopCard();

        if (!this.game.isValidMove(topCard, selectedCard)) return;

        if (activePlayer.getHandSize() == 1 && !this.activePlayer.getHasUno()) {
            this.game.drawCards(this.activePlayer, 2);
            return;
        }

        this.game.getDiscardPile().addToPile(
                this.activePlayer.playCard(index));

        this.turnTracker = this.game.incrementTurn(this.turnTracker, this.numberOfPlayers, this.isReverse);
        whoseTurn();

        checkSpecialCard(selectedCard);

        if (this.activePlayer.getHandSize() == 0) {
            this.gameOver = true;
        }
    }
    public void checkSpecialCard(Card selectedCard) {
        //checking for SpecialCards functions
        if(SpecialCards.checkForDraw(selectedCard.getValue()) == 4){
            this.game.drawCards(this.activePlayer, 4);
            this.turnTracker = this.game.incrementTurn(this.turnTracker, this.numberOfPlayers, this.isReverse);
        }
        else if (SpecialCards.checkForDraw(selectedCard.getValue()) == 2){
            this.game.drawCards(this.activePlayer, 2);
            this.turnTracker = this.game.incrementTurn(this.turnTracker, this.numberOfPlayers, this.isReverse);
        }
        else if (SpecialCards.checkForSkip(selectedCard.getValue())){
            this.turnTracker = this.game.incrementTurn(this.turnTracker, this.numberOfPlayers, this.isReverse);
        }
        this.isReverse = SpecialCards.checkForReverse(selectedCard.getValue(), this.isReverse);
    }
}
