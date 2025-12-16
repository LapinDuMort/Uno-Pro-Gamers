package com.progamers.uno.controller;

import com.progamers.uno.domain.multiplayer.lobby.LobbySnapshot;
import com.progamers.uno.domain.multiplayer.lobby.dto.JoinRequestDTO;
import com.progamers.uno.domain.multiplayer.lobby.dto.StartGameRequestDTO;
import com.progamers.uno.service.LobbyService;
import com.progamers.uno.service.MultiplayerGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class LobbyWebSocketController {

    @Autowired
    private final LobbyService lobbyService;

    @Autowired
    private final MultiplayerGameService multiplayerGameService;

    @Autowired
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/lobby/open")
    public void openLobby(JoinRequestDTO requestDTO) {
        LobbySnapshot snapshot = lobbyService.openLobby(requestDTO.getToken());
        messagingTemplate.convertAndSend("/topic/lobby", snapshot);
    }

    @MessageMapping("/lobby/join")
    public void joinLobby(JoinRequestDTO requestDTO, SimpMessageHeaderAccessor headers) {
        String sessionId = headers.getSessionId();
        LobbySnapshot snapshot = lobbyService.joinLobby(requestDTO.getToken(), requestDTO.getPlayerId(), requestDTO.getPlayerName(), sessionId);
        messagingTemplate.convertAndSend("/topic/lobby", snapshot);
    }

    @MessageMapping("/lobby/start")
    public void start(StartGameRequestDTO requestDTO) {
        System.out.println("=== LobbyWebSocketController.start() called with token: " + requestDTO.getToken());
        try {
            LobbySnapshot snap = lobbyService.startGame(requestDTO.getToken());
            messagingTemplate.convertAndSend("/topic/lobby", snap);
            System.out.println("=== Starting multiplayer game from lobby");
            multiplayerGameService.startFromLobby(requestDTO.getToken());
            
            // Publish initial game state to all players
            System.out.println("=== Publishing initial game state");
            publishGameState(requestDTO.getToken());
            
            System.out.println("=== Publishing STARTED event to /topic/game/events");
            messagingTemplate.convertAndSend("/topic/game/events", "STARTED");
            System.out.println("=== STARTED event published");
        } catch (Exception e) {
            System.out.println("=== ERROR in start(): " + e.getMessage());
            e.printStackTrace();
            messagingTemplate.convertAndSend("/topic/errors", "Failed to start game: " + e.getMessage());
        }
    }

    private void publishGameState(String token) {
        // public snapshot
        messagingTemplate.convertAndSend(
                "/topic/game/" + token,
                multiplayerGameService.publicSnapshot()
        );

        // per-player hands
        for (String playerId : multiplayerGameService.getTurnOrder()) {
            messagingTemplate.convertAndSend(
                    "/topic/game/" + token + "/hand/" + playerId,
                    multiplayerGameService.getHand(playerId)
            );
        }
    }

    @MessageMapping("/lobby/status")
    public void lobbyStatus() {
        messagingTemplate.convertAndSend("/topic/lobby", lobbyService.getLobbyStatus());
    }

    @MessageExceptionHandler
    public void handle(Exception ex) {
        messagingTemplate.convertAndSend("/topic/errors", ex.getMessage());
    }
}
