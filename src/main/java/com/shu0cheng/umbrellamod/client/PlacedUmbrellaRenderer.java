package com.shu0cheng.umbrellamod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.shu0cheng.umbrellamod.block.PlacedUmbrellaBlock;
import com.shu0cheng.umbrellamod.block.PlacedUmbrellaBlockEntity;
import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import com.shu0cheng.umbrellamod.item.UmbrellaState;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import com.shu0cheng.umbrellamod.registry.ModComponents;
import com.shu0cheng.umbrellamod.registry.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class PlacedUmbrellaRenderer implements BlockEntityRenderer<PlacedUmbrellaBlockEntity> {
    private static final Map<String, ItemStack> STACKS = new HashMap<>();
    private static final Map<String, PlacedGeoRenderer> MODELS = new HashMap<>();

    public PlacedUmbrellaRenderer(BlockEntityRendererProvider.Context context) {
    }

    private static String modelKey(UmbrellaType type, boolean inverted) {
        return type.id() + "|" + inverted;
    }

    private static PlacedGeoRenderer model(UmbrellaType type, boolean inverted) {
        return MODELS.computeIfAbsent(modelKey(type, inverted),
                k -> new PlacedGeoRenderer(type.id() + "_umbrella", inverted));
    }

    private static double centerX(UmbrellaType type) {
        return switch (type) {
            case TELESCOPIC -> -6.0D;
            case STRAIGHT -> -6.0D;
            case OIL_PAPER -> -8.0D;
        };
    }

    private static double centerZ(UmbrellaType type) {
        return switch (type) {
            case TELESCOPIC -> 6.18D;
            case STRAIGHT -> 6.13D;
            case OIL_PAPER -> 5.83D;
        };
    }

    private static double minY(UmbrellaType type) {
        return switch (type) {
            case TELESCOPIC -> -0.4D;
            case STRAIGHT -> 0.1D;
            case OIL_PAPER -> 0.05D;
        };
    }

    private static double maxY(UmbrellaType type) {
        return switch (type) {
            case TELESCOPIC -> 45.57D;
            case STRAIGHT -> 44.44D;
            case OIL_PAPER -> 34.1D;
        };
    }

    @Override
    public void render(PlacedUmbrellaBlockEntity be, float partialTick, PoseStack pose,
                       MultiBufferSource buffer, int light, int overlay) {
        BlockState state = be.getBlockState();
        if (!(state.getBlock() instanceof PlacedUmbrellaBlock block)) return;
        UmbrellaType type = block.umbrellaType();
        int rotation = state.getValue(PlacedUmbrellaBlock.ROTATION);
        boolean open = state.getValue(PlacedUmbrellaBlock.OPEN);
        boolean inverted = state.getValue(PlacedUmbrellaBlock.INVERTED);
        boolean half = state.getValue(PlacedUmbrellaBlock.HALF);
        boolean leaning = state.getValue(PlacedUmbrellaBlock.LEANING);

        String key = type.id() + "|" + open;
        ItemStack stack = STACKS.computeIfAbsent(key, k -> {
            ItemStack st = new ItemStack(ModItems.itemFor(type));
            st.set(ModComponents.UMBRELLA_STATE.get(), new UmbrellaState(open, 0L));
            return st;
        });
        UmbrellaItem item = (UmbrellaItem) stack.getItem();

        RandomSource random = RandomSource.create(be.getBlockPos().asLong());
        double randomX = inverted ? (random.nextFloat() - 0.5F) * 0.22D : 0.0D;
        double randomZ = inverted ? (random.nextFloat() - 0.5F) * 0.22D : 0.0D;
        double randomY = inverted ? (random.nextFloat() - 0.5F) * 0.14D : 0.0D;

        pose.pushPose();
        pose.translate(0.5D + randomX, (half ? -0.5D : 0.0D) + randomY, 0.5D + randomZ);
        UmbrellaItem.STATIC_RENDER.set(true);
        pose.mulPose(Axis.YP.rotationDegrees(rotation * 22.5F + ((rotation == 2 || rotation == 4 || rotation == 10 || rotation == 12) ? 180.0F : 0.0F)));

        float scale = type == UmbrellaType.OIL_PAPER ? 1.2F : 1.0F;
        PlacedUmbrellaDefaults.T dev;
        if (inverted && UmbrellaBlockDevConfig.has(type, true, true, half, false, "inverted")) {
            UmbrellaBlockDevConfig.T d = UmbrellaBlockDevConfig.get(type, true, true, half, false, "inverted");
            dev = new PlacedUmbrellaDefaults.T(d.x(), d.y(), d.z(), d.yaw(), d.pitch(), d.roll(), d.scale());
        } else {
            dev = PlacedUmbrellaDefaults.get(type, open, inverted, half, leaning,
                    (rotation % 4 == 0) ? "cardinal" : "diagonal");
        }
        pose.translate(dev.x, dev.y, dev.z);
        pose.mulPose(Axis.YP.rotationDegrees(dev.yaw));
        pose.mulPose(Axis.XP.rotationDegrees(dev.pitch));
        pose.mulPose(Axis.ZP.rotationDegrees(dev.roll));
        scale *= dev.scale;
        if (!inverted) {
            // Upright only: from the player's placement direction,
            // 9 px down, 14 px forward, 6 px left.
            pose.mulPose(Axis.YP.rotationDegrees(-90.0F));
            pose.translate(-6.0D / 16.0D, -9.0D / 16.0D, -14.0D / 16.0D);
        }
        pose.scale(scale, scale, scale);
        if (open) {
            model(type, inverted).renderInWorld(pose, item, stack, buffer, light, overlay);
        } else {
            UmbrellaClient.renderer(type.id() + "_umbrella")
                    .renderInWorld(pose, item, stack, buffer, light, overlay);
        }
        pose.popPose();
        UmbrellaItem.STATIC_RENDER.set(false);
    }

    @Override
    public boolean shouldRender(PlacedUmbrellaBlockEntity be, Vec3 cameraPos) {
        return be.getBlockPos().distToCenterSqr(cameraPos.x, cameraPos.y, cameraPos.z) < 24.0D * 24.0D;
    }
}
