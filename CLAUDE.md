# Adventure-KM — Contexte pour Claude Code

## C'est quoi

Journal de bord trail/ultra-trail style RPG pixel art. Aventures = articles enrichis (GPX, photos, équipement, markdown). Chaque utilisateur a une fiche de personnage qui progresse avec ses sorties. Design "Terminal Trail + Pixel Fantasy" : fond noir `#0d1117`, vert `#22c55e`, doré `#f0a500`.

## Stack

| Couche | Techno |
|--------|--------|
| Backend | Java 17, Spring Boot 4, Spring Security 7 (JWT), Hibernate 7.2 |
| BDD | H2 in-memory (dev) / PostgreSQL 16 (prod), Flyway (V1→V9) |
| Frontend | Angular 21, SSR Client-only, Signals |
| Carte | Leaflet + OpenStreetMap / OpenTopoMap |
| Infra | Docker Compose : nginx + backend + frontend + postgres |

## Dev local

```bash
# Backend (H2 in-memory, port 8080)
cd adventure-km-backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Frontend (port 4200, proxy → 8080)
cd adventure-km-frontend && npx ng serve

# Compte seed : joffrey / 123456 (ADMIN)
# H2 console : http://localhost:8080/h2-console  (jdbc:h2:mem:adventurekm, user: sa, pass: vide)
```

## Structure backend

```
com.adventurekm.backend/
  config/      SecurityConfig, WebConfig, CorsConfig
  controller/  AdventureController, AuthController, UserController,
               LeaderboardController, AdminController, FileController
  dto/         request/ + response/ — jamais d'entité JPA retournée directement
  mapper/      MapStruct (compilation)
  model/       Adventure, AdventureStats, User, UserLevel, Photo, EquipmentItem
  repository/  Spring Data JPA
  service/     AdventureService, GpxProcessingService (JPX), LevelCalculationService,
               FileStorageService, AuthService, AdventureEventListener
  exception/   GlobalExceptionHandler (@ControllerAdvice)
```

**Pattern clé :** `AdventurePublishedEvent` → `@TransactionalEventListener(AFTER_COMMIT)` → recalcul `UserLevel`.

## Structure frontend

```
src/app/
  core/        auth/ (JWT interceptor, refresh auto), models/, services/
  shared/      header (avatar dropdown), pixel-avatar, adventure-card, stat-badge
  features/    home/, adventures/ (list + detail + form 3 étapes), profile/, leaderboard/, admin/
```

**SSR :** toutes les routes en `RenderMode.Client` (évite les erreurs SSRF Angular).

## Modèle de données (résumé)

```
users            id, username, role (USER|ADMIN), avatar_sprite_id
adventures       id, user_id, title, date, content (markdown), type, difficulty (1-5), status (DRAFT|PUBLISHED)
adventure_stats  distance_km, elevation_gain/loss_m, duration_minutes, max/min/avg_altitude_m
photos           adventure_id, file_path, sort_order (1-5)
equipment_items  name, category, icon_key, pixel_sprite_key
user_levels      total_km, total_elevation_m, adventure_count, rpg_score, level
```

**Score RPG :** `score = km + (D+/100)×2 + aventures×50` | `level = floor(sqrt(score/10))`

## Routes

| Route | Accès |
|-------|-------|
| `/`, `/adventures`, `/adventures/:id`, `/profile/:username`, `/leaderboard` | Public |
| `/login`, `/register?token=` | Public |
| `/adventures/new`, `/adventures/:id/edit` | Connecté / propriétaire |
| `/admin` | ADMIN uniquement |

## Gotchas connus

- **Hibernate 7 + H2 CHECK constraints** : les contraintes `CHK_ADVENTURES_TYPE` etc. explosaient même avec des valeurs enum valides → supprimées en migration V9. Ne pas les remettre.
- **SSRF Angular** : `AngularNodeAppEngine` bloque `localhost` par défaut en dev → `allowedHosts` passé au constructeur dans `server.ts`.
- **Auth flow** : intercepteur JWT → sur 401 : refresh → retry → sinon `tokenStorage.clear()` (logout visible).
- **H2 dev** : base réinitialisée à chaque démarrage du backend, seed via Flyway.

## V2 — Plan approuvé (pas encore codé)

Ordre d'implémentation : 4 → 1 → 2 → 3

1. **Avatar + menu navbar** — dropdown avatar, page `/settings`, picker 10 sprites
2. **Import activités** — OAuth Strava, Coros non-officiel, table `imported_activities`
3. **Classement étendu** — score inclut km des activités importées
4. **Hero section home** — animation CSS rétro pixel art (montagnes + runners)
