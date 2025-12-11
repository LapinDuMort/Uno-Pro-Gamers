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
    boolean gameOver = false;

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
        model.addAttribute("hasUno", MyplayerController.getHasUno());
        model.addAttribute("gameOver", gameOver);
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


        if(Mygame.isValidMove(Mygame.getDiscardPile().getTopCard(), MyplayerController.getCurrentSelectedCard(cardIndex))) {
            // penalty for not declaring uno
            // player tries to play their final card while hasUno is false
            if (MyplayerController.getHandSize() == 1 && !MyplayerController.getHasUno()) {
                // draw 2 cards automatically instead of playing card
                Mygame.drawCards(MyplayerController, 2);
                // redirect to /playerpage
                return new RedirectView("/playerpage");
            }
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
     * Endpoint to allow player to declare uno on their final card
     * <p>
     * Pre-requisite to player being allowed to win.
     * If game is already over redirect to game-over view otherwise delegate to
     * {@link com.progamers.uno.PlayerController#DeclareUno()} to update {@code hasUno}
     * flag based on player's current hand size. Then redirect to player view to update
     * uno status
     * </p>
     * @return redirect to player page or game-over page if game has ended
     */
    @PostMapping("/uno")
    public RedirectView declareUno() {
        // if game is over redirect game-over view
        if (gameOver) { return new RedirectView("/gameover"); }
        // let player decide if valid uno
        MyplayerController.DeclareUno();
        // return to player view to see uno status
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