package sonnenlichts.tje.client.event;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.block.AirBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
    private static final RenderLayer BUFFS = ModRenderType.cube(StringHelper.create("textures/point/0.png"));
    private int soundPlayCount = 0;
    public static final Logger LOGGER = LoggerFactory.getLogger(TrajectoryEstimationClient.MOD_ID);

    public void onWorldRender(LevelRenderContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        World world = mc.world;
        if (player == null) return;
        if (world == null) return;

        ItemStack itemStackUsing = player.getActiveItem();
        ItemStack itemStack = ModUtils.getCorrectItem(player);
        MatrixStack matrix = new MatrixStack();
        matrix.multiplyPositionMatrix(context.poseStack().peek().getPositionMatrix());

        VertexConsumerProvider.Immediate buffer = mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer builder = buffer.getBuffer(BUFFS);

        Vec3d viewPos = mc.getEntityRenderDispatcher().camera.getCameraPos();
        Vec3d originPos = new Vec3d(player.getX(), player.getEyeY() - (double) 0.1F, player.getZ());

        // Bow handling
        if (ModUtils.isClassOrSuperClass(itemStackUsing.getItem(), BowItem.class)) {
            int remainTick = player.getItemUseTimeLeft();
            int i = itemStackUsing.getItem().getMaxUseTime(itemStackUsing, player) - remainTick;
            float power = ModUtils.getPowerForTime(i);
            float pVelocity = power * 3.0F;
            float pInaccuracy = 0.0F;
            float gravity = 0.05F;
            Vec3d vec3 = ModUtils.calculateShootVec(player, pVelocity, pInaccuracy);
            this.renderTracePoint(vec3, originPos, viewPos, matrix, player, builder, gravity, itemStackUsing, world);
        }

        // Crossbow handling
        if (ModUtils.isClassOrSuperClass(itemStack.getItem(), CrossbowItem.class)) {
            ChargedProjectilesComponent chargedProjectiles = itemStack.get(DataComponentTypes.CHARGED_PROJECTILES);
            if (chargedProjectiles == null || chargedProjectiles.isEmpty()) return;

            float pProjectileAngle = 0;
            boolean hasFirework = chargedProjectiles.contains(Items.FIREWORK_ROCKET);
            float pVelocity = hasFirework ? 1.6F : 3.15F;
            float pInaccuracy = 0.0F;
            float gravity = 0.05F;
            Vec3d vec31 = player.getRotationVec(1.0F);
            Vec3d vec32 = player.getRotationVec(1.0F);

            List<ItemStack> projectiles = chargedProjectiles.getProjectiles();
            for (int j = 0; j < projectiles.size(); ++j) {
                ItemStack itemstack = projectiles.get(j);
                if (!itemstack.isEmpty()) {
                    if (itemstack.isOf(Items.FIREWORK_ROCKET)) {
                        originPos = new Vec3d(player.getX(), player.getEyeY() - (double) 0.15F, player.getZ());
                        gravity = 0;
                    }
                    switch (j) {
                        case 0 -> pProjectileAngle = 0.0F;
                        case 1 -> pProjectileAngle = -10.0F;
                        case 2 -> pProjectileAngle = 10.0F;
                    }
                    Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(pProjectileAngle * MathHelper.RADIANS_PER_DEGREE, (float) vec31.x, (float) vec31.y, (float) vec31.z);
                    Vector3f vector3f = vec32.toVector3f().rotate(quaternionf);
                    Vec3d vec3 = ModUtils.calculateVec(player, pVelocity, pInaccuracy, new Vec3d(vector3f.x(), vector3f.y(), vector3f.z()));
                    this.renderTracePoint(vec3, originPos, viewPos, matrix, player, builder, gravity, itemStack, world);
                }
            }
        }

        // Trident handling
        if (ModUtils.isClassOrSuperClass(itemStack.getItem(), TridentItem.class)) {
            int remainTick = player.getItemUseTimeLeft();
            int i = itemStackUsing.getItem().getMaxUseTime(itemStackUsing, player) - remainTick;
            if (i < 10) return;

            // Check for Riptide enchantment using 1.21 API
            int riptideLevel = EnchantmentHelper.getLevel(
                    world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT)
                            .getEntry(Enchantments.RIPTIDE).orElse(null),
                    itemStackUsing
            );
            if (!(riptideLevel <= 0 || player.isTouchingWaterOrRain())) return;
            if (riptideLevel != 0) return;

            float pVelocity = 2.5F + (float) riptideLevel * 0.5F;
            float pInaccuracy = 0.0F;
            float gravity = 0.05F;
            Vec3d vec3 = ModUtils.calculateShootVec(player, pVelocity, pInaccuracy);
            this.renderTracePoint(vec3, originPos, viewPos, matrix, player, builder, gravity, itemStackUsing, world);
        }

        // Snowball handling
        if (itemStack.getItem() instanceof SnowballItem) {
            float pVelocity = 1.5F;
            float pInaccuracy = 0.0F;
            float gravity = 0.03F;
            Vec3d vec3 = ModUtils.calculateShootVec(player, pVelocity, pInaccuracy);
            this.renderTracePoint(vec3, originPos, viewPos, matrix, player, builder, gravity, itemStack, world);
        }

        // Egg handling
        if (itemStack.getItem() instanceof EggItem) {
            float pVelocity = 1.5F;
            float pInaccuracy = 0.0F;
            float gravity = 0.03F;
            Vec3d vec3 = ModUtils.calculateShootVec(player, pVelocity, pInaccuracy);
            this.renderTracePoint(vec3, originPos, viewPos, matrix, player, builder, gravity, itemStack, world);
        }

        // Experience bottle handling
        if (itemStack.getItem() instanceof ExperienceBottleItem) {
            float pVelocity = 0.7F;
            float pInaccuracy = 0.0F;
            float gravity = 0.07F;
            Vec3d vec3 = ModUtils.calculateShootVec(player, pVelocity, pInaccuracy, -20F);
            this.renderTracePoint(vec3, originPos, viewPos, matrix, player, builder, gravity, itemStack, world);
        }

        // Potion handling
        if (itemStack.getItem() instanceof ThrowablePotionItem) {
            float pVelocity = 0.5F;
            float pInaccuracy = 0.0F;
            float gravity = 0.03F;
            Vec3d vec3 = ModUtils.calculateShootVec(player, pVelocity, pInaccuracy);
            this.renderTracePoint(vec3, originPos, viewPos, matrix, player, builder, gravity, itemStack, world);
        }

        // Ender pearl handling
        if (itemStack.getItem() instanceof EnderPearlItem) {
            float pVelocity = 1.5F;
            float pInaccuracy = 0.0F;
            float gravity = 0.03F;
            Vec3d vec3 = ModUtils.calculateShootVec(player, pVelocity, pInaccuracy);
            this.renderTracePoint(vec3, originPos, viewPos, matrix, player, builder, gravity, itemStack, world);
        }

        buffer.draw(BUFFS);
    }

    private void renderTracePoint(Vec3d vec3, Vec3d origin, Vec3d view, MatrixStack matrix, PlayerEntity player, VertexConsumer builder, float gravity, ItemStack stack, World world) {
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

            Vec3d pos = new Vec3d(x, y, z);
            if (TjeModConfig.renderPoint) {
                if (ModUtils.isVanillaItems(stack)) {
                    matrix.push();
                    matrix.translate(-view.x, -view.y, -view.z);
                    float minU = 0, maxU = 1, minV = 0, maxV = 1;
                    matrix.translate(x, y, z);
                    if (TjeModConfig.renderLine) {
                        ModUtils.drawLineFullLight(matrix, player, xo, yo, zo, x, y, z, count, stp, lr, lg, lb, la, lw);
                    }
                    if (TjeModConfig.renderCube) {
                        ModUtils.drawCubeFullLight(builder, matrix, 0, 0, 0, hw, h, minU, maxU, minV, maxV, r, g, b, a);
                    }
                    matrix.pop();
                    if (renderPlane) {
                        if (!(world.getBlockState(ModUtils.getCorrectPos(world, pos)).getBlock() instanceof AirBlock || world.getBlockState(ModUtils.getCorrectPos(world, pos)).getBlock() instanceof FluidBlock)) {
                            matrix.push();
                            h = MathHelper.clamp(h * 0.8F, 0.01F, 10F);
                            hw = MathHelper.clamp(hw * 4F, 0.01F, 20F);
                            r = 255;
                            g = 0;
                            b = 0;
                            a = 60;
                            y += 0.3F;
                            matrix.translate(-view.x, -view.y, -view.z);
                            matrix.translate(x, y, z);
                            ModUtils.drawCubeFullLight(builder, matrix, 0, 0, 0, hw, h, minU, maxU, minV, maxV, r, g, b, a);
                            matrix.pop();
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
            this.playHitSound(pos, player, stack, world);
        }
    }

    private void playHitSound(Vec3d pos, PlayerEntity player, ItemStack stack, World world) {
        if (!TjeModConfig.targetSound) return;
        if (ModUtils.isVanillaItemsSound(stack)) {
            List<LivingEntity> targets = ModUtils.checkEntityOnBlock(BlockPos.ofFloored(pos), world, 0, LivingEntity.class);
            targets.remove(player);
            for (Entity entity : targets) {
                if (entity instanceof LivingEntity entity2 && entity2 != MinecraftClient.getInstance().getCameraEntity()) {
                    if (this.soundPlayCount % 60 == 0) player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 2F);
                }
            }
            if (this.soundPlayCount % 60 == 0) this.soundPlayCount = 0;
            this.soundPlayCount++;
        }
    }
}
