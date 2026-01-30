package sonnenlichts.tje.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import sonnenlichts.tje.client.config.TjeModConfig;

import java.util.ArrayList;
import java.util.List;

public class ModUtils {
    private static final int FULL_LIGHT = 15728880;

    // Fabric mod detection methods
    public static boolean CGMLoaded() {
        return FabricLoader.getInstance().isModLoaded("cgm");
    }

    public static boolean CataclysmLoaded() {
        return FabricLoader.getInstance().isModLoaded("cataclysm");
    }

    public static boolean IceAndFireLoaded() {
        return FabricLoader.getInstance().isModLoaded("iceandfire");
    }

    public static boolean BlueSkiesLoaded() {
        return FabricLoader.getInstance().isModLoaded("blue_skies");
    }

    public static boolean TwilightForestLoaded() {
        return FabricLoader.getInstance().isModLoaded("twilightforest");
    }

    public static boolean ImmersiveEngineeringLoaded() {
        return FabricLoader.getInstance().isModLoaded("immersiveengineering");
    }

    public static boolean AlexCavesLoaded() {
        return FabricLoader.getInstance().isModLoaded("alexscaves");
    }

    public static boolean TheBumblezoneLoaded() {
        return FabricLoader.getInstance().isModLoaded("the_bumblezone");
    }

    public static boolean AetherLoaded() {
        return FabricLoader.getInstance().isModLoaded("aether");
    }

    public static boolean AlexsMobsLoaded() {
        return FabricLoader.getInstance().isModLoaded("alexsmobs");
    }

    public static boolean ArchBowsLoaded() {
        return FabricLoader.getInstance().isModLoaded("archbows");
    }

    public static boolean VampirismLoaded() {
        return FabricLoader.getInstance().isModLoaded("vampirism");
    }

    public static boolean L2WeaponryLoaded() {
        return FabricLoader.getInstance().isModLoaded("l2weaponry");
    }

    public static boolean L2ArcheryLoaded() {
        return FabricLoader.getInstance().isModLoaded("l2archery");
    }

    public static boolean AdventOfAscension3Loaded() {
        return FabricLoader.getInstance().isModLoaded("aoa3");
    }

    public static boolean isVanillaItemsSound(ItemStack stack) {
        return (ModUtils.isClassOrSuperClass(stack.getItem(), BowItem.class) && TjeModConfig.targetSoundBow)
                || (ModUtils.isClassOrSuperClass(stack.getItem(), TridentItem.class) && TjeModConfig.targetSoundTrident)
                || (ModUtils.isClassOrSuperClass(stack.getItem(), CrossbowItem.class) && TjeModConfig.targetSoundCrossbow)
                || (stack.getItem() instanceof SnowballItem && TjeModConfig.targetSoundSnowball)
                || (stack.getItem() instanceof EggItem && TjeModConfig.targetSoundEgg)
                || (stack.getItem() instanceof ExperienceBottleItem && TjeModConfig.targetSoundExperienceBottle)
                || (stack.getItem() instanceof ThrowablePotionItem && TjeModConfig.targetSoundSplashBottle)
                || (stack.getItem() instanceof EnderpearlItem && TjeModConfig.targetSoundEnderpearl);
    }

    public static boolean isVanillaItems(ItemStack stack) {
        return (stack.getItem() instanceof BowItem && TjeModConfig.renderBow)
                || (stack.getItem() instanceof TridentItem && TjeModConfig.renderTrident)
                || (stack.getItem() instanceof CrossbowItem && TjeModConfig.renderCrossbow)
                || (stack.getItem() instanceof SnowballItem && TjeModConfig.renderSnowball)
                || (stack.getItem() instanceof EggItem && TjeModConfig.renderEgg)
                || (stack.getItem() instanceof ExperienceBottleItem && TjeModConfig.renderExperienceBottle)
                || (stack.getItem() instanceof ThrowablePotionItem && TjeModConfig.renderSplashBottle)
                || (stack.getItem() instanceof EnderpearlItem && TjeModConfig.renderEnderpearl);
    }

