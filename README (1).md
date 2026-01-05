# Java Chess Game (Console, OOP)

A console-based chess game implemented in Java, built with an object-oriented design and JSON persistence for accounts and saved games. The project focuses on clean modeling of the chess domain (board, pieces, moves, players) and robust validation through custom exceptions.

## Key features

- Object-oriented architecture using encapsulation, inheritance, and polymorphism
- Piece-specific movement logic (King, Queen, Rook, Bishop, Knight, Pawn)
- Move validation with clear error reporting via custom exceptions
- Console rendering of the board and interactive move input (e.g., `B2-B4`)
- JSON-based persistence for users and in-progress games (via `json-simple`)
- Simple computer opponent (random legal move selection)

## Project structure

```
SURSE/
  Main.java                   # Application entry point and menu flow
  Game.java                   # Game state, turns, and end-game handling
  Board.java                  # Board state, piece lookup, and move execution
  Player.java                 # Player info, captured pieces, scoring
  Piece.java                  # Abstract base class for pieces
  King.java, Queen.java, ...  # Piece implementations
  Position.java               # Board coordinates (A-H, 1-8)
  ChessPair.java              # Pair container used in board representation
  InvalidMoveException.java   # Thrown on illegal moves
  InvalidCommandException.java# Thrown on invalid commands/inputs
  input/
    accounts.json             # Accounts data
    games.json                # Saved games data
    JsonReaderUtil.java       # JSON parsing utilities
```

## How to run

### Option A: IntelliJ IDEA (recommended)

1. Open the `PROIECT/` directory as a project.
2. Ensure the `json-simple` library is available on the classpath.
3. Run `Main`.

### Option B: Command line

This project uses `org.json.simple.*`, so you must add `json-simple` to the classpath.

1. Compile:

```bash
javac -cp .:json-simple.jar SURSE/*.java SURSE/input/*.java
```

2. Run:

```bash
java -cp .:json-simple.jar:SURSE Main
```

On Windows, replace `:` with `;` in the `-cp` argument.

## Usage

- The program prints the current board state in the console.
- Moves are entered in the format `FROM-TO` (for example, `B2-B4`).
- Depending on the menu flow, you can start a new game, resume a saved game, or resign/exit.
- The computer opponent chooses a random piece and a random legal move from the available options.

Note: The UI/messages are primarily in Romanian (prompts and errors).

## Persistence (JSON)

- `SURSE/input/accounts.json` stores user accounts and accumulated points.
- `SURSE/input/games.json` stores in-progress games (pieces on board, current turn, history).

Parsing and loading are handled by `SURSE/input/JsonReaderUtil.java`.

## Design overview

- `Position` models coordinates and provides consistent comparisons/formatting.
- `Piece` is an abstract base class; each concrete piece implements its movement rules.
- `Board` is the authoritative source for piece placement and move application.
- `Game` coordinates turns, move history, and termination checks.
- `Main` handles IO, persistence, authentication flow, and user interaction.

## Requirements

- Java 8+ (Java 17+ recommended)
- `json-simple` available at runtime (`org.json.simple`)

## Author

Alesia-Raluca Năstase  
Faculty of Automatic Control and Computers, University POLITEHNICA of Bucharest  
Course: Object-Oriented Programming (POO)
