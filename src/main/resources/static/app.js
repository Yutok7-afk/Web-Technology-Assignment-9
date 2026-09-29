const state = {
    teams: [],
    matches: [],
    selectedMatchId: null,
    dashboard: null
};

const $ = (id) => document.getElementById(id);

async function api(url, options = {}) {
    const response = await fetch(url, {
        headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
        ...options
    });
    const text = await response.text();
    let data;
    try { data = text ? JSON.parse(text) : {}; } catch { data = { message: text }; }
    if (!response.ok) {
        throw new Error(data.detail || data.message || data.error || `Request failed: ${response.status}`);
    }
    return data;
}

async function loadTeams() {
    state.teams = await api('/api/teams');
    renderTeamLists();
    populateTeamSelects();
}

async function loadMatches() {
    state.matches = await api('/api/matches');
    const select = $('matchSelect');
    select.innerHTML = state.matches.map(m =>
        `<option value="${m.id}">${escapeHtml(m.title)} — ${escapeHtml(m.teamOne.shortName)} vs ${escapeHtml(m.teamTwo.shortName)}</option>`
    ).join('');

    if (!state.selectedMatchId || !state.matches.some(m => m.id === state.selectedMatchId)) {
        const live = state.matches.find(m => m.status === 'LIVE');
        state.selectedMatchId = live ? live.id : (state.matches[0]?.id ?? null);
    }
    if (state.selectedMatchId) {
        select.value = state.selectedMatchId;
        await loadDashboard();
    }
}

async function loadDashboard() {
    if (!state.selectedMatchId) return;
    state.dashboard = await api(`/api/matches/${state.selectedMatchId}/dashboard`);
    const d = state.dashboard;

    $('matchTitle').textContent = d.title;
    $('venue').textContent = d.venue;
    $('matchStatus').textContent = d.status;
    $('runs').textContent = d.runs;
    $('wickets').textContent = d.wickets;
    $('overs').textContent = `${d.overs} overs`;
    $('battingTeam').textContent = d.battingTeam;
    $('bowlingTeam').textContent = d.bowlingTeam;
    $('target').textContent = d.targetRuns ?? '-';
    $('striker').textContent = d.striker;
    $('nonStriker').textContent = d.nonStriker;
    $('currentBowler').textContent = d.currentBowler;

    renderBatting(d.battingStats || []);
    renderBowling(d.bowlingStats || []);
    renderEvents(d.recentEvents || []);
    renderSummary(d.inningsSummary || [], d.resultSummary);

    await loadPlayersForCurrentInnings();
}

async function loadPlayersForCurrentInnings() {
    if (!state.dashboard) return;
    const d = state.dashboard;
    const [batters, bowlers] = await Promise.all([
        api(`/api/players?teamId=${d.battingTeamId}`),
        api(`/api/players?teamId=${d.bowlingTeamId}`)
    ]);
    populatePlayerSelect($('batsmanSelect'), batters, d.striker);
    populatePlayerSelect($('bowlerSelect'), bowlers, d.currentBowler);
}

function populatePlayerSelect(select, players, preferredName) {
    select.innerHTML = players.map(p => `<option value="${p.id}">${escapeHtml(p.name)} — ${escapeHtml(p.role)}</option>`).join('');
    const preferred = players.find(p => p.name === preferredName);
    if (preferred) select.value = preferred.id;
}

function renderBatting(rows) {
    $('battingTable').innerHTML = rows.length ? rows.map(r => `
        <tr>
            <td>${escapeHtml(r.playerName)}</td><td>${r.runs}</td><td>${r.balls}</td><td>${r.fours}</td><td>${r.sixes}</td><td>${r.strikeRate.toFixed(2)}</td>
            <td>${r.out ? 'OUT' : 'NOT OUT'}</td>
        </tr>`).join('') : '<tr><td colspan="7">No batting events yet.</td></tr>';
}

function renderBowling(rows) {
    $('bowlingTable').innerHTML = rows.length ? rows.map(r => `
        <tr>
            <td>${escapeHtml(r.playerName)}</td><td>${r.overs}</td><td>${r.runsConceded}</td><td>${r.wickets}</td><td>${r.economy.toFixed(2)}</td>
        </tr>`).join('') : '<tr><td colspan="5">No bowling events yet.</td></tr>';
}

