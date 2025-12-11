package com.progamers.uno.controller;

import com.progamers.uno.PlayerController;
import com.progamers.uno.domain.game.Game;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {

    Game Mygame;
    PlayerController MyplayerController;

    // track whether game is over
    // i.e. is there a winner?
    private boolean gameOver = false;

    public HomeController() {
        Mygame = new Game();
        Mygame.getCardDeck().shuffle();
        Mygame.getDiscardPile().addToPile(Mygame.getCardDeck().drawCard());
        MyplayerController = new PlayerController();

        Mygame.drawCards(MyplayerController, 7);
    }

    @GetMapping("/")
    public String index() {
        return "index"; // returns the name of the view
    }

    @GetMapping("/playerpage")
    public String ViewPlayer(Model model) {

        model.addAttribute("playerHand", MyplayerController.getPlayerHand());
        model.addAttribute("discardCard", Mygame.getDiscardPile().getTopCard());
        return "UnoPlayerPage";
    }

    @PostMapping("/draw")
    public RedirectView drawCard() {

        // if game is over
        // redirect to game-over view
        if (gameOver) {
            return new RedirectView("/gameover");
        }

        Mygame.drawCards(MyplayerController, 1);
        return new RedirectView("/playerpage");
    }

    @PostMapping("/play")
    public RedirectView playCard(@RequestParam("cardIndex") int cardIndex, Model model) throws Exception {

        // if game is over
        // redirect to game-over view
        if (gameOver) {
            return new RedirectView("/gameover");
        }


        if(Mygame.isValidMove(Mygame.getDiscardPile().getTopCard(), MyplayerController.getCurrentSelectedCard(cardIndex))){
            Mygame.getDiscardPile().addToPile(MyplayerController.playCard(cardIndex));
        }

        // win condition
        // if hand is empty
        // end game
        if (MyplayerController.getHandSize() == 0) {
            gameOver = true;
            return new RedirectView("/gameover");
        }

        model.addAttribute("playerHand", MyplayerController.getPlayerHand());
        model.addAttribute("discardCard", Mygame.getDiscardPile().getTopCard());
        return new RedirectView("/playerpage");
    }

    /**
     * Endpoint to display game-over screen when player wins
     * <p>
     * Called once controller determines a player has played their last card
     * and {@code gameOver} is marked {@code true}. Populates model with final state player's hand,
     * top card on discard pile, and flag marking the game as over. This allows stats
     * to be rendered on a "you win" page
     * </p>
     * @param model the model used to expose game state post-win
     * @return logical name of game-over view template
     */
    @GetMapping("/gameover")
    public String gameOver(Model model) {
        model.addAttribute("playerHand", MyplayerController.getPlayerHand());
        model.addAttribute("discardCard", Mygame.getDiscardPile().getTopCard());
        model.addAttribute("gameOver", gameOver);
        return "GameOver";
    }
}