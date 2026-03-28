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
- Every new feature must be covered by tests. Effect tests live in `src/test/kotlin/com/chateaucombo/effet/EffetTest.kt`, one `@Nested` inner class per effect type.

## Architecture

Kotlin + Maven board game simulator for **Château Combo** (a card placement game). Java 25, Kotlin 2.3.

### Package structure

```
com.chateaucombo/
├── ChateauCombo.kt        # Main game orchestrator (9-turn loop)
├── deck/
│   ├── model/             # Carte, Deck, Blason, Chatelain, Villageois
│   └── repository/        # DeckRepository – load JSON, shuffle, draw cards
├── joueur/
│   ├── model/             # Joueur – player state (gold/or, keys/clé, tableau ref)
│   └── repository/        # JoueurRepository – card selection, resource updates
├── tableau/
│   ├── model/             # Tableau, Position (3x3 enum), CartePositionee
│   └── repository/        # TableauRepository – grid placement, adjacency
└── effet/
    └── model/             # Effet interface, EffetContext, concrete effect impls
```

Card definitions live in `src/main/resources/cartes/` as JSON files (one file per card).

### Card JSON structure

```json
{
  "type": "CHATELAIN" | "VILLAGEOIS",
  "nom": "Nom de la carte",
  "cout": 0–7,
  "blasons": ["NOBLE" | "MILITAIRE" | "RELIGIEUX" | "ERUDIT" | "ARTISAN" | "PAYSAN"],
  "effets": {
    "effets": [ /* list of effect objects — omitted or empty array if no effects */ ],
    "separateur": "ET" | "OU"   /* only present when there are multiple effects */
  }
}
```

`blasons` can contain duplicates (e.g. two `"ARTISAN"` entries). `effets` can be an empty object `{}` when the card has no effects.

**Effect object shapes** (discriminated by `"type"`):

| type | extra fields |
|---|---|
| `AjouteCle` | `cle: Int` |
| `AjouteCleParChatelain` | _(none)_ |
| `AjouteCleParVillageois` | _(none)_ |
| `AjouteCleParCarteAvecNbBlason` | `nbBlason: Int` |
| `AjouteClePourChaqueBlason` | `blason: String`, `cleParBlason: Int` |
| `AjouteClePourTousLesJoueurs` | `cle: Int` |
| `AjouteClePourTousLesAdversaires` | `cle: Int` |
| `AjouteOrParChatelain` | _(none)_ |
| `AjouteOrParVillageois` | _(none)_ |
| `AjouteOrParCarteAvecLeCout` | `orParCarte: Int`, `cout: String` |
| `AjouteOrPourChaqueBlason` | `orParBlason: Int`, `blason: String` |
| `AjouteOrPourTousLesAdversaires` | `or: Int` |

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

### Testing stack

JUnit Jupiter 6 + AssertJ + MockK. The Surefire plugin is configured with `--add-opens=java.base/java.lang=ALL-UNNAMED` and `-Dnet.bytebuddy.experimental=true` for MockK compatibility — do not remove these JVM args.
