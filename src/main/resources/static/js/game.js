/* global SockJS, Stomp */

let stompClient = null;
let token = null;
let playerId = null;

function $(id) { return document.getElementById(id); }

function log(msg) {
  const el = $("log");
  el.textContent += `[${new Date().toLocaleTimeString()}] ${msg}\n`;
  el.scrollTop = el.scrollHeight;
}

function parseQuery() {
  const qs = new URLSearchParams(window.location.search);
  return {
    token: qs.get("token") || localStorage.getItem("uno.token") || "",
    playerId: qs.get("playerId") || localStorage.getItem("uno.playerId") || ""
  };
}

function renderPublic(snap) {
  $("turnLabel").textContent = snap.currentPlayerId || "?";
  $("wildLabel").textContent = snap.wildColour || "None";

  // topDiscard is a Card object; render compactly
  $("discardLabel").textContent = snap.topDiscard ? JSON.stringify(snap.topDiscard) : "?";

  const ul = $("players");
  ul.innerHTML = "";
  (snap.players || []).forEach(p => {
    const li = document.createElement("li");
    const turnMark = (p.playerId === snap.currentPlayerId) ? " ⬅︎ turn" : "";
    li.textContent = `${p.playerName || p.playerId} | hand=${p.handSize} | uno=${p.hasUno}${turnMark}`;
    ul.appendChild(li);
  });

  if (snap.gameOver) {
    log("Game over!");
  }
}

function renderHand(hand) {
  const root = $("hand");
  root.innerHTML = "";

  (hand || []).forEach((card, idx) => {
    const el = document.createElement("span");
    el.className = "card";
    el.textContent = `${idx}: ${formatCard(card)}`;
    el.addEventListener("click", () => {
      // If this is a wild card, prompt for colour (demo-friendly)
      let wildColor = null;

      // Heuristic: if card has value/type like "WILD" or "WILD_DRAW_FOUR"
      const text = JSON.stringify(card).toUpperCase();
      if (text.includes("WILD")) {
        wildColor = prompt("Choose wild colour (e.g., RED, GREEN, BLUE, YELLOW):", "RED") || null;
      }

      playCard(idx, wildColor);
    });
    root.appendChild(el);
  });
}

function formatCard(card) {
  if (!card) return "?";
  // If your Card has fields like colour/value, render them. Otherwise fallback to JSON.
  if (card.colour && card.value) return `${card.colour} ${card.value}`;
  if (card.color && card.value) return `${card.color} ${card.value}`;
  return JSON.stringify(card);
}

function connectGame() {
  const q = parseQuery();
  token = q.token;
  playerId = q.playerId;

  if (!token || !playerId) {
    log("Missing token or playerId. Go back to lobby and click 'Go to Game Page'.");
    return;
  }

  $("tokenLabel").textContent = token;
  $("playerLabel").textContent = playerId;

  // Persist for refresh
  localStorage.setItem("uno.token", token);
  localStorage.setItem("uno.playerId", playerId);

  const socket = new SockJS("/ws");
  stompClient = Stomp.over(socket);
  stompClient.debug = () => {};

  stompClient.connect({}, () => {
    log("Connected to game STOMP.");

    // Public game state
    stompClient.subscribe(`/topic/game/${token}`, (msg) => {
      const snap = JSON.parse(msg.body);
      renderPublic(snap);
    });

    // My hand
    stompClient.subscribe(`/topic/game/${token}/hand/${playerId}`, (msg) => {
      const hand = JSON.parse(msg.body);
      renderHand(hand);
    });

    // Optional errors
    stompClient.subscribe("/topic/errors", (msg) => {
      log("ERROR: " + msg.body);
    });

  }, (err) => {
    log("STOMP error: " + err);
  });

  $("drawBtn").addEventListener("click", drawCard);
  $("unoBtn").addEventListener("click", declareUno);
}

function playCard(cardIndex, wildColor) {
  if (!stompClient) return;
  stompClient.send("/app/game/play", {}, JSON.stringify({
    token,
    playerId,
    cardIndex,
    wildColor
  }));
}

function drawCard() {
  if (!stompClient) return;
  stompClient.send("/app/game/draw", {}, JSON.stringify({
    token,
    playerId
  }));
}

function declareUno() {
  if (!stompClient) return;
  stompClient.send("/app/game/uno", {}, JSON.stringify({
    token,
    playerId
  }));
}
