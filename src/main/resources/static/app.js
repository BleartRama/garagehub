/* ============================================================
   GarageHub frontend — vanilla JS single-page app
   Pratar med Spring Boot-API:et på localhost:8080.
   ============================================================ */
 
const API = "http://localhost:8080";
 
/* ---------- Auth-hjälpare ---------- */
 
function getToken() {
    return localStorage.getItem("token");
}
 
function isLoggedIn() {
    return getToken() !== null;
}
 
/* Avkoda JWT-payloaden (utan verifiering — bara för att läsa e-post/exp i UI:t) */
function decodeToken() {
    const token = getToken();
    if (!token) return null;
    try {
        const payload = token.split(".")[1];
        return JSON.parse(atob(payload));
    } catch {
        return null;
    }
}
 
/* Rollen ur token, t.ex. "ROLE_USER" / "ROLE_MECHANIC" / "ROLE_ADMIN" */
function getRole() {
    return decodeToken()?.role || null;
}
function isPrivileged() {
    const r = getRole();
    return r === "ROLE_MECHANIC" || r === "ROLE_ADMIN";
}
function isAdmin() {
    return getRole() === "ROLE_ADMIN";
}
 
/* Central fetch-wrapper: lägger på Authorization automatiskt, hanterar 401 */
async function apiFetch(url, options = {}) {
    const token = getToken();
    const response = await fetch(url, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...(token && { "Authorization": `Bearer ${token}` }),
            ...options.headers
        }
    });
 
    if (response.status === 401) {
        localStorage.removeItem("token");
        showLogin();
        throw new Error("Sessionen har gått ut. Logga in igen.");
    }
    return response;
}
 
/* ---------- Login / register ---------- */
 
function switchTab(tab) {
    document.querySelectorAll(".tab-btn").forEach(b =>
        b.classList.toggle("active", b.dataset.tab === tab));
    document.getElementById("login-form").classList.toggle("hidden", tab !== "login");
    document.getElementById("register-form").classList.toggle("hidden", tab !== "register");
    document.getElementById("login-error").textContent = "";
    document.getElementById("register-error").textContent = "";
    document.getElementById("register-success").textContent = "";
}
 
async function handleLogin(event) {
    event.preventDefault();
    const errorEl = document.getElementById("login-error");
    errorEl.textContent = "";
    const email = document.getElementById("login-email").value;
    const password = document.getElementById("login-password").value;
 
    try {
        const res = await fetch(`${API}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });
        if (!res.ok) {
            errorEl.textContent = "Fel e-post eller lösenord.";
            return;
        }
        const token = await res.text();
        localStorage.setItem("token", token);
        enterApp();
    } catch {
        errorEl.textContent = "Kunde inte nå servern.";
    }
}
 
async function handleRegister(event) {
    event.preventDefault();
    const errorEl = document.getElementById("register-error");
    const okEl = document.getElementById("register-success");
    errorEl.textContent = "";
    okEl.textContent = "";
 
    const body = {
        firstName: document.getElementById("reg-firstname").value,
        lastName: document.getElementById("reg-lastname").value,
        email: document.getElementById("reg-email").value,
        password: document.getElementById("reg-password").value
    };
 
    try {
        const res = await fetch(`${API}/auth/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body)
        });
        if (res.status === 201) {
            okEl.textContent = "Konto skapat! Du kan logga in nu.";
            document.getElementById("register-form").reset();
            setTimeout(() => switchTab("login"), 1200);
        } else {
            const data = await res.json().catch(() => null);
            errorEl.textContent = data?.message || "Registreringen misslyckades.";
        }
    } catch {
        errorEl.textContent = "Kunde inte nå servern.";
    }
}
 
function logout() {
    localStorage.removeItem("token");
    showLogin();
}
 
/* ---------- Vy-växling shell ---------- */
 
function showLogin() {
    document.getElementById("app-view").classList.add("hidden");
    document.getElementById("login-view").classList.remove("hidden");
}
 
function enterApp() {
    document.getElementById("login-view").classList.add("hidden");
    document.getElementById("app-view").classList.remove("hidden");
 
    const payload = decodeToken();
    document.getElementById("user-badge").textContent = payload?.sub || "";
 
    showView("dashboard");
}
 
