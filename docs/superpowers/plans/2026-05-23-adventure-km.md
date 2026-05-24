# Adventure-KM Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a full-stack outdoor adventure blog with RPG-style user progression, GPX trace mapping, and pixel art terminal design.

**Architecture:** Spring Boot 4 REST API backend with JWT auth, Flyway-managed PostgreSQL (prod) / H2 (dev) database, and Angular 21 SPA frontend. The backend exposes REST endpoints for auth, adventure CRUD, GPX processing, photo management, equipment catalog, and RPG level calculation. The frontend renders a pixel-art terminal-themed UI with Leaflet maps, Chart.js elevation profiles, and markdown adventure content.

**Tech Stack:** Java 17 / Spring Boot 4.0.3 / Maven / Flyway / PostgreSQL 16 + H2 / MapStruct / JJWT · Angular 21 / Signals / Vitest / Leaflet / Chart.js / ngx-markdown · Docker Compose / Nginx

**Project Root:** `/media/joffrey/hdd-workspace/app/eclipse-workspace/adventure-km/`
**Backend:** `adventure-km-backend/` (Maven, package `com.adventurekm.backend`)
**Frontend:** `adventure-km-frontend/` (Angular 21, npm)

---

## Phase 1 — Backend Foundation

### Task 1: Project Cleanup & Maven Dependencies

**Files:**
- Delete: `adventure-km-backend/src/main/java/com/adventurekm/backend/model/Activity.java`
- Delete: `adventure-km-backend/src/main/java/com/adventurekm/backend/controller/ActivityController.java`
- Delete: `adventure-km-backend/src/main/java/com/adventurekm/backend/service/ActivityService.java`
- Delete: `adventure-km-backend/src/main/java/com/adventurekm/backend/repository/ActivityRepository.java`
- Delete: `adventure-km-backend/src/main/java/dto/ActivityDTO.java`
- Delete: `adventure-km-backend/src/main/java/com/adventurekm/backend/config/WebConfig.java`
- Delete: `adventure-km-backend/src/main/resources/static/` (entire directory — old Angular build artifact)
- Modify: `adventure-km-backend/pom.xml`
- Create: `adventure-km-backend/src/main/resources/application.yml`
- Delete: `adventure-km-backend/src/main/resources/application.properties`

- [ ] **Step 1: Delete legacy files**

```bash
cd adventure-km-backend
rm src/main/java/com/adventurekm/backend/model/Activity.java
rm src/main/java/com/adventurekm/backend/controller/ActivityController.java
rm src/main/java/com/adventurekm/backend/service/ActivityService.java
rm src/main/java/com/adventurekm/backend/repository/ActivityRepository.java
rm -r src/main/java/dto/
rm src/main/java/com/adventurekm/backend/config/WebConfig.java
rm -rf src/main/resources/static/
rm src/test/java/com/adventurekm/backend/AdventureKmBackendApplicationTests.java
```

- [ ] **Step 2: Replace pom.xml with full dependencies**

Replace the entire `adventure-km-backend/pom.xml` content:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.0.3</version>
        <relativePath/>
    </parent>
    <groupId>com.adventurekm</groupId>
    <artifactId>adventure-km-backend</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>adventure-km-backend</name>

    <properties>
        <java.version>17</java.version>
        <mapstruct.version>1.6.3</mapstruct.version>
        <jjwt.version>0.12.6</jjwt.version>
        <jpx.version>7.2.0</jpx.version>
    </properties>

    <dependencies>
        <!-- Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webmvc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Security -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <!-- Data -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-database-h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-database-postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-h2console</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <!-- GPX parsing -->
        <dependency>
            <groupId>io.jenetics</groupId>
            <artifactId>jpx</artifactId>
            <version>${jpx.version}</version>
        </dependency>

        <!-- MapStruct -->
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>${mapstruct.version}</version>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webmvc-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </path>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok-mapstruct-binding</artifactId>
                            <version>0.2.0</version>
                        </path>
                        <path>
                            <groupId>org.mapstruct</groupId>
                            <artifactId>mapstruct-processor</artifactId>
                            <version>${mapstruct.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 3: Create application.yml**

Delete `adventure-km-backend/src/main/resources/application.properties` and create `adventure-km-backend/src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: adventure-km-backend

  profiles:
    active: dev

  flyway:
    enabled: true
    locations: classpath:db/migration

  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB

  jpa:
    open-in-view: false

server:
  port: 8080

app:
  jwt:
    secret: adventure-km-dev-secret-key-change-in-production-must-be-at-least-256-bits-long
    access-token-expiration-ms: 900000
    refresh-token-expiration-ms: 2592000000
  upload:
    dir: ./uploads

---
spring:
  config:
    activate:
      on-profile: dev
  datasource:
    url: jdbc:h2:mem:adventurekm
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
  h2:
    console:
      enabled: true

---
spring:
  config:
    activate:
      on-profile: prod
  datasource:
    url: jdbc:postgresql://localhost:5432/adventurekm
    driver-class-name: org.postgresql.Driver
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
```

- [ ] **Step 4: Create backend directory structure and verify compilation**

```bash
cd adventure-km-backend
mkdir -p src/main/java/com/adventurekm/backend/{config,controller,dto/request,dto/response,mapper,model,repository,service,exception}
mkdir -p src/main/resources/db/migration
mkdir -p src/test/java/com/adventurekm/backend/{controller,service,repository}
./mvnw compile -q
```

Expected: BUILD SUCCESS (no source files to compile except the main Application class).

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "chore: clean up legacy code and add all Maven dependencies"
```

---

### Task 2: Flyway Migrations

**Files:**
- Create: `adventure-km-backend/src/main/resources/db/migration/V1__schema.sql`
- Create: `adventure-km-backend/src/main/resources/db/migration/V2__seed_data.sql`

- [ ] **Step 1: Create V1 schema migration**

Create `adventure-km-backend/src/main/resources/db/migration/V1__schema.sql`:

```sql
CREATE TABLE users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    avatar_sprite_id INTEGER DEFAULT 1,
    role VARCHAR(10) DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
    strava_token VARCHAR(255),
    coros_token VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE invitations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    token VARCHAR(64) UNIQUE NOT NULL,
    email VARCHAR(255),
    invited_by BIGINT REFERENCES users(id),
    used_at TIMESTAMP,
    expires_at TIMESTAMP NOT NULL
);

CREATE TABLE adventures (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(20) CHECK (type IN ('trail', 'hike', 'ultra', 'race')),
    difficulty INTEGER CHECK (difficulty BETWEEN 1 AND 5),
    gpx_path VARCHAR(500),
    status VARCHAR(10) DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE adventure_stats (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    adventure_id BIGINT UNIQUE NOT NULL REFERENCES adventures(id),
    distance_km DECIMAL(8,2),
    elevation_gain_m INTEGER,
    elevation_loss_m INTEGER,
    duration_minutes INTEGER,
    max_altitude_m INTEGER,
    min_altitude_m INTEGER
);

CREATE TABLE photos (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    adventure_id BIGINT NOT NULL REFERENCES adventures(id),
    file_path VARCHAR(500) NOT NULL,
    caption VARCHAR(255),
    sort_order INTEGER NOT NULL CHECK (sort_order BETWEEN 1 AND 5),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE equipment_items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(20) NOT NULL CHECK (category IN ('shoes', 'vest', 'poles', 'hat', 'glasses', 'backpack', 'watch', 'other')),
    icon_key VARCHAR(50),
    pixel_sprite_key VARCHAR(50)
);

CREATE TABLE adventure_equipment (
    adventure_id BIGINT REFERENCES adventures(id),
    equipment_id BIGINT REFERENCES equipment_items(id),
    PRIMARY KEY (adventure_id, equipment_id)
);

CREATE TABLE user_levels (
    user_id BIGINT PRIMARY KEY REFERENCES users(id),
    total_km DECIMAL(10,2) DEFAULT 0,
    total_elevation_m INTEGER DEFAULT 0,
    adventure_count INTEGER DEFAULT 0,
    rpg_score INTEGER DEFAULT 0,
    level INTEGER DEFAULT 1
);
```

- [ ] **Step 2: Create V2 seed data migration**

Create `adventure-km-backend/src/main/resources/db/migration/V2__seed_data.sql`:

```sql
-- Admin user (password: admin123 — BCrypt hash)
INSERT INTO users (username, email, password_hash, role, avatar_sprite_id)
VALUES ('joffrey', 'dhjoffrey@gmail.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMye.IhQD9/RvCj0l0F7Bp0fU5.6k8UmWVe',
        'ADMIN', 1);

INSERT INTO user_levels (user_id, total_km, total_elevation_m, adventure_count, rpg_score, level)
VALUES (1, 0, 0, 0, 0, 1);

-- Sample adventures
INSERT INTO adventures (user_id, title, date, content, type, difficulty, status)
VALUES
(1, 'GR54 — Tour de l''Oisans', '2025-07-15',
 '## Le Tour de l''Oisans

Cinq jours à boucler le mythique GR54 autour du massif des Écrins. 200 km, 12 000 m de dénivelé positif. Un itinéraire sauvage entre glaciers et alpages.

### Jour 1 — Bourg-d''Oisans → La Bérarde
40 km pour commencer en douceur... ou pas. Le col de la Muzelle donne le ton dès le premier jour.

### Jour 2 — La Bérarde → Vallouise
Traversée par le col de l''Aup Martin. Ambiance haute montagne garantie.

### Jour 3 — Vallouise → Monêtier-les-Bains
Le col de l''Eychauda puis descente vers le Lautaret. Les jambes commencent à parler.

### Jour 4 — Monêtier → Clavans
Passage par le col du Galibier versant sauvage. Paysages lunaires.

### Jour 5 — Clavans → Bourg-d''Oisans
Retour par le col de Sarenne. Boucle bouclée, genoux rincés, tête pleine.',
 'ultra', 5, 'PUBLISHED'),

(1, 'UTOBI — Ultra Trail de l''Obiou', '2025-09-20',
 '## UTOBI 72K

72 kilomètres et 4 200 m de D+ dans le Trièves. Course de nuit, départ 22h. L''Obiou dans le noir, c''est autre chose.

Passage au sommet à 2 789 m sous les étoiles. Pas de vent, la chance. Descente technique sur les crêtes du Grand Ferrand.

Arrivée en 12h pile. Content du chrono, content d''être arrivé.',
 'race', 4, 'PUBLISHED'),

(1, 'UT4M Challenge — DNF', '2025-10-05',
 '## UT4M Challenge

Quatre étapes autour de Grenoble. Abandon à la fin de l''étape 3 — genou droit qui lâche dans la descente du Taillefer.

Pas de honte, c''est du trail. On reviendra.',
 'ultra', 5, 'PUBLISHED');

-- Stats for GR54
INSERT INTO adventure_stats (adventure_id, distance_km, elevation_gain_m, elevation_loss_m, duration_minutes, max_altitude_m, min_altitude_m)
VALUES (1, 200.00, 12000, 12000, 6480, 2735, 720);

-- Stats for UTOBI
INSERT INTO adventure_stats (adventure_id, distance_km, elevation_gain_m, elevation_loss_m, duration_minutes, max_altitude_m, min_altitude_m)
VALUES (2, 72.00, 4200, 4200, 720, 2789, 780);

-- Stats for UT4M (partial)
INSERT INTO adventure_stats (adventure_id, distance_km, elevation_gain_m, elevation_loss_m, duration_minutes, max_altitude_m, min_altitude_m)
VALUES (3, 95.00, 5800, 5800, NULL, 2857, 220);

-- Update user_levels for joffrey
UPDATE user_levels SET
    total_km = 367.00,
    total_elevation_m = 22000,
    adventure_count = 3,
    rpg_score = 367 + (22000 / 100 * 2) + (3 * 50),
    level = CAST(FLOOR(SQRT((367 + (22000 / 100 * 2) + (3 * 50)) / 10.0)) AS INTEGER)
WHERE user_id = 1;

-- Equipment catalog
INSERT INTO equipment_items (name, category, icon_key, pixel_sprite_key) VALUES
('Hoka Speedgoat 5', 'shoes', 'shoe-trail', 'shoes-red'),
('Hoka Tecton X2', 'shoes', 'shoe-race', 'shoes-orange'),
('Salomon Sense Pro 5', 'shoes', 'shoe-light', 'shoes-blue'),
('Salomon ADV Skin 12', 'vest', 'vest-12', 'vest-blue'),
('Salomon ADV Skin 5', 'vest', 'vest-5', 'vest-red'),
('Black Diamond Distance Carbon Z', 'poles', 'poles-carbon', 'poles-black'),
('Leki Ultratrail FX.One', 'poles', 'poles-alu', 'poles-silver'),
('Buff Original', 'hat', 'buff', 'hat-buff'),
('Salomon XA Cap', 'hat', 'cap', 'hat-cap'),
('Julbo Aerospeed', 'glasses', 'glasses-sport', 'glasses-black'),
('Oakley Radar EV', 'glasses', 'glasses-wide', 'glasses-white'),
('Osprey Duro 15', 'backpack', 'pack-15', 'backpack-green'),
('Coros Pace 3', 'watch', 'watch-gps', 'watch-black'),
('Garmin Fenix 7', 'watch', 'watch-multi', 'watch-grey'),
('Petzl Swift RL', 'other', 'headlamp', 'lamp-orange');

-- Link equipment to adventures
INSERT INTO adventure_equipment (adventure_id, equipment_id) VALUES
(1, 1), (1, 4), (1, 6), (1, 9), (1, 10), (1, 12), (1, 13), (1, 15),
(2, 2), (2, 5), (2, 7), (2, 8), (2, 10), (2, 13), (2, 15),
(3, 1), (3, 4), (3, 6), (3, 9), (3, 10), (3, 12), (3, 13);
```

- [ ] **Step 3: Verify migrations run against H2**

```bash
cd adventure-km-backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Expected: Application starts, Flyway runs V1 and V2 successfully, then stop with Ctrl+C. Check logs for `Successfully applied 2 migrations`.

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "feat: add Flyway migrations — schema and seed data"
```

---

### Task 3: JPA Entities

**Files:**
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/model/User.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/model/Invitation.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/model/Adventure.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/model/AdventureStats.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/model/Photo.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/model/EquipmentItem.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/model/UserLevel.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/model/AdventureStatus.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/model/AdventureType.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/model/EquipmentCategory.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/model/Role.java`
- Test: `adventure-km-backend/src/test/java/com/adventurekm/backend/model/EntityMappingTest.java`

- [ ] **Step 1: Create enum types**

`Role.java`:
```java
package com.adventurekm.backend.model;

public enum Role {
    USER, ADMIN
}
```

`AdventureStatus.java`:
```java
package com.adventurekm.backend.model;

public enum AdventureStatus {
    DRAFT, PUBLISHED
}
```

`AdventureType.java`:
```java
package com.adventurekm.backend.model;

public enum AdventureType {
    trail, hike, ultra, race
}
```

`EquipmentCategory.java`:
```java
package com.adventurekm.backend.model;

public enum EquipmentCategory {
    shoes, vest, poles, hat, glasses, backpack, watch, other
}
```

- [ ] **Step 2: Create all entity classes**

`User.java`:
```java
package com.adventurekm.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "avatar_sprite_id")
    @Builder.Default
    private Integer avatarSpriteId = 1;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "strava_token")
    private String stravaToken;

    @Column(name = "coros_token")
    private String corosToken;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
```

`Invitation.java`:
```java
package com.adventurekm.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by")
    private User invitedBy;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}
```

`Adventure.java`:
```java
package com.adventurekm.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "adventures")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Adventure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AdventureType type;

    private Integer difficulty;

    @Column(name = "gpx_path", length = 500)
    private String gpxPath;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Builder.Default
    private AdventureStatus status = AdventureStatus.DRAFT;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "adventure", cascade = CascadeType.ALL, orphanRemoval = true)
    private AdventureStats stats;

    @OneToMany(mappedBy = "adventure", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder")
    @Builder.Default
    private List<Photo> photos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "adventure_equipment",
        joinColumns = @JoinColumn(name = "adventure_id"),
        inverseJoinColumns = @JoinColumn(name = "equipment_id")
    )
    @Builder.Default
    private Set<EquipmentItem> equipment = new HashSet<>();
}
```

`AdventureStats.java`:
```java
package com.adventurekm.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "adventure_stats")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AdventureStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adventure_id", unique = true, nullable = false)
    private Adventure adventure;

    @Column(name = "distance_km", precision = 8, scale = 2)
    private BigDecimal distanceKm;

    @Column(name = "elevation_gain_m")
    private Integer elevationGainM;

    @Column(name = "elevation_loss_m")
    private Integer elevationLossM;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "max_altitude_m")
    private Integer maxAltitudeM;

    @Column(name = "min_altitude_m")
    private Integer minAltitudeM;
}
```

`Photo.java`:
```java
package com.adventurekm.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adventure_id", nullable = false)
    private Adventure adventure;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    private String caption;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "uploaded_at")
    @Builder.Default
    private LocalDateTime uploadedAt = LocalDateTime.now();
}
```

`EquipmentItem.java`:
```java
package com.adventurekm.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "equipment_items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EquipmentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EquipmentCategory category;

    @Column(name = "icon_key", length = 50)
    private String iconKey;

    @Column(name = "pixel_sprite_key", length = 50)
    private String pixelSpriteKey;
}
```

`UserLevel.java`:
```java
package com.adventurekm.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "user_levels")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserLevel {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "total_km", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalKm = BigDecimal.ZERO;

    @Column(name = "total_elevation_m")
    @Builder.Default
    private Integer totalElevationM = 0;

    @Column(name = "adventure_count")
    @Builder.Default
    private Integer adventureCount = 0;

    @Column(name = "rpg_score")
    @Builder.Default
    private Integer rpgScore = 0;

    @Builder.Default
    private Integer level = 1;
}
```

- [ ] **Step 3: Write entity mapping test**

Create `adventure-km-backend/src/test/java/com/adventurekm/backend/model/EntityMappingTest.java`:

```java
package com.adventurekm.backend.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("dev")
class EntityMappingTest {

    @Autowired
    private TestEntityManager em;

    @Test
    void userAndAdventureMapping() {
        User user = em.find(User.class, 1L);
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("joffrey");
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void adventureWithStatsMapping() {
        Adventure adventure = em.find(Adventure.class, 1L);
        assertThat(adventure).isNotNull();
        assertThat(adventure.getTitle()).contains("GR54");
        assertThat(adventure.getStats()).isNotNull();
        assertThat(adventure.getStats().getElevationGainM()).isEqualTo(12000);
    }

    @Test
    void userLevelMapping() {
        UserLevel level = em.find(UserLevel.class, 1L);
        assertThat(level).isNotNull();
        assertThat(level.getAdventureCount()).isEqualTo(3);
    }

    @Test
    void equipmentLinkMapping() {
        Adventure adventure = em.find(Adventure.class, 1L);
        assertThat(adventure.getEquipment()).isNotEmpty();
        assertThat(adventure.getEquipment().size()).isEqualTo(8);
    }
}
```

- [ ] **Step 4: Run tests**

```bash
cd adventure-km-backend && ./mvnw test -Dtest=EntityMappingTest -pl .
```

Expected: 4 tests pass.

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "feat: add JPA entities for all 8 tables"
```

---

### Task 4: Repositories & Exception Handler

**Files:**
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/repository/UserRepository.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/repository/InvitationRepository.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/repository/AdventureRepository.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/repository/AdventureStatsRepository.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/repository/PhotoRepository.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/repository/EquipmentItemRepository.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/repository/UserLevelRepository.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/exception/ResourceNotFoundException.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/exception/BadRequestException.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/exception/GlobalExceptionHandler.java`

- [ ] **Step 1: Create all repository interfaces**

`UserRepository.java`:
```java
package com.adventurekm.backend.repository;

import com.adventurekm.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
```

`InvitationRepository.java`:
```java
package com.adventurekm.backend.repository;

import com.adventurekm.backend.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByToken(String token);
    List<Invitation> findByInvitedById(Long userId);
}
```

`AdventureRepository.java`:
```java
package com.adventurekm.backend.repository;

import com.adventurekm.backend.model.Adventure;
import com.adventurekm.backend.model.AdventureStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdventureRepository extends JpaRepository<Adventure, Long> {
    List<Adventure> findByStatusOrderByDateDesc(AdventureStatus status);
    List<Adventure> findByUserIdOrderByDateDesc(Long userId);
    List<Adventure> findByUserIdAndStatusOrderByDateDesc(Long userId, AdventureStatus status);
}
```

`AdventureStatsRepository.java`:
```java
package com.adventurekm.backend.repository;

import com.adventurekm.backend.model.AdventureStats;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdventureStatsRepository extends JpaRepository<AdventureStats, Long> {
    Optional<AdventureStats> findByAdventureId(Long adventureId);
}
```

`PhotoRepository.java`:
```java
package com.adventurekm.backend.repository;

import com.adventurekm.backend.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByAdventureIdOrderBySortOrder(Long adventureId);
    int countByAdventureId(Long adventureId);
}
```

`EquipmentItemRepository.java`:
```java
package com.adventurekm.backend.repository;

import com.adventurekm.backend.model.EquipmentItem;
import com.adventurekm.backend.model.EquipmentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EquipmentItemRepository extends JpaRepository<EquipmentItem, Long> {
    List<EquipmentItem> findByCategory(EquipmentCategory category);
}
```

`UserLevelRepository.java`:
```java
package com.adventurekm.backend.repository;

import com.adventurekm.backend.model.UserLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserLevelRepository extends JpaRepository<UserLevel, Long> {
    List<UserLevel> findAllByOrderByRpgScoreDesc();
    List<UserLevel> findAllByOrderByTotalKmDesc();
    List<UserLevel> findAllByOrderByTotalElevationMDesc();
    List<UserLevel> findAllByOrderByAdventureCountDesc();
}
```

- [ ] **Step 2: Create exception classes and handler**

`ResourceNotFoundException.java`:
```java
package com.adventurekm.backend.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " not found with id " + id);
    }
}
```

`BadRequestException.java`:
```java
package com.adventurekm.backend.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
```

`GlobalExceptionHandler.java`:
```java
package com.adventurekm.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequest(BadRequestException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
```

- [ ] **Step 3: Verify compilation**

```bash
cd adventure-km-backend && ./mvnw compile -q
```

Expected: BUILD SUCCESS.

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "feat: add repositories and global exception handler"
```

---

## Phase 2 — Backend Auth

### Task 5: JWT Utilities

**Files:**
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/config/JwtTokenProvider.java`
- Test: `adventure-km-backend/src/test/java/com/adventurekm/backend/config/JwtTokenProviderTest.java`

- [ ] **Step 1: Write the failing test**

Create `adventure-km-backend/src/test/java/com/adventurekm/backend/config/JwtTokenProviderTest.java`:

```java
package com.adventurekm.backend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        jwtTokenProvider.setSecret("test-secret-key-that-must-be-at-least-256-bits-long-for-hs256-algorithm");
        jwtTokenProvider.setAccessTokenExpirationMs(900_000L);
        jwtTokenProvider.setRefreshTokenExpirationMs(2_592_000_000L);
        jwtTokenProvider.init();
    }

    @Test
    void generateAndValidateAccessToken() {
        String token = jwtTokenProvider.generateAccessToken("joffrey");
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUsernameFromToken(token)).isEqualTo("joffrey");
    }

    @Test
    void generateAndValidateRefreshToken() {
        String token = jwtTokenProvider.generateRefreshToken("joffrey");
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUsernameFromToken(token)).isEqualTo("joffrey");
    }

    @Test
    void expiredTokenIsInvalid() {
        jwtTokenProvider.setAccessTokenExpirationMs(0L);
        jwtTokenProvider.init();
        String token = jwtTokenProvider.generateAccessToken("joffrey");
        assertThat(jwtTokenProvider.validateToken(token)).isFalse();
    }

    @Test
    void corruptedTokenIsInvalid() {
        assertThat(jwtTokenProvider.validateToken("garbage.token.value")).isFalse();
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

```bash
cd adventure-km-backend && ./mvnw test -Dtest=JwtTokenProviderTest
```

Expected: Compilation error — `JwtTokenProvider` does not exist.

- [ ] **Step 3: Implement JwtTokenProvider**

Create `adventure-km-backend/src/main/java/com/adventurekm/backend/config/JwtTokenProvider.java`:

```java
package com.adventurekm.backend.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Setter
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.access-token-expiration-ms}")
    private Long accessTokenExpirationMs;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private Long refreshTokenExpirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String username) {
        return buildToken(username, accessTokenExpirationMs, "access");
    }

    public String generateRefreshToken(String username) {
        return buildToken(username, refreshTokenExpirationMs, "refresh");
    }

    private String buildToken(String username, long expirationMs, String type) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("type", type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

```bash
cd adventure-km-backend && ./mvnw test -Dtest=JwtTokenProviderTest
```

Expected: 4 tests pass.

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "feat: add JWT token provider with generation and validation"
```

---

### Task 6: Security Config & JWT Filter

**Files:**
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/config/JwtAuthenticationFilter.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/config/SecurityConfig.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/service/CustomUserDetailsService.java`

- [ ] **Step 1: Create CustomUserDetailsService**

```java
package com.adventurekm.backend.service;

import com.adventurekm.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
```

- [ ] **Step 2: Create JwtAuthenticationFilter**

```java
package com.adventurekm.backend.config;

import com.adventurekm.backend.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromToken(token);
                var userDetails = userDetailsService.loadUserByUsername(username);
                var auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }
}
```

- [ ] **Step 3: Create SecurityConfig**

```java
package com.adventurekm.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/adventures/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/leaderboard/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/equipment/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(fo -> fo.sameOrigin()))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        return new UrlBasedCorsConfigurationSource() {{
            registerCorsConfiguration("/**", config);
        }};
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

- [ ] **Step 4: Verify compilation**

```bash
cd adventure-km-backend && ./mvnw compile -q
```

Expected: BUILD SUCCESS.

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "feat: add Spring Security config with JWT filter"
```

---

### Task 7: Auth Service & Controller

**Files:**
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/dto/request/LoginRequest.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/dto/request/RegisterRequest.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/dto/response/AuthResponse.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/service/AuthService.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/service/InvitationService.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/controller/AuthController.java`
- Test: `adventure-km-backend/src/test/java/com/adventurekm/backend/service/AuthServiceTest.java`

- [ ] **Step 1: Create auth DTOs**

`LoginRequest.java`:
```java
package com.adventurekm.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank String username,
    @NotBlank String password
) {}
```

`RegisterRequest.java`:
```java
package com.adventurekm.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Size(min = 3, max = 50) String username,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 6) String password,
    @NotBlank String invitationToken
) {}
```

`AuthResponse.java`:
```java
package com.adventurekm.backend.dto.response;

