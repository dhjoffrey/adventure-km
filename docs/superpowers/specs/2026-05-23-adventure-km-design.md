# Adventure-KM — Design Spec

**Date :** 2026-05-23  
**Statut :** Approuvé  
**Stack :** Java 17 / Spring Boot 4 · Angular 21 · PostgreSQL 16 · Docker Compose

---

## 1. Vision

Blog/journal personnel d'aventures outdoor (trail, ultra-trail, grandes randonnées). Chaque utilisateur possède une fiche de personnage RPG pixel art représentant son profil d'aventurier, avec un niveau qui progresse au fil des aventures publiées. Les aventures sont des articles enrichis : trace GPX interactive, profil altimétrique, galerie photos, équipements utilisés, texte libre.

---

## 2. Style visuel

**Hybride Terminal Trail + accents Pixel Fantasy (inspiré ancien Dofus)**

- Fond noir profond `#0d1117`
- Vert terminal `#22c55e` pour les éléments primaires
- Doré `#f0a500` pour les accents RPG, bordures et titres importants
- Bordures pixelisées, typographie monospace pour les données chiffrées
- Avatar pixel art (sprite) avec équipements visibles, barre XP, niveau
- Pas de glassmorphism ni de gradients bleus — identité visuelle tranchée et cohérente

---

## 3. Fonctionnalités

### 3.1 Authentification & Utilisateurs

- **Inscription par invitation uniquement** : l'admin génère un token unique → lien `/register?token=XYZ` valable 7 jours
- **Auth JWT** : login → `access_token` (15 min) + `refresh_token` (30 jours) stockés côté client
- **Rôles** : `USER` et `ADMIN`
- Chaque utilisateur a un avatar pixel art configurable et un profil public accessible par `username`

### 3.2 Aventures

**Champs obligatoires :** titre, date, texte (article markdown)  
**Champs optionnels :** type (trail/hike/ultra/race), difficulté (1-5), GPX, photos (1-5), équipements

**Statuts :** `DRAFT` → `PUBLISHED`

**Import GPX :**
- Upload du fichier → parsing via `io.jenetics:jpx`
- Extraction automatique : distance (km), D+, D-, durée, altitude max/min
- Données stockées dans `adventure_stats`
- GeoJSON généré pour la carte Leaflet
- Points d'élévation exportés pour le graphique Chart.js

**Photos :**
- Max 5 photos par aventure, 10 Mo chacune
- Redimensionnement automatique côté serveur (max 1200×800 px, JPEG 85%)
- Stockées dans le volume Docker `/uploads/photos/{adventure_id}/`

### 3.3 Fiche RPG Aventurier

Chaque profil utilisateur affiche :
- Sprite pixel art choisi parmi un catalogue de sprites pré-définis (tête + corps, ~10 options) + équipements actifs superposés
- Niveau calculé à partir du score RPG
- Barre XP (progression vers le niveau suivant)
- Stats : km totaux, D+ total, nombre d'aventures, record de distance

**Formule score RPG :**
```
score = (total_km × 1) + (total_d_plus / 100 × 2) + (adventure_count × 50)
level = floor(sqrt(score / 10))
```
Recalculé à chaque publication d'aventure (événement Spring).

**Équipements par aventure :** chaque aventure liste les items du catalogue utilisés (chaussures, gilet, bâtons, casquette, lunettes, sac, montre, autre). Ces items apparaissent sur le sprite de l'avatar dans la page profil (équipements de la dernière aventure publiée).

### 3.4 Classement

Page `/leaderboard` avec basculement entre :
- Score RPG (par défaut)
- Kilomètres totaux
- Dénivelé positif total
- Nombre d'aventures

Section séparée **Volume annuel** : km et D+ de l'année courante, alimentés par l'API Strava ou Coros (OAuth2, lecture seule). Intégration prévue mais non bloquante au lancement.

### 3.5 Administration

Accessible uniquement au rôle ADMIN :
- Générer/révoquer des liens d'invitation
- Liste des utilisateurs et leurs rôles
- CRUD du catalogue d'équipements (avec icône et clé sprite)

---

## 4. Modèle de données

### Tables

