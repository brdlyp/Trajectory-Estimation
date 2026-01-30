# Fabric 1.21.11 Port - Task List

## Build System Setup
- [ ] Replace `build.gradle` with Fabric Loom configuration
- [ ] Update `gradle.properties` with Fabric/MC versions
- [ ] Create `fabric.mod.json` (replaces `mods.toml`)
- [ ] Add Fabric API dependency
- [ ] Add Cloth Config API dependency for configuration
- [ ] Add Mod Menu dependency for settings GUI
- [ ] Delete `src/main/resources/META-INF/mods.toml`
- [ ] Update `.gitignore` for Fabric project structure

## Core Mod Initialization
- [ ] Create `TrajectoryEstimationClient` implementing `ClientModInitializer`
- [ ] Remove `@Mod` annotation from `TrajectoryEstimation.java`
- [ ] Remove `@Mod.EventBusSubscriber` annotations
- [ ] Replace `FMLJavaModLoadingContext` with Fabric entrypoint

## Event System Migration
- [ ] Replace `RenderLevelStageEvent` with `WorldRenderEvents.END`
- [ ] Update `ClientRenderHandler` to use Fabric callback registration
- [ ] Remove all `@SubscribeEvent` annotations
- [ ] Update event parameter access (e.g., `event.getPoseStack()` → `context.matrixStack()`)

## Rendering API Updates (1.21 Breaking Changes)
- [ ] Replace all `vertex()` calls with `addVertex()`
- [ ] Replace all `color()` calls with `setColor()`
- [ ] Replace all `uv()` calls with `setUv()`
- [ ] Replace all `overlayCoords()` calls with `setOverlay()`
- [ ] Replace all `uv2()` calls with `setLight()`
- [ ] Replace all `normal()` calls with `setNormal()`
- [ ] Remove all `endVertex()` calls (no longer needed in 1.21)
- [ ] Update `ModRenderType.java` for Fabric rendering API

## Configuration System
- [ ] Remove `ForgeConfigSpec` usage from `ClientConfig.java`
- [ ] Implement Cloth Config API for all settings
- [ ] Create Mod Menu integration screen
- [ ] Migrate all config values (render toggles, colors, sizes)
- [ ] Update `TjeModConfig.java` to load from new config system
- [ ] Update `ConfigHolder.java` or replace with Cloth Config holder

## Enchantment API Updates
- [ ] Update Riptide enchantment check in trident handling
- [ ] Replace `EnchantmentHelper.getRiptide()` with new 1.21 API
- [ ] Verify other enchantment checks still work

## Mod Detection Updates
- [ ] Replace `ModList.get().isLoaded()` with `FabricLoader.getInstance().isModLoaded()`
- [ ] Update all mod detection methods in `ModUtils.java`

## Mod Compatibility Research
- [ ] Check Ice and Fire Fabric 1.21.11 availability
- [ ] Check Twilight Forest Fabric 1.21.11 availability
- [ ] Check Blue Skies Fabric 1.21.11 availability
- [ ] Check Alex's Caves Fabric 1.21.11 availability
- [ ] Check The Bumblezone Fabric 1.21.11 availability
- [ ] Check The Aether Fabric 1.21.11 availability
- [ ] Check Alex's Mobs Fabric 1.21.11 availability
- [ ] Check Vampirism Fabric 1.21.11 availability
- [ ] Check L2Weaponry/L2Archery Fabric availability
- [ ] Check Advent of Ascension Fabric availability
- [ ] Check Arch Bows Fabric availability
- [ ] Remove/disable handlers for unavailable mods

## Extra Handlers Updates
For each available mod, update the corresponding `*Extra.java` file:
- [ ] Update item class references if changed
- [ ] Update method calls for 1.21 API changes
- [ ] Test projectile velocity/gravity values still accurate

## Resource Files
- [ ] Update `pack.mcmeta` format version for 1.21
- [ ] Verify language files load correctly
- [ ] Verify textures load correctly

## Testing
- [ ] Vanilla bow trajectory renders correctly
- [ ] Vanilla crossbow trajectory renders correctly
- [ ] Vanilla trident trajectory renders correctly
- [ ] Throwables (snowball, egg, ender pearl) work correctly
- [ ] Potions trajectory renders correctly
- [ ] Target hit sound plays when aiming at entities
- [ ] Configuration GUI opens and saves settings
- [ ] Cube rendering mode works
- [ ] Line rendering mode works
- [ ] Color customization works
- [ ] Each supported mod's weapons render trajectories

## Final Cleanup
- [ ] Remove any remaining Forge-specific code
- [ ] Remove unused imports
- [ ] Test full build with `./gradlew build`
- [ ] Test in-game with `./gradlew runClient`