function showView(view) {
    document.querySelectorAll(".nav-btn").forEach(b =>
        b.classList.toggle("active", b.dataset.view === view));
    ["dashboard", "vehicles", "bookings", "records"].forEach(v =>
        document.getElementById(`${v}-panel`).classList.toggle("hidden", v !== view));
 
    if (view === "dashboard") loadDashboard();
    if (view === "vehicles") loadVehicles();
    if (view === "bookings") loadBookings();
    if (view === "records") loadRecords();
}
 
/* ---------- Dataladdning ---------- */
 
async function fetchJson(path) {
    const res = await apiFetch(`${API}${path}`);
    if (!res.ok) throw new Error(`Fel vid hämtning: ${res.status}`);
    return res.json();
}
 
async function loadDashboard() {
    try {
        const [vehicles, bookings, records] = await Promise.all([
            fetchJson("/vehicles"),
            fetchJson("/bookings"),
            fetchJson("/service-records")
        ]);
 
        document.getElementById("stat-vehicles").textContent = vehicles.length;
        document.getElementById("stat-bookings").textContent = bookings.length;
        document.getElementById("stat-records").textContent = records.length;
 
        // Hämta servicepåminnelse per fordon
        const reminders = await Promise.all(
            vehicles.map(v =>
                fetchJson(`/vehicles/${v.id}/service-reminder`).catch(() => null))
        );
        const valid = reminders.filter(Boolean);
        const dueCount = valid.filter(r => r.dueForService).length;
        document.getElementById("stat-due").textContent = dueCount;
 
        const dueList = document.getElementById("due-list");
        if (valid.length === 0) {
            dueList.innerHTML = `<div class="empty-state"><p>Inga fordon registrerade än.</p></div>`;
        } else {
            dueList.innerHTML = valid.map(r => `
                <div class="due-row">
                    <span class="due-plate">${esc(r.registrationNumber)}</span>
                    <span class="due-status">${esc(r.status)}</span>
                    <span class="due-flag ${r.dueForService ? "due-flag--yes" : "due-flag--no"}">
                        ${r.dueForService ? "Dags" : "OK"}
                    </span>
                </div>
            `).join("");
        }
    } catch (e) {
        toast(e.message, true);
    }
}
 
async function loadVehicles() {
    const el = document.getElementById("vehicles-list");
    try {
        const vehicles = await fetchJson("/vehicles");
        if (vehicles.length === 0) {
            el.innerHTML = emptyState("Inga fordon än.", "vehicle", "Lägg till fordon");
            return;
        }
        el.innerHTML = vehicles.map(v => `
            <div class="data-card">
                <span class="card-plate">${esc(v.registrationNumber)}</span>
                <div class="card-main">
                    <div class="card-title">${esc(v.brand)} ${esc(v.model)}</div>
                    <div class="card-meta">Årsmodell ${esc(v.year)} · ${fmtNum(v.mileage)} mil · Ägare: ${esc(v.ownerName || "–")}</div>
                </div>
                ${isAdmin() ? `<button class="btn-danger" onclick="deleteItem('vehicles', ${v.id})">Ta bort</button>` : ""}
            </div>
        `).join("");
    } catch (e) {
        toast(e.message, true);
    }
}
 
async function loadBookings() {
    const el = document.getElementById("bookings-list");
    try {
        const bookings = await fetchJson("/bookings");
        if (bookings.length === 0) {
            el.innerHTML = emptyState("Inga bokningar än.", "booking", "Ny bokning");
            return;
        }
        el.innerHTML = bookings.map(b => {
            const canAct = b.status === "PENDING" || b.status === "APPROVED";
            // Godkänn: bara mekaniker/admin, och bara på PENDING
            const approveBtn = (isPrivileged() && b.status === "PENDING")
                ? `<button class="btn-approve" onclick="bookingAction(${b.id}, 'approve')">Godkänn</button>` : "";
            // Avboka: alla (kund får avboka sin egen — backend har ägarskapskollen)
            const cancelBtn = canAct
                ? `<button class="btn-ghost btn-sm" onclick="bookingAction(${b.id}, 'cancel')">Avboka</button>` : "";
            // Ta bort: bara admin
            const deleteBtn = isAdmin()
                ? `<button class="btn-danger" onclick="deleteItem('bookings', ${b.id})">Ta bort</button>` : "";
            return `
            <div class="data-card">
                <span class="card-plate">${esc(b.vehicleRegistrationNumber || "–")}</span>
                <div class="card-main">
                    <div class="card-title">${esc(b.description)}</div>
                    <div class="card-meta">${esc(b.bookingDate)} · Mekaniker: ${esc(b.mechanicName || "–")}</div>
                </div>
                <span class="status-chip ${chipClass(b.status)}">${esc(b.status)}</span>
                ${approveBtn}
                ${cancelBtn}
                ${deleteBtn}
            </div>`;
        }).join("");
    } catch (e) {
        toast(e.message, true);
    }
}
 
