package com.shu0cheng.umbrellamod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.shu0cheng.umbrellamod.block.PlacedUmbrellaBlock;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;

public final class PlacedUmbrellaModels {
    private PlacedUmbrellaModels() {}

    public static boolean renderHandleOutline(BlockState state, BlockPos pos, PoseStack matrices,
                                              VertexConsumer lines, double cameraX, double cameraY, double cameraZ) {
        if (!(state.getBlock() instanceof PlacedUmbrellaBlock)) return false;
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        return model instanceof PlacedUmbrellaBakedModel umbrella && umbrella.renderHandleOutline(matrices, lines,
                pos.getX() - cameraX, pos.getY() - cameraY, pos.getZ() - cameraZ);
    }

    public static Map<BlockState, BakedModel> replaceModels(Map<BlockState, BakedModel> models, ResourceManager resources) {
        Map<UmbrellaType, Map<PlacedUmbrellaMesh.Pose, PlacedUmbrellaMesh>> meshes = new EnumMap<>(UmbrellaType.class);
        Map<BlockState, BakedModel> result = new IdentityHashMap<>(models);
        result.replaceAll((state, model) -> {
            if (!(state.getBlock() instanceof PlacedUmbrellaBlock block)) return model;
            UmbrellaType type = block.umbrellaType();
            PlacedUmbrellaMesh.Pose pose = PlacedUmbrellaMesh.Pose.CLOSED;
            if (state.getValue(PlacedUmbrellaBlock.OPEN)) {
                pose = state.getValue(PlacedUmbrellaBlock.INVERTED)
                        ? PlacedUmbrellaMesh.Pose.INVERTED : PlacedUmbrellaMesh.Pose.UPRIGHT;
            }
            PlacedUmbrellaMesh mesh = meshes.computeIfAbsent(type, key -> new EnumMap<>(PlacedUmbrellaMesh.Pose.class))
                    .computeIfAbsent(pose, key -> PlacedUmbrellaMesh.load(resources, type, key));
            return new PlacedUmbrellaBakedModel(mesh, model.getParticleIcon(), type, state);
        });
        return result;
    }
}
