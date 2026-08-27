# GarageHub

Ett backend-tungt fullstack-projekt: ett verkstadssystem för bilägare och verkstäder. Kunder
registrerar sina fordon, bokar servicetider, ser sin servicehistorik och får
servicepåminnelser. Mekaniker hanterar bokningar och serviceposter, och
administratörer har full kontroll.

Byggt med Java och Spring Boot med ett REST-API, JWT-baserad autentisering,
rollstyrd åtkomst och ägarskapskontroll så att varje kund bara ser sina egna
uppgifter.

- **Backend-repo:** https://github.com/BleartRama/garagehub
- **Frontend-repo:** https://github.com/BleartRama/Garagehub-frontend

---

## Innehåll

- [Teknik](#teknik)
- [Funktioner](#funktioner)
- [Arkitektur](#arkitektur)
- [Kom igång](#kom-igång)
- [API-översikt](#api-översikt)
- [Roller och behörigheter](#roller-och-behörigheter)
- [Säkerhet](#säkerhet)
- [Frontend](#frontend)
- [Möjliga vidareutvecklingar](#möjliga-vidareutvecklingar)

---

## Teknik

- **Java** och **Spring Boot** (Spring Web, Spring Data JPA, Spring Security)
- **MySQL** som databas, Hibernate som ORM
- **JWT** (jjwt) för stateless autentisering
- **BCrypt** för lösenordshashning
- **Bean Validation** för validering av inkommande data
- Byggs med **Maven**

---

## Funktioner

- Fullständig CRUD för fordon, bokningar och serviceposter
- Registrering och inloggning med JWT
- Rollstyrd åtkomst: `USER`, `MECHANIC`, `ADMIN`
- Ägarskapskontroll – kunder ser och hanterar bara sina egna fordon,
  bokningar och serviceposter
- Bokningsflöde: en bokning skapas som `PENDING` och godkänns (`APPROVED`)
  eller avbokas (`CANCELLED`) av en mekaniker; kunden kan avboka sin egen
- Servicehistorik per fordon
- Servicepåminnelse: ett beräknat värde som avgör om ett fordon behöver
  service baserat på mätarställning eller tid sedan senaste service
- Global felhantering med egna undantag och tydliga HTTP-statuskoder
- Felmeddelanden på svenska

---

## Arkitektur

Projektet följer en strikt trelagersarkitektur med tydlig ansvarsfördelning:

```
Controller  →  Service  →  Repository  →  Databas
```

- **Controller** – tar emot HTTP-anrop, returnerar `ResponseEntity`, delegerar
  allt arbete till service-lagret. Innehåller ingen affärslogik.
- **Service** – all affärslogik, ägarskapskontroller, transaktionshantering,
  kastar egna namngivna undantag.
- **Repository** – databasåtkomst via Spring Data JPA med derived queries.
- **Entity** – JPA-entiteter som speglar databastabellerna.
- **DTO** – separata `XxxRequestDto` och `XxxResponseDto` så att entiteter
  aldrig exponeras direkt i API:et.
- **Mapper** – översätter mellan entitet och DTO.

Domänen har fyra huvudentiteter: `User`, `Vehicle`, `Booking` och
`ServiceRecord`. En mekaniker är inte en egen entitet utan en `User` med rollen
`MECHANIC`.

---

## Kom igång

### Förutsättningar

- Java (17 eller senare)
- Maven
- En körande MySQL-server

### 1. Skapa databasen

Skapa en tom databas i MySQL:

```sql
CREATE DATABASE garagehub;
```

Tabellerna skapas automatiskt av Hibernate vid uppstart
(`spring.jpa.hibernate.ddl-auto=update`).

### 2. Sätt miljövariabler

Applikationen läser två hemligheter från miljövariabler – de finns aldrig i
koden eller i versionshanteringen. Sätt dem i din IDE:s körkonfiguration (eller
i din terminalmiljö):

| Variabel      | Beskrivning                                            |
|---------------|--------------------------------------------------------|
| `DB_PASSWORD` | Lösenordet till din MySQL-användare (`root`)           |
| `JWT_SECRET`  | En lång, slumpmässig hemlig nyckel (minst 32 tecken)   |

`application.properties` refererar till dem via `${DB_PASSWORD}` och
`${JWT_SECRET}`, så filen är säker att versionshantera.

### 3. Starta applikationen

Starta via din IDE, eller med Maven:

```bash
./mvnw spring-boot:run
```

API:et körs på `http://localhost:8080`.

---

## API-översikt

Alla endpoints utom `/auth/**` kräver en giltig JWT i `Authorization`-headern:

```
Authorization: Bearer <token>
```

### Autentisering

| Metod | Endpoint         | Beskrivning                          |
|-------|------------------|--------------------------------------|
| POST  | `/auth/register` | Registrera ny användare (blir `USER`)|
| POST  | `/auth/login`    | Logga in, returnerar en JWT          |

### Fordon

| Metod  | Endpoint                          | Beskrivning                          |
|--------|-----------------------------------|--------------------------------------|
| GET    | `/vehicles`                       | Lista fordon (filtrerat på ägarskap) |
| GET    | `/vehicles/{id}`                  | Hämta ett fordon                     |
| POST   | `/vehicles`                       | Skapa fordon (ägare sätts automatiskt)|
| PUT    | `/vehicles/{id}`                  | Uppdatera fordon                     |
| DELETE | `/vehicles/{id}`                  | Ta bort fordon (endast admin)        |
| GET    | `/vehicles/{id}/service-records`  | Servicehistorik för ett fordon       |
| GET    | `/vehicles/{id}/service-reminder` | Servicepåminnelse för ett fordon     |

### Bokningar

| Metod  | Endpoint                  | Beskrivning                              |
|--------|---------------------------|------------------------------------------|
| GET    | `/bookings`               | Lista bokningar (filtrerat på ägarskap)  |
| GET    | `/bookings/{id}`          | Hämta en bokning                         |
| POST   | `/bookings`               | Skapa bokning (blir `PENDING`)           |
| PUT    | `/bookings/{id}/approve`  | Godkänn bokning (mekaniker/admin)        |
| PUT    | `/bookings/{id}/cancel`   | Avboka bokning (ägare, mekaniker, admin) |
| DELETE | `/bookings/{id}`          | Ta bort bokning (endast admin)           |

### Serviceposter

| Metod  | Endpoint                | Beskrivning                                  |
|--------|-------------------------|----------------------------------------------|
| GET    | `/service-records`      | Lista serviceposter (filtrerat på ägarskap)  |
| GET    | `/service-records/{id}` | Hämta en servicepost                         |
| POST   | `/service-records`      | Skapa servicepost (för egen bil)             |
| DELETE | `/service-records/{id}` | Ta bort servicepost (ägare eller admin)      |

---

## Roller och behörigheter

| Roll       | Behörighet                                                           |
|------------|----------------------------------------------------------------------|
| `USER`     | Ser och hanterar bara sina egna fordon, bokningar och serviceposter. Kan boka tid och avboka sina egna bokningar. Kan lägga in egna serviceposter för sina bilar. |
| `MECHANIC` | Ser alla fordon, bokningar och serviceposter. Kan godkänna och avboka bokningar. |
| `ADMIN`    | Full åtkomst till allt, inklusive borttagning.                       |

Åtkomst styrs på två sätt:

- **Rollbaserat** med `@PreAuthorize` där en ren rollkontroll räcker
  (t.ex. att bara mekaniker/admin får godkänna bokningar).
- **Ägarskapsbaserat** i service-lagret, där den inloggade användaren jämförs
  mot resursens ägare – detta kräver ett databasuppslag och är därför
  affärslogik snarare än en enkel annotation.

---

## Säkerhet

- **Lösenord** hashas med BCrypt innan de sparas – aldrig i klartext.
- **JWT** används för stateless autentisering. Token signeras med en hemlig
  nyckel och verifieras vid varje anrop, så servern behöver ingen session.
  Token bär användarens e-post och roll.
- **Hemligheter** (databaslösenord och JWT-nyckel) ligger i miljövariabler,
  aldrig i koden eller i git.
- **CORS** är konfigurerat så att frontenden (`http://localhost:5173`) får
  anropa API:et.
- Statuskoder: `401` (ej inloggad), `403` (inloggad men saknar behörighet),
  `409` (konflikt, t.ex. resurs som används).

---

## Frontend

Projektet har en separat frontend byggd med HTML, CSS och vanilla JavaScript
som konsumerar API:et. Den hanterar inloggning, sparar JWT i webbläsaren och
visar olika vyer för fordon, bokningar och serviceposter.

- **Frontend-repo:** https://github.com/BleartRama/Garagehub-frontend

Frontenden måste köras på `http://localhost:5173` (det är den origin som
CORS tillåter). Starta en enkel statisk server i frontend-mappen:

```bash
python3 -m http.server 5173
```

Öppna sedan `http://localhost:5173` i webbläsaren med backenden igång.

---

## Möjliga vidareutvecklingar

Följande valdes medvetet bort för att hålla projektet inom rimlig omfattning,
men skulle vara naturliga nästa steg:

- Serviceintervall per komponent (olja, kamrem, bromsvätska osv.) med egna
  km- och tidsintervall
- Per-fordon konfigurerbara serviceplaner
- Dropdown-val av fordon i frontend-formulären i stället för att ange id
- Redigering (PUT) av bokningar och serviceposter