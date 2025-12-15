let stompClient = null;

const logEl = document.getElementById("log");
const tokenEl = document.getElementById("token");
const playerEl = document.getElementById("playerId");

function log(msg) {
    logEl.textContent += msg + "\n";
    logEl.scrollTop = logEl.scrollHeight;
}

function generateToken() {
    const uuid = ([1e7]+-1e3+-4e3+-8e3+-1e11)
        .replace(/[018]/g, c =>
            (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
        );
    tokenEl.value = uuid;
}

function connect() {
    const socket = new SockJS('/ws-uno');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;

    stompClient.connect({}, () => {
        log("Connected to WebSocket");

        stompClient.subscribe('/topic/lobby', msg =>
            log("LOBBY: " + msg.body)
        );

        stompClient.subscribe('/topic/lobby/errors', msg =>
            log("ERROR: " + msg.body)
        );
    });
}

function openLobby() {
    stompClient.send('/app/lobby/open', {}, JSON.stringify({
        token: tokenEl.value,
        playerId: playerEl.value
    }));
    log("Sent OPEN");
}

function joinLobby() {
    stompClient.send('/app/lobby/join', {}, JSON.stringify({
        token: tokenEl.value,
        playerId: playerEl.value
    }));
    log("Sent JOIN");
}

/* wire up buttons */
document.getElementById("genToken").onclick = generateToken;
document.getElementById("connectBtn").onclick = connect;
document.getElementById("openBtn").onclick = openLobby;
document.getElementById("joinBtn").onclick = joinLobby;
