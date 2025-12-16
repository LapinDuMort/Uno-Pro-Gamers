/* global SockJS, Stomp */

let stompClient = null;

// playerId should be per-tab (sessionStorage), not shared (localStorage)
const SS_PLAYER_ID = "uno.session.playerId";

// name + token can stay in localStorage if you want convenience
const LS_PLAYER_NAME = "uno.playerName";
const LS_TOKEN = "uno.token";

function uuidV4() {
  if (window.crypto && crypto.getRandomValues) {
    const buf = new Uint8Array(16);
    crypto.getRandomValues(buf);
    buf[6] = (buf[6] & 0x0f) | 0x40;
    buf[8] = (buf[8] & 0x3f) | 0x80;

    const hex = [...buf].map(b => b.toString(16).padStart(2, "0")).join("");
    return (
      hex.slice(0, 8) + "-" +
      hex.slice(8, 12) + "-" +
      hex.slice(12, 16) + "-" +
      hex.slice(16, 20) + "-" +
      hex.slice(20)
    );
  }

  return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0;
    const v = c === "x" ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
}

function $(id) { return document.getElementById(id); }

function log(msg) {
  const el = $("log");
  el.textContent += `[${new Date().toLocaleTimeString()}] ${msg}\n`;
  el.scrollTop = el.scrollHeight;
}

function loadIdentity() {
  // Per-tab playerId
  let playerId = sessionStorage.getItem(SS_PLAYER_ID);
  if (!playerId) {
    playerId = uuidV4();
    sessionStorage.setItem(SS_PLAYER_ID, playerId);
  }

  const savedName = localStorage.getItem(LS_PLAYER_NAME) || "";
  const savedToken = localStorage.getItem(LS_TOKEN) || "";

  $("playerName").value = savedName;
  $("token").value = savedToken;
  $("playerIdLabel").textContent = ` (id: ${playerId.slice(0, 8)}…)`;

  return { playerId };
}

function saveInputs() {
  localStorage.setItem(LS_PLAYER_NAME, $("playerName").value.trim());
  localStorage.setItem(LS_TOKEN, $("token").value.trim());
}

function setButtonsEnabled(connected) {
  $("createBtn").disabled = !connected;
  $("joinBtn").disabled = !connected;
  $("copyBtn").disabled = !connected;
  $("startBtn").disabled = !connected;
  if (!connected) $("goGameBtn").disabled = true;
}

function updatePlayersList(items) {
  const ul = $("players");
  ul.innerHTML = "";
  (items || []).forEach((p) => {
    const li = document.createElement("li");
    li.textContent = String(p);
    ul.appendChild(li);
  });
}

function updateStatus(text) {
  $("status").textContent = text;
}

function copyToken() {
  const token = $("token").value.trim();
  if (!token) return;
  navigator.clipboard.writeText(token).then(() => log("Copied token to clipboard."))
    .catch(() => log("Failed to copy token (clipboard permission?)."));
}

// Token must be UUID because backend does UUID.fromString(token)
function ensureUuidTokenForHostCreate(alwaysNew = false) {
  let token = $("token").value.trim();
  if (alwaysNew || !token) {
    token = uuidV4();
    log(`Generated lobby token (UUID): ${token}`);
  } else {
    log(`Using existing token: ${token}`);
  }
  $("token").value = token;
  localStorage.setItem(LS_TOKEN, token);
  return token;
}

function connect() {
  const socket = new SockJS("/ws");
  stompClient = Stomp.over(socket);
  stompClient.debug = () => {};

  stompClient.connect({}, () => {
    log("STOMP connected.");
    setButtonsEnabled(true);

    stompClient.subscribe("/topic/lobby", (msg) => {
      const snap = JSON.parse(msg.body);
      log(`Lobby snapshot: state=${snap.lobbyState} players=${(snap.playerNames || []).length}/${snap.maxPlayers}`);
      updatePlayersList(snap.playerNames || []);
      updateStatus(`Lobby: ${snap.lobbyState} | Players: ${(snap.playerNames || []).length}/${snap.maxPlayers}`);

      const canStart = snap.lobbyState === "OPEN" && (snap.playerNames || []).length >= 2;
      $("startBtn").disabled = !canStart;
    });

    log("Subscribing to /topic/game/events");
    console.log("Subscribing to /topic/game/events");
    stompClient.subscribe("/topic/game/events", (msg) => {
      const body = msg.body ? msg.body.trim() : "";
      log(`Received game event: "${body}" (raw: "${msg.body}")`);
      console.log("Game event received:", body, "raw:", msg.body);
      if (body === "STARTED") {
          log("Game STARTED event received, redirecting...");
          const tokenVal = $("token").value.trim();
          const playerIdVal = sessionStorage.getItem(SS_PLAYER_ID) || "";
          log(`Redirecting to /game?token=${tokenVal}&playerId=${playerIdVal}`);
          console.log("Redirecting with token:", tokenVal, "playerId:", playerIdVal);
          window.location.href = `/game?token=${encodeURIComponent(tokenVal)}&playerId=${encodeURIComponent(playerIdVal)}`;
      } else {
          log(`Unexpected game event body: "${body}" (type: ${typeof body})`);
      }
    });

    stompClient.send("/app/lobby/status", {}, JSON.stringify({}));

  }, (err) => {
    log("STOMP connection error: " + err);
    setButtonsEnabled(false);
  });
}

function openLobby(playerId) {
  saveInputs();
  const token = ensureUuidTokenForHostCreate(false);

  stompClient.send("/app/lobby/open", {}, JSON.stringify({
    token,
    playerId,
    playerName: $("playerName").value.trim() || "Host"
  }));

  log("Sent: /app/lobby/open");
  updateStatus("Lobby opening...");
}

function joinLobby(playerId) {
  saveInputs();

  const token = $("token").value.trim();
  const playerName = $("playerName").value.trim();

  if (!token) return log("Token is required to join lobby (paste it from the host).");
  if (!playerName) return log("Player name is required.");

  stompClient.send("/app/lobby/join", {}, JSON.stringify({
    token,
    playerId,
    playerName
  }));

  log("Sent: /app/lobby/join");
  updateStatus("Joining lobby...");
}

function startGame(playerId) {
  saveInputs();
  const token = $("token").value.trim();
  if (!token) return log("Token is required to start game.");

  log(`Starting game with token: ${token}, playerId: ${playerId}`);
  console.log("Sending /app/lobby/start with:", { token, playerId });
  stompClient.send("/app/lobby/start", {}, JSON.stringify({ token, playerId }));
  log("Sent: /app/lobby/start");
  updateStatus("Starting game...");
}

function goToGamePage(playerId) {
  saveInputs();
  const token = $("token").value.trim();
  if (!token) return log("Token is required to go to game page.");

  window.location.href =
    `/game?token=${encodeURIComponent(token)}&playerId=${encodeURIComponent(playerId)}`;
}

(function init() {
  const { playerId } = loadIdentity();

  $("copyBtn").addEventListener("click", copyToken);
  $("createBtn").addEventListener("click", () => openLobby(playerId));
  $("joinBtn").addEventListener("click", () => joinLobby(playerId));
  $("startBtn").addEventListener("click", () => startGame(playerId));
  $("goGameBtn").addEventListener("click", () => goToGamePage(playerId));

  setButtonsEnabled(false);
  connect();
})();