```sql
users
  id BIGSERIAL PK
  username VARCHAR(50) UNIQUE NOT NULL
  email VARCHAR(255) UNIQUE NOT NULL
  password_hash VARCHAR NOT NULL
  avatar_sprite_id INTEGER DEFAULT 1
  role VARCHAR(10) DEFAULT 'USER'   -- USER | ADMIN
  strava_token VARCHAR
  coros_token VARCHAR
  created_at TIMESTAMP DEFAULT NOW()

invitations
  id BIGSERIAL PK
  token VARCHAR(64) UNIQUE NOT NULL
  email VARCHAR(255)
  invited_by BIGINT REFERENCES users(id)
  used_at TIMESTAMP
  expires_at TIMESTAMP NOT NULL

adventures
  id BIGSERIAL PK
  user_id BIGINT REFERENCES users(id) NOT NULL
  title VARCHAR(255) NOT NULL
  date DATE NOT NULL
  content TEXT NOT NULL               -- markdown
  type VARCHAR(20)                    -- trail | hike | ultra | race
  difficulty INTEGER CHECK (difficulty BETWEEN 1 AND 5)
  gpx_path VARCHAR
  status VARCHAR(10) DEFAULT 'DRAFT'  -- DRAFT | PUBLISHED
  created_at TIMESTAMP DEFAULT NOW()
  updated_at TIMESTAMP DEFAULT NOW()

adventure_stats
  id BIGSERIAL PK
  adventure_id BIGINT REFERENCES adventures(id) UNIQUE NOT NULL
  distance_km DECIMAL(8,2)
  elevation_gain_m INTEGER
  elevation_loss_m INTEGER
  duration_minutes INTEGER
  max_altitude_m INTEGER
  min_altitude_m INTEGER

photos
  id BIGSERIAL PK
  adventure_id BIGINT REFERENCES adventures(id) NOT NULL
  file_path VARCHAR NOT NULL
  caption VARCHAR(255)
  sort_order INTEGER NOT NULL CHECK (sort_order BETWEEN 1 AND 5)
  uploaded_at TIMESTAMP DEFAULT NOW()

equipment_items
  id BIGSERIAL PK
  name VARCHAR(100) NOT NULL
  category VARCHAR(20) NOT NULL   -- shoes | vest | poles | hat | glasses | backpack | watch | other
  icon_key VARCHAR(50)
  pixel_sprite_key VARCHAR(50)

adventure_equipment
  adventure_id BIGINT REFERENCES adventures(id)
  equipment_id BIGINT REFERENCES equipment_items(id)
  PRIMARY KEY (adventure_id, equipment_id)

user_levels   -- recalculé à chaque publication
  user_id BIGINT REFERENCES users(id) PRIMARY KEY
  total_km DECIMAL(10,2) DEFAULT 0
  total_elevation_m INTEGER DEFAULT 0
  adventure_count INTEGER DEFAULT 0
  rpg_score INTEGER DEFAULT 0
  level INTEGER DEFAULT 1
```

**Migrations :** Flyway (`V1__schema.sql`, `V2__seed_data.sql`)  
**Profils Spring :** `dev` → H2 in-memory, `prod` → PostgreSQL 16

---

## 5. Architecture technique

### 5.1 Backend — Spring Boot 4 / Java 17

**Packages :**
```
com.adventurekm/
  config/       SecurityConfig, WebConfig, CorsConfig
  controller/   AuthController, AdventureController, UserController,
                LeaderboardController, AdminController, FileController
  dto/          request/ et response/ séparés
  mapper/       MapStruct — Entity ↔ DTO
  model/        entités JPA (User, Adventure, AdventureStats, Photo, EquipmentItem…)
  repository/   interfaces Spring Data JPA
  service/      AuthService, AdventureService, GpxProcessingService,
                LevelCalculationService, FileStorageService, InvitationService
  exception/    GlobalExceptionHandler (@ControllerAdvice)
```

