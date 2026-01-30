# Migration Plan: Minecraft 1.20.1 Forge → 1.21.11 Fabric

This document outlines the requirements and steps needed to port the Trajectory Estimation mod from Minecraft 1.20.1 (Forge) to Minecraft 1.21.11 (Fabric Loader 0.18.4).

## Overview

| Current | Target |
|---------|--------|
| Minecraft 1.20.1 | Minecraft 1.21.11 |
| Forge 47.2.20 | Fabric Loader 0.18.4 |
| Java 17 | Java 21 |
| ForgeGradle 5.1.+ | Fabric Loom |

---

## Phase 1: Build System Migration

### Replace `build.gradle`

**Current (Forge):**
- Uses `net.minecraftforge.gradle` plugin
- `fg.deobf()` for dependency deobfuscation
- `mods.toml` for mod metadata

**Target (Fabric):**
- Use `fabric-loom` plugin
- Replace with Fabric Loom's remapping
- `fabric.mod.json` for mod metadata

### New Dependencies Required

```groovy
// build.gradle (Fabric)
plugins {
    id 'fabric-loom' version '1.9-SNAPSHOT'
}

dependencies {
    minecraft "com.mojang:minecraft:1.21.11"
    mappings "net.fabricmc:yarn:1.21.11+build.1:v2"
    modImplementation "net.fabricmc:fabric-loader:0.18.4"
    modImplementation "net.fabricmc.fabric-api:fabric-api:0.140.0+1.21.11"
}
```

### Create `fabric.mod.json`

Replace `src/main/resources/META-INF/mods.toml` with `fabric.mod.json`:

```json
{
  "schemaVersion": 1,
  "id": "trajectory_estimation",
  "version": "1.1.0",
  "name": "Trajectory Estimation",
  "environment": "client",
  "entrypoints": {
    "client": ["sonnenlichts.tje.TrajectoryEstimationClient"]
  },
  "depends": {
    "fabricloader": ">=0.18.4",
    "minecraft": "~1.21.11",
    "java": ">=21",
    "fabric-api": "*"
  }
}
```

---

## Phase 2: Core API Migrations

### 2.1 Mod Initialization

| Forge | Fabric |
|-------|--------|
| `@Mod` annotation | `ModInitializer` / `ClientModInitializer` interface |
| `FMLJavaModLoadingContext` | `onInitializeClient()` method |
| `MinecraftForge.EVENT_BUS` | Fabric API callbacks |

**Current:**
```java
@Mod(TrajectoryEstimation.MOD_ID)
public class TrajectoryEstimation {
    public TrajectoryEstimation() {
        MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
    }
}
```

**Target:**
```java
public class TrajectoryEstimationClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register callbacks here
    }
}
```

### 2.2 Event System

| Forge Event | Fabric Equivalent |
|-------------|-------------------|
| `RenderLevelStageEvent` | `WorldRenderEvents.AFTER_TRANSLUCENT` or `WorldRenderEvents.END` |
| `@SubscribeEvent` | Register callbacks via `WorldRenderEvents.END.register()` |
| `@Mod.EventBusSubscriber` | No equivalent - use direct registration |

**Rendering Event Migration:**
```java
// Forge
@SubscribeEvent
public void rendersWorldEvent(RenderLevelStageEvent event) {
    if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
    // ...
}

// Fabric
WorldRenderEvents.END.register((context) -> {
    // context.matrixStack() instead of event.getPoseStack()
    // context.world() for level access
    // ...
});
```

### 2.3 Configuration System

| Forge | Fabric |
|-------|--------|
| `ForgeConfigSpec` | Cloth Config API or custom JSON |
| `ModConfig.Type.CLIENT` | Custom implementation |
| Auto-sync on load | Manual file watching |

**Options:**
1. **Cloth Config API** - Most popular, provides GUI
2. **MidnightLib** - Lightweight alternative
3. **Custom JSON** - Using Gson, full control

**Recommendation:** Use Cloth Config API with Mod Menu integration for user-friendly settings.

---

## Phase 3: Rendering API Changes (1.20 → 1.21)

### 3.1 VertexConsumer Method Renames

These are breaking changes in Minecraft 1.21:

| Old (1.20) | New (1.21) |
|------------|------------|
| `vertex(x, y, z)` | `addVertex(x, y, z)` |
| `color(r, g, b, a)` | `setColor(r, g, b, a)` |
| `uv(u, v)` | `setUv(u, v)` |
| `overlayCoords(u, v)` | `setOverlay(u, v)` / `setUv1(u, v)` |
| `uv2(u, v)` | `setLight(u, v)` / `setUv2(u, v)` |
| `normal(x, y, z)` | `setNormal(x, y, z)` |
| `endVertex()` | **REMOVED** - no longer needed |

**Example Migration:**
```java
// Old (1.20)
builder.vertex(matrix, x, y, z)
    .color(r, g, b, a)
    .uv(u, v)
    .overlayCoords(OverlayTexture.NO_OVERLAY)
    .uv2(light)
    .normal(nx, ny, nz)
    .endVertex();

// New (1.21)
builder.addVertex(matrix, x, y, z)
    .setColor(r, g, b, a)
    .setUv(u, v)
    .setOverlay(OverlayTexture.NO_OVERLAY)
    .setLight(light)
    .setNormal(nx, ny, nz);
```

### 3.2 Model Rendering Changes

