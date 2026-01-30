package sonnenlichts.tje.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonnenlichts.tje.TrajectoryEstimationClient;
import sonnenlichts.tje.client.config.TjeModConfig;
import sonnenlichts.tje.client.render.ModRenderType;
import sonnenlichts.tje.client.util.ModUtils;
import sonnenlichts.tje.client.util.StringHelper;

import java.util.List;

public class ClientRenderHandler {
    private static final RenderType BUFFS = ModRenderType.cube(StringHelper.create("textures/point/0.png"));
    private int soundPlayCount = 0;
    public static final Logger LOGGER = LoggerFactory.getLogger(TrajectoryEstimationClient.MOD_ID);

    public void onWorldRender(WorldRenderContext context) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        Level pLevel = mc.level;
        if (player == null) return;
        if (pLevel == null) return;

        ItemStack itemStackUsing = player.getUseItem();
        ItemStack itemStack = ModUtils.getCorrectItem(player);
        PoseStack matrix = context.matrixStack();

        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        VertexConsumer builder = buffer.getBuffer(BUFFS);

        Vec3 viewPos = mc.getEntityRenderDispatcher().camera.getPosition();
        Vec3 originPos = new Vec3(player.getX(), player.getEyeY() - (double) 0.1F, player.getZ());

        // Bow handling
        if (ModUtils.isClassOrSuperClass(itemStackUsing.getItem(), BowItem.class)) {
            int remainTick = player.getUseItemRemainingTicks();
            int i = itemStackUsing.getItem().getUseDuration(itemStackUsing, player) - remainTick;
            float power = ModUtils.getPowerForTime(i);
            float pVelocity = power * 3.0F;
            float pInaccuracy = 0.0F;
            float gravity = 0.05F;
            Vec3 vec3 = ModUtils.calculateShootVec(player, pVelocity, pInaccuracy);
            this.renderTracePoint(vec3, originPos, viewPos, matrix, player, builder, gravity, itemStackUsing);
        }

        // Crossbow handling
        if (ModUtils.isClassOrSuperClass(itemStack.getItem(), CrossbowItem.class)) {
            ChargedProjectiles chargedProjectiles = itemStack.get(DataComponents.CHARGED_PROJECTILES);
            if (chargedProjectiles == null || chargedProjectiles.isEmpty()) return;

            float pProjectileAngle = 0;
            boolean hasFirework = chargedProjectiles.contains(Items.FIREWORK_ROCKET);
            float pVelocity = hasFirework ? 1.6F : 3.15F;
            float pInaccuracy = 0.0F;
            float gravity = 0.05F;
            Vec3 vec31 = player.getUpVector(1.0F);
            Vec3 vec32 = player.getViewVector(1.0F);

            List<ItemStack> projectiles = chargedProjectiles.getItems();
            for (int j = 0; j < projectiles.size(); ++j) {
                ItemStack itemstack = projectiles.get(j);
                if (!itemstack.isEmpty()) {
                    if (itemstack.is(Items.FIREWORK_ROCKET)) {
                        originPos = new Vec3(player.getX(), player.getEyeY() - (double) 0.15F, player.getZ());
                        gravity = 0;
                    }
                    switch (j) {
                        case 0 -> pProjectileAngle = 0.0F;
                        case 1 -> pProjectileAngle = -10.0F;
                        case 2 -> pProjectileAngle = 10.0F;
                    }
                    Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(pProjectileAngle * Mth.DEG_TO_RAD, vec31.x, vec31.y, vec31.z);
                    Vector3f vector3f = vec32.toVector3f().rotate(quaternionf);
                    Vec3 vec3 = ModUtils.calculateVec(player, pVelocity, pInaccuracy, new Vec3(vector3f.x(), vector3f.y(), vector3f.z()));
                    this.renderTracePoint(vec3, originPos, viewPos, matrix, player, builder, gravity, itemStack);
                }
            }
        }

        // Trident handling
        if (ModUtils.isClassOrSuperClass(itemStack.getItem(), TridentItem.class)) {
            int remainTick = player.getUseItemRemainingTicks();
            int i = itemStackUsing.getItem().getUseDuration(itemStackUsing, player) - remainTick;
            if (i < 10) return;

            // Check for Riptide enchantment using 1.21 API
            int riptideLevel = EnchantmentHelper.getItemEnchantmentLevel(
                    player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                            .getOrThrow(Enchantments.RIPTIDE),
                    itemStackUsing
            );
            if (!(riptideLevel <= 0 || player.isInWaterOrRain())) return;
            if (riptideLevel != 0) return;

            float pVelocity = 2.5F + (float) riptideLevel * 0.5F;
            float pInaccuracy = 0.0F;
            float gravity = 0.05F;
            Vec3 vec3 = ModUtils.calculateShootVec(player, pVelocity, pInaccuracy);
            this.renderTracePoint(vec3, originPos, viewPos, matrix, player, builder, gravity, itemStackUsing);
        }

        // Snowball handling
        if (itemStack.getItem() instanceof SnowballItem) {
            float pVelocity = 1.5F;
            float pInaccuracy = 0.0F;
            float gravity = 0.03F;
            Vec3 vec3 = ModUtils.calculateShootVec(player, pVelocity, pInaccuracy);
            this.renderTracePoint(vec3, originPos, viewPos, matrix, player, builder, gravity, itemStack);
        }

        // Egg handling
        if (itemStack.getItem() instanceof EggItem) {
            float pVelocity = 1.5F;
            float pInaccuracy = 0.0F;
            float gravity = 0.03F;
            Vec3 vec3 = ModUtils.calculateShootVec(player, pVelocity, pInaccuracy);
            this.renderTracePoint(vec3, originPos, viewPos, matrix, player, builder, gravity, itemStack);
        }