**Design patterns :**
- **Repository** : une interface JPA par entité, zéro SQL manuel sauf requêtes complexes via `@Query`
- **Service layer** : toute la logique métier dans les services, les controllers ne font que router
- **DTO** : MapStruct génère les mappers à la compilation, jamais d'entité JPA retournée en réponse REST
- **Strategy** : `LevelCalculationStrategy` interface → `DefaultLevelStrategy` (extensible)
- **Facade** : `GpxProcessingService` encapsule le parsing JPX + génération stats + export GeoJSON
- **Observer** : événement Spring `AdventurePublishedEvent` → recalcul du score RPG de l'auteur

**Dépendances clés :**
```xml
spring-boot-starter-security
spring-boot-starter-data-jpa
spring-boot-starter-web
flyway-core
io.jsonwebtoken:jjwt
io.jenetics:jpx          <!-- parsing GPX -->
org.mapstruct:mapstruct
org.projectlombok:lombok
postgresql / com.h2database:h2
```

### 5.2 Frontend — Angular 21

**Structure `src/app/` :**
```
core/
  auth/         AuthService, AuthGuard, JwtInterceptor, TokenStorageService
  models/       interfaces TypeScript (User, Adventure, AdventureStats…)
shared/
  components/   header/, footer/, pixel-avatar/, adventure-card/, stat-badge/
  pipes/        duration.pipe, elevation.pipe
features/
  home/         HomeComponent (smart)
  profile/      ProfileComponent (smart) + components/ (rpg-sheet, xp-bar, equipment-grid)
  adventures/   AdventureListComponent (smart), AdventureDetailComponent (smart)
                adventure-form/ (smart, multi-étapes)
  leaderboard/  LeaderboardComponent (smart)
  admin/        AdminComponent (smart)
```

**Design patterns :**
- **Smart / Dumb** : les pages (features/) injectent les services et gèrent l'état ; les composants partagés (`shared/components/`) reçoivent uniquement des `@Input()` et émettent des `@Output()`
- **Feature lazy-loading** : chaque feature est un module routé chargé à la demande
- **HTTP Interceptor** : injection automatique du header `Authorization: Bearer <token>` sur toutes les requêtes API
- **Angular Signals** : état des services via `signal()` / `computed()` — pas de NgRx pour ce scope

**Librairies :**
- `@bluehalo/ngx-leaflet` + `leaflet` — affichage carte + trace GPX
- `ng2-charts` + `chart.js` — profil altimétrique (LineChart)
- `ngx-markdown` — rendu des articles + éditeur

### 5.3 Infrastructure Docker Compose

```
services:
  nginx         → port 80/443, reverse proxy, sert le front et /uploads
  backend       → port 8080, Spring Boot JAR, profil prod
  frontend      → Nginx servant le build Angular dist/
  postgres      → PostgreSQL 16, volume persistant

volumes:
  postgres_data
  uploads_data   (GPX + photos)
```

SSL via Certbot / Let's Encrypt sur le VPS.

---

## 6. Pages et navigation

| Route | Composant | Accès |
|-------|-----------|-------|
| `/` | HomeComponent | Public |
| `/login` | LoginComponent | Public |
| `/register` | RegisterComponent | Public (token requis) |
| `/adventures` | AdventureListComponent | Public |
| `/adventures/:id` | AdventureDetailComponent | Public |
| `/adventures/new` | AdventureFormComponent | Connecté |
| `/adventures/:id/edit` | AdventureFormComponent | Auteur ou Admin |
| `/profile/:username` | ProfileComponent | Public |
| `/leaderboard` | LeaderboardComponent | Public |
| `/admin` | AdminComponent | Admin |

---

## 7. Seed data

`V2__seed_data.sql` crée un utilisateur `joffrey` (admin) et 3 aventures pré-remplies :
- **GR54** — Tour de l'Oisans, 200 km, 12 000 m D+, 4,5 jours
- **UTOBI** — Ultra Trail de l'Obiou, 72 km, 4 200 m D+, 12h
- **UT4M Challenge** — 4 étapes autour de Grenoble, DNF

Catalogue d'équipements par défaut : 15 items couvrant les 8 catégories.

---

## 8. Hors périmètre v1

- Commentaires sur les aventures
- Notifications (email, push)
- Carte globale avec toutes les traces
- Import automatique depuis Strava/Coros (prévu v2, OAuth2 en base)
- Application mobile