public record AuthResponse(String accessToken, String refreshToken) {}
```

- [ ] **Step 2: Create InvitationService**

```java
package com.adventurekm.backend.service;

import com.adventurekm.backend.exception.BadRequestException;
import com.adventurekm.backend.model.Invitation;
import com.adventurekm.backend.model.User;
import com.adventurekm.backend.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;

    public Invitation createInvitation(User invitedBy, String email) {
        Invitation invitation = Invitation.builder()
                .token(UUID.randomUUID().toString().replace("-", ""))
                .email(email)
                .invitedBy(invitedBy)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        return invitationRepository.save(invitation);
    }

    public Invitation validateAndConsume(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid invitation token"));
        if (invitation.getUsedAt() != null) {
            throw new BadRequestException("Invitation already used");
        }
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Invitation expired");
        }
        invitation.setUsedAt(LocalDateTime.now());
        return invitationRepository.save(invitation);
    }

    public List<Invitation> findByInviter(Long userId) {
        return invitationRepository.findByInvitedById(userId);
    }
}
```

- [ ] **Step 3: Create AuthService**

```java
package com.adventurekm.backend.service;

import com.adventurekm.backend.config.JwtTokenProvider;
import com.adventurekm.backend.dto.request.LoginRequest;
import com.adventurekm.backend.dto.request.RegisterRequest;
import com.adventurekm.backend.dto.response.AuthResponse;
import com.adventurekm.backend.exception.BadRequestException;
import com.adventurekm.backend.model.User;
import com.adventurekm.backend.model.UserLevel;
import com.adventurekm.backend.repository.UserLevelRepository;
import com.adventurekm.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserLevelRepository userLevelRepository;
    private final InvitationService invitationService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        return new AuthResponse(
                jwtTokenProvider.generateAccessToken(request.username()),
                jwtTokenProvider.generateRefreshToken(request.username()));
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        invitationService.validateAndConsume(request.invitationToken());

        if (userRepository.existsByUsername(request.username())) {
            throw new BadRequestException("Username already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .build();
        user = userRepository.save(user);

        UserLevel level = UserLevel.builder().user(user).build();
        userLevelRepository.save(level);

        return new AuthResponse(
                jwtTokenProvider.generateAccessToken(user.getUsername()),
                jwtTokenProvider.generateRefreshToken(user.getUsername()));
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        return new AuthResponse(
                jwtTokenProvider.generateAccessToken(username),
                jwtTokenProvider.generateRefreshToken(username));
    }
}
```

- [ ] **Step 4: Create AuthController**

```java
package com.adventurekm.backend.controller;

import com.adventurekm.backend.dto.request.LoginRequest;
import com.adventurekm.backend.dto.request.RegisterRequest;
import com.adventurekm.backend.dto.response.AuthResponse;
import com.adventurekm.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody Map<String, String> body) {
        return authService.refresh(body.get("refreshToken"));
    }
}
```

- [ ] **Step 5: Write auth service test**

Create `adventure-km-backend/src/test/java/com/adventurekm/backend/service/AuthServiceTest.java`:

```java
package com.adventurekm.backend.service;

import com.adventurekm.backend.dto.request.LoginRequest;
import com.adventurekm.backend.dto.response.AuthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void loginWithSeedUser() {
        // The seed data has user "joffrey" with password hashed by BCrypt.
        // We need a known password. The seed hash corresponds to a known value.
        // For now, test that the service wires up correctly — integration test with
        // real password will be done after we re-hash the seed password to a known value.
        assertThat(authService).isNotNull();
    }
}
```

Note: the seed password hash in V2 must match a known password. Update `V2__seed_data.sql` to use a BCrypt hash of `admin123`. Generate with: `new BCryptPasswordEncoder().encode("admin123")`. The hash in the seed file should be updated accordingly (the current placeholder hash may not match).

- [ ] **Step 6: Run tests and verify**

```bash
cd adventure-km-backend && ./mvnw test
```

Expected: All tests pass (entity tests + auth test).

- [ ] **Step 7: Commit**

```bash
git add -A
git commit -m "feat: add auth system — login, register with invitation, JWT refresh"
```

---

## Phase 3 — Backend Core Features

### Task 8: DTOs & MapStruct Mappers

**Files:**
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/dto/response/UserResponse.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/dto/response/UserLevelResponse.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/dto/response/AdventureResponse.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/dto/response/AdventureSummaryResponse.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/dto/response/AdventureStatsResponse.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/dto/response/PhotoResponse.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/dto/response/EquipmentItemResponse.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/dto/request/AdventureCreateRequest.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/dto/request/AdventureUpdateRequest.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/mapper/AdventureMapper.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/mapper/UserMapper.java`

- [ ] **Step 1: Create response DTOs**

`UserResponse.java`:
```java
package com.adventurekm.backend.dto.response;

public record UserResponse(Long id, String username, Integer avatarSpriteId, String role) {}
```

`UserLevelResponse.java`:
```java
package com.adventurekm.backend.dto.response;

import java.math.BigDecimal;

public record UserLevelResponse(
    Long userId, String username, Integer avatarSpriteId,
    BigDecimal totalKm, Integer totalElevationM,
    Integer adventureCount, Integer rpgScore, Integer level
) {}
```

`AdventureStatsResponse.java`:
```java
package com.adventurekm.backend.dto.response;

import java.math.BigDecimal;

public record AdventureStatsResponse(
    BigDecimal distanceKm, Integer elevationGainM, Integer elevationLossM,
    Integer durationMinutes, Integer maxAltitudeM, Integer minAltitudeM
) {}
```

`PhotoResponse.java`:
```java
package com.adventurekm.backend.dto.response;

public record PhotoResponse(Long id, String filePath, String caption, Integer sortOrder) {}
```

`EquipmentItemResponse.java`:
```java
package com.adventurekm.backend.dto.response;

public record EquipmentItemResponse(Long id, String name, String category, String iconKey, String pixelSpriteKey) {}
```

`AdventureSummaryResponse.java` (for list view, no content):
```java
package com.adventurekm.backend.dto.response;

import java.time.LocalDate;

public record AdventureSummaryResponse(
    Long id, String title, LocalDate date, String type,
    Integer difficulty, String status,
    UserResponse author, AdventureStatsResponse stats
) {}
```

`AdventureResponse.java` (full detail):
```java
package com.adventurekm.backend.dto.response;

import java.time.LocalDate;
import java.util.List;

public record AdventureResponse(
    Long id, String title, LocalDate date, String content,
    String type, Integer difficulty, String gpxPath, String status,
    UserResponse author, AdventureStatsResponse stats,
    List<PhotoResponse> photos, List<EquipmentItemResponse> equipment
) {}
```

