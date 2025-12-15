package com.progamers.uno.controller;

import com.progamers.uno.dto.PlayCardMessage;
import com.progamers.uno.service.GameService;
import com.progamers.uno.dto.GameStateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {

    @Autowired
    private GameService gameService;

    @MessageMapping("/draw")
    @SendTo("/topic/gamestate")
    public GameStateDto drawCard() throws Exception {
        gameService.drawCard();
        return buildGameState();
    }

    @MessageMapping("/play")
    @SendTo("/topic/gamestate")
    public GameStateDto playCard(PlayCardMessage message) throws Exception {
        gameService.playCard(message.getCardIndex(), message.getWildColor());
        return buildGameState();
    }

    @MessageMapping("/uno")
    @SendTo("/topic/gamestate")
    public GameStateDto declareUno() throws Exception {
        gameService.declareUno();
        return buildGameState();
    }

    private GameStateDto buildGameState() {
        return GameStateDto.builder()
                .playerHand(gameService.getPlayerHand())
                .discardCard(gameService.getTopDiscard())
                .wildColour(gameService.checkTopDiscardWild())
                .gameOver(gameService.isGameOver())
                .hasUno(gameService.hasUno())
                .build();
    }
}
