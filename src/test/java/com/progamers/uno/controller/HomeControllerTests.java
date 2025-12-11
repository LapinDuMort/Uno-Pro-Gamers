package com.progamers.uno.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
// we need to push game state into a service class
// otherwise tests share the state of the controller causing flakiness
// temp fix for now
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class HomeControllerTests {

    @Autowired
    MockMvc mockMvc;

    /* --- testing win condition --- */

    @Test
    void testWhenCardsRemainAfterPlay_thenRedirectsToPlayerPage() throws Exception {
        // new game with 7 cards
        // play should resume after one card played while others still exist in hand
        // expect to land on /playerpage
        mockMvc.perform(post("/play")
                .param("cardIndex", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/playerpage"));
    }

    @Test
    void testWhenFinalCardPlayed_thenRedirectToGameOver() throws Exception {
        // new game with 7 cards
        // play 6 cards
        // each play should redirect to /playerpage
        for (int i=0; i<6; i++) {
            mockMvc.perform(post("/play")
                    .param("cardIndex", "0"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/playerpage"));
        }

        // we expect there to be 1 card left
        // playing final card should trigger win condition
        // redirec to /gameover
        mockMvc.perform(post("/play")
                .param("cardIndex", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gameover"));
    }

    @Test
    void testWhenGameOverIsTrue_thenPlayAndDrawRedirectToGameOver() throws Exception {
        // when gameOver is true
        // play() and draw() should redirect to game over screen
        // until a new game is started

        // push player to winning position
        for (int i=0; i<7; i++) {
            mockMvc.perform(post("/play").param("cardIndex", "0"));
        }

        // we expect game to be over
        // trying to play card should redirect to /gameover
        mockMvc.perform(post("/play")
                .param("cardIndex", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gameover"));

        // draw should also redirect to g/ameover
        mockMvc.perform(post("/draw"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gameover"));
    }

    @Test
    void testGameOverEndpointRendersGameOverView() throws Exception {
        // manual navigation to this endpoint will work if game is not over yet
        // testing functionality rather than internal game state
        mockMvc.perform(get("/gameover"))
                .andExpect(status().isOk())
                .andExpect(view().name("GameOver"))
                .andExpect(model().attributeExists("playerHand"))
                .andExpect(model().attributeExists("discardCard"))
                .andExpect(model().attributeExists("gameOver"));
    }
}
