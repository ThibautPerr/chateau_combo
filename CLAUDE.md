# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
mvn clean package

# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=ChateauComboTest

# Compile only
mvn clean compile
```

## Conventions

- Domain code (variable names, method names, class names, comments) is written in **French**.
- Git commit messages are written in **English**.
- Every new feature must be covered by tests. Effect tests live in `src/test/kotlin/com/chateaucombo/effet/EffetTest.kt`, one `@Nested` inner class per effect type. Passive effects that modify purchase behaviour are tested in `src/test/kotlin/com/chateaucombo/joueur/JoueurRepositoryTest.kt`.
- **Always run the full test suite before committing.** Use `JAVA_HOME=/home/thibaut-perrouin/.jdks/ms-25.0.2 /home/thibaut-perrouin/.m2/wrapper/dists/apache-maven-3.9.14-bin/1cb7fhup6b5n3bed6kckbrnspv/apache-maven-3.9.14/bin/mvn test` and fix all failures before staging a commit.

## Architecture

Kotlin + Maven board game simulator for **Château Combo** (a card placement game). Java 25, Kotlin 2.3.

### Package structure

```
com.chateaucombo/
├── ChateauCombo.kt        # Main game orchestrator (9-turn loop)
├── deck/
│   ├── model/             # Carte, Deck, Blason, Chatelain, Villageois, CarteVerso
│   └── repository/        # DeckRepository – load JSON, shuffle, draw cards
├── joueur/
│   ├── model/             # Joueur – player state (gold/or, keys/clé, tableau ref, score)
│   └── repository/        # JoueurRepository – card selection, resource updates
├── tableau/
│   ├── model/             # Tableau, Position (3x3 enum), PositionHorizontale, PositionVerticale, CartePositionee
│   └── repository/        # TableauRepository – grid placement, adjacency
├── score/
│   └── ScoreRepository.kt # End-of-game scoring (fills bourses then calls score effects)
└── effet/
    └── model/             # Effet interface, EffetContext, EffetScore, EffetPassif, concrete impls