`Model#renderToBuffer` now takes an integer ARGB tint instead of four floats:

```java
// Old
model.renderToBuffer(poseStack, buffer, light, overlay, r, g, b, a);

// New
model.renderToBuffer(poseStack, buffer, light, overlay, 0xFFFFFFFF); // ARGB int
```

### 3.3 HUD Rendering

`HudRenderCallback` now passes `RenderTickCounter` instead of `tickDelta`:

```java
// Fabric 1.21
HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
    float tickDelta = renderTickCounter.getTickDelta(true);
    // ...
});
```

---

## Phase 4: Enchantment System Changes

Enchantments are now **data-driven** in 1.21. This affects trident Riptide detection:

**Current (1.20):**
```java
int j = EnchantmentHelper.getRiptide(itemStackUsing);
```

**Target (1.21):**
```java
// Use EnchantmentHelper with the new API
int j = EnchantmentHelper.getLevel(Enchantments.RIPTIDE, itemStackUsing);
// Or check via tags
boolean hasRiptide = EnchantmentHelper.hasAnyEnchantmentsIn(stack, EnchantmentTags.RIPTIDE);
```

---

## Phase 5: Mod Detection Migration

### Current Forge Approach
```java
ModList.get().isLoaded("modid")
```

### Fabric Equivalent
```java
FabricLoader.getInstance().isModLoaded("modid")
```

### Mod Compatibility Considerations

Many mods supported by the current version may not have Fabric ports for 1.21.11. Research needed:

| Mod | Fabric 1.21.11 Status | Notes |
|-----|----------------------|-------|
| MrCrayfish's Guns | Unknown | May need alternative |
| Ice and Fire | Unknown | Research Fabric port |
| Twilight Forest | Has Fabric version | Verify 1.21 support |
| L_Ender's Cataclysm | Unknown | Research needed |
| Blue Skies | Has Fabric version | Verify 1.21 support |
| Immersive Engineering | Forge only | May need to drop |
| Alex's Caves | Unknown | Research needed |
| The Bumblezone | Has Fabric version | Verify 1.21 support |
| The Aether | Has Fabric version | Verify 1.21 support |
| Alex's Mobs | Has Fabric version | Verify 1.21 support |
| Vampirism | Unknown | Research needed |
| L2Weaponry/L2Archery | Unknown | Research needed |
| Advent of Ascension | Unknown | Research needed |
| Arch Bows | Unknown | Research needed |

---

## Phase 6: File Structure Changes

### Data Pack Path Changes (1.21)

Tags directories now use **singular nouns**:

| Old (1.20) | New (1.21) |
|------------|------------|
| `tags/blocks/` | `tags/block/` |
| `tags/items/` | `tags/item/` |
| `tags/entity_types/` | `tags/entity_type/` |

### Resource Structure

```
src/main/resources/
├── fabric.mod.json              # NEW - replaces mods.toml
├── assets/trajectory_estimation/
│   ├── lang/
│   │   ├── en_us.json
│   │   └── zh_cn.json
│   └── textures/point/
│       └── 0.png
└── pack.mcmeta
```

---

## Phase 7: Implementation Checklist

### Build System
- [ ] Create new `build.gradle` with Fabric Loom
- [ ] Create `gradle.properties` with Fabric versions
- [ ] Create `fabric.mod.json`
- [ ] Remove `mods.toml`
- [ ] Update `.gitignore` for Fabric

### Core Mod
- [ ] Create `TrajectoryEstimationClient` implementing `ClientModInitializer`
- [ ] Remove Forge annotations (`@Mod`, `@Mod.EventBusSubscriber`)
- [ ] Migrate event registration to Fabric callbacks

### Rendering
- [ ] Update all `VertexConsumer` method calls (vertex→addVertex, etc.)
- [ ] Remove all `endVertex()` calls
- [ ] Update `RenderLevelStageEvent` to `WorldRenderEvents`
- [ ] Update `ModRenderType` for Fabric rendering API

### Configuration
- [ ] Add Cloth Config API dependency
- [ ] Migrate `ClientConfig` from ForgeConfigSpec to Cloth Config
- [ ] Add Mod Menu integration
- [ ] Update `TjeModConfig` runtime values

### Mod Compatibility
- [ ] Research each supported mod's Fabric availability
- [ ] Update mod detection to use `FabricLoader`
- [ ] Remove/update handlers for unavailable mods
- [ ] Test each remaining mod integration

### Testing
- [ ] Basic trajectory rendering (vanilla items)
- [ ] Sound alerts working
- [ ] Configuration GUI functional
- [ ] Each supported mod integration

---

## Estimated Scope

### High Effort
- Rendering API migration (VertexConsumer changes throughout)
- Configuration system rewrite (ForgeConfigSpec → Cloth Config)
- Mod compatibility research and updates

### Medium Effort
- Build system setup
- Event system migration
- Enchantment API updates

### Low Effort
- File structure changes
- Mod detection updates
- Resource file moves

---

## References

- [NeoForge 1.21 Migration Primer](https://docs.neoforged.net/primer/docs/1.21/)
- [Fabric Wiki](https://wiki.fabricmc.net/)
- [Fabric API GitHub](https://github.com/FabricMC/fabric)
- [Cloth Config](https://github.com/shedaniel/cloth-config)
- [1.20.5/6 → 1.21 Migration Gist](https://gist.github.com/ChampionAsh5357/d895a7b1a34341e19c80870720f9880f)
