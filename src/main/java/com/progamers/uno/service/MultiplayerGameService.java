package com.progamers.uno.service;

import com.progamers.uno.domain.cards.Card;
import com.progamers.uno.domain.cards.Value;
import com.progamers.uno.domain.game.Game;
import com.progamers.uno.domain.multiplayer.lobby.LobbyPlayer;
import com.progamers.uno.domain.player.Player;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Getter
public class MultiplayerGameService {

    private final LobbyService lobbyService;

    // Single active multiplayer game (single lobby)
    private Game game;
    private boolean gameOver;

    private final Map<String, Player> playersById = new LinkedHashMap<>();
    private final Map<String, String> playerNamesById = new LinkedHashMap<>();
    private final List<String> turnOrder = new ArrayList<>();
    private int currentTurnIndex = 0;

    public MultiplayerGameService(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    public synchronized boolean isActive() {
        return game != null && !turnOrder.isEmpty();
    }

    public synchronized void startFromLobby(String token) {
        // Note: lobbyService.startGame(token) is called by LobbyWebSocketController
        // Do not call it again here to avoid "Lobby is not open" error

        // Fresh game each start (demo-safe)
        Game g = new Game();
        g.getCardDeck().shuffle();
        g.getDiscardPile().addToPile(g.getCardDeck().drawCard());

        // Reset multiplayer containers
        playersById.clear();
        playerNamesById.clear();
        turnOrder.clear();
        currentTurnIndex = 0;
        gameOver = false;

        // Create per-player Player and deal 7 each
        for (LobbyPlayer lp : lobbyService.getPlayersInOrder()) {
            Player p = new Player();
            g.drawCards(p, 7); // already player-parameterized
            playersById.put(lp.getPlayerId(), p);
            playerNamesById.put(lp.getPlayerId(), lp.getPlayerName());
            turnOrder.add(lp.getPlayerId());
        }

        this.game = g;
    }

    public synchronized String getCurrentPlayerId() {
        if (!isActive()) return null;
        return turnOrder.get(currentTurnIndex);
    }

    private void requireActive() {
        if (!isActive()) throw new IllegalStateException("Multiplayer game not started.");
    }

    private Player requirePlayer(String playerId) {
        Player p = playersById.get(playerId);
        if (p == null) throw new IllegalArgumentException("Unknown playerId: " + playerId);
        return p;
    }

    private void requireMyTurn(String playerId) {
        if (!Objects.equals(getCurrentPlayerId(), playerId)) {
            throw new IllegalStateException("Not your turn.");
        }
    }

    private void advanceTurn() {
        currentTurnIndex = (currentTurnIndex + 1) % turnOrder.size();
    }

    public synchronized java.util.List<String> getTurnOrder() {
        return new java.util.ArrayList<>(turnOrder);
    }

    /* ===== Read model ===== */

    public synchronized List<Card> getHand(String playerId) {
        requireActive();
        return requirePlayer(playerId).getPlayerHand();
    }

    public synchronized Card getTopDiscard() {
        requireActive();
        return game.getDiscardPile().getTopCard();
    }

    public synchronized String getWildColourOrNone() {
        requireActive();
        return game.getDiscardPile().WildColour != null ? game.getDiscardPile().WildColour : "None";
    }

    public synchronized boolean hasUno(String playerId) {
        requireActive();
        return requirePlayer(playerId).getHasUno();
    }

    /* ===== Actions ===== */

    public synchronized void declareUno(String playerId) {
        requireActive();
        requireMyTurn(playerId);
        requirePlayer(playerId).declareUno();
    }

    public synchronized void drawCard(String playerId) {
        requireActive();
        if (gameOver) return;
        requireMyTurn(playerId);

        Player p = requirePlayer(playerId);
        game.drawCards(p, 1);

        // demo-simple: draw ends turn
        advanceTurn();
    }

    public synchronized void playCard(String playerId, int cardIndex, String wildColor) throws Exception {
        requireActive();
        if (gameOver) return;
        requireMyTurn(playerId);

        Player p = requirePlayer(playerId);

        Card selected = p.getCurrentSelectedCard(cardIndex);
        Card top = game.getDiscardPile().getTopCard();

        if (!game.isValidMove(top, selected)) return;

        // Your existing “UNO penalty” rule, now per-player
        if (p.getHandSize() == 1 && !p.getHasUno()) {
            game.drawCards(p, 2);
            advanceTurn();
            return;
        }

        game.getDiscardPile().addToPile(p.playCard(cardIndex));

        if (wildColor != null) {
            System.out.println("=== Setting wild colour to: " + wildColor);
            game.getDiscardPile().setWildColour(wildColor);
        } else {
            System.out.println("=== wildColor is NULL, not setting wild colour");
        }

        // Handle special card effects
        Card playedCard = game.getDiscardPile().getTopCard();
        if (playedCard.getValue().equals(Value.Skip)) {
            // Skip card: advance turn twice to skip the next player
            advanceTurn();
        } else if (playedCard.getValue().equals(Value.DrawTwo)) {
            // DrawTwo card: next player draws 2 cards and their turn is skipped
            advanceTurn();
            Player nextPlayer = requirePlayer(getCurrentPlayerId());
            game.drawCards(nextPlayer, 2);
            advanceTurn();
            return;
        }

        if (p.getHandSize() == 0) {
            gameOver = true;
            return;
        }

        advanceTurn();
    }

    /* ===== Optional: tiny snapshot for STOMP broadcast (no hands) ===== */

    public synchronized Map<String, Object> publicSnapshot() {
        requireActive();

        List<Map<String, Object>> players = new ArrayList<>();
        for (String id : turnOrder) {
            Player p = requirePlayer(id);
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("playerId", id);
            entry.put("playerName", playerNamesById.get(id));
            entry.put("handSize", p.getHandSize());
            entry.put("hasUno", p.getHasUno());
            players.add(entry);
        }

        Map<String, Object> snap = new LinkedHashMap<>();
        snap.put("gameOver", gameOver);
        snap.put("currentPlayerId", getCurrentPlayerId());
        snap.put("topDiscard", getTopDiscard());
        snap.put("wildColour", getWildColourOrNone());
        snap.put("players", players);
        return snap;
    }
}
