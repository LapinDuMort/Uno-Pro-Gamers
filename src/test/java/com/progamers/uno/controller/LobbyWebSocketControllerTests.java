package com.progamers.uno.controller;

import com.progamers.uno.domain.multiplayer.lobby.LobbySnapshot;
import com.progamers.uno.domain.multiplayer.lobby.dto.JoinRequestDTO;
import com.progamers.uno.domain.multiplayer.lobby.dto.StartGameRequestDTO;
import com.progamers.uno.service.LobbyService;
import com.progamers.uno.service.MultiplayerGameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Test suite for {@link LobbyWebSocketController}
 * All tests are AI generated, but human-reviewed
 * Test cases seem robust
 * Coverage is 100%
 */
@ExtendWith(MockitoExtension.class)
class LobbyWebSocketControllerTests {

    @Mock
    private LobbyService lobbyService;

    @Mock
    private MultiplayerGameService multiplayerGameService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private LobbyWebSocketController controller;

    /* --- openLobby tests --- */

    @Test
    void testOpenLobby_withValidToken_thenPublishesLobbySnapshotToTopicLobby() {
        JoinRequestDTO request = mock(JoinRequestDTO.class);
        when(request.getToken()).thenReturn("token-123");

        LobbySnapshot snapshot = mock(LobbySnapshot.class);
        when(lobbyService.openLobby("token-123")).thenReturn(snapshot);

        controller.openLobby(request);

        verify(lobbyService).openLobby("token-123");
        verify(messagingTemplate).convertAndSend("/topic/lobby", snapshot);
        verifyNoMoreInteractions(messagingTemplate);
    }

    /* --- joinLobby tests --- */

    @Test
    void testJoinLobby_withValidRequestAndSessionId_thenPublishesLobbySnapshotToTopicLobby() {
        JoinRequestDTO request = mock(JoinRequestDTO.class);
        when(request.getToken()).thenReturn("token-abc");
        when(request.getPlayerId()).thenReturn("p1");
        when(request.getPlayerName()).thenReturn("Alice");

        SimpMessageHeaderAccessor headers = mock(SimpMessageHeaderAccessor.class);
        when(headers.getSessionId()).thenReturn("session-999");

        LobbySnapshot snapshot = mock(LobbySnapshot.class);
        when(lobbyService.joinLobby("token-abc", "p1", "Alice", "session-999")).thenReturn(snapshot);

        controller.joinLobby(request, headers);

        verify(lobbyService).joinLobby("token-abc", "p1", "Alice", "session-999");
        verify(messagingTemplate).convertAndSend("/topic/lobby", snapshot);
        verifyNoMoreInteractions(messagingTemplate);
    }

    /* --- start tests --- */

    @Test
    void testStart_withValidToken_whenServicesSucceed_thenPublishesLobbyAndGameStateAndStartedEvent() {
        StartGameRequestDTO request = mock(StartGameRequestDTO.class);
        when(request.getToken()).thenReturn("token-x");

        LobbySnapshot lobbySnap = mock(LobbySnapshot.class);
        when(lobbyService.startGame("token-x")).thenReturn(lobbySnap);

        // publicSnapshot() returns Map (per your error)
        @SuppressWarnings("unchecked")
        Map<String, Object> publicSnapshot = mock(Map.class);
        doReturn(publicSnapshot).when(multiplayerGameService).publicSnapshot();

        List<String> turnOrder = List.of("p1", "p2");
        doReturn(turnOrder).when(multiplayerGameService).getTurnOrder();

        // We don't know the generic element type of the hand, so return a raw List mock safely.
        @SuppressWarnings("rawtypes")
        List hand1 = mock(List.class);
        @SuppressWarnings("rawtypes")
        List hand2 = mock(List.class);

        doReturn(hand1).when(multiplayerGameService).getHand("p1");
        doReturn(hand2).when(multiplayerGameService).getHand("p2");

        controller.start(request);

        verify(lobbyService).startGame("token-x");
        verify(multiplayerGameService).startFromLobby("token-x");

        InOrder inOrder = inOrder(messagingTemplate, multiplayerGameService);

        // lobby snapshot first
        inOrder.verify(messagingTemplate).convertAndSend("/topic/lobby", lobbySnap);

        // publishGameState: public snapshot
        inOrder.verify(multiplayerGameService).publicSnapshot();
        inOrder.verify(messagingTemplate).convertAndSend("/topic/game/token-x", publicSnapshot);

        // publishGameState: per-player hands
        inOrder.verify(multiplayerGameService).getTurnOrder();

        inOrder.verify(multiplayerGameService).getHand("p1");
        inOrder.verify(messagingTemplate).convertAndSend("/topic/game/token-x/hand/p1", hand1);

        inOrder.verify(multiplayerGameService).getHand("p2");
        inOrder.verify(messagingTemplate).convertAndSend("/topic/game/token-x/hand/p2", hand2);

        // started event
        inOrder.verify(messagingTemplate).convertAndSend("/topic/game/events", "STARTED");

        verifyNoMoreInteractions(messagingTemplate);
    }

    @Test
    void testStart_withValidToken_whenLobbyServiceThrows_thenPublishesErrorToTopicErrors() {
        StartGameRequestDTO request = mock(StartGameRequestDTO.class);
        when(request.getToken()).thenReturn("token-bad");

        doThrow(new RuntimeException("boom")).when(lobbyService).startGame("token-bad");

        controller.start(request);

        verify(lobbyService).startGame("token-bad");
        verify(messagingTemplate).convertAndSend("/topic/errors", "Failed to start game: boom");

        verify(multiplayerGameService, never()).startFromLobby(anyString());
        verify(multiplayerGameService, never()).publicSnapshot();
        verify(multiplayerGameService, never()).getTurnOrder();
        verify(multiplayerGameService, never()).getHand(anyString());

        verifyNoMoreInteractions(messagingTemplate);
    }

    /* --- lobbyStatus tests --- */

    @Test
    void testLobbyStatus_withNoArgs_thenPublishesLobbySnapshotToTopicLobby() {
        // getLobbyStatus() returns LobbySnapshot (per your error)
        LobbySnapshot status = mock(LobbySnapshot.class);
        when(lobbyService.getLobbyStatus()).thenReturn(status);

        controller.lobbyStatus();

        verify(lobbyService).getLobbyStatus();
        verify(messagingTemplate).convertAndSend("/topic/lobby", status);
        verifyNoMoreInteractions(messagingTemplate);
    }

    @Test
    void testHandle_withException_thenPublishesExceptionMessageToTopicErrors() {
        Exception ex = new Exception("nope");

        controller.handle(ex);

        verify(messagingTemplate).convertAndSend("/topic/errors", "nope");
        verifyNoMoreInteractions(messagingTemplate);
    }
}