        // Experience bottle handling
        if (itemStack.getItem() instanceof ExperienceBottleItem) {
            float pVelocity = 0.7F;
            float pInaccuracy = 0.0F;
            float gravity = 0.07F;
            Vec3 vec3 = ModUtils.calculateShootVec(player, pVelocity, pInaccuracy, -20F);
            this.renderTracePoint(vec3, originPos, viewPos, matrix, player, builder, gravity, itemStack);
        }

        // Potion handling
        if (itemStack.getItem() instanceof ThrowablePotionItem) {
            float pVelocity = 0.5F;
            float pInaccuracy = 0.0F;
            float gravity = 0.03F;
            Vec3 vec3 = ModUtils.calculateShootVec(player, pVelocity, pInaccuracy);
            this.renderTracePoint(vec3, originPos, viewPos, matrix, player, builder, gravity, itemStack);
        }

        // Ender pearl handling
        if (itemStack.getItem() instanceof EnderpearlItem) {
            float pVelocity = 1.5F;
            float pInaccuracy = 0.0F;
            float gravity = 0.03F;
            Vec3 vec3 = ModUtils.calculateShootVec(player, pVelocity, pInaccuracy);
            this.renderTracePoint(vec3, originPos, viewPos, matrix, player, builder, gravity, itemStack);
        }

        buffer.endBatch(BUFFS);
    }

    private void renderTracePoint(Vec3 vec3, Vec3 origin, Vec3 view, PoseStack matrix, Player player, VertexConsumer builder, float gravity, ItemStack stack) {
        boolean renderPlane = true;
        float step = 0.7F, begin = 1, end = 500;

        double xo = 0, yo = 0, zo = 0;
        int count = 0, stp = 4;
        if (TjeModConfig.renderLine) {
            xo = origin.x + vec3.x * begin;
            yo = origin.y + vec3.y * begin - 0.5 * gravity * Math.pow(begin, 2);
            zo = origin.z + vec3.z * begin;
        }

        for (float t = begin; t < end; t += step) {
            int r = TjeModConfig.cubeRed, g = TjeModConfig.cubeGreen, b = TjeModConfig.cubeBlue, a = TjeModConfig.cubeAlpha;
            float hw = (float) TjeModConfig.cubeSize, h = (float) TjeModConfig.cubeSize;

            int lr = TjeModConfig.lineRed, lg = TjeModConfig.lineGreen, lb = TjeModConfig.lineBlue, la = TjeModConfig.lineAlpha;
            float lw = (float) TjeModConfig.lineWidth;

            double x = origin.x + vec3.x * t;
            double y = origin.y + vec3.y * t - 0.5 * gravity * Math.pow(t, 2);
            double z = origin.z + vec3.z * t;

            Vec3 pos = new Vec3(x, y, z);
            if (TjeModConfig.renderPoint) {
                if (ModUtils.isVanillaItems(stack)) {
                    matrix.pushPose();
                    matrix.translate(-view.x, -view.y, -view.z);
                    float minU = 0, maxU = 1, minV = 0, maxV = 1;
                    matrix.translate(x, y, z);
                    if (TjeModConfig.renderLine) {
                        ModUtils.drawLineFullLight(matrix, player, xo, yo, zo, x, y, z, count, stp, lr, lg, lb, la, lw);
                    }
                    if (TjeModConfig.renderCube) {
                        ModUtils.drawCubeFullLight(builder, matrix, 0, 0, 0, hw, h, minU, maxU, minV, maxV, r, g, b, a);
                    }
                    matrix.popPose();
                    if (renderPlane) {
                        if (!(player.level().getBlockState(ModUtils.getCorrectPos(player.level(), pos)).getBlock() instanceof AirBlock || player.level().getBlockState(ModUtils.getCorrectPos(player.level(), pos)).getBlock() instanceof LiquidBlock)) {
                            matrix.pushPose();
                            h = Mth.clamp(h * 0.8F, 0.01F, 10F);
                            hw = Mth.clamp(hw * 4F, 0.01F, 20F);
                            r = 255;
                            g = 0;
                            b = 0;
                            a = 60;
                            y += 0.3F;
                            matrix.translate(-view.x, -view.y, -view.z);
                            matrix.translate(x, y, z);
                            ModUtils.drawCubeFullLight(builder, matrix, 0, 0, 0, hw, h, minU, maxU, minV, maxV, r, g, b, a);
                            matrix.popPose();
                            renderPlane = false;
                        }
                    }

                    if (TjeModConfig.renderLine) {
                        if (count % stp == 0) {
                            xo = x;
                            yo = y;
                            zo = z;
                        }
                        count++;
                    }
                }
            }
            this.playHitSound(pos, player, stack);
        }
    }

    private void playHitSound(Vec3 pos, Player player, ItemStack stack) {
        if (!TjeModConfig.targetSound) return;
        if (ModUtils.isVanillaItemsSound(stack)) {
            List<LivingEntity> targets = ModUtils.checkEntityOnBlock(BlockPos.containing(pos), player.level(), 0, LivingEntity.class);
            targets.remove(player);
            for (Entity entity : targets) {
                if (entity instanceof LivingEntity entity2 && entity2 != Minecraft.getInstance().cameraEntity) {
                    if (this.soundPlayCount % 60 == 0) player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 2F);
                }
            }
            if (this.soundPlayCount % 60 == 0) this.soundPlayCount = 0;
            this.soundPlayCount++;
        }
    }
}