function renderEvents(events) {
    $('eventList').innerHTML = events.length ? [...events].reverse().map(e => `
        <div class="event-row">
            <div class="event-over">${escapeHtml(e.over)}</div>
            <div>
                <strong>${escapeHtml(e.batsman)}</strong>
                <small>vs ${escapeHtml(e.bowler)}${e.note ? ` · ${escapeHtml(e.note)}` : ''}</small>
            </div>
            <div class="event-type">${escapeHtml(e.type)} ${e.runs > 0 ? `+${e.runs}` : ''}</div>
        </div>`).join('') : '<p class="small-note">No events stored yet.</p>';
}

function renderSummary(summaries, result) {
    $('summary').innerHTML = summaries.map(s => `<div class="summary-item">${escapeHtml(s)}</div>`).join('') || '<p class="small-note">No innings summary available.</p>';
    if (result) $('summary').insertAdjacentHTML('beforeend', `<div class="summary-item">Result: ${escapeHtml(result)}</div>`);
}

function renderTeamLists() {
    $('teamList').innerHTML = state.teams.map(t => `<div><strong>${escapeHtml(t.name)}</strong> (${escapeHtml(t.shortName)})${t.city ? ` — ${escapeHtml(t.city)}` : ''}</div>`).join('');
}

async function renderPlayerList() {
    const players = await api('/api/players');
    $('playerList').innerHTML = players.map(p => `<div><strong>${escapeHtml(p.name)}</strong> — ${escapeHtml(p.role)} — ${escapeHtml(p.team?.name || '')}</div>`).join('');
}

function populateTeamSelects() {
    const html = '<option value="">Select team</option>' + state.teams.map(t => `<option value="${t.id}">${escapeHtml(t.name)} (${escapeHtml(t.shortName)})</option>`).join('');
    $('playerTeam').innerHTML = html;
    $('matchTeamOne').innerHTML = html;
    $('matchTeamTwo').innerHTML = html;
}

async function recordEvent(type) {
    if (!state.selectedMatchId) return;
    const batsmanId = Number($('batsmanSelect').value);
    const bowlerId = Number($('bowlerSelect').value);
    if (!batsmanId || !bowlerId) {
        showMessage('Select a batsman and bowler first.', true);
        return;
    }

    const runs = type === 'RUN' ? Number($('customRuns').value) : 0;
    try {
        await api(`/api/matches/${state.selectedMatchId}/events`, {
            method: 'POST',
            body: JSON.stringify({ batsmanId, bowlerId, type, runs, note: 'Dashboard event' })
        });
        showMessage(`${type} recorded successfully.`);
        await loadDashboard();
    } catch (error) {
        showMessage(error.message, true);
    }
}

function showMessage(message, isError = false) {
    const el = $('eventMessage');
    el.textContent = message;
    el.style.color = isError ? '#b91c1c' : '#166534';
}

$('matchSelect').addEventListener('change', async (event) => {
    state.selectedMatchId = Number(event.target.value);
    await loadDashboard();
});

$('refreshBtn').addEventListener('click', loadDashboard);

document.querySelectorAll('[data-type]').forEach(button => {
    button.addEventListener('click', () => recordEvent(button.dataset.type));
});

$('teamForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    try {
        await api('/api/teams', {
            method: 'POST',
            body: JSON.stringify({
                name: $('teamName').value,
                shortName: $('teamShortName').value,
                city: $('teamCity').value
            })
        });
        event.target.reset();
        await loadTeams();
        await loadMatches();
    } catch (error) { alert(error.message); }
});

$('playerForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    try {
        await api('/api/players', {
            method: 'POST',
            body: JSON.stringify({
                name: $('playerName').value,
                role: $('playerRole').value,
                teamId: Number($('playerTeam').value)
            })
        });
        event.target.reset();
        await loadTeams();
        await renderPlayerList();
    } catch (error) { alert(error.message); }
});

$('matchForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    try {
        await api('/api/matches', {
            method: 'POST',
            body: JSON.stringify({
                title: $('newMatchTitle').value,
                venue: $('newMatchVenue').value,
                teamOneId: Number($('matchTeamOne').value),
                teamTwoId: Number($('matchTeamTwo').value),
                oversLimit: Number($('oversLimit').value)
            })
        });
        event.target.reset();
        await loadMatches();
    } catch (error) { alert(error.message); }
});

function escapeHtml(value) {
    return String(value ?? '').replace(/[&<>'"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c]));
}

async function init() {
    try {
        await loadTeams();
        await loadMatches();
        await renderPlayerList();
        setInterval(async () => {
            try { await loadDashboard(); } catch (e) { console.error(e); }
        }, 2500);
    } catch (error) {
        alert(`Unable to load application data: ${error.message}`);
    }
}

init();
