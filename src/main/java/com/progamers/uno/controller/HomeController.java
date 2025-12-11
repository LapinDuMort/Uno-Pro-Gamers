package com.progamers.uno.controller;

import com.progamers.uno.PlayerController;
import com.progamers.uno.domain.Card;
import com.progamers.uno.domain.Colour;
import com.progamers.uno.domain.Deck;
import com.progamers.uno.domain.Value;
import com.progamers.uno.domain.factory.StandardDeckFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;

@Controller
public class HomeController {

    private ArrayList<Card> playerHand = new ArrayList<>();
    private Deck deck = new StandardDeckFactory().createDeck();
    private PlayerController playerController = new PlayerController();
    private Card discardCard = Card.builder().colour(Colour.Red).value(Value.Five).build();

    public HomeController() {
        deck.shuffle();

        // Deal 7 cards to the player
        for (int i = 0; i < 7; i++) {
            playerHand.add(deck.drawCard());
        }
    }

    @GetMapping("/")
    public String index() {
        return "index"; // returns the name of the view
    }

    @GetMapping("/playerpage")
    public String ViewPlayer(Model model) {

        model.addAttribute("playerHand", playerHand);
        model.addAttribute("discardCard", discardCard);
        return "UnoPlayerPage";
    }

    @PostMapping("/draw")
    public RedirectView drawCard() {

        playerHand.add(deck.drawCard());
        return new RedirectView("/playerpage");
    }

    @PostMapping("/play")
    public RedirectView playCard(@RequestParam("cardIndex") int cardIndex, Model model) {
        Card playedCard = playerHand.remove(cardIndex);
        discardCard = playedCard;

        model.addAttribute("playerHand", playerHand);
        model.addAttribute("discardCard", discardCard);
        return new RedirectView("/playerpage");
    }
}