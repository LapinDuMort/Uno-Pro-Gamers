package com.progamers.uno.controller;

import com.progamers.uno.domain.multiplayer.game.dto.DeclareUnoRequestDTO;
import com.progamers.uno.domain.multiplayer.game.dto.DrawCardRequestDTO;
import com.progamers.uno.domain.multiplayer.game.dto.PlayCardRequestDTO;
import com.progamers.uno.service.MultiplayerGameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Test suite for {@link GameWebSocketController}
 * Tests are all AI generated, though there was an error that was manually corrected:
 * Mockito does NOT like when behaviours are tested that aren't interacted with
 * Tests have been partially, but not fully, reviewed for robustness
 * Coverage is 100%
 */
@ExtendWith(MockitoExtension.class)
class GameWebSocketControllerTests {

    @Mock
    private MultiplayerGameService gameService;

    @Mock
    private SimpMessagingTemplate messaging;

    private GameWebSocketController controller;

    @BeforeEach
    void setUp() {
        controller = new GameWebSocketController(gameService, messaging);
    }

    /* --- play tests --- */

    @Test
    void testPlay_withValidDto_thenCallsServiceAndPublishesSnapshotAndHands() throws Exception {
        // arrange
        PlayCardRequestDTO dto = mock(PlayCardRequestDTO.class);
        when(dto.getPlayerId()).thenReturn("p1");
        when(dto.getCardIndex()).thenReturn(3);
        when(dto.getWildColour()).thenReturn("RED");
        when(dto.getToken()).thenReturn("token-123");

        when(gameService.publicSnapshot()).thenReturn(Collections.emptyMap());
        when(gameService.getTurnOrder()).thenReturn(List.of("p1", "p2"));
        when(gameService.getHand("p1")).thenReturn(Collections.emptyList());
        when(gameService.getHand("p2")).thenReturn(Collections.emptyList());

        // act
        controller.play(dto);

        // assert
        verify(gameService).playCard("p1", 3, "RED");

        InOrder inOrder = inOrder(messaging);
        inOrder.verify(messaging).convertAndSend("/topic/game/token-123", Collections.emptyMap());
        inOrder.verify(messaging).convertAndSend("/topic/game/token-123/hand/p1", Collections.emptyList());
        inOrder.verify(messaging).convertAndSend("/topic/game/token-123/hand/p2", Collections.emptyList());

        verifyNoMoreInteractions(messaging);
    }

    @Test
    void testPlay_whenServiceThrows_thenRethrowsAndDoesNotPublish() throws Exception {
        // arrange
        PlayCardRequestDTO dto = mock(PlayCardRequestDTO.class);
        when(dto.getPlayerId()).thenReturn("p1");
        when(dto.getCardIndex()).thenReturn(1);
        when(dto.getWildColour()).thenReturn(null);

        doThrow(new RuntimeException("boom"))
                .when(gameService).playCard("p1", 1, null);

        // act + assert
        assertThrows(RuntimeException.class, () -> controller.play(dto));

        verify(gameService).playCard("p1", 1, null);
        verify(gameService, never()).publicSnapshot();
        verify(messaging, never()).convertAndSend(anyString(), any(Object.class));
    }

    /* --- draw tests --- */

    @Test
    void testDraw_withValidDto_thenCallsServiceAndPublishesSnapshotAndHands() {
        // arrange
        DrawCardRequestDTO dto = mock(DrawCardRequestDTO.class);
        when(dto.getPlayerId()).thenReturn("p1");
        when(dto.getToken()).thenReturn("token-123");

        when(gameService.publicSnapshot()).thenReturn(Collections.emptyMap());
        when(gameService.getTurnOrder()).thenReturn(List.of("p1", "p2"));
        when(gameService.getHand("p1")).thenReturn(Collections.emptyList());
        when(gameService.getHand("p2")).thenReturn(Collections.emptyList());

        // act
        controller.draw(dto);

        // assert
        verify(gameService).drawCard("p1");

        InOrder inOrder = inOrder(messaging);
        inOrder.verify(messaging).convertAndSend("/topic/game/token-123", Collections.emptyMap());
        inOrder.verify(messaging).convertAndSend("/topic/game/token-123/hand/p1", Collections.emptyList());
        inOrder.verify(messaging).convertAndSend("/topic/game/token-123/hand/p2", Collections.emptyList());

        verifyNoMoreInteractions(messaging);
    }

