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

 /**
  * Test suite for {@link GameController}
  * Tests are mostly corrected versions of the original GameControllerTest class
  * There are some new AI generated tests cases to improve coverage,
  * particularly around playerId validation.
  * NOTE: AI generated tests should be reviewed for correctness
  * Coverage is 100% of lines and ~96% of branches
  */
 @WebMvcTest(GameController.class)
 public class GameControllerTests {

     @Autowired
     private MockMvc mockMvc;

     @MockitoBean
     private GameService gameService;

     private static final String PLAYER_ID = "p1";

     /* --- GET /playerpage --- */

     @Test
     void testPlayerPage_rendersViewWithModelFromService() throws Exception {
         List<Card> hand = Collections.emptyList();
         var topDiscard = mock(Card.class);

         when(gameService.getPlayerHand(PLAYER_ID)).thenReturn(hand);
         when(gameService.getTopDiscard()).thenReturn(topDiscard);
         when(gameService.checkTopDiscardWild()).thenReturn("None");
         when(gameService.isGameOver()).thenReturn(false);
         when(gameService.hasUno(PLAYER_ID)).thenReturn(false);

         mockMvc.perform(get("/playerpage")
                 .param("playerId", PLAYER_ID))
                 .andExpect(status().isOk())
                 .andExpect(view().name("UnoPlayerPage"))
                 .andExpect(model().attribute("playerHand", sameInstance(hand)))
                 .andExpect(model().attribute("discardCard", sameInstance(topDiscard)))
                 .andExpect(model().attribute("wildColour", is("None")))
                 .andExpect(model().attribute("gameOver", is(false)))
                 .andExpect(model().attribute("hasUno", is(false)));

         verify(gameService).getPlayerHand(PLAYER_ID);
         verify(gameService).getTopDiscard();
         verify(gameService).checkTopDiscardWild();
         verify(gameService).isGameOver();
         verify(gameService).hasUno(PLAYER_ID);
         verifyNoMoreInteractions(gameService);
     }

     @Test
     void testPlayerPage_whenPlayerIdMissing_thenRedirectsToPlayerPageWithNewID() throws Exception {
         String location = mockMvc.perform(get("/playerpage"))
                 .andExpect(status().is3xxRedirection())
                 .andReturn()
                 .getResponse()
                 .getRedirectedUrl();

         org.junit.jupiter.api.Assertions.assertNotNull(location);
         org.junit.jupiter.api.Assertions.assertTrue(location.startsWith("/playerpage?playerId="));

         verifyNoInteractions(gameService);
     }

     /* --- POST /draw --- */

     @Test
     void testDraw_whenGameNotOver_thenDrawsCardAndRedirectsToPlayerPage() throws Exception {
         when(gameService.isGameOver()).thenReturn(false);

         mockMvc.perform(post("/draw")
                 .param("playerId", PLAYER_ID))
                 .andExpect(status().is3xxRedirection())
                 .andExpect(redirectedUrl("/playerpage?playerId=" + PLAYER_ID));

         verify(gameService).isGameOver();
         verify(gameService).drawCard(PLAYER_ID);
         verifyNoMoreInteractions(gameService);
     }

     @Test
     void testDraw_whenGameOver_thenRedirectsToGameOverAndDoesNotDraw() throws Exception {
         when(gameService.isGameOver()).thenReturn(true);

         mockMvc.perform(post("/draw")
                 .param("playerId", PLAYER_ID))
                 .andExpect(status().is3xxRedirection())
                 .andExpect(redirectedUrl("/gameover?playerId=" + PLAYER_ID));

         verify(gameService).isGameOver();
         verifyNoMoreInteractions(gameService);
     }

     @Test
     void testDraw_whenPlayerIdNull_thenGeneratesPlayerIdAndDrawsCardAndRedirects() throws Exception {
         when(gameService.isGameOver()).thenReturn(false);

         String location = mockMvc.perform(post("/draw")) // no playerId param => null
                 .andExpect(status().is3xxRedirection())
                 .andReturn()
                 .getResponse()
                 .getRedirectedUrl();

         org.junit.jupiter.api.Assertions.assertNotNull(location);
         org.junit.jupiter.api.Assertions.assertTrue(location.startsWith("/playerpage?playerId="));

         String generatedId = location.substring("/playerpage?playerId=".length());
         java.util.UUID.fromString(generatedId); // validates it's a UUID

         verify(gameService).isGameOver();
         verify(gameService).drawCard(generatedId);
         verifyNoMoreInteractions(gameService);
     }

     @Test
     void testDraw_whenPlayerIdInvalid_thenGeneratesPlayerIdAndDrawsCardAndRedirects() throws Exception {
         when(gameService.isGameOver()).thenReturn(false);

         String location = mockMvc.perform(post("/draw").param("playerId", "   ")) // blank
                 .andExpect(status().is3xxRedirection())
                 .andReturn()
                 .getResponse()
                 .getRedirectedUrl();

         org.junit.jupiter.api.Assertions.assertNotNull(location);
         org.junit.jupiter.api.Assertions.assertTrue(location.startsWith("/playerpage?playerId="));

         String generatedId = location.substring("/playerpage?playerId=".length());
         java.util.UUID.fromString(generatedId);

         verify(gameService).isGameOver();
         verify(gameService).drawCard(generatedId);
         verifyNoMoreInteractions(gameService);
     }

     /* --- POST /play --- */

     @Test
     void testPlay_whenGameNotOver_thenPlayPlaysCardAndRedirectsToPlayerPage() throws Exception {
         when(gameService.isGameOver()).thenReturn(false);

         mockMvc.perform(post("/play")
                 .param("cardIndex", "0")
                 .param("playerId", PLAYER_ID))
                 .andExpect(status().is3xxRedirection())
                 .andExpect(redirectedUrl("/playerpage?playerId=" + PLAYER_ID));

         verify(gameService).playCard(PLAYER_ID, 0, null);
         verify(gameService).isGameOver();
         verifyNoMoreInteractions(gameService);
     }

     @Test
     void testPlay_whenGameOver_thenPlayRedirectsToGameOverAndDoesNotPlayCard() throws Exception {
         when(gameService.isGameOver()).thenReturn(true);

         mockMvc.perform(post("/play")
                         .param("cardIndex", "0")
                         .param("playerId", PLAYER_ID))
                 .andExpect(status().is3xxRedirection())
                 .andExpect(redirectedUrl("/gameover?playerId=" + PLAYER_ID));

         verify(gameService).playCard(PLAYER_ID, 0, null);
         verify(gameService).isGameOver();
         verifyNoMoreInteractions(gameService);
     }

     @Test
     void testPlay_whenWildCardPlayed_thenPlaysCardAndRedirectsToPlayerPage() throws Exception {
         when(gameService.isGameOver()).thenReturn(false);

         mockMvc.perform(post("/play")
                 .param("playerId", PLAYER_ID)
                 .param("cardIndex", "0")
                 .param("wildoutput", "Blue"))
                 .andExpect(status().is3xxRedirection())
                 .andExpect(redirectedUrl("/playerpage?playerId=" + PLAYER_ID));

         verify(gameService).playCard(PLAYER_ID, 0, "Blue");
         verify(gameService).isGameOver();
         verifyNoMoreInteractions(gameService);
     }

     @Test
     void testPlay_whenPlayerIdMissing_thenGeneratesPlayerId_playsCard_andRedirectsWithPlayerId() throws Exception {
         when(gameService.isGameOver()).thenReturn(false);

         String location = mockMvc.perform(post("/play").param("cardIndex", "0")) // no playerId param => null
                 .andExpect(status().is3xxRedirection())
                 .andReturn()
                 .getResponse()
                 .getRedirectedUrl();

         org.junit.jupiter.api.Assertions.assertNotNull(location);
         org.junit.jupiter.api.Assertions.assertTrue(location.startsWith("/playerpage?playerId="));

         String generatedId = location.substring("/playerpage?playerId=".length());
         java.util.UUID.fromString(generatedId); // validates it's a UUID

         verify(gameService).playCard(generatedId, 0, null);
         verify(gameService).isGameOver();
         verifyNoMoreInteractions(gameService);
     }

     @Test
     void testPlay_whenPlayerIdBlank_thenGeneratesPlayerId_playsCard_andRedirectsWithPlayerId() throws Exception {
         when(gameService.isGameOver()).thenReturn(false);

         String location = mockMvc.perform(post("/play")
                         .param("playerId", "   ") // blank
                         .param("cardIndex", "0"))
                 .andExpect(status().is3xxRedirection())
                 .andReturn()
                 .getResponse()
                 .getRedirectedUrl();

         org.junit.jupiter.api.Assertions.assertNotNull(location);
         org.junit.jupiter.api.Assertions.assertTrue(location.startsWith("/playerpage?playerId="));

         String generatedId = location.substring("/playerpage?playerId=".length());
         java.util.UUID.fromString(generatedId);

         verify(gameService).playCard(generatedId, 0, null);
         verify(gameService).isGameOver();
         verifyNoMoreInteractions(gameService);
     }

     /* --- POST /uno --- */

     @Test
     void testUno_whenGameNotOver_thenDeclareUnoAndRedirectToPlayerPage() throws Exception {
         when(gameService.isGameOver()).thenReturn(false);

         mockMvc.perform(post("/uno")
                 .param("playerId", PLAYER_ID))
                 .andExpect(status().is3xxRedirection())
                 .andExpect(redirectedUrl("/playerpage?playerId=" + PLAYER_ID));

         verify(gameService).isGameOver();
         verify(gameService).declareUno(PLAYER_ID);
         verifyNoMoreInteractions(gameService);
     }

     @Test
     void testUno_whenGameOver_thenRedirectsToGameOverAndDoesNotDeclare() throws Exception {
         when(gameService.isGameOver()).thenReturn(true);

         // Act + Assert
         mockMvc.perform(post("/uno")
                 .param("playerId", PLAYER_ID))
                 .andExpect(status().is3xxRedirection())
                 .andExpect(redirectedUrl("/gameover?playerId=" + PLAYER_ID));

         verify(gameService).isGameOver();
         verifyNoMoreInteractions(gameService);
     }

     @Test
     void testUno_whenPlayerIdMissing_thenGeneratesPlayerId_declaresUno_andRedirectsWithPlayerId() throws Exception {
         when(gameService.isGameOver()).thenReturn(false);

         String location = mockMvc.perform(post("/uno")) // playerId == null
                 .andExpect(status().is3xxRedirection())
                 .andReturn()
                 .getResponse()
                 .getRedirectedUrl();

         org.junit.jupiter.api.Assertions.assertNotNull(location);
         org.junit.jupiter.api.Assertions.assertTrue(location.startsWith("/playerpage?playerId="));

         String generatedId = location.substring("/playerpage?playerId=".length());
         java.util.UUID.fromString(generatedId); // validate UUID

         verify(gameService).isGameOver();
         verify(gameService).declareUno(generatedId);
         verifyNoMoreInteractions(gameService);
     }

     @Test
     void testUno_whenPlayerIdBlank_thenGeneratesPlayerId_declaresUno_andRedirectsWithPlayerId() throws Exception {
         when(gameService.isGameOver()).thenReturn(false);

         String location = mockMvc.perform(post("/uno")
                         .param("playerId", "   ")) // blank
                 .andExpect(status().is3xxRedirection())
                 .andReturn()
                 .getResponse()
                 .getRedirectedUrl();

         org.junit.jupiter.api.Assertions.assertNotNull(location);
         org.junit.jupiter.api.Assertions.assertTrue(location.startsWith("/playerpage?playerId="));

         String generatedId = location.substring("/playerpage?playerId=".length());
         java.util.UUID.fromString(generatedId);

         verify(gameService).isGameOver();
         verify(gameService).declareUno(generatedId);
         verifyNoMoreInteractions(gameService);
     }

     /* --- GET /game --- */

     @Test
     void testGame_whenRequested_thenRendersGameView() throws Exception {
         mockMvc.perform(get("/game"))
                 .andExpect(status().isOk())
                 .andExpect(view().name("game/game"));

         verifyNoInteractions(gameService);
     }

     /* --- GET /gameover --- */

     @Test
     void testGameOver_rendersGameOverViewWithModelFromService() throws Exception {
         List<Card> hand = Collections.emptyList();
         Card topDiscard = mock(Card.class);

         when(gameService.getPlayerHand(PLAYER_ID)).thenReturn(hand);
         when(gameService.getTopDiscard()).thenReturn(topDiscard);
         when(gameService.isGameOver()).thenReturn(true);

         mockMvc.perform(get("/gameover")
                 .param("playerId", PLAYER_ID))
                 .andExpect(status().isOk())
                 .andExpect(view().name("GameOver"))
                 .andExpect(model().attribute("playerHand", sameInstance(hand)))
                 .andExpect(model().attribute("discardCard", sameInstance(topDiscard)))
                 .andExpect(model().attribute("gameOver", is(true)));

     }

     @Test
     void testGameOver_whenPlayerIdMissing_thenGeneratesPlayerId_andCallsServiceWithIt() throws Exception {
         List<Card> hand = Collections.emptyList();
         Card topDiscard = mock(Card.class);

         when(gameService.getTopDiscard()).thenReturn(topDiscard);
         when(gameService.isGameOver()).thenReturn(true);
         // return hand for any generated id
         when(gameService.getPlayerHand(anyString())).thenReturn(hand);

         var result = mockMvc.perform(get("/gameover")) // playerId == null
                 .andExpect(status().isOk())
                 .andExpect(view().name("GameOver"))
                 .andExpect(model().attribute("discardCard", sameInstance(topDiscard)))
                 .andExpect(model().attribute("gameOver", is(true)))
                 .andExpect(model().attributeExists("playerId"))
                 .andExpect(model().attribute("playerHand", sameInstance(hand)))
                 .andReturn();

         String generatedId = (String) result.getModelAndView().getModel().get("playerId");
         org.junit.jupiter.api.Assertions.assertNotNull(generatedId);
         java.util.UUID.fromString(generatedId);

         verify(gameService).getPlayerHand(generatedId);
         verify(gameService).getTopDiscard();
         verify(gameService).isGameOver();
         verifyNoMoreInteractions(gameService);
     }

     @Test
     void testGameOver_whenPlayerIdBlank_thenGeneratesPlayerId_andCallsServiceWithIt() throws Exception {
         List<Card> hand = Collections.emptyList();
         Card topDiscard = mock(Card.class);

         when(gameService.getTopDiscard()).thenReturn(topDiscard);
         when(gameService.isGameOver()).thenReturn(true);
         when(gameService.getPlayerHand(anyString())).thenReturn(hand);

         var result = mockMvc.perform(get("/gameover").param("playerId", "   ")) // blank
                 .andExpect(status().isOk())
                 .andExpect(view().name("GameOver"))
                 .andExpect(model().attribute("discardCard", sameInstance(topDiscard)))
                 .andExpect(model().attribute("gameOver", is(true)))
                 .andExpect(model().attributeExists("playerId"))
                 .andExpect(model().attribute("playerHand", sameInstance(hand)))
                 .andReturn();

         String generatedId = (String) result.getModelAndView().getModel().get("playerId");
         org.junit.jupiter.api.Assertions.assertNotNull(generatedId);
         java.util.UUID.fromString(generatedId);

         verify(gameService).getPlayerHand(generatedId);
         verify(gameService).getTopDiscard();
         verify(gameService).isGameOver();
         verifyNoMoreInteractions(gameService);
     }


 }
