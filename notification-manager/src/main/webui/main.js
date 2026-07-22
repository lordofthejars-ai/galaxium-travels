import './style.css';

// ── Starfield ──────────────────────────────────────────────────────────────
function initStarfield() {
  const canvas = document.createElement('canvas');
  canvas.id = 'starfield';
  document.body.prepend(canvas);
  const ctx = canvas.getContext('2d');

  const stars = Array.from({ length: 180 }, () => ({
    x: Math.random(),
    y: Math.random(),
    r: Math.random() * 1.2 + 0.2,
    o: Math.random() * 0.6 + 0.2,
  }));

  function draw() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    for (const s of stars) {
      ctx.beginPath();
      ctx.arc(s.x * canvas.width, s.y * canvas.height, s.r, 0, Math.PI * 2);
      ctx.fillStyle = `rgba(200, 216, 255, ${s.o})`;
      ctx.fill();
    }
  }

  draw();
  window.addEventListener('resize', draw);
}

// ── Auth state ─────────────────────────────────────────────────────────────
let accessToken = null;
let currentUser = null;

function parseJwtPayload(token) {
  try {
    return JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
  } catch {
    return {};
  }
}

// ── OIDC — fetch token URL from backend, then POST password grant ──────────
async function fetchAuthConfig() {
  const res = await fetch('/q/auth-config');
  if (!res.ok) throw new Error('Unable to reach auth config endpoint');
  return res.json();
}

async function login(username, password) {
  const { tokenEndpoint, clientId } = await fetchAuthConfig();
  const body = new URLSearchParams({
    grant_type:    'password',
    client_id:     clientId,
    client_secret: 'secret',
    username,
    password,
  });
  const res = await fetch(tokenEndpoint, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body,
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.error_description || 'Login failed');
  }
  const data = await res.json();
  accessToken = data.access_token;
  const payload = parseJwtPayload(accessToken);
  currentUser = payload.preferred_username || username;
}

function logout() {
  accessToken = null;
  currentUser = null;
  renderApp();
}

// ── Flights list ───────────────────────────────────────────────────────────
async function fetchFlights() {
  const res = await fetch('/notifications', {
    headers: { 'Authorization': `Bearer ${accessToken}` },
  });
  if (res.status === 401) { logout(); throw new Error('Session expired — please log in again'); }
  if (!res.ok) throw new Error(`Failed to load flights (HTTP ${res.status})`);
  return res.json();
}