    @Test
    void testDraw_whenServiceThrows_thenSwallowsAndDoesNotPublish() {
        // arrange
        DrawCardRequestDTO dto = mock(DrawCardRequestDTO.class);
        when(dto.getPlayerId()).thenReturn("p1");

        doThrow(new RuntimeException("boom")).when(gameService).drawCard("p1");

        // act (should not throw)
        controller.draw(dto);

        // assert
        verify(gameService).drawCard("p1");
        verify(gameService, never()).publicSnapshot(); // publish() not entered
        verify(messaging, never()).convertAndSend(anyString(), any(Object.class));
    }

    /* --- uno tests --- */

    @Test
    void testUno_withValidDto_thenCallsServiceAndPublishesSnapshotAndHands() {
        // arrange
        DeclareUnoRequestDTO dto = mock(DeclareUnoRequestDTO.class);
        when(dto.getPlayerId()).thenReturn("p1");
        when(dto.getToken()).thenReturn("token-123");

        when(gameService.publicSnapshot()).thenReturn(Collections.emptyMap());
        when(gameService.getTurnOrder()).thenReturn(List.of("p1", "p2"));
        when(gameService.getHand("p1")).thenReturn(Collections.emptyList());
        when(gameService.getHand("p2")).thenReturn(Collections.emptyList());

        // act
        controller.uno(dto);

        // assert
        verify(gameService).declareUno("p1");

        InOrder inOrder = inOrder(messaging);
        inOrder.verify(messaging).convertAndSend("/topic/game/token-123", Collections.emptyMap());
        inOrder.verify(messaging).convertAndSend("/topic/game/token-123/hand/p1", Collections.emptyList());
        inOrder.verify(messaging).convertAndSend("/topic/game/token-123/hand/p2", Collections.emptyList());

        verifyNoMoreInteractions(messaging);
    }

    @Test
    void testUno_whenServiceThrows_thenSwallowsAndDoesNotPublish() {
        // arrange
        DeclareUnoRequestDTO dto = mock(DeclareUnoRequestDTO.class);
        when(dto.getPlayerId()).thenReturn("p1");

        doThrow(new RuntimeException("boom"))
                .when(gameService).declareUno("p1");

        // act (should not throw)
        controller.uno(dto);

        // assert
        verify(gameService).declareUno("p1");
        verify(gameService, never()).publicSnapshot();
        verify(messaging, never()).convertAndSend(anyString(), any(Object.class));
    }


    /* --- syncGameState tests --- */

    @Test
    void testSyncGameState_withValidDto_thenPublishesSnapshotAndHandsWithoutMutations() throws Exception {
        // arrange
        PlayCardRequestDTO dto = mock(PlayCardRequestDTO.class);
        when(dto.getPlayerId()).thenReturn("p1");
        when(dto.getToken()).thenReturn("token-123");

        when(gameService.publicSnapshot()).thenReturn(Collections.emptyMap());
        when(gameService.getTurnOrder()).thenReturn(List.of("p1", "p2"));
        when(gameService.getHand("p1")).thenReturn(Collections.emptyList());
        when(gameService.getHand("p2")).thenReturn(Collections.emptyList());

        // act
        controller.syncGameState(dto);

        // assert
        verify(gameService, never()).playCard(anyString(), anyInt(), any());
        verify(gameService, never()).drawCard(anyString());
        verify(gameService, never()).declareUno(anyString());

        InOrder inOrder = inOrder(messaging);
        inOrder.verify(messaging).convertAndSend("/topic/game/token-123", Collections.emptyMap());
        inOrder.verify(messaging).convertAndSend("/topic/game/token-123/hand/p1", Collections.emptyList());
        inOrder.verify(messaging).convertAndSend("/topic/game/token-123/hand/p2", Collections.emptyList());

        verifyNoMoreInteractions(messaging);
    }

    @Test
    void testSyncGameState_whenPublishFails_thenSwallowsException() {
        // arrange
        PlayCardRequestDTO dto = mock(PlayCardRequestDTO.class);
        when(dto.getToken()).thenReturn("token-123");

        // publish() starts here
        when(gameService.publicSnapshot()).thenReturn(Collections.emptyMap());

        // first send fails
        doThrow(new RuntimeException("send failed"))
                .when(messaging)
                .convertAndSend(eq("/topic/game/token-123"), any(Object.class));

        // act (should not throw)
        controller.syncGameState(dto);

        // assert
        verify(gameService).publicSnapshot();
        verify(messaging).convertAndSend(eq("/topic/game/token-123"), any(Object.class));

        // execution stops before hands are sent
        verify(gameService, never()).getTurnOrder();
        verify(messaging, never()).convertAndSend(contains("/hand/"), any(Object.class));
    }

}
