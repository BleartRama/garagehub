# GarageHub – Frontend

Enkel single-page frontend i HTML, CSS och vanilla JavaScript. Pratar med
Spring Boot-backendens REST-API på `http://localhost:8080`.

## Filer

- `index.html` – all markup (login-vy + app-shell + modal)
- `styles.css` – stilar (verkstadstema: stål, varningsgult, registreringsskyltar)
- `app.js` – all logik: auth, `apiFetch`-wrapper, vyer, CRUD

## Köra frontenden

Backenden tillåter CORS-anrop från `http://localhost:5173`, så frontenden
**måste** köras på just den porten. Starta en enkel statisk server i den här mappen:

**Med Python (finns oftast förinstallerat):**
```bash
python3 -m http.server 5173
```

**Eller med Node (om du har det):**
```bash
npx serve -l 5173
```

Öppna sedan `http://localhost:5173` i webbläsaren.

> Viktigt: öppna INTE `index.html` direkt via `file://` – då blir origin fel
> och CORS blockerar anropen. Använd alltid en server på port 5173.

## Innan du testar

1. Starta backenden (Spring Boot) på `http://localhost:8080`.
2. Se till att det finns minst en registrerad användare (registrera via
   login-sidans "Registrera"-flik, eller använd en befintlig).
3. Kom ihåg rollreglerna från backenden:
   - Alla inloggade kan läsa och skapa fordon/bokningar.
   - Endast MECHANIC/ADMIN kan skapa serviceposter.
   - Endast ADMIN kan ta bort.
   - En bokning/servicepost kräver att `mechanicId` pekar på en användare med
     rollen MECHANIC.

## Flöde

1. **Logga in** eller **registrera** på startsidan. Token sparas i `localStorage`.
2. **Översikt** – antal fordon/bokningar/serviceposter + servicestatus per fordon.
3. **Fordon / Bokningar / Service** – lista, skapa (via "+"-knappen), ta bort.

All auth-logik ligger i `apiFetch()` i `app.js` – den lägger automatiskt på
`Authorization: Bearer <token>` och loggar ut vid `401`.