// ── Notification submit ────────────────────────────────────────────────────
async function sendNotification(flightId, message) {
  const res = await fetch('/notifications', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${accessToken}`,
    },
    body: JSON.stringify({ flightId: Number(flightId), message }),
  });
  if (res.status === 401) { logout(); throw new Error('Session expired — please log in again'); }
  if (res.status === 403) throw new Error('Forbidden — admin role required');
  if (!res.ok) throw new Error(`Unexpected error (HTTP ${res.status})`);
}

// ── UI helpers ─────────────────────────────────────────────────────────────
function setStatus(el, text, type) {
  el.textContent = text;
  el.className = type;
}

// ── Render ─────────────────────────────────────────────────────────────────
function renderApp() {
  const root = document.getElementById('app');

  if (!accessToken) {
    renderLogin(root);
  } else {
    renderDashboard(root);
  }
}

function renderLogin(root) {
  root.innerHTML = `
    <header>
      <div class="logo-mark">🚀</div>
      <h1>GALAXIUM TRAVELS</h1>
      <p>Mission Control · Notification System</p>
    </header>

    <div class="card" id="login-card">
      <h2>🔐 Crew Authentication</h2>
      <div class="form-group">
        <label for="username">Commander ID</label>
        <input id="username" type="text" placeholder="alice" autocomplete="username" />
      </div>
      <div class="form-group">
        <label for="password">Access Code</label>
        <input id="password" type="password" placeholder="••••••••" autocomplete="current-password" />
      </div>
      <button class="btn btn-primary" id="login-btn">Initiate Login</button>
    </div>

    <div id="status"></div>

    <footer>Galaxium Travels · Secure Comms Layer</footer>
  `;

  const statusEl = root.querySelector('#status');
  const loginBtn = root.querySelector('#login-btn');
  const usernameEl = root.querySelector('#username');
  const passwordEl = root.querySelector('#password');

  async function handleLogin() {
    const username = usernameEl.value.trim();
    const password = passwordEl.value;
    if (!username || !password) {
      setStatus(statusEl, 'Commander ID and Access Code are required.', 'error');
      return;
    }
    loginBtn.disabled = true;
    setStatus(statusEl, 'Authenticating with command center…', 'info');
    try {
      await login(username, password);
      setStatus(statusEl, '', '');
      renderApp();
    } catch (e) {
      setStatus(statusEl, e.message, 'error');
      loginBtn.disabled = false;
    }
  }

  loginBtn.addEventListener('click', handleLogin);
  passwordEl.addEventListener('keydown', e => { if (e.key === 'Enter') handleLogin(); });
}

function renderDashboard(root) {
  root.innerHTML = `
    <header>
      <div class="logo-mark">🚀</div>
      <h1>GALAXIUM TRAVELS</h1>
      <p>Mission Control · Notification System</p>
    </header>

    <div class="card" id="notify-card">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:1.4rem">
        <h2 style="margin-bottom:0">📡 Transmit Alert</h2>
        <div class="user-badge">
          <span>👤</span>
          <span class="name">${currentUser}</span>
        </div>
      </div>

      <div class="form-group">
        <label>Flight</label>
        <div class="flight-dropdown" id="flight-dropdown">
          <button type="button" class="flight-trigger" id="flight-trigger" aria-haspopup="listbox" aria-expanded="false">
            <span class="flight-trigger-text" id="flight-trigger-text">Loading flights…</span>
            <svg class="flight-trigger-chevron" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
              <polyline points="4 6 8 10 12 6"/>
            </svg>
          </button>
          <div class="flight-panel" id="flight-panel" role="listbox">
            <div class="flight-loading">Scanning sector for flights…</div>
          </div>
        </div>
      </div>
      <div class="form-group">
        <label for="message">Transmission Message</label>
        <textarea id="message" placeholder="Enter passenger alert message…"></textarea>
      </div>
      <button class="btn btn-primary" id="send-btn" disabled>📤 Transmit Notification</button>
      <hr class="divider" />
      <button class="btn btn-ghost" id="logout-btn">Disconnect</button>
    </div>

    <div id="status"></div>

    <footer>Galaxium Travels · Secure Comms Layer</footer>
  `;

  const statusEl   = root.querySelector('#status');
  const sendBtn    = root.querySelector('#send-btn');
  const msgEl      = root.querySelector('#message');
  const trigger    = root.querySelector('#flight-trigger');
  const triggerTxt = root.querySelector('#flight-trigger-text');
  const panel      = root.querySelector('#flight-panel');

  let selectedFlightId = null;

  // Toggle panel open/closed
  trigger.addEventListener('click', () => {
    const isOpen = panel.classList.toggle('open');
    trigger.classList.toggle('open', isOpen);
    trigger.setAttribute('aria-expanded', isOpen);
  });

  // Close panel when clicking outside
  document.addEventListener('click', e => {
    if (!root.querySelector('#flight-dropdown').contains(e.target)) {
      panel.classList.remove('open');
      trigger.classList.remove('open');
      trigger.setAttribute('aria-expanded', false);
    }
  });

  function selectFlight(id, label) {
    selectedFlightId = id;
    triggerTxt.textContent = label;
    trigger.classList.add('has-value');
    panel.querySelectorAll('.flight-option').forEach(el =>
      el.classList.toggle('selected', el.dataset.id === String(id))
    );
    panel.classList.remove('open');
    trigger.classList.remove('open');
    trigger.setAttribute('aria-expanded', false);
    sendBtn.disabled = false;
  }

  // Load flights and populate the custom panel
  fetchFlights().then(flights => {
    if (!flights.length) {
      panel.innerHTML = '<div class="flight-loading">No flights available</div>';
      return;
    }
    panel.innerHTML = flights.map(f => `
      <div class="flight-option" role="option" data-id="${f.flight_id}">
        <span class="flight-option-id">${f.flight_id}</span>
        <span class="flight-option-route">${f.origin} → ${f.destination}</span>
        <span class="flight-option-arrow">›</span>
      </div>`).join('');
    triggerTxt.textContent = '— Select a flight —';

    panel.querySelectorAll('.flight-option').forEach(el => {
      el.addEventListener('click', () => {
        const f = flights.find(x => String(x.flight_id) === el.dataset.id);
        selectFlight(f.flight_id, `${f.flight_id} · ${f.origin} → ${f.destination}`);
      });
    });
  }).catch(e => {
    panel.innerHTML = '<div class="flight-loading">Failed to load flights</div>';
    setStatus(statusEl, e.message, 'error');
  });

  root.querySelector('#logout-btn').addEventListener('click', logout);

  sendBtn.addEventListener('click', async () => {
    const message = msgEl.value.trim();
    if (!selectedFlightId || !message) {
      setStatus(statusEl, 'Select a flight and enter a message.', 'error');
      return;
    }
    sendBtn.disabled = true;
    setStatus(statusEl, 'Transmitting to passengers…', 'info');
    try {
      await sendNotification(selectedFlightId, message);
      setStatus(statusEl, '✓ Notification transmitted successfully.', 'success');
      selectedFlightId = null;
      triggerTxt.textContent = '— Select a flight —';
      trigger.classList.remove('has-value');
      panel.querySelectorAll('.flight-option').forEach(el => el.classList.remove('selected'));
      msgEl.value = '';
    } catch (e) {
      setStatus(statusEl, e.message, 'error');
    } finally {
      sendBtn.disabled = false;
    }
  });
}

// ── Bootstrap ──────────────────────────────────────────────────────────────
initStarfield();
renderApp();
