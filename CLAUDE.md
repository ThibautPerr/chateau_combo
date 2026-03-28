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

## Architecture

Kotlin + Maven board game simulator for **Château Combo** (a card placement game). Java 17, Kotlin 1.9.25.

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

Card definitions live in `src/main/resources/cartes/` as JSON files.

### Core game flow

1. Two decks are initialized (Chatelains + Villageois cards loaded from JSON).
2. Each of 9 turns: players select an affordable card (or a free "Verso"), place it on their 3×3 tableau, then card effects fire.
3. Effects receive an `EffetContext` (current player, all players, card played, available decks) and mutate player resources.

### Effect system

`Effet` is a sealed polymorphic interface deserialized via Jackson `@JsonTypeInfo`/`@JsonSubTypes`. Concrete implementations follow the naming pattern `Ajoute{Resource}{Scope}` (e.g. `AjouteCleParCarteChatelain`). Multiple effects on a card are combined with a `separateur`: `ET` (all fire in order) or `OU` (one chosen randomly).

### Deck management

`DeckRepository` manages two cycling decks. Players see the first 3 available cards. When the main deck empties, the discard pile (`defausse`) is reshuffled. Players can spend keys (`clés`) to refresh or swap the active deck.

### Board (Tableau)

`Position` is a 3×3 enum that knows its adjacent positions. The first card always goes to center; subsequent cards go to an adjacent empty position (chosen randomly by default). Cards can shift within the grid when adjacency constraints apply.

### Testing stack

JUnit Jupiter 5 + AssertJ + MockK. The Surefire plugin is configured with `--add-opens=java.base/java.lang=ALL-UNNAMED` and `-Dnet.bytebuddy.experimental=true` for MockK compatibility — do not remove these JVM args.
