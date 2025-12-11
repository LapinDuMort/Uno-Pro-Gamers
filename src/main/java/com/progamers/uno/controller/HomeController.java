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

        Mygame.drawCards(MyplayerController, 1);
        return new RedirectView("/playerpage");
    }

    @PostMapping("/play")
    public RedirectView playCard(@RequestParam("cardIndex") int cardIndex, Model model) throws Exception {

        Mygame.getDiscardPile().addToPile(MyplayerController.playCard(cardIndex));

        model.addAttribute("playerHand", MyplayerController.getPlayerHand());
        model.addAttribute("discardCard", Mygame.getDiscardPile().getTopCard());
        return new RedirectView("/playerpage");
    }
}