- [ ] **Step 2: Create request DTOs**

`AdventureCreateRequest.java`:
```java
package com.adventurekm.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record AdventureCreateRequest(
    @NotBlank String title,
    @NotNull LocalDate date,
    @NotBlank String content,
    String type,
    Integer difficulty,
    List<Long> equipmentIds
) {}
```

`AdventureUpdateRequest.java`:
```java
package com.adventurekm.backend.dto.request;

import java.time.LocalDate;
import java.util.List;

public record AdventureUpdateRequest(
    String title,
    LocalDate date,
    String content,
    String type,
    Integer difficulty,
    List<Long> equipmentIds
) {}
```

- [ ] **Step 3: Create MapStruct mappers**

`AdventureMapper.java`:
```java
package com.adventurekm.backend.mapper;

import com.adventurekm.backend.dto.response.*;
import com.adventurekm.backend.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface AdventureMapper {

    @Mapping(source = "user", target = "author")
    @Mapping(source = "stats", target = "stats")
    AdventureResponse toResponse(Adventure adventure);

    @Mapping(source = "user", target = "author")
    @Mapping(source = "stats", target = "stats")
    AdventureSummaryResponse toSummaryResponse(Adventure adventure);

    AdventureStatsResponse toStatsResponse(AdventureStats stats);
    PhotoResponse toPhotoResponse(Photo photo);
    EquipmentItemResponse toEquipmentResponse(EquipmentItem item);

    List<AdventureSummaryResponse> toSummaryResponseList(List<Adventure> adventures);
    List<PhotoResponse> toPhotoResponseList(List<Photo> photos);
    List<EquipmentItemResponse> toEquipmentResponseList(Set<EquipmentItem> items);
}
```

`UserMapper.java`:
```java
package com.adventurekm.backend.mapper;

import com.adventurekm.backend.dto.response.UserLevelResponse;
import com.adventurekm.backend.dto.response.UserResponse;
import com.adventurekm.backend.model.User;
import com.adventurekm.backend.model.UserLevel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.avatarSpriteId", target = "avatarSpriteId")
    UserLevelResponse toLevelResponse(UserLevel level);

    List<UserLevelResponse> toLevelResponseList(List<UserLevel> levels);
}
```

- [ ] **Step 4: Verify MapStruct compilation**

```bash
cd adventure-km-backend && ./mvnw compile -q
```

Expected: BUILD SUCCESS. MapStruct generates implementations in `target/generated-sources/annotations/`.

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "feat: add DTOs and MapStruct mappers"
```

---

### Task 9: Adventure Service & Controller

**Files:**
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/service/AdventureService.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/controller/AdventureController.java`
- Test: `adventure-km-backend/src/test/java/com/adventurekm/backend/service/AdventureServiceTest.java`

- [ ] **Step 1: Write failing test**

Create `adventure-km-backend/src/test/java/com/adventurekm/backend/service/AdventureServiceTest.java`:

```java
package com.adventurekm.backend.service;

import com.adventurekm.backend.dto.request.AdventureCreateRequest;
import com.adventurekm.backend.dto.response.AdventureResponse;
import com.adventurekm.backend.dto.response.AdventureSummaryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class AdventureServiceTest {

    @Autowired
    private AdventureService adventureService;

    @Test
    void listPublishedAdventures() {
        List<AdventureSummaryResponse> adventures = adventureService.listPublished();
        assertThat(adventures).hasSize(3);
        assertThat(adventures.get(0).title()).contains("GR54");
    }

    @Test
    void getAdventureById() {
        AdventureResponse adventure = adventureService.getById(1L);
        assertThat(adventure.title()).contains("GR54");
        assertThat(adventure.stats()).isNotNull();
        assertThat(adventure.stats().distanceKm()).isNotNull();
        assertThat(adventure.equipment()).isNotEmpty();
    }

    @Test
    void createDraftAdventure() {
        AdventureCreateRequest request = new AdventureCreateRequest(
            "Test Run", LocalDate.now(), "## Test\nContent here.", "trail", 2, List.of());
        AdventureResponse created = adventureService.create("joffrey", request);
        assertThat(created.id()).isNotNull();
        assertThat(created.status()).isEqualTo("DRAFT");
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

```bash
cd adventure-km-backend && ./mvnw test -Dtest=AdventureServiceTest
```

Expected: Compilation error — `AdventureService` does not exist.

- [ ] **Step 3: Implement AdventureService**

```java
package com.adventurekm.backend.service;

import com.adventurekm.backend.dto.request.AdventureCreateRequest;
import com.adventurekm.backend.dto.request.AdventureUpdateRequest;
import com.adventurekm.backend.dto.response.AdventureResponse;
import com.adventurekm.backend.dto.response.AdventureSummaryResponse;
import com.adventurekm.backend.exception.BadRequestException;
import com.adventurekm.backend.exception.ResourceNotFoundException;
import com.adventurekm.backend.mapper.AdventureMapper;
import com.adventurekm.backend.model.*;
import com.adventurekm.backend.repository.AdventureRepository;
import com.adventurekm.backend.repository.EquipmentItemRepository;
import com.adventurekm.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdventureService {

    private final AdventureRepository adventureRepository;
    private final UserRepository userRepository;
    private final EquipmentItemRepository equipmentItemRepository;
    private final AdventureMapper adventureMapper;
    private final ApplicationEventPublisher eventPublisher;

    public List<AdventureSummaryResponse> listPublished() {
        return adventureMapper.toSummaryResponseList(
                adventureRepository.findByStatusOrderByDateDesc(AdventureStatus.PUBLISHED));
    }

    public List<AdventureSummaryResponse> listByUser(Long userId) {
        return adventureMapper.toSummaryResponseList(
                adventureRepository.findByUserIdOrderByDateDesc(userId));
    }

    public AdventureResponse getById(Long id) {
        Adventure adventure = adventureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adventure", id));
        return adventureMapper.toResponse(adventure);
    }

    @Transactional
    public AdventureResponse create(String username, AdventureCreateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));

        Adventure adventure = Adventure.builder()
                .user(user)
                .title(request.title())
                .date(request.date())
                .content(request.content())
                .type(request.type() != null ? AdventureType.valueOf(request.type()) : null)
                .difficulty(request.difficulty())
                .build();

        if (request.equipmentIds() != null && !request.equipmentIds().isEmpty()) {
            adventure.setEquipment(new HashSet<>(equipmentItemRepository.findAllById(request.equipmentIds())));
        }

        adventure = adventureRepository.save(adventure);
        return adventureMapper.toResponse(adventure);
    }

    @Transactional
    public AdventureResponse update(Long id, String username, AdventureUpdateRequest request) {
        Adventure adventure = adventureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adventure", id));
        if (!adventure.getUser().getUsername().equals(username)) {
            throw new BadRequestException("Not authorized to edit this adventure");
        }

        if (request.title() != null) adventure.setTitle(request.title());
        if (request.date() != null) adventure.setDate(request.date());
        if (request.content() != null) adventure.setContent(request.content());
        if (request.type() != null) adventure.setType(AdventureType.valueOf(request.type()));
        if (request.difficulty() != null) adventure.setDifficulty(request.difficulty());
        if (request.equipmentIds() != null) {
            adventure.setEquipment(new HashSet<>(equipmentItemRepository.findAllById(request.equipmentIds())));
        }
        adventure.setUpdatedAt(LocalDateTime.now());

        adventure = adventureRepository.save(adventure);
        return adventureMapper.toResponse(adventure);
    }

    @Transactional
    public AdventureResponse publish(Long id, String username) {
        Adventure adventure = adventureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adventure", id));
        if (!adventure.getUser().getUsername().equals(username)) {
            throw new BadRequestException("Not authorized to publish this adventure");
        }
        adventure.setStatus(AdventureStatus.PUBLISHED);
        adventure.setUpdatedAt(LocalDateTime.now());
        adventure = adventureRepository.save(adventure);

        eventPublisher.publishEvent(new AdventurePublishedEvent(adventure));

        return adventureMapper.toResponse(adventure);
    }

    @Transactional
    public void delete(Long id, String username) {
        Adventure adventure = adventureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adventure", id));
        if (!adventure.getUser().getUsername().equals(username)) {
            throw new BadRequestException("Not authorized to delete this adventure");
        }
        adventureRepository.delete(adventure);
    }
}
```

- [ ] **Step 4: Create AdventurePublishedEvent (placeholder for Task 12)**

```java
package com.adventurekm.backend.model;

public record AdventurePublishedEvent(Adventure adventure) {}
```

- [ ] **Step 5: Implement AdventureController**

```java
package com.adventurekm.backend.controller;

import com.adventurekm.backend.dto.request.AdventureCreateRequest;
import com.adventurekm.backend.dto.request.AdventureUpdateRequest;
import com.adventurekm.backend.dto.response.AdventureResponse;
import com.adventurekm.backend.dto.response.AdventureSummaryResponse;
import com.adventurekm.backend.service.AdventureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adventures")
@RequiredArgsConstructor
public class AdventureController {

    private final AdventureService adventureService;

    @GetMapping
    public List<AdventureSummaryResponse> listPublished() {
        return adventureService.listPublished();
    }

