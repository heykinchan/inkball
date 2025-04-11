
# Ink Ball Game

A Java-based arcade puzzle game developed using the **Processing** graphics library and managed by **Gradle**. This project was developed as part of the INFO1113 / COMP9003 assignment at the University of Sydney.

## 🎮 Game Objective

Direct coloured balls into matching holes using strategically drawn lines. Correct matches increase the score, while mismatches result in penalties. Win the game by successfully capturing all balls before time runs out.

---

## 📁 Project Structure

```
Ink ball/
├── build.gradle                          # Gradle build file
├── config.json                           # Game configuration
├── level1.txt, level2.txt, level3.txt    # Level layouts (18x18 grids)
├── src/
│   ├── main/
│   │   ├── java/inkball/
│   │   │   ├── App.java                # Game launcher and loop
│   │   │   ├── Ball.java               # Ball entity
│   │   │   ├── Hole.java               # Hole logic and attraction
│   │   │   ├── Wall.java               # Static and colour-changing walls
│   │   │   ├── GameObject.java         # Abstract parent class
│   │   │   ├── Point.java              # Used for drawing hitboxes
│   │   │   ├── Cell.java               # Grid layout cell handler
│   │   │   └── Spawner.java            # Spawner locations
│   │   └── resources/inkball/          # Sprites for walls, balls, holes
│   └── test/java/inkball/SampleTest.java  # Sample unit test
```

---

## 🚀 Getting Started

### Prerequisites

- Java 8
- Gradle
- Processing Core Library (`org.processing:core:3.3.7`)

### Running the Game

```bash
./gradlew run
```

Manual compilation:
```bash
javac -cp path_to_processing_core.jar src/main/java/inkball/*.java
java -cp .:path_to_processing_core.jar inkball.App
```

---

## ⚙️ Game Configuration

The `config.json` defines:

- **layout**: text file (18x18 grid) that maps entities (`X`, `S`, `H`, `B0`, etc.)
- **time**: time limit for each level
- **spawn_interval**: time between ball spawns
- **score modifiers**: values added/subtracted per capture or error

---

## 🎮 Gameplay Mechanics

- **Ball Movement**: Velocity (±2, ±2) per frame, changes on collisions.
- **Walls**:
  - `X` = Static wall (reflects balls)
  - `1–4` = Colour wall (reflects and changes colour)
- **Holes**: 2x2 areas that attract matching balls. Grey balls/hole are neutral.
- **Drawn Lines**:
  - Left-click: Draw
  - Right-click: Remove
  - Reflect balls once then disappear
- **Spawning**:
  - Spawners (`S`) release balls at `spawn_interval`
  - Balls can also appear directly via layout using `B0`, `B1`...

---

## 🧠 Player Controls

- `Left Click`: Draw line
- `Right Click`: Erase line
- `Spacebar`: Pause / unpause game
- `r`: Restart level or game

---

## 🧪 Testing & Coverage

Unit tests are in `src/test/`. Use JUnit and jacoco:

```bash
./gradlew test jacocoTestReport
```

Target >90% test coverage.

---

## 🧱 Extension Possibilities

You can add:

- Bricks that take 3 hits
- One-way or coloured walls
- Acceleration tiles
- Key wall / hole toggles

See assignment brief for full list of optional extensions (worth extra marks).

---

## ✅ Features Checklist (from Assignment Brief)

- [x] Ball & hole spawning
- [x] Line drawing and reflection
- [x] Score tracking
- [x] Timer with loss condition
- [x] Level transitions
- [x] Physics-based ball movement and reflection
- [x] Attraction into hole with size scaling
- [x] Functional config parsing
- [x] Pause & restart mechanics
- [x] Unit test coverage

---

## 📐 Design Overview

Object-oriented design with modular classes for each major game entity (Ball, Wall, Hole, etc.). The `App` class handles rendering, input, and game state control.

---

## 📸 Assets

Sprites are in `src/main/resources/inkball/` and mapped using filenames like:

- `ball0.png` to `ball4.png`
- `hole0.png` to `hole4.png`
- `wall0.png` to `wall4.png`
- `inkball_spritesheet.png`

Use `PApplet.loadImage` for rendering.

---

## 👨‍🏫 For Markers and Tutors

This project was implemented following the INFO1113 / COMP9003 Assignment Specifications. Please refer to:

- `config.json` and level files in root
- `src/` for game logic
- `build.gradle` for dependencies
- `SampleTest.java` for test setup

---

Enjoy guiding the ink balls home!
