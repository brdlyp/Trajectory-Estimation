package sonnenlichts.tje.client.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.world.World;
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
                || (stack.getItem() instanceof EnderPearlItem && TjeModConfig.targetSoundEnderpearl);
    }

    public static boolean isVanillaItems(ItemStack stack) {
        return (stack.getItem() instanceof BowItem && TjeModConfig.renderBow)
                || (stack.getItem() instanceof TridentItem && TjeModConfig.renderTrident)
                || (stack.getItem() instanceof CrossbowItem && TjeModConfig.renderCrossbow)
                || (stack.getItem() instanceof SnowballItem && TjeModConfig.renderSnowball)
                || (stack.getItem() instanceof EggItem && TjeModConfig.renderEgg)
                || (stack.getItem() instanceof ExperienceBottleItem && TjeModConfig.renderExperienceBottle)
                || (stack.getItem() instanceof ThrowablePotionItem && TjeModConfig.renderSplashBottle)
                || (stack.getItem() instanceof EnderPearlItem && TjeModConfig.renderEnderpearl);
    }

    public static ItemStack getCorrectItem(PlayerEntity player) {
        Hand hand = player.getActiveHand();
        ItemStack mainStack = player.getStackInHand(Hand.MAIN_HAND);
        ItemStack offStack = player.getStackInHand(Hand.OFF_HAND);
        if (mainStack.getItem() instanceof ThrowablePotionItem
                || mainStack.getItem().getUseAction(mainStack) != UseAction.NONE
                || mainStack.getItem() instanceof EggItem
                || mainStack.getItem() instanceof SnowballItem
                || mainStack.getItem() instanceof ExperienceBottleItem
                || mainStack.getItem() instanceof EnderPearlItem
        ) {
            return mainStack;
        } else {
            if (offStack.getItem() instanceof ThrowablePotionItem
                    || offStack.getItem().getUseAction(offStack) != UseAction.NONE
                    || offStack.getItem() instanceof EggItem
                    || offStack.getItem() instanceof SnowballItem
                    || offStack.getItem() instanceof ExperienceBottleItem
                    || offStack.getItem() instanceof EnderPearlItem
            ) {
                return offStack;
            }
        }
        return player.getStackInHand(hand);
    }

    @SafeVarargs
    public static <T extends Entity> List<T> checkEntityOnBlock(BlockPos pos, World world, double inflate, Class<? extends T>... entities) {
        List<T> list = new ArrayList<>();
        for (Class<? extends T> classes : entities) {
            list.addAll(world.getEntitiesByClass(classes, new Box(pos).expand(inflate, inflate, inflate), EntityPredicates.EXCEPT_SPECTATOR));
        }
        return list;
    }

    public static BlockPos getCorrectPos(World world, Vec3d vec) {
        int i = MathHelper.floor(vec.x);
        int j = MathHelper.floor(vec.y - (double) (1.0E-5F));
        int k = MathHelper.floor(vec.z);
        BlockPos blockpos = new BlockPos(i, j, k);
        if (world.isAir(blockpos)) {
            BlockPos blockpos1 = blockpos.down();
            BlockState blockstate = world.getBlockState(blockpos1);
            if (blockstate.getCollisionShape(world, blockpos1).getMax(net.minecraft.util.math.Direction.Axis.Y) > 0) {
                return blockpos1;
            }
        }
        return blockpos;
    }

    /**
     * Line rendering has been significantly changed in 1.21.x.
     * This method is stubbed out for now - line effects are disabled.
     * TODO: Implement proper 1.21.x line rendering if needed.
     */
    public static void drawLineFullLight(MatrixStack matrix, PlayerEntity player, double xo, double yo, double zo, double x, double y, double z, int count, int stp, int lr, int lg, int lb, int la, float lw) {
        // Line rendering API removed in 1.21.x - method stubbed out
    }

    public static void drawCubeFullLight(VertexConsumer builder, MatrixStack matrix, double x, double y, double z, float hw, float h, float minU, float maxU, float minV, float maxV, int r, int g, int b, int a) {
        drawCubeFlat(builder, matrix, -hw + (float) x, h + (float) y, hw + (float) z, hw + (float) x, h + (float) y, -hw + (float) z, minU, minV, maxU, maxV, r, g, b, a, FULL_LIGHT);
        drawCubeFlat(builder, matrix, -hw + (float) x, -h + (float) y, -hw + (float) z, hw + (float) x, -h + (float) y, hw + (float) z, minU, minV, maxU, maxV, r, g, b, a, FULL_LIGHT);
        drawCubeFace(builder, matrix, hw + (float) x, -h + (float) y, -hw + (float) z, -hw + (float) x, h + (float) y, -hw + (float) z, minU, minV, maxU, maxV, r, g, b, a, FULL_LIGHT);
        drawCubeFace(builder, matrix, -hw + (float) x, -h + (float) y, -hw + (float) z, -hw + (float) x, h + (float) y, hw + (float) z, minU, minV, maxU, maxV, r, g, b, a, FULL_LIGHT);
        drawCubeFace(builder, matrix, -hw + (float) x, -h + (float) y, hw + (float) z, hw + (float) x, h + (float) y, hw + (float) z, minU, minV, maxU, maxV, r, g, b, a, FULL_LIGHT);
        drawCubeFace(builder, matrix, hw + (float) x, -h + (float) y, hw + (float) z, hw + (float) x, h + (float) y, -hw + (float) z, minU, minV, maxU, maxV, r, g, b, a, FULL_LIGHT);
    }

    public static void drawCubeFlat(VertexConsumer builder, MatrixStack matrixStackIn, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, int r, int g, int b, int a, int packedLight) {
        buildVertex(builder, matrixStackIn, x0, y0, z0, u0, v1, r, g, b, a, packedLight);
        buildVertex(builder, matrixStackIn, x1, y0, z0, u1, v1, r, g, b, a, packedLight);
        buildVertex(builder, matrixStackIn, x1, y1, z1, u1, v0, r, g, b, a, packedLight);
        buildVertex(builder, matrixStackIn, x0, y1, z1, u0, v0, r, g, b, a, packedLight);
    }

    public static void drawCubeFace(VertexConsumer builder, MatrixStack matrixStackIn, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, int r, int g, int b, int a, int packedLight) {
        buildVertex(builder, matrixStackIn, x0, y0, z0, u0, v1, r, g, b, a, packedLight);
        buildVertex(builder, matrixStackIn, x1, y0, z1, u1, v1, r, g, b, a, packedLight);
        buildVertex(builder, matrixStackIn, x1, y1, z1, u1, v0, r, g, b, a, packedLight);
        buildVertex(builder, matrixStackIn, x0, y1, z0, u0, v0, r, g, b, a, packedLight);
    }

    // Updated for 1.21 rendering API - removed endVertex()
    public static void buildVertex(VertexConsumer builder, MatrixStack matrixStackIn, float x, float y, float z, float u, float v, int r, int g, int b, int a, int packedLight) {
        builder.vertex(matrixStackIn.peek().getPositionMatrix(), x, y, z)
                .color(r, g, b, a)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(packedLight)
                .normal(matrixStackIn.peek(), 0.0F, 1.0F, 0.0F);
    }

    public static Vec3d calculateShootVec(PlayerEntity player, double pVelocity, double pInaccuracy) {
        float f = -MathHelper.sin(player.getYaw() * MathHelper.RADIANS_PER_DEGREE) * MathHelper.cos(player.getPitch() * MathHelper.RADIANS_PER_DEGREE);
        float f1 = -MathHelper.sin((player.getPitch()) * MathHelper.RADIANS_PER_DEGREE);
        float f2 = MathHelper.cos(player.getYaw() * MathHelper.RADIANS_PER_DEGREE) * MathHelper.cos(player.getPitch() * MathHelper.RADIANS_PER_DEGREE);
        return calculateVec(player, pVelocity, pInaccuracy, new Vec3d(f, f1, f2));
    }

    public static Vec3d calculateShootVec(PlayerEntity player, double pVelocity, double pInaccuracy, float offset) {
        float f = -MathHelper.sin(player.getYaw() * MathHelper.RADIANS_PER_DEGREE) * MathHelper.cos(player.getPitch() * MathHelper.RADIANS_PER_DEGREE);
        float f1 = -MathHelper.sin((player.getPitch() + offset) * MathHelper.RADIANS_PER_DEGREE);
        float f2 = MathHelper.cos(player.getYaw() * MathHelper.RADIANS_PER_DEGREE) * MathHelper.cos(player.getPitch() * MathHelper.RADIANS_PER_DEGREE);
        return calculateVec(player, pVelocity, pInaccuracy, new Vec3d(f, f1, f2));
    }

    public static Vec3d calculateVec(PlayerEntity player, double pVelocity, double pInaccuracy, Vec3d base) {
        double f = base.x;
        double f1 = base.y;
        double f2 = base.z;
        return (new Vec3d(f, f1, f2)).normalize().add(
                player.getRandom().nextTriangular(0.0D, 0.0172275D * pInaccuracy),
                player.getRandom().nextTriangular(0.0D, 0.0172275D * pInaccuracy),
                player.getRandom().nextTriangular(0.0D, 0.0172275D * pInaccuracy)).multiply(pVelocity);
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