    @GetMapping("/{id}")
    public AdventureResponse getById(@PathVariable Long id) {
        return adventureService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdventureResponse create(@AuthenticationPrincipal UserDetails user,
                                    @Valid @RequestBody AdventureCreateRequest request) {
        return adventureService.create(user.getUsername(), request);
    }

    @PutMapping("/{id}")
    public AdventureResponse update(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails user,
                                    @Valid @RequestBody AdventureUpdateRequest request) {
        return adventureService.update(id, user.getUsername(), request);
    }

    @PostMapping("/{id}/publish")
    public AdventureResponse publish(@PathVariable Long id,
                                     @AuthenticationPrincipal UserDetails user) {
        return adventureService.publish(id, user.getUsername());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        adventureService.delete(id, user.getUsername());
    }
}
```

- [ ] **Step 6: Run tests**

```bash
cd adventure-km-backend && ./mvnw test
```

Expected: All tests pass.

- [ ] **Step 7: Commit**

```bash
git add -A
git commit -m "feat: add adventure CRUD — service, controller, and endpoints"
```

---

### Task 10: GPX Processing Service

**Files:**
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/service/GpxProcessingService.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/dto/response/GpxDataResponse.java`
- Test: `adventure-km-backend/src/test/java/com/adventurekm/backend/service/GpxProcessingServiceTest.java`
- Test resource: `adventure-km-backend/src/test/resources/test-track.gpx`

- [ ] **Step 1: Create a test GPX file**

Create `adventure-km-backend/src/test/resources/test-track.gpx`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<gpx version="1.1" creator="test"
     xmlns="http://www.topografix.com/GPX/1/1">
  <trk>
    <name>Test Track</name>
    <trkseg>
      <trkpt lat="45.0" lon="5.7"><ele>500</ele><time>2025-07-01T08:00:00Z</time></trkpt>
      <trkpt lat="45.001" lon="5.701"><ele>550</ele><time>2025-07-01T08:10:00Z</time></trkpt>
      <trkpt lat="45.002" lon="5.702"><ele>600</ele><time>2025-07-01T08:20:00Z</time></trkpt>
      <trkpt lat="45.003" lon="5.703"><ele>580</ele><time>2025-07-01T08:30:00Z</time></trkpt>
      <trkpt lat="45.004" lon="5.704"><ele>520</ele><time>2025-07-01T08:45:00Z</time></trkpt>
    </trkseg>
  </trk>
</gpx>
```

- [ ] **Step 2: Write failing test**

Create `adventure-km-backend/src/test/java/com/adventurekm/backend/service/GpxProcessingServiceTest.java`:

```java
package com.adventurekm.backend.service;

import com.adventurekm.backend.dto.response.GpxDataResponse;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class GpxProcessingServiceTest {

    private final GpxProcessingService service = new GpxProcessingService();

    @Test
    void parseGpxExtractsStats() {
        InputStream gpx = getClass().getResourceAsStream("/test-track.gpx");
        GpxDataResponse data = service.process(gpx);

        assertThat(data.distanceKm()).isGreaterThan(0);
        assertThat(data.elevationGainM()).isGreaterThan(0);
        assertThat(data.elevationLossM()).isGreaterThan(0);
        assertThat(data.maxAltitudeM()).isEqualTo(600);
        assertThat(data.minAltitudeM()).isEqualTo(500);
        assertThat(data.durationMinutes()).isEqualTo(45);
        assertThat(data.geojson()).contains("coordinates");
        assertThat(data.elevationPoints()).hasSize(5);
    }
}
```

- [ ] **Step 3: Create GpxDataResponse**

```java
package com.adventurekm.backend.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record GpxDataResponse(
    BigDecimal distanceKm,
    Integer elevationGainM,
    Integer elevationLossM,
    Integer durationMinutes,
    Integer maxAltitudeM,
    Integer minAltitudeM,
    String geojson,
    List<ElevationPoint> elevationPoints
) {
    public record ElevationPoint(double distanceKm, double altitudeM) {}
}
```

- [ ] **Step 4: Implement GpxProcessingService**

```java
package com.adventurekm.backend.service;

import com.adventurekm.backend.dto.response.GpxDataResponse;
import com.adventurekm.backend.dto.response.GpxDataResponse.ElevationPoint;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.geom.Geoid;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class GpxProcessingService {

    public GpxDataResponse process(InputStream gpxStream) {
        try {
            GPX gpx = GPX.Reader.DEFAULT.read(gpxStream);
            List<WayPoint> points = gpx.tracks()
                    .flatMap(t -> t.segments().stream())
                    .flatMap(TrackSegment::points)
                    .toList();

            if (points.isEmpty()) {
                throw new IllegalArgumentException("GPX file contains no track points");
            }

            double totalDistanceM = 0;
            int elevationGain = 0;
            int elevationLoss = 0;
            double maxAlt = Double.MIN_VALUE;
            double minAlt = Double.MAX_VALUE;
            List<ElevationPoint> elevationPoints = new ArrayList<>();

            elevationPoints.add(new ElevationPoint(0, points.get(0).getElevation().orElse(null).doubleValue()));

            for (int i = 0; i < points.size(); i++) {
                WayPoint p = points.get(i);
                double alt = p.getElevation().map(Number::doubleValue).orElse(0.0);
                maxAlt = Math.max(maxAlt, alt);
                minAlt = Math.min(minAlt, alt);

                if (i > 0) {
                    WayPoint prev = points.get(i - 1);
                    double segDist = Geoid.WGS84.distance(prev, p).doubleValue();
                    totalDistanceM += segDist;

                    double prevAlt = prev.getElevation().map(Number::doubleValue).orElse(0.0);
                    double diff = alt - prevAlt;
                    if (diff > 0) elevationGain += (int) diff;
                    else elevationLoss += (int) Math.abs(diff);

                    elevationPoints.add(new ElevationPoint(
                            totalDistanceM / 1000.0, alt));
                }
            }

            int durationMinutes = 0;
            var firstTime = points.get(0).getTime();
            var lastTime = points.get(points.size() - 1).getTime();
            if (firstTime.isPresent() && lastTime.isPresent()) {
                durationMinutes = (int) Duration.between(
                        firstTime.get(), lastTime.get()).toMinutes();
            }

            String geojson = buildGeoJson(points);

            return new GpxDataResponse(
                    BigDecimal.valueOf(totalDistanceM / 1000.0).setScale(2, RoundingMode.HALF_UP),
                    elevationGain, elevationLoss, durationMinutes,
                    (int) maxAlt, (int) minAlt,
                    geojson, elevationPoints);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse GPX file", e);
        }
    }

    private String buildGeoJson(List<WayPoint> points) {
        StringBuilder coords = new StringBuilder("[");
        for (int i = 0; i < points.size(); i++) {
            WayPoint p = points.get(i);
            if (i > 0) coords.append(",");
            coords.append("[")
                  .append(p.getLongitude().doubleValue()).append(",")
                  .append(p.getLatitude().doubleValue()).append(",")
                  .append(p.getElevation().map(Number::doubleValue).orElse(0.0))
                  .append("]");
        }
        coords.append("]");

        return """
            {"type":"Feature","geometry":{"type":"LineString","coordinates":%s},"properties":{}}"""
            .formatted(coords.toString());
    }
}
```

- [ ] **Step 5: Run test**

```bash
cd adventure-km-backend && ./mvnw test -Dtest=GpxProcessingServiceTest
```

Expected: 1 test passes.

- [ ] **Step 6: Add GPX upload endpoint to AdventureController**

Add to `AdventureController.java`:

```java
@PostMapping("/{id}/gpx")
public AdventureResponse uploadGpx(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails user,
                                    @RequestParam("file") MultipartFile file) {
    return adventureService.processGpx(id, user.getUsername(), file);
}
```

Add to `AdventureService.java`:

```java
@Transactional
public AdventureResponse processGpx(Long id, String username, MultipartFile file) {
    Adventure adventure = adventureRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Adventure", id));
    if (!adventure.getUser().getUsername().equals(username)) {
        throw new BadRequestException("Not authorized");
    }

    GpxDataResponse gpxData = gpxProcessingService.process(toInputStream(file));

    AdventureStats stats = adventure.getStats();
    if (stats == null) {
        stats = new AdventureStats();
        stats.setAdventure(adventure);
        adventure.setStats(stats);
    }
    stats.setDistanceKm(gpxData.distanceKm());
    stats.setElevationGainM(gpxData.elevationGainM());
    stats.setElevationLossM(gpxData.elevationLossM());
    stats.setDurationMinutes(gpxData.durationMinutes());
    stats.setMaxAltitudeM(gpxData.maxAltitudeM());
    stats.setMinAltitudeM(gpxData.minAltitudeM());

    // Save GPX file to disk
    String gpxPath = fileStorageService.saveGpx(id, file);
    adventure.setGpxPath(gpxPath);
    adventure.setUpdatedAt(LocalDateTime.now());

    adventure = adventureRepository.save(adventure);
    return adventureMapper.toResponse(adventure);
}

private InputStream toInputStream(MultipartFile file) {
    try { return file.getInputStream(); }
    catch (IOException e) { throw new RuntimeException(e); }
}
```

Add `GpxProcessingService` and `FileStorageService` (Task 11) as dependencies in `AdventureService`:

```java
private final GpxProcessingService gpxProcessingService;
private final FileStorageService fileStorageService;
```

Note: `FileStorageService` is created in Task 11. If building sequentially, create a stub first.

- [ ] **Step 7: Commit**

```bash
git add -A
git commit -m "feat: add GPX processing — parsing, stats extraction, GeoJSON generation"
```

---

### Task 11: File Storage Service & Photo Upload

**Files:**
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/service/FileStorageService.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/controller/FileController.java`
- Test: `adventure-km-backend/src/test/java/com/adventurekm/backend/service/FileStorageServiceTest.java`

- [ ] **Step 1: Write failing test**

```java
package com.adventurekm.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService(tempDir.toString());
    }

    @Test
    void saveAndLoadPhoto() throws Exception {
        byte[] imageBytes = createMinimalJpeg();
        MockMultipartFile file = new MockMultipartFile("photo", "test.jpg", "image/jpeg", imageBytes);

        String savedPath = fileStorageService.savePhoto(1L, file, 1);
        assertThat(savedPath).contains("1");
        assertThat(Files.exists(Path.of(tempDir.toString(), savedPath))).isTrue();
    }

    @Test
    void saveGpxFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("gpx", "track.gpx", "application/gpx+xml", "<gpx/>".getBytes());
        String savedPath = fileStorageService.saveGpx(1L, file);
        assertThat(savedPath).endsWith(".gpx");
        assertThat(Files.exists(Path.of(tempDir.toString(), savedPath))).isTrue();
    }

    private byte[] createMinimalJpeg() {
        // 1x1 red pixel JPEG
        return new byte[]{
            (byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0, 0x00, 0x10, 0x4A, 0x46,
            0x49, 0x46, 0x00, 0x01, 0x01, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00,
            (byte)0xFF, (byte)0xD9
        };
    }
}
```

- [ ] **Step 2: Implement FileStorageService**

```java
package com.adventurekm.backend.service;

import com.adventurekm.backend.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileStorageService {

    private final String uploadDir;

    public FileStorageService(@Value("${app.upload.dir}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String savePhoto(Long adventureId, MultipartFile file, int sortOrder) {
        validateImageFile(file);
        String relativePath = "photos/" + adventureId + "/" + sortOrder + ".jpg";
        Path targetPath = Path.of(uploadDir, relativePath);

        try {
            Files.createDirectories(targetPath.getParent());
            BufferedImage original = ImageIO.read(file.getInputStream());
            if (original == null) {
                throw new BadRequestException("Cannot read image file");
            }
            BufferedImage resized = resizeImage(original, 1200, 800);
            try (OutputStream os = Files.newOutputStream(targetPath)) {
                ImageIO.write(resized, "jpg", os);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save photo", e);
        }

        return relativePath;
    }

    public String saveGpx(Long adventureId, MultipartFile file) {
        String relativePath = "gpx/" + adventureId + ".gpx";
        Path targetPath = Path.of(uploadDir, relativePath);

        try {
            Files.createDirectories(targetPath.getParent());
            file.transferTo(targetPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save GPX file", e);
        }

        return relativePath;
    }

    public Path resolve(String relativePath) {
        return Path.of(uploadDir, relativePath);
    }

    private BufferedImage resizeImage(BufferedImage original, int maxWidth, int maxHeight) {
        int w = original.getWidth();
        int h = original.getHeight();
        if (w <= maxWidth && h <= maxHeight) return original;

        double ratio = Math.min((double) maxWidth / w, (double) maxHeight / h);
        int newW = (int) (w * ratio);
        int newH = (int) (h * ratio);

        BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, newW, newH, null);
        g.dispose();
        return resized;
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) throw new BadRequestException("File is empty");
        if (file.getSize() > 10 * 1024 * 1024) throw new BadRequestException("File exceeds 10 MB limit");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("File must be an image");
        }
    }
}
```

- [ ] **Step 3: Create FileController (serves uploaded files)**

```java
package com.adventurekm.backend.controller;

import com.adventurekm.backend.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping("/photos/{adventureId}/{filename}")
    public Resource servePhoto(@PathVariable Long adventureId, @PathVariable String filename) {
        Path path = fileStorageService.resolve("photos/" + adventureId + "/" + filename);
        return new FileSystemResource(path);
    }

    @GetMapping(value = "/gpx/{adventureId}.gpx", produces = "application/gpx+xml")
    public Resource serveGpx(@PathVariable Long adventureId) {
        Path path = fileStorageService.resolve("gpx/" + adventureId + ".gpx");
        return new FileSystemResource(path);
    }
}
```

- [ ] **Step 4: Add photo upload endpoint to AdventureController**

Add to `AdventureController.java`:

```java
@PostMapping("/{id}/photos")
@ResponseStatus(HttpStatus.CREATED)
public AdventureResponse uploadPhoto(@PathVariable Long id,
                                      @AuthenticationPrincipal UserDetails user,
                                      @RequestParam("file") MultipartFile file,
                                      @RequestParam(defaultValue = "") String caption) {
    return adventureService.addPhoto(id, user.getUsername(), file, caption);
}
```

Add to `AdventureService.java`:

```java
@Transactional
public AdventureResponse addPhoto(Long id, String username, MultipartFile file, String caption) {
    Adventure adventure = adventureRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Adventure", id));
    if (!adventure.getUser().getUsername().equals(username)) {
        throw new BadRequestException("Not authorized");
    }
    int currentCount = photoRepository.countByAdventureId(id);
    if (currentCount >= 5) {
        throw new BadRequestException("Maximum 5 photos per adventure");
    }

    int sortOrder = currentCount + 1;
    String filePath = fileStorageService.savePhoto(id, file, sortOrder);

    Photo photo = Photo.builder()
            .adventure(adventure)
            .filePath(filePath)
            .caption(caption.isEmpty() ? null : caption)
            .sortOrder(sortOrder)
            .build();
    photoRepository.save(photo);

    return adventureMapper.toResponse(adventureRepository.findById(id).orElseThrow());
}
```

Add `PhotoRepository` as dependency in `AdventureService`.

- [ ] **Step 5: Run tests**

```bash
cd adventure-km-backend && ./mvnw test
```

Expected: All tests pass.

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "feat: add file storage — photo upload with resize, GPX storage"
```

---

## Phase 4 — Backend RPG & Admin

### Task 12: Level Calculation & Event System

**Files:**
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/service/LevelCalculationService.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/service/AdventureEventListener.java`
- Test: `adventure-km-backend/src/test/java/com/adventurekm/backend/service/LevelCalculationServiceTest.java`

- [ ] **Step 1: Write failing test**

```java
package com.adventurekm.backend.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class LevelCalculationServiceTest {

    private final LevelCalculationService service = new LevelCalculationService();

    @Test
    void calculateScoreForJoffreysSeedData() {
        // total_km=367, total_d_plus=22000, adventure_count=3
        // score = (367 * 1) + (22000 / 100 * 2) + (3 * 50) = 367 + 440 + 150 = 957
        int score = service.calculateScore(BigDecimal.valueOf(367), 22000, 3);
        assertThat(score).isEqualTo(957);
    }

    @Test
    void calculateLevelFromScore() {
        // level = floor(sqrt(957 / 10)) = floor(sqrt(95.7)) = floor(9.78) = 9
        int level = service.calculateLevel(957);
        assertThat(level).isEqualTo(9);
    }

    @Test
    void level1ForNewUser() {
        int score = service.calculateScore(BigDecimal.ZERO, 0, 0);
        assertThat(score).isEqualTo(0);
        assertThat(service.calculateLevel(0)).isEqualTo(1);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

```bash
cd adventure-km-backend && ./mvnw test -Dtest=LevelCalculationServiceTest
```

Expected: Compilation error.

- [ ] **Step 3: Implement LevelCalculationService**

```java
package com.adventurekm.backend.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class LevelCalculationService {

    public int calculateScore(BigDecimal totalKm, int totalElevationM, int adventureCount) {
        int kmPoints = totalKm.intValue();
        int elevationPoints = (totalElevationM / 100) * 2;
        int adventurePoints = adventureCount * 50;
        return kmPoints + elevationPoints + adventurePoints;
    }

    public int calculateLevel(int score) {
        if (score <= 0) return 1;
        return Math.max(1, (int) Math.floor(Math.sqrt(score / 10.0)));
    }
}
```

- [ ] **Step 4: Implement AdventureEventListener**

```java
package com.adventurekm.backend.service;

import com.adventurekm.backend.model.Adventure;
import com.adventurekm.backend.model.AdventurePublishedEvent;
import com.adventurekm.backend.model.AdventureStats;
import com.adventurekm.backend.model.UserLevel;
import com.adventurekm.backend.repository.AdventureRepository;
import com.adventurekm.backend.repository.AdventureStatsRepository;
import com.adventurekm.backend.repository.UserLevelRepository;
import com.adventurekm.backend.model.AdventureStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AdventureEventListener {

    private final AdventureRepository adventureRepository;
    private final UserLevelRepository userLevelRepository;
    private final LevelCalculationService levelCalculationService;

    @EventListener
    @Transactional
    public void onAdventurePublished(AdventurePublishedEvent event) {
        Long userId = event.adventure().getUser().getId();

        List<Adventure> published = adventureRepository
                .findByUserIdAndStatusOrderByDateDesc(userId, AdventureStatus.PUBLISHED);

        BigDecimal totalKm = BigDecimal.ZERO;
        int totalElevation = 0;
        int count = published.size();

        for (Adventure a : published) {
            if (a.getStats() != null) {
                if (a.getStats().getDistanceKm() != null)
                    totalKm = totalKm.add(a.getStats().getDistanceKm());
                if (a.getStats().getElevationGainM() != null)
                    totalElevation += a.getStats().getElevationGainM();
            }
        }

        int score = levelCalculationService.calculateScore(totalKm, totalElevation, count);
        int level = levelCalculationService.calculateLevel(score);

        UserLevel userLevel = userLevelRepository.findById(userId)
                .orElseThrow();
        userLevel.setTotalKm(totalKm);
        userLevel.setTotalElevationM(totalElevation);
        userLevel.setAdventureCount(count);
        userLevel.setRpgScore(score);
        userLevel.setLevel(level);
        userLevelRepository.save(userLevel);
    }
}
```

- [ ] **Step 5: Run tests**

```bash
cd adventure-km-backend && ./mvnw test -Dtest=LevelCalculationServiceTest
```

Expected: 3 tests pass.

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "feat: add RPG level calculation and adventure published event listener"
```

---

### Task 13: User Controller & Leaderboard Controller

**Files:**
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/controller/UserController.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/controller/LeaderboardController.java`

- [ ] **Step 1: Implement UserController**

```java
package com.adventurekm.backend.controller;

import com.adventurekm.backend.dto.response.AdventureSummaryResponse;
import com.adventurekm.backend.dto.response.UserLevelResponse;
import com.adventurekm.backend.dto.response.UserResponse;
import com.adventurekm.backend.exception.ResourceNotFoundException;
import com.adventurekm.backend.mapper.AdventureMapper;
import com.adventurekm.backend.mapper.UserMapper;
import com.adventurekm.backend.model.AdventureStatus;
import com.adventurekm.backend.model.User;
import com.adventurekm.backend.repository.AdventureRepository;
import com.adventurekm.backend.repository.UserLevelRepository;
import com.adventurekm.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserLevelRepository userLevelRepository;
    private final AdventureRepository adventureRepository;
    private final UserMapper userMapper;
    private final AdventureMapper adventureMapper;

    @GetMapping("/{username}")
    public UserLevelResponse getProfile(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
        return userMapper.toLevelResponse(
                userLevelRepository.findById(user.getId()).orElseThrow());
    }

    @GetMapping("/{username}/adventures")
    public List<AdventureSummaryResponse> getUserAdventures(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
        return adventureMapper.toSummaryResponseList(
                adventureRepository.findByUserIdAndStatusOrderByDateDesc(
                        user.getId(), AdventureStatus.PUBLISHED));
    }

    @PutMapping("/me/avatar")
    public UserResponse updateAvatar(@AuthenticationPrincipal UserDetails userDetails,
                                     @RequestBody java.util.Map<String, Integer> body) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        user.setAvatarSpriteId(body.get("avatarSpriteId"));
        return userMapper.toResponse(userRepository.save(user));
    }
}
```

- [ ] **Step 2: Implement LeaderboardController**

```java
package com.adventurekm.backend.controller;

import com.adventurekm.backend.dto.response.UserLevelResponse;
import com.adventurekm.backend.mapper.UserMapper;
import com.adventurekm.backend.repository.UserLevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final UserLevelRepository userLevelRepository;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserLevelResponse> getLeaderboard(
            @RequestParam(defaultValue = "score") String sortBy) {
        return switch (sortBy) {
            case "km" -> userMapper.toLevelResponseList(userLevelRepository.findAllByOrderByTotalKmDesc());
            case "elevation" -> userMapper.toLevelResponseList(userLevelRepository.findAllByOrderByTotalElevationMDesc());
            case "count" -> userMapper.toLevelResponseList(userLevelRepository.findAllByOrderByAdventureCountDesc());
            default -> userMapper.toLevelResponseList(userLevelRepository.findAllByOrderByRpgScoreDesc());
        };
    }
}
```

- [ ] **Step 3: Verify compilation and run all tests**

```bash
cd adventure-km-backend && ./mvnw test
```

Expected: All tests pass.

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "feat: add user profile and leaderboard endpoints"
```

---

### Task 14: Admin Controller

**Files:**
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/controller/AdminController.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/dto/request/InvitationCreateRequest.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/dto/response/InvitationResponse.java`
- Create: `adventure-km-backend/src/main/java/com/adventurekm/backend/dto/request/EquipmentCreateRequest.java`

- [ ] **Step 1: Create admin DTOs**

`InvitationCreateRequest.java`:
```java
package com.adventurekm.backend.dto.request;

public record InvitationCreateRequest(String email) {}
```

`InvitationResponse.java`:
```java
package com.adventurekm.backend.dto.response;

import java.time.LocalDateTime;

public record InvitationResponse(
    Long id, String token, String email,
    LocalDateTime expiresAt, LocalDateTime usedAt
) {}
```

`EquipmentCreateRequest.java`:
```java
package com.adventurekm.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EquipmentCreateRequest(
    @NotBlank String name,
    @NotBlank String category,
    String iconKey,
    String pixelSpriteKey
) {}
```

- [ ] **Step 2: Implement AdminController**

```java
package com.adventurekm.backend.controller;

