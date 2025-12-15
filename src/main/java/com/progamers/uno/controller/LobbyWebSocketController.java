package com.progamers.uno.controller;

import com.progamers.uno.domain.multiplayer.lobby.LobbySnapshot;
import com.progamers.uno.domain.multiplayer.lobby.dto.JoinRequestDTO;
import com.progamers.uno.service.LobbyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/lobby/open")
    public void openLobby(JoinRequestDTO requestDTO) {
        LobbySnapshot snapshot = lobbyService.openLobby(requestDTO.getToken());
        messagingTemplate.convertAndSend("/topic/lobby", snapshot);
    }

    @MessageMapping("/lobby/join")
    public void joinLobby(JoinRequestDTO requestDTO, SimpMessageHeaderAccessor headers) {
        String sessionId = headers.getSessionId();
        LobbySnapshot snapshot = lobbyService.joinLobby(requestDTO.getToken(), requestDTO.getPlayerId(), sessionId);
        messagingTemplate.convertAndSend("/topic/lobby", snapshot);
    }
}
