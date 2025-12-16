package com.progamers.uno.controller;

import com.progamers.uno.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

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
    public String viewPlayer(@RequestParam(value = "playerId", required = false) String playerId, Model model) {
        // If no playerId supplied, generate one and redirect so subsequent requests include it
        if (playerId == null || playerId.isBlank()) {
            String newId = UUID.randomUUID().toString();
            return "redirect:/playerpage?playerId=" + newId;
        }

        model.addAttribute("playerHand", gameService.getPlayerHand(playerId));
        model.addAttribute("discardCard", gameService.getTopDiscard());
        model.addAttribute("wildColour", gameService.checkTopDiscardWild());
        model.addAttribute("gameOver", gameService.isGameOver());
        model.addAttribute("hasUno", gameService.hasUno(playerId));
        model.addAttribute("playerId", playerId);
        return "UnoPlayerPage";
    }

    @PostMapping("/draw")
    public RedirectView drawCard(@RequestParam(value = "playerId", required = false) String playerId) {
        if (playerId == null || playerId.isBlank()) {
            playerId = UUID.randomUUID().toString();
        }
        if (gameService.isGameOver()) {
            return new RedirectView("/gameover?playerId=" + playerId);
        }
        gameService.drawCard(playerId);
        return new RedirectView("/playerpage?playerId=" + playerId);
    }

    @PostMapping("/play")
    public RedirectView playCard(
            @RequestParam(value = "playerId", required = false) String playerId,
            @RequestParam("cardIndex") int cardIndex,
            @RequestParam(value = "wildoutput", required = false) String wildColor,
            Model model)
            throws Exception
    {
        if (playerId == null || playerId.isBlank()) {
            playerId = UUID.randomUUID().toString();
        }
        gameService.playCard(playerId, cardIndex, wildColor);
        return new RedirectView(
                gameService.isGameOver() ? "/gameover?playerId=" + playerId : "/playerpage?playerId=" + playerId
        );
    }

    @PostMapping("/uno")
    public RedirectView declareUno(@RequestParam(value = "playerId", required = false) String playerId) {
        if (playerId == null || playerId.isBlank()) {
            playerId = UUID.randomUUID().toString();
        }
        if (gameService.isGameOver()) {
            return new RedirectView("/gameover?playerId=" + playerId);
        }
        gameService.declareUno(playerId);
        return new RedirectView("/playerpage?playerId=" + playerId);
    }

    @GetMapping("/game")
    public String game() {
        return "game/game";
    }

    @GetMapping("/gameover")
    public String gameOver(@RequestParam(value = "playerId", required = false) String playerId, Model model) {
        if (playerId == null || playerId.isBlank()) {
            playerId = UUID.randomUUID().toString();
        }
        model.addAttribute("playerHand", gameService.getPlayerHand(playerId));
        model.addAttribute("discardCard", gameService.getTopDiscard());
        model.addAttribute("gameOver", gameService.isGameOver());
        model.addAttribute("playerId", playerId);
        return "GameOver";
    }
}