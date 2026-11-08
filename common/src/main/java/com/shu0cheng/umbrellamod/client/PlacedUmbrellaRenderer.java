package com.shu0cheng.umbrellamod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.shu0cheng.umbrellamod.block.PlacedUmbrellaBlock;
import com.shu0cheng.umbrellamod.block.PlacedUmbrellaBlockEntity;
import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import com.shu0cheng.umbrellamod.item.UmbrellaState;
import com.shu0cheng.umbrellamod.item.UmbrellaTransform;
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

import java.util.EnumMap;
import java.util.Map;

public class PlacedUmbrellaRenderer implements BlockEntityRenderer<PlacedUmbrellaBlockEntity> {
    private final Map<UmbrellaType, ItemStack> openStacks = new EnumMap<>(UmbrellaType.class);
    private final Map<UmbrellaType, ItemStack> closedStacks = new EnumMap<>(UmbrellaType.class);
    private final Map<UmbrellaType, PlacedGeoRenderer> uprightModels = new EnumMap<>(UmbrellaType.class);
    private final Map<UmbrellaType, PlacedGeoRenderer> invertedModels = new EnumMap<>(UmbrellaType.class);

    public PlacedUmbrellaRenderer(BlockEntityRendererProvider.Context context) {}

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

        ItemStack stack = (open ? openStacks : closedStacks).computeIfAbsent(type, key -> {
            ItemStack result = new ItemStack(ModItems.itemFor(key));
            result.set(ModComponents.UMBRELLA_STATE.get(), new UmbrellaState(open, -1L));
            return result;
        });
        UmbrellaItem item = (UmbrellaItem) stack.getItem();
        UmbrellaTransform transform = UmbrellaBlockDevConfig.get(type, open, inverted, half, leaning,
                inverted ? "inverted" : UmbrellaBlockDevConfig.variant(rotation));

        double x = 0.5;
        double y = half ? -0.5 : 0.0;
        double z = 0.5;
        if (inverted) {
            RandomSource random = RandomSource.create(be.getBlockPos().asLong());
            x += (random.nextFloat() - 0.5F) * 0.22;
            z += (random.nextFloat() - 0.5F) * 0.22;
            y += (random.nextFloat() - 0.5F) * 0.14;
        }

        pose.pushPose();
        try {
            pose.translate(x, y, z);
            float yaw = rotation * 22.5F;
            if (rotation == 2 || rotation == 4 || rotation == 10 || rotation == 12) yaw += 180.0F;
            pose.mulPose(Axis.YP.rotationDegrees(yaw));
            pose.translate(transform.x(), transform.y(), transform.z());
            pose.mulPose(Axis.YP.rotationDegrees(transform.yaw()));
            pose.mulPose(Axis.XP.rotationDegrees(transform.pitch()));
            pose.mulPose(Axis.ZP.rotationDegrees(transform.roll()));
            if (!inverted) {
                pose.mulPose(Axis.YP.rotationDegrees(-90.0F));
                pose.translate(-6.0 / 16.0, -9.0 / 16.0, -14.0 / 16.0);
            }
            float scale = transform.scale() * (type == UmbrellaType.OIL_PAPER ? 1.2F : 1.0F);
            pose.scale(scale, scale, scale);
            if (open) {
                (inverted ? invertedModels : uprightModels)
                        .computeIfAbsent(type, key -> new PlacedGeoRenderer(key.id() + "_umbrella", inverted))
                        .renderInWorld(pose, item, stack, buffer, light, overlay);
            } else {
                UmbrellaClient.renderer(type.id() + "_umbrella").renderInWorld(pose, item, stack, buffer, light, overlay);
            }
        } finally {
            pose.popPose();
        }
    }

    @Override
    public boolean shouldRender(PlacedUmbrellaBlockEntity be, Vec3 cameraPos) {
        return be.getBlockPos().distToCenterSqr(cameraPos.x, cameraPos.y, cameraPos.z) < 24.0 * 24.0;
    }
}
