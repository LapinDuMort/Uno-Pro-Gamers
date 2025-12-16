package com.progamers.uno.controller;

import com.progamers.uno.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

/**
 * MVC controller for handling web requests for the game.
 * <p>
 *     This class is intentionally thin: It should delegate all game state
 *     and business rules to {@link GameService} and is only concerned with:
 *     <ul>
 *         <li>Routing HTTP requests to GameService methods</li>
 *         <li>Adding attributes to view models and displaying them</li>
 *         <li>Redirecting as appropriate</li>
 *     </ul>
 * </p>
 */
@Controller
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping("/playerpage")
    public String viewPlayer(Model model) {
        model.addAttribute("playerHand", gameService.getPlayerHand());
        model.addAttribute("discardCard", gameService.getTopDiscard());
        model.addAttribute("wildColour", gameService.checkTopDiscardWild());
        model.addAttribute("gameOver", gameService.isGameOver());
        model.addAttribute("hasUno", gameService.hasUno());
        model.addAttribute("isReversed", gameService.isReverse());
        model.addAttribute("players", gameService.getPlayerList());
        model.addAttribute("activePlayer", gameService.getActivePlayer());
        return "UnoPlayerPage";
    }

    @PostMapping("/draw")
    public RedirectView drawCard() {
        if (gameService.isGameOver()) {
            return new RedirectView("/gameover");
        }
        gameService.drawCard();
        return new RedirectView("/playerpage");
    }

    @PostMapping("/play")
    public RedirectView playCard(
            @RequestParam("cardIndex") int cardIndex,
            @RequestParam(value = "wildoutput", required = false) String wildColor,
            Model model)
            throws Exception
    {
        gameService.playCard(cardIndex, wildColor);
        return new RedirectView(
                gameService.isGameOver() ? "/gameover" : "/playerpage"
        );
    }

    @PostMapping("/uno")
    public RedirectView declareUno() {
        if (gameService.isGameOver()) {
            return new RedirectView("/gameover");
        }
        System.out.println(gameService.getActivePlayer().getHasUno());
        System.out.println(gameService.getActivePlayer());
        gameService.declareUno();
        return new RedirectView("/playerpage");
    }

    @GetMapping("/gameover")
    public String gameOver(Model model) {
        model.addAttribute("playerHand", gameService.getPlayerHand());
        model.addAttribute("discardCard", gameService.getTopDiscard());
        model.addAttribute("gameOver", gameService.isGameOver());
        return "GameOver";
    }
}