    public static ItemStack getCorrectItem(Player player) {
        InteractionHand hand = player.getUsedItemHand();
        ItemStack mainStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offStack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (mainStack.getItem() instanceof ThrowablePotionItem
                || mainStack.getItem().getUseAnimation(mainStack) != UseAnim.NONE
                || mainStack.getItem() instanceof EggItem
                || mainStack.getItem() instanceof SnowballItem
                || mainStack.getItem() instanceof ExperienceBottleItem
                || mainStack.getItem() instanceof EnderpearlItem
        ) {
            return mainStack;
        } else {
            if (offStack.getItem() instanceof ThrowablePotionItem
                    || offStack.getItem().getUseAnimation(offStack) != UseAnim.NONE
                    || offStack.getItem() instanceof EggItem
                    || offStack.getItem() instanceof SnowballItem
                    || offStack.getItem() instanceof ExperienceBottleItem
                    || offStack.getItem() instanceof EnderpearlItem
            ) {
                return offStack;
            }
        }
        return player.getItemInHand(hand);
    }

    @SafeVarargs
    public static <T extends Entity> List<T> checkEntityOnBlock(BlockPos pos, Level level, double inflate, Class<? extends T>... entities) {
        List<T> list = new ArrayList<>();
        for (Class<? extends T> classes : entities) {
            list.addAll(level.getEntitiesOfClass(classes, new AABB(pos).inflate(inflate, inflate, inflate), EntitySelector.NO_SPECTATORS));
        }
        return list;
    }

    public static BlockPos getCorrectPos(Level level, Vec3 vec) {
        int i = Mth.floor(vec.x);
        int j = Mth.floor(vec.y - (double) (1.0E-5F));
        int k = Mth.floor(vec.z);
        BlockPos blockpos = new BlockPos(i, j, k);
        if (level.isEmptyBlock(blockpos)) {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = level.getBlockState(blockpos1);
            if (blockstate.collisionExtendsVertically(level, blockpos1, null)) {
                return blockpos1;
            }
        }
        return blockpos;
    }