async function loadRecords() {
    const el = document.getElementById("records-list");
    try {
        const records = await fetchJson("/service-records");
        if (records.length === 0) {
            el.innerHTML = emptyState("Inga serviceposter än.", "record", "Ny servicepost");
            return;
        }
        el.innerHTML = records.map(r => `
            <div class="data-card">
                <span class="card-plate">${esc(r.vehicleRegistrationNumber || "–")}</span>
                <div class="card-main">
                    <div class="card-title">${esc(r.description)}</div>
                    <div class="card-meta">${esc(r.serviceDate)} · ${fmtNum(r.cost)} kr · Vid ${fmtNum(r.mileageAtService)} mil · Nästa vid ${fmtNum(r.nextRecommendedServiceMileage)} mil</div>
                    <div class="card-meta">Utförd av: ${esc(r.performedAt || "–")}${r.mechanicName ? " · " + esc(r.mechanicName) : ""}</div>
                </div>
                <button class="btn-danger" onclick="deleteItem('service-records', ${r.id})">Ta bort</button>
            </div>
        `).join("");
    } catch (e) {
        toast(e.message, true);
    }
}
 
/* ---------- Radera ---------- */
 
async function deleteItem(resource, id) {
    if (!confirm("Är du säker på att du vill ta bort?")) return;
    try {
        const res = await apiFetch(`${API}/${resource}/${id}`, { method: "DELETE" });
        if (res.status === 204) {
            toast("Borttaget.");
            showView(currentView());
        } else if (res.status === 403) {
            const data = await res.json().catch(() => null);
            toast(data?.message || "Du saknar behörighet för detta.", true);
        } else if (res.status === 409) {
            const data = await res.json().catch(() => null);
            toast(data?.message || "Kan inte tas bort — används på annat håll.", true);
        } else {
            toast("Kunde inte ta bort.", true);
        }
    } catch (e) {
        toast(e.message, true);
    }
}
 
/* ---------- Godkänn / avboka bokning ---------- */
 
async function bookingAction(id, action) {
    const label = action === "approve" ? "godkänna" : "avboka";
    if (!confirm(`Vill du ${label} bokningen?`)) return;
    try {
        const res = await apiFetch(`${API}/bookings/${id}/${action}`, { method: "PUT" });
        if (res.ok) {
            toast(action === "approve" ? "Bokning godkänd." : "Bokning avbokad.");
            loadBookings();
        } else if (res.status === 403) {
            const data = await res.json().catch(() => null);
            toast(data?.message || "Du saknar behörighet för detta.", true);
        } else {
            toast("Åtgärden misslyckades.", true);
        }
    } catch (e) {
        toast(e.message, true);
    }
}
 
/* ---------- Modaler för att skapa ---------- */
 
let modalType = null;
 