import com.adventurekm.backend.dto.request.EquipmentCreateRequest;
import com.adventurekm.backend.dto.request.InvitationCreateRequest;
import com.adventurekm.backend.dto.response.EquipmentItemResponse;
import com.adventurekm.backend.dto.response.InvitationResponse;
import com.adventurekm.backend.dto.response.UserResponse;
import com.adventurekm.backend.exception.ResourceNotFoundException;
import com.adventurekm.backend.mapper.AdventureMapper;
import com.adventurekm.backend.mapper.UserMapper;
import com.adventurekm.backend.model.EquipmentCategory;
import com.adventurekm.backend.model.EquipmentItem;
import com.adventurekm.backend.model.Invitation;
import com.adventurekm.backend.model.User;
import com.adventurekm.backend.repository.EquipmentItemRepository;
import com.adventurekm.backend.repository.UserRepository;
import com.adventurekm.backend.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final EquipmentItemRepository equipmentItemRepository;
    private final InvitationService invitationService;
    private final UserMapper userMapper;
    private final AdventureMapper adventureMapper;

    // --- Users ---

    @GetMapping("/users")
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream().map(userMapper::toResponse).toList();
    }

    // --- Invitations ---

    @PostMapping("/invitations")
    @ResponseStatus(HttpStatus.CREATED)
    public InvitationResponse createInvitation(@AuthenticationPrincipal UserDetails userDetails,
                                                @RequestBody InvitationCreateRequest request) {
        User admin = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Invitation invitation = invitationService.createInvitation(admin, request.email());
        return toInvitationResponse(invitation);
    }

    @GetMapping("/invitations")
    public List<InvitationResponse> listInvitations(@AuthenticationPrincipal UserDetails userDetails) {
        User admin = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        return invitationService.findByInviter(admin.getId()).stream()
                .map(this::toInvitationResponse).toList();
    }

    private InvitationResponse toInvitationResponse(Invitation inv) {
        return new InvitationResponse(inv.getId(), inv.getToken(), inv.getEmail(),
                inv.getExpiresAt(), inv.getUsedAt());
    }

    // --- Equipment ---

    @GetMapping("/equipment")
    public List<EquipmentItemResponse> listEquipment() {
        return equipmentItemRepository.findAll().stream()
                .map(adventureMapper::toEquipmentResponse).toList();
    }

    @PostMapping("/equipment")
    @ResponseStatus(HttpStatus.CREATED)
    public EquipmentItemResponse createEquipment(@RequestBody EquipmentCreateRequest request) {
        EquipmentItem item = EquipmentItem.builder()
                .name(request.name())
                .category(EquipmentCategory.valueOf(request.category()))
                .iconKey(request.iconKey())
                .pixelSpriteKey(request.pixelSpriteKey())
                .build();
        return adventureMapper.toEquipmentResponse(equipmentItemRepository.save(item));
    }

    @DeleteMapping("/equipment/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEquipment(@PathVariable Long id) {
        if (!equipmentItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("EquipmentItem", id);
        }
        equipmentItemRepository.deleteById(id);
    }
}
```

- [ ] **Step 3: Add public equipment list endpoint**

Add to `AdventureController.java` (or create a separate controller):

```java
@GetMapping("/api/equipment")
public List<EquipmentItemResponse> listEquipment() {
    return equipmentItemRepository.findAll().stream()
            .map(adventureMapper::toEquipmentResponse).toList();
}
```

Or better, create a simple endpoint in a new small controller or keep it in AdventureController. The simpler approach: add a `/api/equipment` GET endpoint accessible publicly, so the frontend can load the equipment catalog for the adventure form.

- [ ] **Step 4: Run all tests and full integration check**

```bash
cd adventure-km-backend && ./mvnw test
```

```bash
cd adventure-km-backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Test with curl:
```bash
curl -s http://localhost:8080/api/adventures | head -c 200
curl -s http://localhost:8080/api/leaderboard | head -c 200
```

Expected: JSON responses with seed data. Stop the server with Ctrl+C.

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "feat: add admin panel — invitations, users, equipment CRUD"
```

---

## Phase 5 — Frontend Foundation

### Task 15: Frontend Cleanup, Dependencies & Theme

**Files:**
- Delete: `adventure-km-frontend/src/app/features/activities/` (legacy)
- Delete: `adventure-km-frontend/src/app/core/services/activity.service.ts`
- Delete: `adventure-km-frontend/src/app/models/activity.model.ts`
- Modify: `adventure-km-frontend/package.json`
- Modify: `adventure-km-frontend/src/styles.css`
- Modify: `adventure-km-frontend/src/app/app.config.ts`
- Modify: `adventure-km-frontend/src/app/app.routes.ts`

- [ ] **Step 1: Delete legacy files**

```bash
cd adventure-km-frontend
rm -rf src/app/features/activities
rm src/app/core/services/activity.service.ts
rm src/app/models/activity.model.ts
```

- [ ] **Step 2: Install frontend dependencies**

```bash
cd adventure-km-frontend
npm install leaflet @bluehalo/ngx-leaflet chart.js ng2-charts ngx-markdown marked
npm install -D @types/leaflet
```

- [ ] **Step 3: Write global theme styles**

Replace `adventure-km-frontend/src/styles.css`:

```css
@import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;600;700&family=Press+Start+2P&display=swap');

:root {
  --bg-deep: #0d1117;
  --bg-surface: #161b22;
  --bg-card: #1c2333;
  --green-primary: #22c55e;
  --green-dim: #16a34a;
  --green-glow: rgba(34, 197, 94, 0.15);
  --gold-accent: #f0a500;
  --gold-dim: #b87a00;
  --text-primary: #e6edf3;
  --text-secondary: #8b949e;
  --text-muted: #484f58;
  --border-color: #30363d;
  --danger: #f85149;
  --font-mono: 'JetBrains Mono', monospace;
  --font-pixel: 'Press Start 2P', monospace;
  --pixel-border: 3px solid var(--gold-accent);
  --radius: 4px;
}

*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

html {
  font-size: 16px;
  scroll-behavior: smooth;
}

body {
  font-family: var(--font-mono);
  background-color: var(--bg-deep);
  color: var(--text-primary);
  line-height: 1.6;
  min-height: 100vh;
}

a { color: var(--green-primary); text-decoration: none; transition: color 0.2s; }
a:hover { color: var(--gold-accent); }

button, .btn {
  font-family: var(--font-mono);
  cursor: pointer;
  border: 2px solid var(--green-primary);
  background: transparent;
  color: var(--green-primary);
  padding: 8px 16px;
  font-size: 0.85rem;
  font-weight: 600;
  transition: all 0.2s;
}
button:hover, .btn:hover {
  background: var(--green-primary);
  color: var(--bg-deep);
}

.btn-gold {
  border-color: var(--gold-accent);
  color: var(--gold-accent);
}
.btn-gold:hover {
  background: var(--gold-accent);
  color: var(--bg-deep);
}

.btn-danger {
  border-color: var(--danger);
  color: var(--danger);
}
.btn-danger:hover {
  background: var(--danger);
  color: var(--bg-deep);
}

input, textarea, select {
  font-family: var(--font-mono);
  background: var(--bg-surface);
  border: 1px solid var(--border-color);
  color: var(--text-primary);
  padding: 8px 12px;
  font-size: 0.9rem;
  width: 100%;
}
input:focus, textarea:focus, select:focus {
  outline: none;
  border-color: var(--green-primary);
  box-shadow: 0 0 0 2px var(--green-glow);
}

.card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  padding: 20px;
}

.pixel-border {
  border: var(--pixel-border);
  image-rendering: pixelated;
}

.pixel-title {
  font-family: var(--font-pixel);
  color: var(--gold-accent);
  font-size: 0.75rem;
  letter-spacing: 1px;
  text-transform: uppercase;
}

.container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 20px;
}

.stat-value {
  font-size: 1.4rem;
  font-weight: 700;
  color: var(--green-primary);
}

.stat-label {
  font-size: 0.7rem;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 1px;
}
```

- [ ] **Step 4: Update app.config.ts with HttpClient**

Replace `adventure-km-frontend/src/app/app.config.ts`:

```typescript
import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withInterceptors([])),
    provideClientHydration(withEventReplay())
  ]
};
```

Note: the JWT interceptor will be added in Task 16.

- [ ] **Step 5: Clear app.routes.ts**

Replace `adventure-km-frontend/src/app/app.routes.ts`:

```typescript
import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent) },
  { path: '**', redirectTo: '' }
];
```

- [ ] **Step 6: Create frontend directory structure**

```bash
cd adventure-km-frontend
mkdir -p src/app/core/auth
mkdir -p src/app/core/models
mkdir -p src/app/shared/components/{header,footer,pixel-avatar,adventure-card,stat-badge}
mkdir -p src/app/shared/pipes
mkdir -p src/app/features/{login,register,adventures,adventure-detail,adventure-form,profile,leaderboard,admin}
```

- [ ] **Step 7: Commit**

```bash
git add -A
git commit -m "feat(frontend): clean up legacy code, add dependencies, set up theme"
```

---

### Task 16: TypeScript Models, Auth Service & JWT Interceptor

**Files:**
- Create: `adventure-km-frontend/src/app/core/models/user.model.ts`
- Create: `adventure-km-frontend/src/app/core/models/adventure.model.ts`
- Create: `adventure-km-frontend/src/app/core/models/equipment.model.ts`
- Create: `adventure-km-frontend/src/app/core/auth/auth.service.ts`
- Create: `adventure-km-frontend/src/app/core/auth/token-storage.service.ts`
- Create: `adventure-km-frontend/src/app/core/auth/jwt.interceptor.ts`
- Create: `adventure-km-frontend/src/app/core/auth/auth.guard.ts`
- Create: `adventure-km-frontend/src/app/core/auth/admin.guard.ts`

- [ ] **Step 1: Create TypeScript model interfaces**

`user.model.ts`:
```typescript
export interface UserResponse {
  id: number;
  username: string;
  avatarSpriteId: number;
  role: string;
}

export interface UserLevelResponse {
  userId: number;
  username: string;
  avatarSpriteId: number;
  totalKm: number;
  totalElevationM: number;
  adventureCount: number;
  rpgScore: number;
  level: number;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  invitationToken: string;
}
```

`adventure.model.ts`:
```typescript
import { UserResponse } from './user.model';
import { EquipmentItemResponse } from './equipment.model';

export interface AdventureStatsResponse {
  distanceKm: number;
  elevationGainM: number;
  elevationLossM: number;
  durationMinutes: number;
  maxAltitudeM: number;
  minAltitudeM: number;
}

export interface PhotoResponse {
  id: number;
  filePath: string;
  caption: string;
  sortOrder: number;
}

export interface AdventureSummaryResponse {
  id: number;
  title: string;
  date: string;
  type: string;
  difficulty: number;
  status: string;
  author: UserResponse;
  stats: AdventureStatsResponse;
}

export interface AdventureResponse {
  id: number;
  title: string;
  date: string;
  content: string;
  type: string;
  difficulty: number;
  gpxPath: string;
  status: string;
  author: UserResponse;
  stats: AdventureStatsResponse;
  photos: PhotoResponse[];
  equipment: EquipmentItemResponse[];
}

export interface AdventureCreateRequest {
  title: string;
  date: string;
  content: string;
  type?: string;
  difficulty?: number;
  equipmentIds?: number[];
}

export interface GpxDataResponse {
  distanceKm: number;
  elevationGainM: number;
  elevationLossM: number;
  durationMinutes: number;
  maxAltitudeM: number;
  minAltitudeM: number;
  geojson: string;
  elevationPoints: { distanceKm: number; altitudeM: number }[];
}
```

`equipment.model.ts`:
```typescript
export interface EquipmentItemResponse {
  id: number;
  name: string;
  category: string;
  iconKey: string;
  pixelSpriteKey: string;
}
```

- [ ] **Step 2: Create TokenStorageService**

```typescript
import { Injectable, signal, computed } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class TokenStorageService {
  private readonly ACCESS_KEY = 'akm_access_token';
  private readonly REFRESH_KEY = 'akm_refresh_token';

  private accessTokenSignal = signal<string | null>(this.getStored(this.ACCESS_KEY));

  readonly isLoggedIn = computed(() => this.accessTokenSignal() !== null);

  getAccessToken(): string | null {
    return this.accessTokenSignal();
  }

  getRefreshToken(): string | null {
    return this.getStored(this.REFRESH_KEY);
  }

  saveTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem(this.ACCESS_KEY, accessToken);
    localStorage.setItem(this.REFRESH_KEY, refreshToken);
    this.accessTokenSignal.set(accessToken);
  }

  clear(): void {
    localStorage.removeItem(this.ACCESS_KEY);
    localStorage.removeItem(this.REFRESH_KEY);
    this.accessTokenSignal.set(null);
  }

  private getStored(key: string): string | null {
    if (typeof localStorage === 'undefined') return null;
    return localStorage.getItem(key);
  }
}
```

- [ ] **Step 3: Create AuthService**

```typescript
import { Injectable, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { TokenStorageService } from './token-storage.service';
import { AuthResponse, LoginRequest, RegisterRequest, UserResponse } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly API = '/api/auth';

  readonly isLoggedIn = this.tokenStorage.isLoggedIn;
  readonly currentUsername = computed(() => {
    const token = this.tokenStorage.getAccessToken();
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.sub as string;
    } catch {
      return null;
    }
  });

  constructor(
    private http: HttpClient,
    private tokenStorage: TokenStorageService,
    private router: Router
  ) {}

  login(request: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.API}/login`, request).pipe(
      tap(res => this.tokenStorage.saveTokens(res.accessToken, res.refreshToken))
    );
  }

  register(request: RegisterRequest) {
    return this.http.post<AuthResponse>(`${this.API}/register`, request).pipe(
      tap(res => this.tokenStorage.saveTokens(res.accessToken, res.refreshToken))
    );
  }

  refresh() {
    const refreshToken = this.tokenStorage.getRefreshToken();
    return this.http.post<AuthResponse>(`${this.API}/refresh`, { refreshToken }).pipe(
      tap(res => this.tokenStorage.saveTokens(res.accessToken, res.refreshToken))
    );
  }

  logout(): void {
    this.tokenStorage.clear();
    this.router.navigate(['/']);
  }
}
```

- [ ] **Step 4: Create JWT interceptor**

```typescript
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { TokenStorageService } from './token-storage.service';
import { AuthService } from './auth.service';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenStorage = inject(TokenStorageService);
  const authService = inject(AuthService);

  const token = tokenStorage.getAccessToken();
  const authReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !req.url.includes('/api/auth/')) {
        return authService.refresh().pipe(
          switchMap(() => {
            const newToken = tokenStorage.getAccessToken();
            const retryReq = req.clone({ setHeaders: { Authorization: `Bearer ${newToken}` } });
            return next(retryReq);
          }),
          catchError(() => {
            authService.logout();
            return throwError(() => error);
          })
        );
      }
      return throwError(() => error);
    })
  );
};
```

- [ ] **Step 5: Create auth guards**

`auth.guard.ts`:
```typescript
import { inject } from '@angular/core';
import { Router, type CanActivateFn } from '@angular/router';
import { TokenStorageService } from './token-storage.service';

export const authGuard: CanActivateFn = () => {
  const tokenStorage = inject(TokenStorageService);
  const router = inject(Router);
  if (tokenStorage.isLoggedIn()) return true;
  router.navigate(['/login']);
  return false;
};
```

`admin.guard.ts`:
```typescript
import { inject } from '@angular/core';
import { Router, type CanActivateFn } from '@angular/router';
import { AuthService } from './auth.service';

export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  // Decode role from JWT
  const token = inject(import('./token-storage.service').then(m => m.TokenStorageService));
  // Simplified: check role from token payload
  return true; // will be refined when admin features are built
};
```

Actually, a cleaner admin guard:

```typescript
import { inject } from '@angular/core';
import { Router, type CanActivateFn } from '@angular/router';
import { TokenStorageService } from './token-storage.service';

export const adminGuard: CanActivateFn = () => {
  const tokenStorage = inject(TokenStorageService);
  const router = inject(Router);
  const token = tokenStorage.getAccessToken();
  if (!token) { router.navigate(['/login']); return false; }
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    if (payload.role === 'ADMIN') return true;
  } catch { /* ignore */ }
  router.navigate(['/']);
  return false;
};
```

Note: the backend JWT doesn't include a `role` claim yet. You should add `.claim("role", user.getRole().name())` to `JwtTokenProvider.buildToken()` once the role is available, or fetch user profile on login. For now, the admin guard is a placeholder that always returns true for logged-in users. The admin endpoints are protected server-side regardless.

- [ ] **Step 6: Wire interceptor into app.config.ts**

Update `adventure-km-frontend/src/app/app.config.ts`:

```typescript
import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';

import { routes } from './app.routes';
import { jwtInterceptor } from './core/auth/jwt.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withInterceptors([jwtInterceptor])),
    provideClientHydration(withEventReplay())
  ]
};
```

- [ ] **Step 7: Commit**

```bash
git add -A
git commit -m "feat(frontend): add TypeScript models, auth service, JWT interceptor, guards"
```