    public static void drawLineFullLight(PoseStack matrix, Player player, double xo, double yo, double zo, double x, double y, double z, int count, int stp, int lr, int lg, int lb, int la, float lw) {
        PoseStack.Pose entry = matrix.last();
        float changeX = (float) (xo - x);
        float changeY = (float) (yo - y);
        float changeZ = (float) (zo - z);
        if (!(changeX == 0 && changeY == 0 && changeZ == 0) && count % stp == 0) {
            float factor = 2F;
            float amplitude = Mth.clamp((factor - player.tickCount - Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true)) / factor, 0F, 1F);
            RenderSystem.depthMask(false);
            RenderSystem.disableCull();
            RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
            RenderSystem.lineWidth(lw);
            float rd = 2F;
            for (int p = 0; p < rd; ++p)
                drawLineVertex(changeX, changeY, changeZ, bufferbuilder, entry, p / rd, (p + 1) / rd, amplitude, lr, lg, lb, la);
            BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
            RenderSystem.lineWidth(1.0F);
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
        }
    }

    private static void drawLineVertex(float x, float y, float z, BufferBuilder buffer, PoseStack.Pose normal, float t, float t1, float amplitude, int r, int g, int b, int a) {
        float px = (float) (x * t + amplitude * Math.sin(t * 2F * Math.PI));
        float py = y * t + 0.25F;
        float pz = z * t;
        float nx = (float) (x * t1 + amplitude * Math.sin(t1 * 2F * Math.PI)) - px;
        float ny = y * t1 + 0.25F - py;
        float nz = z * t1 - pz;
        float s = Mth.sqrt(nx * nx + ny * ny + nz * nz);
        nx /= s;
        ny /= s;
        nz /= s;
        // Updated for 1.21 rendering API
        buffer.addVertex(normal.pose(), px, py, pz)
                .setColor(r, g, b, a)
                .setNormal(normal, nx, ny, nz);
    }

    public static void drawCubeFullLight(VertexConsumer builder, PoseStack matrix, double x, double y, double z, float hw, float h, float minU, float maxU, float minV, float maxV, int r, int g, int b, int a) {
        drawCubeFlat(builder, matrix, -hw + (float) x, h + (float) y, hw + (float) z, hw + (float) x, h + (float) y, -hw + (float) z, minU, minV, maxU, maxV, r, g, b, a, FULL_LIGHT);
        drawCubeFlat(builder, matrix, -hw + (float) x, -h + (float) y, -hw + (float) z, hw + (float) x, -h + (float) y, hw + (float) z, minU, minV, maxU, maxV, r, g, b, a, FULL_LIGHT);
        drawCubeFace(builder, matrix, hw + (float) x, -h + (float) y, -hw + (float) z, -hw + (float) x, h + (float) y, -hw + (float) z, minU, minV, maxU, maxV, r, g, b, a, FULL_LIGHT);
        drawCubeFace(builder, matrix, -hw + (float) x, -h + (float) y, -hw + (float) z, -hw + (float) x, h + (float) y, hw + (float) z, minU, minV, maxU, maxV, r, g, b, a, FULL_LIGHT);
        drawCubeFace(builder, matrix, -hw + (float) x, -h + (float) y, hw + (float) z, hw + (float) x, h + (float) y, hw + (float) z, minU, minV, maxU, maxV, r, g, b, a, FULL_LIGHT);
        drawCubeFace(builder, matrix, hw + (float) x, -h + (float) y, hw + (float) z, hw + (float) x, h + (float) y, -hw + (float) z, minU, minV, maxU, maxV, r, g, b, a, FULL_LIGHT);
    }

    public static void drawCubeFlat(VertexConsumer builder, PoseStack matrixStackIn, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, int r, int g, int b, int a, int packedLight) {
        buildVertex(builder, matrixStackIn, x0, y0, z0, u0, v1, r, g, b, a, packedLight);
        buildVertex(builder, matrixStackIn, x1, y0, z0, u1, v1, r, g, b, a, packedLight);
        buildVertex(builder, matrixStackIn, x1, y1, z1, u1, v0, r, g, b, a, packedLight);
        buildVertex(builder, matrixStackIn, x0, y1, z1, u0, v0, r, g, b, a, packedLight);
    }

    public static void drawCubeFace(VertexConsumer builder, PoseStack matrixStackIn, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, int r, int g, int b, int a, int packedLight) {
        buildVertex(builder, matrixStackIn, x0, y0, z0, u0, v1, r, g, b, a, packedLight);
        buildVertex(builder, matrixStackIn, x1, y0, z1, u1, v1, r, g, b, a, packedLight);
        buildVertex(builder, matrixStackIn, x1, y1, z1, u1, v0, r, g, b, a, packedLight);
        buildVertex(builder, matrixStackIn, x0, y1, z0, u0, v0, r, g, b, a, packedLight);
    }

    // Updated for 1.21 rendering API - removed endVertex()
    public static void buildVertex(VertexConsumer builder, PoseStack matrixStackIn, float x, float y, float z, float u, float v, int r, int g, int b, int a, int packedLight) {
        builder.addVertex(matrixStackIn.last().pose(), x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(matrixStackIn.last(), 0.0F, 1.0F, 0.0F);
    }

    public static Vec3 calculateShootVec(Player player, double pVelocity, double pInaccuracy) {
        float f = -Mth.sin(player.getYRot() * Mth.DEG_TO_RAD) * Mth.cos(player.getXRot() * Mth.DEG_TO_RAD);
        float f1 = -Mth.sin((player.getXRot()) * Mth.DEG_TO_RAD);
        float f2 = Mth.cos(player.getYRot() * Mth.DEG_TO_RAD) * Mth.cos(player.getXRot() * Mth.DEG_TO_RAD);
        return calculateVec(player, pVelocity, pInaccuracy, new Vec3(f, f1, f2));
    }

    public static Vec3 calculateShootVec(Player player, double pVelocity, double pInaccuracy, float offset) {
        float f = -Mth.sin(player.getYRot() * Mth.DEG_TO_RAD) * Mth.cos(player.getXRot() * Mth.DEG_TO_RAD);
        float f1 = -Mth.sin((player.getXRot() + offset) * Mth.DEG_TO_RAD);
        float f2 = Mth.cos(player.getYRot() * Mth.DEG_TO_RAD) * Mth.cos(player.getXRot() * Mth.DEG_TO_RAD);
        return calculateVec(player, pVelocity, pInaccuracy, new Vec3(f, f1, f2));
    }

    public static Vec3 calculateVec(Player player, double pVelocity, double pInaccuracy, Vec3 base) {
        double f = base.x;
        double f1 = base.y;
        double f2 = base.z;
        return (new Vec3(f, f1, f2)).normalize().add(
                player.getRandom().triangle(0.0D, 0.0172275D * pInaccuracy),
                player.getRandom().triangle(0.0D, 0.0172275D * pInaccuracy),
                player.getRandom().triangle(0.0D, 0.0172275D * pInaccuracy)).scale(pVelocity);
    }

    public static float getPowerForTime(int releasingTime) {
        float f = (float) releasingTime / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    public static <T, S> boolean isClassOrSuperClass(T instance, Class<S> target) {
        return instance.getClass().isAssignableFrom(target);
    }
}
