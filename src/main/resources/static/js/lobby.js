let stompClient = null;
let connected = false;

// Keep a stable playerId per tab so reconnects don't create "new" players.
const playerId = localStorage.getItem("unoPlayerId") || crypto.randomUUID();
localStorage.setItem("unoPlayerId", playerId);

const nameInput = document.getElementById("playerName");
const tokenInput = document.getElementById("token");
const copyBtn = document.getElementById("copyBtn");
const createBtn = document.getElementById("createBtn");
const joinBtn = document.getElementById("joinBtn");
const startBtn = document.getElementById("startBtn");
const goGameBtn = document.getElementById("goGameBtn");
const statusEl = document.getElementById("status");
const playersList = document.getElementById("players");
const logEl = document.getElementById("log");

function log(msg) {
  logEl.textContent += msg + "\n";
  logEl.scrollTop = logEl.scrollHeight;
}

function normalizePlayerName() {
  const n = (nameInput.value || "").trim();
  return n.length ? n : "Player";
}

function renderPlayers(names) {
  playersList.innerHTML = "";
  (names || []).forEach(n => {
    const li = document.createElement("li");
    li.textContent = n;
    playersList.appendChild(li);
  });
}

function setUiConnected(isConnected) {
  createBtn.disabled = !isConnected;
  joinBtn.disabled = !isConnected;
  startBtn.disabled = true;
  goGameBtn.disabled = true;
  copyBtn.disabled = !isConnected || !tokenInput.value;
}

function setUiForLobbyState(snapshot) {
  const state = snapshot?.lobbyState;
  const playerCount = (snapshot?.playerNames || []).length;
  const hasToken = !!tokenInput.value;

  if (!state) {
    statusEl.textContent = "Connected. Checking lobby...";
    startBtn.disabled = true;
    goGameBtn.disabled = true;
    return;
  }

  if (state === "OPEN") {
    statusEl.textContent = "Lobby is open. Share the token, Join, then Start.";
    createBtn.disabled = true;
    joinBtn.disabled = false;
    startBtn.disabled = !(playerCount >= 2 && hasToken); // host can start once >=2
    goGameBtn.disabled = true;
    copyBtn.disabled = !hasToken;
  } else if (state === "IN_GAME") {
    statusEl.textContent = "Game is in progress. Go to the game page.";
    createBtn.disabled = true;
    joinBtn.disabled = true;
    startBtn.disabled = true;
    goGameBtn.disabled = false;
    copyBtn.disabled = !hasToken;
  } else {
    // CLOSED (or anything else)
    statusEl.textContent = "No open lobby. Click Create Lobby.";
    createBtn.disabled = false;
    joinBtn.disabled = false; // allow join if they paste a token someone sends
    startBtn.disabled = true;
    goGameBtn.disabled = true;
    copyBtn.disabled = !hasToken;
  }
}

function connect() {
  const socket = new SockJS("/ws-uno");
  stompClient = Stomp.over(socket);
  stompClient.debug = null;

  stompClient.connect({}, () => {
    connected = true;
    log("Connected");
    statusEl.textContent = "Connected. Checking lobby...";
    setUiConnected(true);

    stompClient.subscribe("/topic/lobby", msg => {
      const snapshot = JSON.parse(msg.body);
      handleLobbySnapshot(snapshot);
    });

    stompClient.subscribe("/topic/lobby/errors", msg => {
      log("ERROR: " + msg.body);
    });

    // Optional: auto-navigate to game when server signals start.
    stompClient.subscribe("/topic/game/events", msg => {
      if (msg.body === "STARTED") {
        log("Game started; navigating to /playerpage");
        window.location.href = "/playerpage";
      }
    });

    requestLobbyStatus();
  }, err => {
    connected = false;
    setUiConnected(false);
    statusEl.textContent = "Not connected.";
    log("Connect error: " + err);
  });
}

function requestLobbyStatus() {
  stompClient.send("/app/lobby/status", {}, {});
}

function handleLobbySnapshot(snapshot) {
  log("LOBBY: " + JSON.stringify(snapshot));

  // If backend includes token in snapshot when OPEN, auto-fill it.
  // If it doesn't, manual copy/paste still works.
  if (snapshot.token && !tokenInput.value) {
    tokenInput.value = snapshot.token;
  }

  renderPlayers(snapshot.playerNames);
  setUiForLobbyState(snapshot);
}

function createLobby() {
  if (!connected) return;

  if (!tokenInput.value) {
    tokenInput.value = crypto.randomUUID(); // token is visible/shareable
  }

  stompClient.send("/app/lobby/open", {}, JSON.stringify({
    token: tokenInput.value,
    playerId: playerId,
    playerName: normalizePlayerName()
  }));

  log("Sent CREATE");
}

function joinLobby() {
  if (!connected) return;

  if (!tokenInput.value) {
    log("Paste a token to join, or create a lobby.");
    return;
  }

  stompClient.send("/app/lobby/join", {}, JSON.stringify({
    token: tokenInput.value,
    playerId: playerId,
    playerName: normalizePlayerName()
  }));

  log("Sent JOIN");
}

function startGame() {
  if (!connected) return;

  if (!tokenInput.value) {
    log("No token available to start game.");
    return;
  }

  stompClient.send("/app/lobby/start", {}, JSON.stringify({
    token: tokenInput.value,
    playerId: playerId
  }));

  log("Sent START");
}

async function copyToken() {
  if (!tokenInput.value) return;
  try {
    await navigator.clipboard.writeText(tokenInput.value);
    log("Token copied");
  } catch (e) {
    tokenInput.select();
    log("Clipboard blocked; token selected for manual copy.");
  }
}

function goToGamePage() {
  window.location.href = "/playerpage";
}

createBtn.onclick = createLobby;
joinBtn.onclick = joinLobby;
startBtn.onclick = startGame;
copyBtn.onclick = copyToken;
goGameBtn.onclick = goToGamePage;

// Auto-connect when page loads
window.addEventListener("load", () => {
  setUiConnected(false);
  statusEl.textContent = "Connecting...";
  connect();
});