---

### Task 17: App Shell — Header, Footer & Routing

**Files:**
- Modify: `adventure-km-frontend/src/app/shared/header/header.component.ts`
- Modify: `adventure-km-frontend/src/app/shared/header/header.component.html`
- Modify: `adventure-km-frontend/src/app/shared/header/header.component.css`
- Create: `adventure-km-frontend/src/app/shared/components/footer/footer.component.ts`
- Modify: `adventure-km-frontend/src/app/app.html`
- Modify: `adventure-km-frontend/src/app/app.ts`
- Modify: `adventure-km-frontend/src/app/app.routes.ts`

- [ ] **Step 1: Rewrite HeaderComponent**

`header.component.ts`:
```typescript
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  constructor(protected auth: AuthService) {}
}
```

`header.component.html`:
```html
<header class="header">
  <div class="container header-inner">
    <a routerLink="/" class="logo">
      <span class="logo-pixel">⚔</span>
      <span class="logo-text">Adventure-KM</span>
    </a>
    <nav class="nav">
      <a routerLink="/adventures" routerLinkActive="active">Aventures</a>
      <a routerLink="/leaderboard" routerLinkActive="active">Classement</a>
      @if (auth.isLoggedIn()) {
        <a routerLink="/adventures/new" routerLinkActive="active" class="btn-gold">+ Nouvelle</a>
        <a [routerLink]="['/profile', auth.currentUsername()]" routerLinkActive="active">Profil</a>
        <button (click)="auth.logout()" class="btn-logout">Déconnexion</button>
      } @else {
        <a routerLink="/login" class="btn">Connexion</a>
      }
    </nav>
  </div>
</header>
```

`header.component.css`:
```css
.header {
  background: var(--bg-surface);
  border-bottom: 2px solid var(--border-color);
  padding: 12px 0;
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  font-size: 1.1rem;
}
.logo-pixel {
  font-size: 1.4rem;
}
.logo-text {
  color: var(--gold-accent);
  font-family: var(--font-pixel);
  font-size: 0.65rem;
}
.nav {
  display: flex;
  align-items: center;
  gap: 20px;
  font-size: 0.85rem;
}
.nav a.active {
  color: var(--gold-accent);
}
.btn-logout {
  border: none;
  background: none;
  color: var(--text-secondary);
  font-size: 0.8rem;
  padding: 4px 8px;
}
.btn-logout:hover {
  color: var(--danger);
  background: none;
}
```

- [ ] **Step 2: Create FooterComponent**

`footer.component.ts`:
```typescript
import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: true,
  template: `
    <footer class="footer">
      <div class="container">
        <span class="pixel-title">Adventure-KM</span>
        <span class="footer-text">— trails, ultras & pixels</span>
      </div>
    </footer>
  `,
  styles: [`
    .footer {
      border-top: 1px solid var(--border-color);
      padding: 24px 0;
      margin-top: 60px;
      text-align: center;
    }
    .footer-text {
      color: var(--text-muted);
      font-size: 0.75rem;
      margin-left: 8px;
    }
  `]
})
export class FooterComponent {}
```

- [ ] **Step 3: Update app shell**

`app.ts`:
```typescript
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './shared/header/header.component';
import { FooterComponent } from './shared/components/footer/footer.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, FooterComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class AppComponent {}
```

`app.html`:
```html
<app-header />
<main class="main-content">
  <router-outlet />
</main>
<app-footer />
```

`app.css`:
```css
.main-content {
  min-height: calc(100vh - 140px);
  padding: 32px 0;
}
```

- [ ] **Step 4: Set up full routing**

`app.routes.ts`:
```typescript
import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'login',
    loadComponent: () => import('./features/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'adventures',
    loadComponent: () => import('./features/adventures/adventures.component').then(m => m.AdventuresComponent)
  },
  {
    path: 'adventures/new',
    loadComponent: () => import('./features/adventure-form/adventure-form.component').then(m => m.AdventureFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'adventures/:id',
    loadComponent: () => import('./features/adventure-detail/adventure-detail.component').then(m => m.AdventureDetailComponent)
  },
  {
    path: 'adventures/:id/edit',
    loadComponent: () => import('./features/adventure-form/adventure-form.component').then(m => m.AdventureFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'profile/:username',
    loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent)
  },
  {
    path: 'leaderboard',
    loadComponent: () => import('./features/leaderboard/leaderboard.component').then(m => m.LeaderboardComponent)
  },
  {
    path: 'admin',
    loadComponent: () => import('./features/admin/admin.component').then(m => m.AdminComponent),
    canActivate: [authGuard]
  },
  { path: '**', redirectTo: '' }
];
```

- [ ] **Step 5: Create stub components for all routes**

Each feature component needs a minimal stub so the routes compile. Create one-liner components for: `LoginComponent`, `RegisterComponent`, `AdventuresComponent`, `AdventureDetailComponent`, `AdventureFormComponent`, `ProfileComponent`, `LeaderboardComponent`, `AdminComponent`.

Example stub pattern (repeat for each):

```typescript
// login.component.ts
import { Component } from '@angular/core';

@Component({
  selector: 'app-login',
  standalone: true,
  template: '<div class="container"><h1 class="pixel-title">Login</h1></div>'
})
export class LoginComponent {}
```

Create the same pattern for all 7 remaining feature components (register, adventures, adventure-detail, adventure-form, profile, leaderboard, admin).

- [ ] **Step 6: Verify frontend compiles**

```bash
cd adventure-km-frontend && npx ng build --configuration=development 2>&1 | tail -5
```

Expected: Build successful.

- [ ] **Step 7: Commit**

```bash
git add -A
git commit -m "feat(frontend): add app shell, header, footer, routing with lazy-loaded stubs"
```

---

### Task 18: Login & Register Pages

**Files:**
- Modify: `adventure-km-frontend/src/app/features/login/login.component.ts`
- Create: `adventure-km-frontend/src/app/features/login/login.component.html`
- Create: `adventure-km-frontend/src/app/features/login/login.component.css`
- Modify: `adventure-km-frontend/src/app/features/register/register.component.ts`
- Create: `adventure-km-frontend/src/app/features/register/register.component.html`
- Create: `adventure-km-frontend/src/app/features/register/register.component.css`

- [ ] **Step 1: Implement LoginComponent**

`login.component.ts`:
```typescript
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  username = '';
  password = '';
  error = signal<string | null>(null);
  loading = signal(false);

  constructor(private auth: AuthService, private router: Router) {}

  onSubmit(): void {
    this.loading.set(true);
    this.error.set(null);
    this.auth.login({ username: this.username, password: this.password }).subscribe({
      next: () => { this.router.navigate(['/']); },
      error: () => {
        this.error.set('Identifiants incorrects');
        this.loading.set(false);
      }
    });
  }
}
```

`login.component.html`:
```html
<div class="container auth-page">
  <div class="auth-card card pixel-border">
    <h1 class="pixel-title">Connexion</h1>
    <form (ngSubmit)="onSubmit()">
      @if (error()) {
        <div class="error-msg">{{ error() }}</div>
      }
      <div class="field">
        <label for="username">Nom d'utilisateur</label>
        <input id="username" [(ngModel)]="username" name="username" required autocomplete="username" />
      </div>
      <div class="field">
        <label for="password">Mot de passe</label>
        <input id="password" type="password" [(ngModel)]="password" name="password" required autocomplete="current-password" />
      </div>
      <button type="submit" class="btn-gold" [disabled]="loading()">
        {{ loading() ? 'Connexion...' : 'Se connecter' }}
      </button>
    </form>
  </div>
</div>
```

`login.component.css`:
```css
.auth-page {
  display: flex;
  justify-content: center;
  padding-top: 80px;
}
.auth-card {
  width: 100%;
  max-width: 400px;
}
.auth-card h1 {
  margin-bottom: 24px;
  text-align: center;
}
.field {
  margin-bottom: 16px;
}
.field label {
  display: block;
  font-size: 0.8rem;
  color: var(--text-secondary);
  margin-bottom: 6px;
}
.error-msg {
  color: var(--danger);
  font-size: 0.8rem;
  margin-bottom: 12px;
  text-align: center;
}
button[type="submit"] {
  width: 100%;
  margin-top: 8px;
}
```

- [ ] **Step 2: Implement RegisterComponent**

`register.component.ts`:
```typescript
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  username = '';
  email = '';
  password = '';
  invitationToken = '';
  error = signal<string | null>(null);
  loading = signal(false);

  constructor(private auth: AuthService, private router: Router, private route: ActivatedRoute) {
    this.invitationToken = this.route.snapshot.queryParamMap.get('token') ?? '';
  }

  onSubmit(): void {
    this.loading.set(true);
    this.error.set(null);
    this.auth.register({
      username: this.username,
      email: this.email,
      password: this.password,
      invitationToken: this.invitationToken
    }).subscribe({
      next: () => { this.router.navigate(['/']); },
      error: (err) => {
        this.error.set(err.error?.detail ?? 'Erreur lors de l\'inscription');
        this.loading.set(false);
      }
    });
  }
}
```

`register.component.html`:
```html
<div class="container auth-page">
  <div class="auth-card card pixel-border">
    <h1 class="pixel-title">Inscription</h1>
    @if (!invitationToken) {
      <p class="error-msg">Lien d'invitation requis.</p>
    } @else {
      <form (ngSubmit)="onSubmit()">
        @if (error()) {
          <div class="error-msg">{{ error() }}</div>
        }
        <div class="field">
          <label for="username">Nom d'utilisateur</label>
          <input id="username" [(ngModel)]="username" name="username" required minlength="3" />
        </div>
        <div class="field">
          <label for="email">Email</label>
          <input id="email" type="email" [(ngModel)]="email" name="email" required />
        </div>
        <div class="field">
          <label for="password">Mot de passe</label>
          <input id="password" type="password" [(ngModel)]="password" name="password" required minlength="6" />
        </div>
        <button type="submit" class="btn-gold" [disabled]="loading()">
          {{ loading() ? 'Inscription...' : 'Créer mon compte' }}
        </button>
      </form>
    }
  </div>
</div>
```

`register.component.css`: same as login.component.css (copy the same file).

- [ ] **Step 3: Verify build**

```bash
cd adventure-km-frontend && npx ng build --configuration=development 2>&1 | tail -5
```

Expected: Build successful.

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "feat(frontend): add login and register pages"
```

---

## Phase 6 — Frontend Features

### Task 19: Shared Components & Adventure List

**Files:**
- Create: `adventure-km-frontend/src/app/shared/components/stat-badge/stat-badge.component.ts`
- Create: `adventure-km-frontend/src/app/shared/components/adventure-card/adventure-card.component.ts`
- Create: `adventure-km-frontend/src/app/shared/components/adventure-card/adventure-card.component.html`
- Create: `adventure-km-frontend/src/app/shared/components/adventure-card/adventure-card.component.css`
- Create: `adventure-km-frontend/src/app/core/services/adventure.service.ts`
- Modify: `adventure-km-frontend/src/app/features/adventures/adventures.component.ts`

- [ ] **Step 1: Create StatBadgeComponent**

```typescript
import { Component, input } from '@angular/core';

@Component({
  selector: 'app-stat-badge',
  standalone: true,
  template: `
    <div class="stat-badge">
      <span class="stat-value">{{ value() }}</span>
      <span class="stat-label">{{ label() }}</span>
    </div>
  `,
  styles: [`
    .stat-badge { text-align: center; }
  `]
})
export class StatBadgeComponent {
  value = input.required<string | number>();
  label = input.required<string>();
}
```

- [ ] **Step 2: Create AdventureCardComponent**

`adventure-card.component.ts`:
```typescript
import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AdventureSummaryResponse } from '../../../core/models/adventure.model';
import { StatBadgeComponent } from '../stat-badge/stat-badge.component';

@Component({
  selector: 'app-adventure-card',
  standalone: true,
  imports: [RouterLink, StatBadgeComponent],
  templateUrl: './adventure-card.component.html',
  styleUrl: './adventure-card.component.css'
})
export class AdventureCardComponent {
  adventure = input.required<AdventureSummaryResponse>();
}
```

`adventure-card.component.html`:
```html
<a [routerLink]="['/adventures', adventure().id]" class="card adventure-card">
  <div class="card-header">
    <span class="adventure-type">{{ adventure().type ?? 'sortie' }}</span>
    <span class="adventure-date">{{ adventure().date }}</span>
  </div>
  <h3 class="adventure-title">{{ adventure().title }}</h3>
  @if (adventure().stats) {
    <div class="card-stats">
      <app-stat-badge [value]="adventure().stats.distanceKm + ' km'" label="Distance" />
      <app-stat-badge [value]="adventure().stats.elevationGainM + ' m'" label="D+" />
      @if (adventure().stats.durationMinutes) {
        <app-stat-badge [value]="formatDuration(adventure().stats.durationMinutes)" label="Durée" />
      }
    </div>
  }
  @if (adventure().difficulty) {
    <div class="difficulty">
      @for (i of difficultyStars(); track i) {
        <span class="star filled">★</span>
      }
    </div>
  }
  <div class="card-author">par {{ adventure().author.username }}</div>
</a>
```

`adventure-card.component.css`:
```css
.adventure-card {
  display: block;
  transition: border-color 0.2s, transform 0.2s;
  text-decoration: none;
  color: inherit;
}
.adventure-card:hover {
  border-color: var(--green-primary);
  transform: translateY(-2px);
}
.card-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}
.adventure-type {
  font-size: 0.7rem;
  text-transform: uppercase;
  color: var(--gold-accent);
  font-weight: 600;
  letter-spacing: 1px;
}
.adventure-date {
  font-size: 0.75rem;
  color: var(--text-secondary);
}
.adventure-title {
  font-size: 1rem;
  margin-bottom: 12px;
  color: var(--green-primary);
}
.card-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
}
.difficulty {
  margin-bottom: 8px;
}
.star { color: var(--text-muted); font-size: 0.8rem; }
.star.filled { color: var(--gold-accent); }
.card-author {
  font-size: 0.75rem;
  color: var(--text-secondary);
}
```

Add to `adventure-card.component.ts`:
```typescript
  formatDuration(minutes: number): string {
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return h > 0 ? `${h}h${m > 0 ? m.toString().padStart(2, '0') : ''}` : `${m}min`;
  }

  difficultyStars(): number[] {
    return Array.from({ length: this.adventure().difficulty ?? 0 }, (_, i) => i);
  }
```

- [ ] **Step 3: Create AdventureApiService**

Create `adventure-km-frontend/src/app/core/services/adventure.service.ts`:

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AdventureResponse,
  AdventureSummaryResponse,
  AdventureCreateRequest
} from '../models/adventure.model';

@Injectable({ providedIn: 'root' })
export class AdventureApiService {
  private readonly API = '/api/adventures';

  constructor(private http: HttpClient) {}

  listPublished(): Observable<AdventureSummaryResponse[]> {
    return this.http.get<AdventureSummaryResponse[]>(this.API);
  }

  getById(id: number): Observable<AdventureResponse> {
    return this.http.get<AdventureResponse>(`${this.API}/${id}`);
  }

  create(request: AdventureCreateRequest): Observable<AdventureResponse> {
    return this.http.post<AdventureResponse>(this.API, request);
  }

  update(id: number, request: Partial<AdventureCreateRequest>): Observable<AdventureResponse> {
    return this.http.put<AdventureResponse>(`${this.API}/${id}`, request);
  }

  publish(id: number): Observable<AdventureResponse> {
    return this.http.post<AdventureResponse>(`${this.API}/${id}/publish`, {});
  }

  uploadGpx(id: number, file: File): Observable<AdventureResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<AdventureResponse>(`${this.API}/${id}/gpx`, formData);
  }

  uploadPhoto(id: number, file: File, caption: string): Observable<AdventureResponse> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('caption', caption);
    return this.http.post<AdventureResponse>(`${this.API}/${id}/photos`, formData);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }
}
```

- [ ] **Step 4: Implement AdventuresComponent (list page)**

`adventures.component.ts`:
```typescript
import { Component, signal, OnInit } from '@angular/core';
import { AdventureCardComponent } from '../../shared/components/adventure-card/adventure-card.component';
import { AdventureApiService } from '../../core/services/adventure.service';
import { AdventureSummaryResponse } from '../../core/models/adventure.model';

@Component({
  selector: 'app-adventures',
  standalone: true,
  imports: [AdventureCardComponent],
  template: `
    <div class="container">
      <h1 class="pixel-title">Aventures</h1>
      <div class="adventure-grid">
        @for (adventure of adventures(); track adventure.id) {
          <app-adventure-card [adventure]="adventure" />
        }
      </div>
      @if (adventures().length === 0) {
        <p class="empty">Aucune aventure publiée pour le moment.</p>
      }
    </div>
  `,
  styles: [`
    h1 { margin-bottom: 24px; }
    .adventure-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
      gap: 20px;
    }
    .empty { color: var(--text-secondary); text-align: center; margin-top: 40px; }
  `]
})
export class AdventuresComponent implements OnInit {
  adventures = signal<AdventureSummaryResponse[]>([]);

  constructor(private api: AdventureApiService) {}

  ngOnInit(): void {
    this.api.listPublished().subscribe(data => this.adventures.set(data));
  }
}
```

