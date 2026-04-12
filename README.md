# Château Combo — Simulator

A Monte-Carlo simulator for the board game **Château Combo**, written in Kotlin. It runs thousands of games between configurable AI strategies and produces a statistical dashboard to compare card performance, effect value, and strategy win rates.

## Tech stack

- **Kotlin 2.3 / Java 25 / Maven**
- **JUnit Jupiter 6 + AssertJ + MockK** for tests
- **Jackson 3** for JSON card definitions
- **Chart.js** (CDN) for the dashboard — no build step required

## Quick start

```bash
# Run 10 000 simulated games (default)
mvn exec:java -Dexec.mainClass=com.chateaucombo.MainKt

# Run with a custom number of games
mvn exec:java -Dexec.mainClass=com.chateaucombo.MainKt -Dexec.args="50000"

# Open the dashboard
cd stats && python3 -m http.server 8080
# → http://localhost:8080/players.html
```

Results are written to a timestamped directory under `stats/` and indexed in `stats/runs.json`.

## The game

Château Combo is a 9-turn card-placement game for 2–4 players.

Each player builds a **3×3 tableau** by buying cards from two shared decks:

| Deck       | Card type | Theme                     |
|------------|-----------|---------------------------|
| Châtelains | Nobility  | High-cost, strong effects |
| Villageois | Commoners | Low-cost, synergy-based   |

**Resources**
- **Or (gold)** — spent to buy cards; replenished by card effects
- **Clés (keys)** — spent to switch or refresh the active deck; also score 1 pt each at end of game

**Each turn**, a player may:
1. Spend a key to switch deck or refresh available cards
2. Shift their entire tableau one cell in any free direction (HAUT / BAS / GAUCHE / DROITE)
3. Buy one of the 3 visible cards (or take a free *Carte Verso* for +6 or and +2 clés)
4. Place the card on an empty cell adjacent to an existing one; on-placement effects fire immediately

**Scoring** (end of game, per player):
1. Fill bourse cards with remaining gold
2. Evaluate each card's `effetScore`
3. Bourse cards score `orDepose × 2`
4. Keys score 1 pt each

## Cards

78 unique cards are defined as JSON files in `src/main/resources/cartes/`. Each card has:
- A **type** (`Chatelain` or `Villageois`)
- A **cost** (0–7 or)
- One or more **blasons** (NOBLE, MILITAIRE, RELIGIEUX, ERUDIT, ARTISAN, PAYSAN)
- Optional **on-placement effects** (fire when the card is placed)
- Optional **passive effects** (reduce purchase cost of a card type)
- An optional **end-game score effect**
- An optional **bourse** (vault that stores gold for end-game scoring)

A few examples:

| Card       | Type       | Cost | On placement                    | End-game score                                           |
|------------|------------|------|---------------------------------|----------------------------------------------------------|
| Banquière  | Châtelain  | 7    | +3 clés **or** +2 or in bourses | Points = sum of gold deposited in all bourses            |
| Baron      | Châtelain  | 3    | — (reduces card costs)          | 10 pts if no PAYSAN blason on the board                  |
| Mercenaire | Villageois | 6    | +1 or per distinct blason       | 7 pts per complete set of RELIGIEUX + MILITAIRE + PAYSAN |
| Philosophe | Villageois | 2    | — (reduces Châtelain cost)      | 10 pts if no MILITAIRE blason on the board               |

## Strategies

Four strategies are implemented, each implementing the `Strategie` interface:

| Strategy              | Description                                                                                                                                                                                                                                              |
|-----------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `StrategieAleatoire`  | Fully random — random action, random card, random position                                                                                                                                                                                               |
| `StrategieGourmande`  | Greedy — each turn picks the card × position that maximises the marginal score gain (including board synergies and bourse value)                                                                                                                         |
| `StrategiePrevoyante` | Greedy + foresighted — same marginal evaluation as *Gourmande*, plus: evaluates all 4 displacement directions before choosing, reserves at most half of available gold for bourse filling, and scores `PointsParOrDepose` effects at theoretical maximum |

`Main.kt` pits two `StrategieAleatoire` players against one `StrategieGourmande` and one `StrategiePrevoyante`.

## Dashboard

Three static HTML pages read the JSON output and render Chart.js visualisations.

| Page           | Content                                                                                       |
|----------------|-----------------------------------------------------------------------------------------------|
| `players.html` | Summary stats (avg / Q1 / median / Q3) per strategy; average card score per turn (line chart) |
| `cartes.html`  | Card ranking by average score contribution; player score impact vs global average             |
| `effets.html`  | On-placement and end-game effect stats, toggleable by player score or card score metric       |

A run selector at the top of each page lets you compare across simulation runs.

## Project structure

```
src/main/kotlin/com/chateaucombo/
├── ChateauCombo.kt          # 9-turn game loop
├── Main.kt                  # Entry point, writes stats JSON
├── ReglesDuJeu.kt           # Game constants
├── deck/                    # Deck management + card model
│   └── carte/effet/         # Effect implementations
├── joueur/                  # Player state and repository
├── score/                   # End-of-game scoring
├── strategie/               # AI strategies
├── tableau/                 # 3×3 board + position logic
└── simulation/              # N-game runner + stats aggregation

src/main/resources/cartes/   # 78 card definitions (JSON)
stats/                       # Dashboard HTML + simulation output
```

## Running tests

```bash
JAVA_HOME=/home/thibaut-perrouin/.jdks/ms-25.0.2 \
  /home/thibaut-perrouin/.m2/wrapper/dists/apache-maven-3.9.14-bin/1cb7fhup6b5n3bed6kckbrnspv/apache-maven-3.9.14/bin/mvn test
```

Effect tests live in `src/test/kotlin/com/chateaucombo/deck/carte/effet/`, one file per effect type. Strategy tests live in `src/test/kotlin/com/chateaucombo/joueur/strategie/`.
