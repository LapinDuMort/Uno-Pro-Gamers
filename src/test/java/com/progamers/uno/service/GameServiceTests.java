 package com.progamers.uno.service;

 import com.progamers.uno.domain.cards.Card;
 import com.progamers.uno.domain.cards.Colour;
 import com.progamers.uno.domain.cards.Value;
 import com.progamers.uno.domain.game.Game;
 import com.progamers.uno.domain.player.Player;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;

 import java.util.List;

 import static org.junit.jupiter.api.Assertions.*;

 public class GameServiceTests {

     private GameService gameService;
     private Game game;
     private static final String PLAYER_ID = "p123";

     @BeforeEach
     void setup() {
         this.gameService = new GameService();
         this.game = this.gameService.getGame();
     }

     /* --- helpers --- */

     private void setupSingleValidCardHand(String playerId) {
         List<Card> hand = gameService.getPlayerHand(playerId);
         hand.clear();

         Card top = Card.builder()
                 .colour(Colour.Red)
                 .value(Value.One)
                 .build();

         game.getDiscardPile().addToPile(top);

         hand.add(Card.builder()
                 .colour(Colour.Red)
                 .value(Value.One)
                 .build());
     }

     private void setupTwoCardHand(String playerId) {
         List<Card> hand = gameService.getPlayerHand(playerId);
         hand.clear();
         hand.add(Card.builder().colour(Colour.Red).value(Value.One).build());
         hand.add(Card.builder().colour(Colour.Blue).value(Value.Two).build());
     }

    /* --- new api tests --- */

    /* --- getPlayerHand tests --- */

     @Test
     void testGetPlayerHand_withNewPlayer_thenReturnsSevenCards() {
         List<Card> hand = gameService.getPlayerHand(PLAYER_ID);
         assertNotNull(hand);
         assertEquals(7, hand.size());
     }

     @Test
     void testGetPlayerHand_withSamePlayerId_thenReturnsSameHandInstance() {
         List<Card> first = gameService.getPlayerHand(PLAYER_ID);
         List<Card> second = gameService.getPlayerHand(PLAYER_ID);
         assertSame(first, second);
     }

     /* --- drawCard tests --- */

     @Test
     void testDrawCard_withActiveGame_thenIncreasesHandSize() {
         int before = gameService.getPlayerHand(PLAYER_ID).size();
         gameService.drawCard(PLAYER_ID);
         int after = gameService.getPlayerHand(PLAYER_ID).size();
         assertEquals(before + 1, after);
     }

     @Test
     void testDrawCard_withGameOver_thenDoesNothing() throws Exception {
         setupSingleValidCardHand(PLAYER_ID);
         gameService.declareUno(PLAYER_ID);
         gameService.playCard(PLAYER_ID, 0);
         assertTrue(gameService.isGameOver());
         int before = gameService.getPlayerHand(PLAYER_ID).size();
         gameService.drawCard(PLAYER_ID);
         int after = gameService.getPlayerHand(PLAYER_ID).size();
         assertEquals(before, after);
     }

     /* --- declareUno tests --- */

     @Test
     void testDeclareUno_withSingleCard_thenHasUnoIsTrue() {
         setupSingleValidCardHand(PLAYER_ID);
         gameService.declareUno(PLAYER_ID);
         assertTrue(gameService.hasUno(PLAYER_ID));
     }

     @Test
     void testDeclareUno_withMultipleCards_thenHasUnoIsFalse() {
         setupTwoCardHand(PLAYER_ID);
         gameService.declareUno(PLAYER_ID);
         assertFalse(gameService.hasUno(PLAYER_ID));
     }

    /* --- playCard tests --- */

     @Test
     void testPlayCard_withFinalCardAndUnoDeclared_thenGameEnds() throws Exception {
         setupSingleValidCardHand(PLAYER_ID);
         gameService.declareUno(PLAYER_ID);
         gameService.playCard(PLAYER_ID, 0);
         assertTrue(gameService.isGameOver());
         assertEquals(0, gameService.getPlayerHand(PLAYER_ID).size());
     }

     @Test
     void testPlayCard_withFinalCardAndNoUno_thenPenaltyApplied() throws Exception {
         setupSingleValidCardHand(PLAYER_ID);
         gameService.playCard(PLAYER_ID, 0);
         assertEquals(3, gameService.getPlayerHand(PLAYER_ID).size());
         assertFalse(gameService.isGameOver());
     }

     @Test
     void testPlayCard_withInvalidMove_thenStateDoesNotChange() throws Exception {
         List<Card> hand = gameService.getPlayerHand(PLAYER_ID);
         hand.clear();
         game.getDiscardPile().addToPile(
                 Card.builder().colour(Colour.Red).value(Value.Five).build()
         );
         hand.add(
                 Card.builder().colour(Colour.Green).value(Value.Nine).build()
         );

         int before = hand.size();
         gameService.playCard(PLAYER_ID, 0);
         assertEquals(before, hand.size());
         assertFalse(gameService.isGameOver());
     }

     /* --- checkTopDiscardWild tests --- */

     @Test
     void checkTopDiscardWild_withNoWildPlayed_thenReturnsNone() {
         assertEquals("None", gameService.checkTopDiscardWild());
     }

 }