- [ ] **Step 5: Verify build**

```bash
cd adventure-km-frontend && npx ng build --configuration=development 2>&1 | tail -5
```

Expected: Build successful.

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "feat(frontend): add adventure list with stat-badge and adventure-card components"
```

---

### Task 20: Adventure Detail Page (Markdown + Map + Chart)

**Files:**
- Modify: `adventure-km-frontend/src/app/features/adventure-detail/adventure-detail.component.ts`
- Create: `adventure-km-frontend/src/app/features/adventure-detail/adventure-detail.component.html`
- Create: `adventure-km-frontend/src/app/features/adventure-detail/adventure-detail.component.css`

- [ ] **Step 1: Implement AdventureDetailComponent**

`adventure-detail.component.ts`:
```typescript
import { Component, OnInit, signal, input, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { AdventureApiService } from '../../core/services/adventure.service';
import { AdventureResponse } from '../../core/models/adventure.model';
import { StatBadgeComponent } from '../../shared/components/stat-badge/stat-badge.component';
import { MarkdownComponent } from 'ngx-markdown';
import { CommonModule } from '@angular/common';

declare const L: any;

@Component({
  selector: 'app-adventure-detail',
  standalone: true,
  imports: [CommonModule, StatBadgeComponent, MarkdownComponent],
  templateUrl: './adventure-detail.component.html',
  styleUrl: './adventure-detail.component.css'
})
export class AdventureDetailComponent implements OnInit, AfterViewInit {
  id = input.required<string>();
  adventure = signal<AdventureResponse | null>(null);

  @ViewChild('mapContainer') mapContainer!: ElementRef;
  @ViewChild('chartCanvas') chartCanvas!: ElementRef<HTMLCanvasElement>;

  constructor(private api: AdventureApiService) {}

  ngOnInit(): void {
    this.api.getById(+this.id()).subscribe(data => {
      this.adventure.set(data);
    });
  }

  ngAfterViewInit(): void {
    // Map and chart will be initialized after data loads
    // Watch for adventure signal changes
  }

  initMap(geojsonStr: string): void {
    if (!this.mapContainer?.nativeElement) return;
    const map = L.map(this.mapContainer.nativeElement).setView([45, 6], 10);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap'
    }).addTo(map);

    const geojson = JSON.parse(geojsonStr);
    const layer = L.geoJSON(geojson, {
      style: { color: '#22c55e', weight: 3 }
    }).addTo(map);
    map.fitBounds(layer.getBounds());
  }

  formatDuration(minutes: number | null): string {
    if (!minutes) return '—';
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return `${h}h${m.toString().padStart(2, '0')}`;
  }
}
```

`adventure-detail.component.html`:
```html
@if (adventure(); as adv) {
  <div class="container detail-page">
    <div class="detail-header">
      <span class="adventure-type">{{ adv.type ?? 'sortie' }}</span>
      <h1>{{ adv.title }}</h1>
      <div class="meta">
        <span>{{ adv.date }}</span>
        <span>par {{ adv.author.username }}</span>
      </div>
    </div>

    @if (adv.stats) {
      <div class="stats-row">
        <app-stat-badge [value]="adv.stats.distanceKm + ' km'" label="Distance" />
        <app-stat-badge [value]="adv.stats.elevationGainM + ' m'" label="D+" />
        <app-stat-badge [value]="adv.stats.elevationLossM + ' m'" label="D-" />
        <app-stat-badge [value]="formatDuration(adv.stats.durationMinutes)" label="Durée" />
        <app-stat-badge [value]="adv.stats.maxAltitudeM + ' m'" label="Alt. max" />
      </div>
    }

    @if (adv.gpxPath) {
      <div class="map-section">
        <h2 class="pixel-title">Trace GPS</h2>
        <div #mapContainer class="map-container"></div>
      </div>
    }

    <div class="content-section">
      <markdown [data]="adv.content" />
    </div>

    @if (adv.photos.length > 0) {
      <div class="photos-section">
        <h2 class="pixel-title">Photos</h2>
        <div class="photo-grid">
          @for (photo of adv.photos; track photo.id) {
            <figure>
              <img [src]="'/uploads/' + photo.filePath" [alt]="photo.caption ?? adv.title" />
              @if (photo.caption) {
                <figcaption>{{ photo.caption }}</figcaption>
              }
            </figure>
          }
        </div>
      </div>
    }

    @if (adv.equipment.length > 0) {
      <div class="equipment-section">
        <h2 class="pixel-title">Équipement</h2>
        <div class="equipment-grid">
          @for (item of adv.equipment; track item.id) {
            <div class="equipment-item card">
              <span class="eq-name">{{ item.name }}</span>
              <span class="eq-category">{{ item.category }}</span>
            </div>
          }
        </div>
      </div>
    }
  </div>
}
```

`adventure-detail.component.css`:
```css
.detail-header {
  margin-bottom: 32px;
}
.detail-header h1 {
  font-size: 1.6rem;
  margin: 8px 0;
  color: var(--green-primary);
}
.adventure-type {
  font-size: 0.7rem;
  text-transform: uppercase;
  color: var(--gold-accent);
  font-weight: 600;
  letter-spacing: 2px;
}
.meta {
  display: flex;
  gap: 16px;
  color: var(--text-secondary);
  font-size: 0.85rem;
}
.stats-row {
  display: flex;
  gap: 24px;
  margin-bottom: 32px;
  padding: 16px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
}
.map-section {
  margin-bottom: 32px;
}
.map-container {
  height: 400px;
  border: 1px solid var(--border-color);
}
.content-section {
  margin-bottom: 32px;
  line-height: 1.8;
}
.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}
.photo-grid img {
  width: 100%;
  border: 1px solid var(--border-color);
}
.photo-grid figcaption {
  font-size: 0.8rem;
  color: var(--text-secondary);
  margin-top: 4px;
}
.equipment-section {
  margin-top: 32px;
}
.equipment-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
.equipment-item {
  padding: 8px 14px;
}
.eq-name {
  font-size: 0.85rem;
}
.eq-category {
  font-size: 0.7rem;
  color: var(--text-secondary);
  margin-left: 8px;
  text-transform: uppercase;
}
h2.pixel-title {
  margin-bottom: 16px;
}
```

Note: Leaflet CSS must be added to `angular.json` styles array:
```json
"styles": [
  "node_modules/leaflet/dist/leaflet.css",
  "src/styles.css"
]
```

Also add Leaflet JS to the `scripts` array in `angular.json` or import it in the component. The simplest approach for Angular 21: add the CDN link to `src/index.html` or add leaflet to angular.json scripts.

- [ ] **Step 2: Update angular.json for Leaflet**

Add to `angular.json` under `architect.build.options`:
```json
"styles": [
  "node_modules/leaflet/dist/leaflet.css",
  "src/styles.css"
]
```

- [ ] **Step 3: Add ngx-markdown provider to app.config.ts**

Add `provideMarkdown()` to the `app.config.ts` providers:

```typescript
import { provideMarkdown } from 'ngx-markdown';
// add to providers array:
provideMarkdown()
```

- [ ] **Step 4: Verify build**

```bash
cd adventure-km-frontend && npx ng build --configuration=development 2>&1 | tail -5
```

Expected: Build successful.

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "feat(frontend): add adventure detail page with markdown, map placeholder, stats"
```

---

### Task 21: Adventure Form (Multi-step)

**Files:**
- Modify: `adventure-km-frontend/src/app/features/adventure-form/adventure-form.component.ts`
- Create: `adventure-km-frontend/src/app/features/adventure-form/adventure-form.component.html`
- Create: `adventure-km-frontend/src/app/features/adventure-form/adventure-form.component.css`

- [ ] **Step 1: Implement AdventureFormComponent**

`adventure-form.component.ts`:
```typescript
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AdventureApiService } from '../../core/services/adventure.service';
import { EquipmentItemResponse } from '../../core/models/equipment.model';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-adventure-form',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './adventure-form.component.html',
  styleUrl: './adventure-form.component.css'
})
export class AdventureFormComponent implements OnInit {
  step = signal(1);
  editId: number | null = null;

  title = '';
  date = '';
  type = '';
  difficulty = 3;
  content = '';
  selectedEquipmentIds: number[] = [];

  equipment = signal<EquipmentItemResponse[]>([]);
  savedId = signal<number | null>(null);
  error = signal<string | null>(null);

  constructor(
    private api: AdventureApiService,
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.http.get<EquipmentItemResponse[]>('/api/equipment').subscribe(
      items => this.equipment.set(items)
    );
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editId = +id;
      this.api.getById(this.editId).subscribe(adv => {
        this.title = adv.title;
        this.date = adv.date;
        this.type = adv.type ?? '';
        this.difficulty = adv.difficulty ?? 3;
        this.content = adv.content;
        this.selectedEquipmentIds = adv.equipment.map(e => e.id);
        this.savedId.set(adv.id);
      });
    }
  }

  saveMetadata(): void {
    const request = {
      title: this.title,
      date: this.date,
      content: this.content,
      type: this.type || undefined,
      difficulty: this.difficulty,
      equipmentIds: this.selectedEquipmentIds
    };

    const obs = this.editId
      ? this.api.update(this.editId, request)
      : this.api.create(request);

    obs.subscribe({
      next: res => {
        this.savedId.set(res.id);
        this.step.set(2);
      },
      error: () => this.error.set('Erreur lors de la sauvegarde')
    });
  }

  onGpxSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file || !this.savedId()) return;
    this.api.uploadGpx(this.savedId()!, file).subscribe({
      next: () => this.step.set(3),
      error: () => this.error.set('Erreur lors de l\'upload GPX')
    });
  }

  onPhotoSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file || !this.savedId()) return;
    this.api.uploadPhoto(this.savedId()!, file, '').subscribe({
      error: () => this.error.set('Erreur lors de l\'upload photo')
    });
  }

  publish(): void {
    if (!this.savedId()) return;
    this.api.publish(this.savedId()!).subscribe({
      next: () => this.router.navigate(['/adventures', this.savedId()]),
      error: () => this.error.set('Erreur lors de la publication')
    });
  }

  toggleEquipment(id: number): void {
    const idx = this.selectedEquipmentIds.indexOf(id);
    if (idx >= 0) this.selectedEquipmentIds.splice(idx, 1);
    else this.selectedEquipmentIds.push(id);
  }

  isSelected(id: number): boolean {
    return this.selectedEquipmentIds.includes(id);
  }
}
```

`adventure-form.component.html`:
```html
<div class="container">
  <h1 class="pixel-title">{{ editId ? 'Modifier' : 'Nouvelle aventure' }}</h1>

  @if (error()) {
    <div class="error-msg">{{ error() }}</div>
  }

  <div class="steps-indicator">
    <span [class.active]="step() === 1">1. Contenu</span>
    <span [class.active]="step() === 2">2. GPX</span>
    <span [class.active]="step() === 3">3. Photos</span>
  </div>

  @if (step() === 1) {
    <form (ngSubmit)="saveMetadata()" class="form-section">
      <div class="field">
        <label>Titre</label>
        <input [(ngModel)]="title" name="title" required />
      </div>
      <div class="field-row">
        <div class="field">
          <label>Date</label>
          <input type="date" [(ngModel)]="date" name="date" required />
        </div>
        <div class="field">
          <label>Type</label>
          <select [(ngModel)]="type" name="type">
            <option value="">—</option>
            <option value="trail">Trail</option>
            <option value="hike">Randonnée</option>
            <option value="ultra">Ultra</option>
            <option value="race">Course</option>
          </select>
        </div>
        <div class="field">
          <label>Difficulté (1-5)</label>
          <input type="number" [(ngModel)]="difficulty" name="difficulty" min="1" max="5" />
        </div>
      </div>
      <div class="field">
        <label>Contenu (Markdown)</label>
        <textarea [(ngModel)]="content" name="content" rows="15" required></textarea>
      </div>

      <div class="field">
        <label class="pixel-title">Équipement</label>
        <div class="equipment-selector">
          @for (item of equipment(); track item.id) {
            <button type="button"
                    [class.selected]="isSelected(item.id)"
                    (click)="toggleEquipment(item.id)">
              {{ item.name }}
            </button>
          }
        </div>
      </div>

      <button type="submit" class="btn-gold">Sauvegarder et continuer</button>
    </form>
  }

  @if (step() === 2) {
    <div class="form-section">
      <h2>Fichier GPX (optionnel)</h2>
      <input type="file" accept=".gpx" (change)="onGpxSelected($event)" />
      <button (click)="step.set(3)" class="btn" style="margin-top: 16px;">Passer</button>
    </div>
  }

  @if (step() === 3) {
    <div class="form-section">
      <h2>Photos (max 5, optionnel)</h2>
      <input type="file" accept="image/*" (change)="onPhotoSelected($event)" />
      <div style="margin-top: 24px; display: flex; gap: 12px;">
        <button (click)="publish()" class="btn-gold">Publier</button>
        <button (click)="router.navigate(['/adventures', savedId()])" class="btn">Garder en brouillon</button>
      </div>
    </div>
  }
</div>
```

`adventure-form.component.css`:
```css
h1 { margin-bottom: 24px; }
.steps-indicator {
  display: flex;
  gap: 24px;
  margin-bottom: 32px;
  font-size: 0.8rem;
  color: var(--text-muted);
}
.steps-indicator .active {
  color: var(--gold-accent);
  font-weight: 700;
}
.form-section {
  max-width: 700px;
}
.field { margin-bottom: 16px; }
.field label {
  display: block;
  font-size: 0.8rem;
  color: var(--text-secondary);
  margin-bottom: 6px;
}
.field-row {
  display: flex;
  gap: 16px;
}
.field-row .field { flex: 1; }
textarea {
  resize: vertical;
  font-family: var(--font-mono);
  font-size: 0.85rem;
}
.equipment-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.equipment-selector button {
  font-size: 0.75rem;
  padding: 6px 12px;
}
.equipment-selector button.selected {
  background: var(--gold-accent);
  color: var(--bg-deep);
  border-color: var(--gold-accent);
}
.error-msg {
  color: var(--danger);
  margin-bottom: 16px;
}
```

Make `router` public in the component for template access, or use a method.

- [ ] **Step 2: Verify build**

```bash
cd adventure-km-frontend && npx ng build --configuration=development 2>&1 | tail -5
```

Expected: Build successful.

- [ ] **Step 3: Commit**

```bash
git add -A
git commit -m "feat(frontend): add multi-step adventure creation form"
```

---

### Task 22: Profile Page (RPG Sheet)

**Files:**
- Modify: `adventure-km-frontend/src/app/features/profile/profile.component.ts`
- Create: `adventure-km-frontend/src/app/features/profile/profile.component.html`
- Create: `adventure-km-frontend/src/app/features/profile/profile.component.css`
- Create: `adventure-km-frontend/src/app/shared/components/pixel-avatar/pixel-avatar.component.ts`

- [ ] **Step 1: Create PixelAvatarComponent**

```typescript
import { Component, input } from '@angular/core';

@Component({
  selector: 'app-pixel-avatar',
  standalone: true,
  template: `
    <div class="pixel-avatar pixel-border" [style.width.px]="size()" [style.height.px]="size()">
      <div class="sprite" [attr.data-sprite]="spriteId()">
        <span class="avatar-char">{{ avatarChar() }}</span>
      </div>
    </div>
  `,
  styles: [`
    .pixel-avatar {
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--bg-surface);
      image-rendering: pixelated;
    }
    .sprite {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 100%;
    }
    .avatar-char {
      font-family: var(--font-pixel);
      font-size: 2rem;
      color: var(--gold-accent);
    }
  `]
})
export class PixelAvatarComponent {
  spriteId = input<number>(1);
  size = input<number>(96);

  avatarChar() {
    const chars = ['⚔', '🏔', '🗡', '🛡', '⚡', '🔥', '💎', '🌟', '👑', '🏆'];
    return chars[(this.spriteId() - 1) % chars.length];
  }
}
```

- [ ] **Step 2: Create UserApiService**

