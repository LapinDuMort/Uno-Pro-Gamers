package com.progamers.uno.controller;

import com.progamers.uno.domain.multiplayer.game.dto.DeclareUnoRequestDTO;
import com.progamers.uno.domain.multiplayer.game.dto.DrawCardRequestDTO;
import com.progamers.uno.domain.multiplayer.game.dto.PlayCardRequestDTO;
import com.progamers.uno.service.MultiplayerGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GameWebSocketController {

    private final MultiplayerGameService gameService;
    private final SimpMessagingTemplate messaging;

    @MessageMapping("/game/play")
    public void play(PlayCardRequestDTO dto) throws Exception {
        gameService.playCard(dto.getPlayerId(), dto.getCardIndex(), dto.getWildColour());
        publish(dto.getToken());
    }

    @MessageMapping("/game/draw")
    public void draw(DrawCardRequestDTO dto) {
        try {
            gameService.drawCard(dto.getPlayerId());
            publish(dto.getToken());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MessageMapping("/game/uno")
    public void uno(DeclareUnoRequestDTO dto) {
        try {
            gameService.declareUno(dto.getPlayerId());
            publish(dto.getToken());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MessageMapping("/game/sync")
    public void syncGameState(PlayCardRequestDTO dto) {
        try {
            publish(dto.getToken());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publish(String token) {
        // public snapshot
        messaging.convertAndSend(
                "/topic/game/" + token,
                gameService.publicSnapshot()
        );

        // per-player hands (demo-safe)
        for (String playerId : gameService.getTurnOrder()) {
            messaging.convertAndSend(
                    "/topic/game/" + token + "/hand/" + playerId,
                    gameService.getHand(playerId)
            );
        }
    }
}
