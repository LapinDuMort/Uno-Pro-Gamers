package com.progamers.uno.controller;

import com.progamers.uno.domain.cards.Card;
import com.progamers.uno.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
public class GameControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

    /* --- GET /playerpage --- */

    @Test
    void playerPage_rendersViewWithModelFromService() throws Exception {
        List<Card> hand = Collections.emptyList();
        var topDiscard = mock(Card.class);

        when(gameService.getPlayerHand()).thenReturn(hand);
        when(gameService.getTopDiscard()).thenReturn(topDiscard);
        when(gameService.checkTopDiscardWild()).thenReturn("None");
        when(gameService.isGameOver()).thenReturn(false);
        when(gameService.hasUno()).thenReturn(false);

        mockMvc.perform(get("/playerpage"))
                .andExpect(status().isOk())
                .andExpect(view().name("UnoPlayerPage"))
                .andExpect(model().attribute("playerHand", sameInstance(hand)))
                .andExpect(model().attribute("discardCard", sameInstance(topDiscard)))
                .andExpect(model().attribute("wildColour", is("None")))
                .andExpect(model().attribute("gameOver", is(false)))
                .andExpect(model().attribute("hasUno", is(false)));

        verify(gameService).getPlayerHand();
        verify(gameService).getTopDiscard();
        verify(gameService).checkTopDiscardWild();
        verify(gameService).isGameOver();
        verify(gameService).hasUno();
        verifyNoMoreInteractions(gameService);
    }

    /* --- POST /draw --- */

    @Test
    void testDraw_whenGameNotOver_thenDrawsCardAndRedirectsToPlayerPage() throws Exception {
        when(gameService.isGameOver()).thenReturn(false);

        mockMvc.perform(post("/draw"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/playerpage"));

        verify(gameService).isGameOver();
        verify(gameService).drawCard();
        verifyNoMoreInteractions(gameService);
    }

    @Test
    void testDraw_whenGameOver_thenRedirectsToGameOverAndDoesNotDraw() throws Exception {
        when(gameService.isGameOver()).thenReturn(true);

        mockMvc.perform(post("/draw"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gameover"));

        verify(gameService).isGameOver();
        verifyNoMoreInteractions(gameService);
    }

    /* --- POST /play --- */

    @Test
    void testPlay_whenGameNotOver_thenPlayPlaysCardAndRedirectsToPlayerPage() throws Exception {
        when(gameService.isGameOver()).thenReturn(false);

        mockMvc.perform(post("/play")
                .param("cardIndex", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/playerpage"));

        verify(gameService).playCard(0, null);
        verify(gameService).isGameOver();
        verifyNoMoreInteractions(gameService);
    }

    @Test
    void testPlay_whenGameOver_thenPlayRedirectsToGameOverAndDoesNotPlayCard() throws Exception {
        when(gameService.isGameOver()).thenReturn(true);

        mockMvc.perform(post("/play").param("cardIndex", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gameover"));

        verify(gameService).playCard(0, null);
        verify(gameService).isGameOver();
        verifyNoMoreInteractions(gameService);
    }

    @Test
    void testPlay_whenWildCardPlayed_thenPlaysCardAndRedirectsToPlayerPage() throws Exception {
        when(gameService.isGameOver()).thenReturn(false);

        mockMvc.perform(post("/play")
                        .param("cardIndex", "0")
                        .param("wildoutput", "Blue"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/playerpage"));

        verify(gameService).playCard(0, "Blue");
        verify(gameService).isGameOver();
        verifyNoMoreInteractions(gameService);
    }

    /* --- POST /uno --- */

    @Test
    void testUno_whenGameNotOver_thenDeclareUnoAndRedirectToPlayerPage() throws Exception {
        when(gameService.isGameOver()).thenReturn(false);

        mockMvc.perform(post("/uno"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/playerpage"));

        verify(gameService).isGameOver();
        verify(gameService).declareUno();
        verifyNoMoreInteractions(gameService);
    }

    @Test
    void testUno_whenGameOver_thenRedirectsToGameOverAndDoesNotDeclare() throws Exception {
        when(gameService.isGameOver()).thenReturn(true);

        // Act + Assert
        mockMvc.perform(post("/uno"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gameover"));

        verify(gameService).isGameOver();
        verifyNoMoreInteractions(gameService);
    }

    /* --- GET /gameover --- */

    @Test
    void testGameOver_rendersGameOverViewWithModelFromService() throws Exception {
        List<Card> hand = Collections.emptyList();
        Card topDiscard = mock(Card.class);

        when(gameService.getPlayerHand()).thenReturn(hand);
        when(gameService.getTopDiscard()).thenReturn(topDiscard);
        when(gameService.isGameOver()).thenReturn(true);

        mockMvc.perform(get("/gameover"))
                .andExpect(status().isOk())
                .andExpect(view().name("GameOver"))
                .andExpect(model().attribute("playerHand", sameInstance(hand)))
                .andExpect(model().attribute("discardCard", sameInstance(topDiscard)))
                .andExpect(model().attribute("gameOver", is(true)));

    }
}