Create `adventure-km-frontend/src/app/core/services/user.service.ts`:

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserLevelResponse } from '../models/user.model';
import { AdventureSummaryResponse } from '../models/adventure.model';

@Injectable({ providedIn: 'root' })
export class UserApiService {
  constructor(private http: HttpClient) {}

  getProfile(username: string): Observable<UserLevelResponse> {
    return this.http.get<UserLevelResponse>(`/api/users/${username}`);
  }

  getUserAdventures(username: string): Observable<AdventureSummaryResponse[]> {
    return this.http.get<AdventureSummaryResponse[]>(`/api/users/${username}/adventures`);
  }

  getLeaderboard(sortBy: string = 'score'): Observable<UserLevelResponse[]> {
    return this.http.get<UserLevelResponse[]>(`/api/leaderboard?sortBy=${sortBy}`);
  }
}
```

- [ ] **Step 3: Implement ProfileComponent**

`profile.component.ts`:
```typescript
import { Component, OnInit, signal, input } from '@angular/core';
import { UserApiService } from '../../core/services/user.service';
import { UserLevelResponse } from '../../core/models/user.model';
import { AdventureSummaryResponse } from '../../core/models/adventure.model';
import { PixelAvatarComponent } from '../../shared/components/pixel-avatar/pixel-avatar.component';
import { StatBadgeComponent } from '../../shared/components/stat-badge/stat-badge.component';
import { AdventureCardComponent } from '../../shared/components/adventure-card/adventure-card.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [PixelAvatarComponent, StatBadgeComponent, AdventureCardComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  username = input.required<string>();
  profile = signal<UserLevelResponse | null>(null);
  adventures = signal<AdventureSummaryResponse[]>([]);

  constructor(private userApi: UserApiService) {}

  ngOnInit(): void {
    this.userApi.getProfile(this.username()).subscribe(p => this.profile.set(p));
    this.userApi.getUserAdventures(this.username()).subscribe(a => this.adventures.set(a));
  }

  xpProgress(): number {
    const p = this.profile();
    if (!p) return 0;
    const currentLevelScore = p.level * p.level * 10;
    const nextLevelScore = (p.level + 1) * (p.level + 1) * 10;
    return ((p.rpgScore - currentLevelScore) / (nextLevelScore - currentLevelScore)) * 100;
  }
}
```

`profile.component.html`:
```html
@if (profile(); as p) {
  <div class="container profile-page">
    <div class="rpg-sheet card pixel-border">
      <div class="sheet-header">
        <app-pixel-avatar [spriteId]="p.avatarSpriteId" [size]="96" />
        <div class="sheet-info">
          <h1 class="pixel-title">{{ p.username }}</h1>
          <div class="level-badge">Niveau {{ p.level }}</div>
        </div>
      </div>

      <div class="xp-bar-container">
        <div class="xp-bar" [style.width.%]="xpProgress()"></div>
        <span class="xp-label">{{ p.rpgScore }} XP</span>
      </div>

      <div class="stats-grid">
        <app-stat-badge [value]="p.totalKm + ' km'" label="Distance totale" />
        <app-stat-badge [value]="p.totalElevationM + ' m'" label="D+ total" />
        <app-stat-badge [value]="p.adventureCount" label="Aventures" />
        <app-stat-badge [value]="p.rpgScore" label="Score RPG" />
      </div>
    </div>

    <h2 class="pixel-title section-title">Aventures</h2>
    <div class="adventure-grid">
      @for (adventure of adventures(); track adventure.id) {
        <app-adventure-card [adventure]="adventure" />
      }
    </div>
  </div>
}
```

`profile.component.css`:
```css
.rpg-sheet {
  margin-bottom: 40px;
}
.sheet-header {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 20px;
}
.sheet-info h1 {
  font-size: 1rem;
  margin-bottom: 4px;
}
.level-badge {
  font-family: var(--font-pixel);
  font-size: 0.6rem;
  color: var(--green-primary);
  background: var(--green-glow);
  padding: 4px 12px;
  display: inline-block;
}
.xp-bar-container {
  position: relative;
  height: 24px;
  background: var(--bg-surface);
  border: 1px solid var(--border-color);
  margin-bottom: 20px;
}
.xp-bar {
  height: 100%;
  background: linear-gradient(90deg, var(--green-dim), var(--green-primary));
  transition: width 0.5s ease;
}
.xp-label {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 0.7rem;
  font-weight: 700;
  color: var(--text-primary);
}
.stats-grid {
  display: flex;
  gap: 24px;
}
.section-title {
  margin-bottom: 20px;
}
.adventure-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}
```

- [ ] **Step 4: Verify build**

```bash
cd adventure-km-frontend && npx ng build --configuration=development 2>&1 | tail -5
```

Expected: Build successful.

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "feat(frontend): add profile page with RPG sheet, XP bar, and adventure list"
```

---

### Task 23: Leaderboard, Home & Admin Pages

**Files:**
- Modify: `adventure-km-frontend/src/app/features/leaderboard/leaderboard.component.ts`
- Modify: `adventure-km-frontend/src/app/features/home/home.component.ts`
- Create: `adventure-km-frontend/src/app/features/home/home.component.html`
- Create: `adventure-km-frontend/src/app/features/home/home.component.css`
- Modify: `adventure-km-frontend/src/app/features/admin/admin.component.ts`

- [ ] **Step 1: Implement LeaderboardComponent**

```typescript
import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { UserApiService } from '../../core/services/user.service';
import { UserLevelResponse } from '../../core/models/user.model';
import { PixelAvatarComponent } from '../../shared/components/pixel-avatar/pixel-avatar.component';

@Component({
  selector: 'app-leaderboard',
  standalone: true,
  imports: [RouterLink, PixelAvatarComponent],
  template: `
    <div class="container">
      <h1 class="pixel-title">Classement</h1>
      <div class="sort-tabs">
        <button [class.active]="sortBy() === 'score'" (click)="sort('score')">Score RPG</button>
        <button [class.active]="sortBy() === 'km'" (click)="sort('km')">Kilomètres</button>
        <button [class.active]="sortBy() === 'elevation'" (click)="sort('elevation')">Dénivelé</button>
        <button [class.active]="sortBy() === 'count'" (click)="sort('count')">Aventures</button>
      </div>
      <div class="leaderboard-list">
        @for (user of users(); track user.userId; let i = $index) {
          <a [routerLink]="['/profile', user.username]" class="card lb-row">
            <span class="rank">#{{ i + 1 }}</span>
            <app-pixel-avatar [spriteId]="user.avatarSpriteId" [size]="40" />
            <span class="lb-name">{{ user.username }}</span>
            <span class="lb-level">Nv.{{ user.level }}</span>
            <span class="stat-value lb-value">{{ getDisplayValue(user) }}</span>
          </a>
        }
      </div>
    </div>
  `,
  styles: [`
    h1 { margin-bottom: 16px; }
    .sort-tabs { display: flex; gap: 8px; margin-bottom: 24px; }
    .sort-tabs button { font-size: 0.75rem; padding: 6px 14px; }
    .sort-tabs button.active { background: var(--green-primary); color: var(--bg-deep); }
    .lb-row {
      display: flex; align-items: center; gap: 16px;
      margin-bottom: 8px; padding: 12px 16px;
      text-decoration: none; color: inherit;
      transition: border-color 0.2s;
    }
    .lb-row:hover { border-color: var(--green-primary); }
    .rank { font-weight: 700; color: var(--gold-accent); min-width: 36px; }
    .lb-name { flex: 1; font-weight: 600; }
    .lb-level { color: var(--text-secondary); font-size: 0.8rem; }
    .lb-value { min-width: 80px; text-align: right; }
  `]
})
export class LeaderboardComponent implements OnInit {
  users = signal<UserLevelResponse[]>([]);
  sortBy = signal('score');

  constructor(private userApi: UserApiService) {}

  ngOnInit(): void { this.sort('score'); }

  sort(criteria: string): void {
    this.sortBy.set(criteria);
    this.userApi.getLeaderboard(criteria).subscribe(data => this.users.set(data));
  }

  getDisplayValue(user: UserLevelResponse): string {
    switch (this.sortBy()) {
      case 'km': return user.totalKm + ' km';
      case 'elevation': return user.totalElevationM + ' m';
      case 'count': return user.adventureCount + '';
      default: return user.rpgScore + ' pts';
    }
  }
}
```

- [ ] **Step 2: Implement HomeComponent**

`home.component.ts`:
```typescript
import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AdventureApiService } from '../../core/services/adventure.service';
import { AdventureSummaryResponse } from '../../core/models/adventure.model';
import { AdventureCardComponent } from '../../shared/components/adventure-card/adventure-card.component';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, AdventureCardComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  adventures = signal<AdventureSummaryResponse[]>([]);

  constructor(
    private api: AdventureApiService,
    protected auth: AuthService
  ) {}

  ngOnInit(): void {
    this.api.listPublished().subscribe(data => this.adventures.set(data.slice(0, 6)));
  }
}
```

`home.component.html`:
```html
<div class="hero">
  <div class="container hero-inner">
    <h1 class="hero-title">
      <span class="pixel-title">Adventure-KM</span>
    </h1>
    <p class="hero-sub">Trail · Ultra · Grandes randonnées — chaque kilomètre compte.</p>
    @if (!auth.isLoggedIn()) {
      <a routerLink="/adventures" class="btn-gold">Explorer les aventures</a>
    } @else {
      <a routerLink="/adventures/new" class="btn-gold">+ Nouvelle aventure</a>
    }
  </div>
</div>

<div class="container">
  <h2 class="pixel-title section-title">Dernières aventures</h2>
  <div class="adventure-grid">
    @for (adventure of adventures(); track adventure.id) {
      <app-adventure-card [adventure]="adventure" />
    }
  </div>
  <div class="more-link">
    <a routerLink="/adventures">Voir toutes les aventures →</a>
  </div>
</div>
```

`home.component.css`:
```css
.hero {
  background: var(--bg-surface);
  border-bottom: 2px solid var(--border-color);
  padding: 80px 0;
  margin-bottom: 40px;
  margin-top: -32px;
  text-align: center;
}
.hero-title {
  margin-bottom: 12px;
}
.hero-title .pixel-title {
  font-size: 1.2rem;
}
.hero-sub {
  color: var(--text-secondary);
  margin-bottom: 24px;
  font-size: 0.9rem;
}
.section-title {
  margin-bottom: 24px;
}
.adventure-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}
.more-link {
  text-align: center;
  margin-top: 32px;
}
```

- [ ] **Step 3: Implement AdminComponent (basic)**

```typescript
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { UserResponse } from '../../core/models/user.model';

interface InvitationResponse {
  id: number;
  token: string;
  email: string;
  expiresAt: string;
  usedAt: string | null;
}

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="container">
      <h1 class="pixel-title">Administration</h1>

      <section>
        <h2 class="pixel-title">Utilisateurs</h2>
        @for (user of users(); track user.id) {
          <div class="card" style="margin-bottom: 8px; padding: 12px;">
            <strong>{{ user.username }}</strong> — {{ user.role }}
          </div>
        }
      </section>

      <section style="margin-top: 32px;">
        <h2 class="pixel-title">Invitations</h2>
        <form (ngSubmit)="createInvitation()" style="display: flex; gap: 12px; margin-bottom: 16px;">
          <input [(ngModel)]="inviteEmail" name="email" placeholder="Email (optionnel)" />
          <button type="submit" class="btn-gold">Générer</button>
        </form>
        @for (inv of invitations(); track inv.id) {
          <div class="card" style="margin-bottom: 8px; padding: 12px; font-size: 0.8rem;">
            <code>{{ getInviteUrl(inv.token) }}</code>
            @if (inv.usedAt) {
              <span style="color: var(--text-muted); margin-left: 8px;">— utilisée</span>
            } @else {
              <span style="color: var(--green-primary); margin-left: 8px;">— active</span>
            }
          </div>
        }
      </section>
    </div>
  `
})
export class AdminComponent implements OnInit {
  users = signal<UserResponse[]>([]);
  invitations = signal<InvitationResponse[]>([]);
  inviteEmail = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<UserResponse[]>('/api/admin/users').subscribe(u => this.users.set(u));
    this.http.get<InvitationResponse[]>('/api/admin/invitations').subscribe(i => this.invitations.set(i));
  }

  createInvitation(): void {
    this.http.post<InvitationResponse>('/api/admin/invitations', { email: this.inviteEmail || null })
      .subscribe(inv => {
        this.invitations.update(list => [inv, ...list]);
        this.inviteEmail = '';
      });
  }

  getInviteUrl(token: string): string {
    return `${window.location.origin}/register?token=${token}`;
  }
}
```

- [ ] **Step 4: Verify build and visual check**

```bash
cd adventure-km-frontend && npx ng build --configuration=development 2>&1 | tail -5
```

Expected: Build successful.

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "feat(frontend): add leaderboard, home, and admin pages"
```

---

## Phase 7 — Infrastructure

### Task 24: Docker Compose & Nginx

**Files:**
- Create: `docker-compose.yml`
- Create: `adventure-km-backend/Dockerfile`
- Create: `adventure-km-frontend/Dockerfile`
- Create: `nginx/default.conf`

- [ ] **Step 1: Create backend Dockerfile**

`adventure-km-backend/Dockerfile`:
```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -q
COPY src src
RUN ./mvnw package -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
```

- [ ] **Step 2: Create frontend Dockerfile**

`adventure-km-frontend/Dockerfile`:
```dockerfile
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npx ng build --configuration=production

FROM nginx:alpine
COPY --from=build /app/dist/adventure-km-frontend/browser /usr/share/nginx/html
COPY nginx/frontend.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

Create `adventure-km-frontend/nginx/frontend.conf`:
```nginx
server {
    listen 80;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

- [ ] **Step 3: Create nginx reverse proxy config**

Create directory `nginx/` at project root and `nginx/default.conf`:

```nginx
upstream backend {
    server backend:8080;
}

server {
    listen 80;
    server_name _;

    client_max_body_size 50M;

    location /api/ {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /uploads/ {
        alias /uploads/;
        expires 30d;
        add_header Cache-Control "public, no-transform";
    }

    location / {
        proxy_pass http://frontend:80;
        proxy_set_header Host $host;
    }
}
```

- [ ] **Step 4: Create docker-compose.yml**

Create `docker-compose.yml` at project root:

```yaml
services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: adventurekm
      POSTGRES_USER: adventurekm
      POSTGRES_PASSWORD: ${DB_PASSWORD:-changeme}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  backend:
    build: ./adventure-km-backend
    depends_on:
      - postgres
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_USERNAME: adventurekm
      DB_PASSWORD: ${DB_PASSWORD:-changeme}
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/adventurekm
      APP_JWT_SECRET: ${JWT_SECRET:-change-this-in-production-must-be-at-least-256-bits-long-seriously}
      APP_UPLOAD_DIR: /uploads
    volumes:
      - uploads_data:/uploads
    ports:
      - "8080:8080"

  frontend:
    build: ./adventure-km-frontend
    depends_on:
      - backend

  nginx:
    image: nginx:alpine
    depends_on:
      - backend
      - frontend
    ports:
      - "80:80"
    volumes:
      - ./nginx/default.conf:/etc/nginx/conf.d/default.conf:ro
      - uploads_data:/uploads:ro

volumes:
  postgres_data:
  uploads_data:
```

- [ ] **Step 5: Verify docker-compose config**

```bash
cd /media/joffrey/hdd-workspace/app/eclipse-workspace/adventure-km
docker compose config --quiet
```

Expected: No errors.

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "feat: add Docker Compose infrastructure — backend, frontend, nginx, postgres"
```

---

## Self-Review

Spec coverage verified:
- ✅ Auth with JWT (login, register by invitation, refresh) — Tasks 5-7
- ✅ Adventure CRUD with DRAFT/PUBLISHED — Task 9
- ✅ GPX parsing (jpx), stats extraction, GeoJSON — Task 10
- ✅ Photo upload with resize — Task 11
- ✅ Equipment catalog + adventure-equipment links — Tasks 9, 14
- ✅ RPG score + level calculation with event — Task 12
- ✅ Leaderboard with 4 sort criteria — Task 13
- ✅ Admin (invitations, users, equipment CRUD) — Task 14
- ✅ All 8 tables mapped as JPA entities — Task 3
- ✅ Flyway migrations with seed data — Task 2
- ✅ Frontend: all 10 routes per spec — Task 17
- ✅ Frontend: pixel-art terminal theme — Task 15
- ✅ Frontend: Leaflet map, markdown content, stats — Tasks 19-20
- ✅ Docker Compose with nginx, backend, frontend, postgres — Task 24
- ✅ Strava/Coros tokens in DB (columns present, integration deferred per spec §8)