```

Card definitions live in `src/main/resources/cartes/` as JSON files (one file per card).

### Card JSON structure

```json
{
  "type": "Chatelain" | "Villageois",
  "nom": "Nom de la carte",
  "cout": 0–7,
  "blasons": ["NOBLE" | "MILITAIRE" | "RELIGIEUX" | "ERUDIT" | "ARTISAN" | "PAYSAN"],
  "effets": {
    "effets": [ /* list of effect objects — omitted or empty array if no effects */ ],
    "effetsPassifs": [ /* list of passive effect objects — omitted if none */ ],
    "separateur": "ET" | "OU"   /* only present when there are multiple effects */
  },
  "effetScore": { "type": "...", /* fields */ },  /* omitted if no score effect */
  "bourse": { "taille": 5 }                       /* omitted if the card is not a bourse */
}
```

`blasons` can contain duplicates (e.g. two `"ARTISAN"` entries). `effets` can be an empty object `{}` when the card has no effects. `effetScore` defaults to `EffetScoreVide` when absent. `bourse` is a plain object (not polymorphic) used by `ScoreRepository` to deposit gold at end of game.

**Effect object shapes** (discriminated by `"type"`):

| type | extra fields |
|---|---|
| `AjouteCle` | `cle: Int` |
| `AjouteCleParBlasonAbsent` | _(none)_ |
| `AjouteCleParBlasonDansTableauVoisin` | `blason: Blason` |
| `AjouteCleParChatelainDansTableauVoisin` | _(none)_ |
| `AjouteCleParBlasonDistinct` | _(none)_ |
| `AjouteCleParChatelain` | _(none)_ |
| `AjouteCleParVillageois` | _(none)_ |
| `AjouteCleParCarteAvecNbBlason` | `nbBlason: Int` |
| `AjouteCleParCarteBourse` | _(none)_ — 1 clé per `BourseScore` card already on the board |
| `AjouteClePourChaqueBlason` | `blason: String`, `cleParBlason: Int` |
| `AjouteClePourTousLesJoueurs` | `cle: Int` |
| `AjouteClePourTousLesAdversaires` | `cle: Int` |
| `AjouteOrParChatelain` | _(none)_ |
| `AjouteOrParVillageois` | _(none)_ |
| `AjouteOrParBlasonDistinct` | _(none)_ |
| `AjouteOrParBlasonDansTableauVoisin` | `blason: Blason` |
| `AjouteOrParCarteAvecLeCout` | `orParCarte: Int`, `cout: String` |
| `AjouteOrParCartePositionee` | _(none)_ |
| `AjouteOrParEmplacementVide` | _(none)_ |
| `AjouteOrPourChaqueBlason` | `orParBlason: Int`, `blason: String` |
| `AjouteOrPourTousLesAdversaires` | `or: Int` |
| `AjouteOrEnDefaussantUnVillageois` | _(none)_ |
| `AjouteOrEnDefaussantUnChatelain` | _(none)_ |
| `AjouteCleEnDefaussantUnVillageois` | _(none)_ |
| `RemplitBourses` | `nb: Int` — fills the `nb` largest `BourseScore` cards on the board to capacity (sets `orDepose = taille`) |
| `AjouteOrDansBourses` | `or: Int` — adds up to `or` gold to every non-full bourse (capped at each bourse's remaining capacity) |

**Score effects** (`effetScore` field on `Carte` — evaluated once at end of game via `ScoreRepository.compteLeScore`; defaults to `EffetScoreVide` (0 pts)); receive a `ScoreContext(joueurActuel, joueurs, cartePositionee)` — note: no `decks` field unlike `EffetContext`):

| type | extra fields |
|---|---|
| `EffetScoreVide` | _(none)_ — 0 points |
| `AjoutePoints` | `points: Int` |
| `PointsParOrDepose` | _(none)_ — scores the sum of `orDepose` across all `BourseScore` cards on the player's board |
| `PointsSiRangSuperieur` | `points: Int` — scores `points` if the card is in the top vertical row |
| `PointsSiRangMilieu` | `points: Int` — scores `points` if the card is in the middle vertical row |
| `PointsSiRangInferieur` | `points: Int` — scores `points` if the card is in the bottom vertical row |
| `PointsSiColonneGauche` | `points: Int` — scores `points` if the card is in the left column |
| `PointsSiColonneMilieu` | `points: Int` — scores `points` if the card is in the middle column |
| `PointsSiColonneDroite` | `points: Int` — scores `points` if the card is in the right column |
| `PointsSiBord` | `points: Int` — scores `points` if the card is in an outer-middle position (HAUTMILIEU, MILIEUGAUCHE, MILIEUDROITE, BASMILIEU) |
| `PointsSiCoin` | `points: Int` — scores `points` if the card is in a corner position (HAUTGAUCHE, HAUTDROITE, BASGAUCHE, BASDROITE) |
| `PointsSiBlasonAbsent` | `points: Int`, `blason: Blason` — scores `points` if no card on the player's board has the given blason |
| `PointsParCle` | _(none)_ — scores 1 point per key (`clé`) the player has at end of game |
| `PointsParCarteAvecReductionDeCout` | `points: Int` — scores `points` per card on the board that has at least one `ReduceCoutChatelain` or `ReduceCoutVillageois` passive effect |
| `PointsParCarteAvecCoutMinimum` | `points: Int`, `coutMinimum: Int` — scores `points` per card on the board whose printed `cout` is ≥ `coutMinimum` |
| `PointsSiCarteVersoPresente` | `points: Int` — scores `points` if at least one `CarteVerso` is on the player's board |
| `PointsParCarteAvecNbBlasonMinimum` | `points: Int`, `nbBlasonMinimum: Int` — scores `points` per card whose total blason count (including duplicates) is ≥ `nbBlasonMinimum` |
| `PointsParCarteAvecCoutExact` | `points: Int`, `cout: Int` — scores `points` per card on the board whose printed `cout` equals `cout` exactly |
| `PointsParBlasonDansLaRangee` | `points: Int`, `blason: Blason` — scores `points` per occurrence of `blason` across all cards in the same horizontal row as this card (duplicates on one card count separately) |
| `PointsParBlasonDansLaColonne` | `points: Int`, `blason: Blason` — scores `points` per occurrence of `blason` across all cards in the same vertical column as this card (duplicates on one card count separately) |
| `PointsParBlasonDansLaRangeeEtLaColonne` | `points: Int`, `blason: Blason` — scores `points` per occurrence of `blason` across all cards in the same row OR column (cross/+ shape); the card itself is counted once even though it belongs to both |
| `PointsParTripleVillageois` | `points: Int` — scores `points` for every complete group of 3 Villageois cards on the board (`nbVillageois / 3 * points`) |
| `PointsParBlasonDistinct` | `points: Int` — scores `points` per distinct blason type present across all cards on the board (max 6) |
| `PointsParChatelain` | `points: Int` — scores `points` per Chatelain card on the board |
| `PointsParVillageois` | `points: Int` — scores `points` per Villageois card on the board |
| `PointsParBlasonDistinctDansLaColonne` | `points: Int` — scores `points` per distinct blason type across all cards in the same vertical column as this card (the card itself is included) |
| `PointsParBlasonDistinctDansLaRangee` | `points: Int` — scores `points` per distinct blason type across all cards in the same horizontal row as this card (the card itself is included) |
| `PointsParTripleBlason` | `points: Int` — scores `points` for every complete group of 3 occurrences of the same blason across the board (counted independently per blason type) |
| `PointsParGroupeDeBlasons` | `points: Int`, `blasons: List<Blason>` — scores `points` × min(count(b) for b in blasons): the number of complete sets where each listed blason appears at least that many times |
| `PointsParBlasonManquant` | `points: Int` — scores `points` per distinct blason type absent from the board (max 6) |
| `PointsParPaireVillageoisChatelain` | `points: Int` — scores `points` × min(nb Villageois, nb Chatelains) on the board |

**`BourseScore`** is NOT an `EffetScore` — it is a plain `data class(val taille: Int)` stored in `Carte.bourse`. It holds a mutable `orDepose: Int = 0` (body property, invisible to data class equality). Scoring for bourses is handled entirely by `ScoreRepository` (see Scoring section below), not through the `effetScore` mechanism.

**Passive effects** (`effetsPassifs` field — applied at purchase time, not on placement):

| type | effect |
|---|---|
| `ReduceCoutVillageois` | buying a villageois costs 1 less gold (min 0); stacks |
| `ReduceCoutChatelain` | buying a chatelain costs 1 less gold (min 0); stacks |

### Core game flow

1. Two decks are initialized (Chatelains + Villageois cards loaded from JSON).
2. Each of 9 turns: players select an affordable card (or a free "Verso"), place it on their 3×3 tableau, then card effects fire.
3. Effects receive an `EffetContext` (current player, all players, card played, available decks) and mutate player resources.

### Effect system

`Effet` is a sealed polymorphic interface deserialized via Jackson `@JsonTypeInfo`/`@JsonSubTypes` (Jackson 3, `tools.jackson` packages). Concrete implementations follow the naming pattern `Ajoute{Resource}{Scope}` (e.g. `AjouteCleParCarteChatelain`). Multiple effects on a card are combined with a `separateur`: `ET` (all fire in order) or `OU` (one chosen randomly).

### Deck management

`DeckRepository` manages two cycling decks. Players see the first 3 available cards. When the main deck empties, the discard pile (`defausse`) is reshuffled. Players can spend keys (`clés`) to refresh or swap the active deck.

### Board (Tableau)

`Position` is a 3×3 enum that knows its adjacent positions. The first card always goes to center; subsequent cards go to an adjacent empty position (chosen randomly by default). Cards can shift within the grid when adjacency constraints apply.

### Scoring

`ScoreRepository.compteLeScore` runs at end of game per player in three steps:
1. `remplitLesBourses` — fills each `BourseScore` card with gold from `joueur.or` (in board order, capped at remaining capacity).
2. `updateScoreWithEffects` — sums `effetScore.score(context)` for every card. Cards without an explicit `effetScore` use `EffetScoreVide` (0 pts). Bourse cards are not involved here.
3. `updateScoreWithBourses` — adds `orDepose * 2` per `BourseScore` card to the player's score.

### Testing stack

JUnit Jupiter 6 + AssertJ + MockK. The Surefire plugin is configured with `--add-opens=java.base/java.lang=ALL-UNNAMED` and `-Dnet.bytebuddy.experimental=true` for MockK compatibility — do not remove these JVM args.
