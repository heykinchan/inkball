
# Ink Ball Game

Ink Ball is a puzzle arcade-style game built using **Java** and the **Processing** graphics library. The player guides coloured balls into matching holes by placing walls and deflectors to control their movement.

## 🧠 Game Concept

- The player interacts with a 2D grid board.
- Balls spawn from entry points and must be guided into holes of matching colours.
- The game progresses through levels, each defined in a text file.
- Sprites and configuration data are used to render and control game logic.

---

## 📁 Project Structure

```
Ink ball/
├── build.gradle                # Gradle build configuration
├── config.json                # Configuration file for game settings
├── level1.txt                 # Level definitions
├── level2.txt
├── level3.txt
├── src/
│   ├── main/
│   │   ├── java/inkball/
│   │   │   ├── App.java              # Main game logic and launcher using Processing
│   │   │   ├── Ball.java             # Ball behaviour and properties
│   │   │   ├── Wall.java             # Wall logic
│   │   │   ├── Hole.java             # Hole logic
│   │   │   ├── Point.java            # Points for tracking ball movement
│   │   │   ├── GameObject.java       # Base class for all game objects
│   │   │   ├── Cell.java             # Grid cell and map layout
│   │   │   └── Spawner.java          # Handles ball spawning mechanics
│   │   └── resources/inkball/
│   │       └── *.png                 # Spritesheet and image assets
│   └── test/java/inkball/
│       └── SampleTest.java          # Test file (basic)
```

---

## 🚀 Getting Started

### Prerequisites

- Java 8 or higher
- Gradle (or use the Gradle wrapper)
- [Processing Core Library for Java](https://processing.org/)

Make sure `processing-core` is added as a dependency in `build.gradle`:

```groovy
dependencies {
    implementation 'org.processing:core:3.3.7'
}
```

### Running the Game

1. Clone or extract the project.
2. Make sure all dependencies are resolved via Gradle.
3. Launch the game from the `App.java` main class.

```bash
./gradlew run
```

Or if running manually:

```bash
javac -cp path_to_processing_core.jar src/main/java/inkball/*.java
java -cp .:path_to_processing_core.jar inkball.App
```

---

## ⚙️ Game Configuration

The file `config.json` contains runtime configurations such as:

- Level count
- Game speed (FPS)
- Parachute availability
- Sprite references

---

## 🧪 Testing

Basic unit test is included under `src/test/java/inkball/SampleTest.java`.

Run it using:

```bash
./gradlew test
```

---

## 📸 Assets

All sprites (balls, holes, walls, etc.) are located under:

```
src/main/resources/inkball/
```

These are loaded during the initial setup in `App.java`.

---

## 🎮 Controls & Gameplay

Controls and instructions are most likely implemented inside the `App` class and are displayed during gameplay. Walls and tools may be placed using the mouse.
