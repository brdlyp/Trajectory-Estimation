# Trajectory Estimation

Minecraft Forge mod (1.20.1) that renders visual trajectory predictions for projectile weapons to help players aim.

## Build Commands

```bash
./gradlew runClient    # Run mod in development client
./gradlew runServer    # Run mod in development server
./gradlew build        # Build the mod JAR (output in build/libs/)
./gradlew clean        # Clean build artifacts
```

## Project Structure

```
src/main/java/sonnenlichts/tje/
├── TrajectoryEstimation.java       # Main entry point (@Mod annotation)
└── client/
    ├── event/
    │   ├── ClientRenderHandler.java  # Core rendering logic (~1084 lines)
    │   └── ModConfigHandler.java     # Config event handling
    ├── config/
    │   ├── ClientConfig.java         # Config spec definition (ForgeConfigSpec)
    │   ├── ConfigHolder.java         # Config holder singleton
    │   └── TjeModConfig.java         # Runtime config values
    ├── render/
    │   ├── ModRenderType.java        # Custom render type for trajectory
    │   └── gui/                      # Config GUI components
    ├── extra/                        # Mod-specific handlers (15 files)
    │   ├── MrcrayfishGunsExtra.java
    │   ├── IceAndFireExtra.java
    │   └── ...                       # One handler per supported mod
    └── util/
        ├── ModUtils.java             # Utilities (mod detection, rendering helpers)
        └── StringHelper.java         # String utilities
```

## Key Files

- **TrajectoryEstimation.java** - Mod initialization, event bus registration
- **ClientRenderHandler.java** - Main trajectory rendering logic, processes `RenderLevelStageEvent`
- **ClientConfig.java** - All configurable values (enable/disable per item, colors, sizes)
- **ModUtils.java** - Helper functions: `calculateShootVec()`, `drawCubeFullLight()`, mod detection

## Technologies

- Java 17
- Minecraft Forge 47.2.20 (MC 1.20.1)
- ForgeGradle 5.1.+

## Architecture Patterns

### Adding Support for a New Mod
1. Create a new `XxxExtra.java` file in `client/extra/`
2. Add mod detection method in `ModUtils.java` (e.g., `XxxLoaded()`)
3. Add item handling in `ClientRenderHandler.rendersWorldEvent()`
4. Add config options in `ClientConfig.java` and `TjeModConfig.java`

### Configuration Flow
1. Define config spec in `ClientConfig.java` using `ForgeConfigSpec.Builder`
2. Store runtime values in `TjeModConfig.java` static fields
3. Sync values in `ModConfigHandler.onLoad()`

### Rendering Flow
1. `RenderLevelStageEvent.AFTER_PARTICLES` triggers rendering
2. Detect held item and calculate trajectory physics (velocity, gravity)
3. Simulate projectile path over 500 ticks with gravity
4. Render cubes/lines at each position using `ModRenderType`
5. Play sound if trajectory intersects an entity

## Code Conventions

- Mod ID: `trajectory_estimation`
- Package: `sonnenlichts.tje`
- Each supported mod has its own `Extra` handler class
- Use `ModList.get().isLoaded("modid")` for mod detection
- Projectile physics: `y = y0 + vy*t - 0.5*gravity*t^2`