const MODAL_CONFIG = {
    vehicle: {
        title: "Nytt fordon",
        resource: "vehicles",
        fields: [
            { name: "registrationNumber", label: "Registreringsnummer", type: "text" },
            { name: "brand", label: "Märke", type: "text" },
            { name: "model", label: "Modell", type: "text" },
            { name: "year", label: "Årsmodell", type: "number" },
            { name: "mileage", label: "Miltal", type: "number" }
        ]
    },
    booking: {
        title: "Boka verkstadstid",
        resource: "bookings",
        fields: [
            { name: "bookingDate", label: "Önskat datum", type: "date" },
            { name: "description", label: "Vad gäller det?", type: "text" },
            { name: "vehicleId", label: "Fordon (ID)", type: "number" }
        ]
    },
    record: {
        title: "Ny servicepost",
        resource: "service-records",
        fields: [
            { name: "serviceDate", label: "Servicedatum", type: "date" },
            { name: "description", label: "Beskrivning", type: "text" },
            { name: "performedAt", label: "Utförd av (verkstad/ort)", type: "text" },
            { name: "cost", label: "Kostnad (kr)", type: "number" },
            { name: "mileageAtService", label: "Miltal vid service (mil)", type: "number" },
            { name: "nextRecommendedServiceMileage", label: "Nästa service vid (mil)", type: "number" },
            { name: "vehicleId", label: "Fordon (ID)", type: "number" }
        ]
    }
};
 
function openModal(type) {
    modalType = type;
    const cfg = MODAL_CONFIG[type];
    document.getElementById("modal-title").textContent = cfg.title;
    document.getElementById("modal-error").textContent = "";
 
    document.getElementById("modal-fields").innerHTML = cfg.fields.map(f => {
        if (f.type === "select") {
            return `<label>${f.label}
                <select name="${f.name}" required>
                    ${f.options.map(o => `<option value="${o}">${o}</option>`).join("")}
                </select>
            </label>`;
        }
        const step = f.type === "number" ? ` step="any"` : "";
        return `<label>${f.label}
            <input type="${f.type}" name="${f.name}" required${step}>
        </label>`;
    }).join("");
 
    document.getElementById("modal-overlay").classList.remove("hidden");
}
 
function closeModal() {
    document.getElementById("modal-overlay").classList.add("hidden");
    document.getElementById("modal-form").reset();
    modalType = null;
}
 
function closeModalOnOverlay(event) {
    if (event.target.id === "modal-overlay") closeModal();
}
 
async function handleModalSubmit(event) {
    event.preventDefault();
    const cfg = MODAL_CONFIG[modalType];
    const errorEl = document.getElementById("modal-error");
    errorEl.textContent = "";
 
    const form = event.target;
    const body = {};
    cfg.fields.forEach(f => {
        const val = form.elements[f.name].value;
        body[f.name] = f.type === "number" ? Number(val) : val;
    });
 
    try {
        const res = await apiFetch(`${API}/${cfg.resource}`, {
            method: "POST",
            body: JSON.stringify(body)
        });
        if (res.status === 201) {
            toast("Sparat.");
            closeModal();
            showView(currentView());
        } else if (res.status === 403) {
            errorEl.textContent = "Du saknar behörighet för detta.";
        } else {
            const data = await res.json().catch(() => null);
            errorEl.textContent = data?.message || `Kunde inte spara (${res.status}).`;
        }
    } catch (e) {
        errorEl.textContent = e.message;
    }
}
 
/* ---------- Småhjälpare ---------- */
 
function currentView() {
    const active = document.querySelector(".nav-btn.active");
    return active ? active.dataset.view : "dashboard";
}
 
function chipClass(status) {
    return {
        PENDING: "chip-pending",
        APPROVED: "chip-approved",
        COMPLETED: "chip-completed",
        CANCELLED: "chip-cancelled"
    }[status] || "chip-completed";
}
 
function emptyState(msg, modalType, btnLabel) {
    return `<div class="empty-state">
        <p>${msg}</p>
        <button class="btn-primary btn-sm" onclick="openModal('${modalType}')">${btnLabel}</button>
    </div>`;
}
 
function esc(str) {
    if (str === null || str === undefined) return "";
    return String(str)
        .replace(/&/g, "&amp;").replace(/</g, "&lt;")
        .replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}
 
function fmtNum(n) {
    if (n === null || n === undefined) return "–";
    return Number(n).toLocaleString("sv-SE");
}
 
let toastTimer;
function toast(msg, isError = false) {
    const el = document.getElementById("toast");
    el.textContent = msg;
    el.className = "toast" + (isError ? " toast--error" : "");
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => el.classList.add("hidden"), 3200);
}
 
/* ---------- Init ---------- */
 
document.addEventListener("DOMContentLoaded", () => {
    if (isLoggedIn()) {
        enterApp();
    } else {
        showLogin();
    }
});