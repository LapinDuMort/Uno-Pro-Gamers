package com.progamers.uno;

import com.progamers.uno.domain.Card;
import com.progamers.uno.domain.game.Game;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class UnoApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(UnoApplication.class, args);
//        Scanner scanner = new Scanner(System.in);
//        Game game = new Game();
//        game.getCardDeck().shuffle();
//        PlayerController player1 = new PlayerController();
//        game.drawCards(player1, 7);
//        game.getDiscardPile().addToPile(game.getCardDeck().drawCard());
//
//        while (true) {
//            System.out.println("Current card in pile: " + game.getDiscardPile().getTopCard().toString());
//            player1.showHand();
//
//            int decision = scanner.nextInt();
//            if (decision == 0) {
//                player1.addCardToHand(game.getCardDeck().drawCard());
//            } else {
//                //card value must match  DiscardPile's TopCard, or value of card must be Wild in order to play,
//                //so I need to get 'em and compare 'em before playing.
//                Card selectedCard = player1.getCurrentSelectedCard(decision - 1);
//                Card topCard = game.getDiscardPile().getTopCard();
//                if (game.isValidMove(topCard, selectedCard)) {
//                    game.getDiscardPile().addToPile(player1.playCard(decision - 1));
//                } else {
//                    System.out.println("Invalid move!");
//                }
//            }
//        }
    }
}
