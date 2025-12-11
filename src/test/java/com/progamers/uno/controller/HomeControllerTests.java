package com.progamers.uno.controller;

import com.progamers.uno.PlayerController;
import com.progamers.uno.domain.Card;
import com.progamers.uno.domain.Colour;
import com.progamers.uno.domain.Value;
import com.progamers.uno.domain.game.Game;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

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

    @Autowired
    HomeController homeController;

    /* --- testing uno declaration --- */

    @Test
    void testWhenDeclareUno_thenRedirectsToPlayerPage() throws Exception {
        mockMvc.perform(post("/uno"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/playerpage"));
    }

    @Test
    void testWhenPlayFinalCardWithoutUnoDeclared_thenDrawsTwoCardsAndGameContinues() throws Exception {
        // new game with 7 cards
        // play down to 6 cards
        // do NOT declare uno
        for (int i=0; i<6; i++) {
            mockMvc.perform(post("/play")
                            .param("cardIndex", "0"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/playerpage"));
        }

        // final card
        // uno NOT declared
        // try to play final card
        // expect game NOT to end - no redirect to /gameover
        mockMvc.perform(post("/play")
                .param("cardIndex", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/playerpage"));
    }

    @Test
    void testWhenDeclareUnoAndPlayFinalCard_thenRedirectsToGameOver() throws Exception {
        // explicit 1 card hand
        // uno declared & valid move
        // grab real domain objects from controller
        PlayerController player = homeController.MyplayerController;
        Game game = homeController.Mygame;

        // ensure discard pile top card is known
        Card topCard = Card.builder()
                .colour(Colour.Red)
                .value(Value.One)
                .build();

        game.getDiscardPile().addToPile(topCard);

        // give player exactly 1 card
        var hand = new ArrayList<Card>();
        Card lastCard = Card.builder()
                .colour(Colour.Red)
                .value(Value.Two)
                .build();
        hand.add(lastCard);
        player.setPlayerHand(hand);

        // declare uno
        mockMvc.perform(post("/uno"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/playerpage"));

        // expect to redirect to /gameover
        // after playing final card
        mockMvc.perform(post("/play").param("cardIndex", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gameover"));
    }

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
    void testWhenGameOverIsTrue_thenPlayAndDrawRedirectToGameOver() throws Exception {
        // when gameOver is true
        // play() and draw() should redirect to game over screen
        // until a new game is started

        homeController.gameOver = true;

        // update: play final card to win game
        // expect redirect to /gameover
        mockMvc.perform(post("/play")
                .param("cardIndex", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gameover"));

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
