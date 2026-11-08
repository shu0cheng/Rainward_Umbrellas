package com.shu0cheng.umbrellamod.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.shu0cheng.umbrellamod.client.PlacedUmbrellaModels;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
abstract class LevelRendererMixin {
    @Inject(method = "renderHitOutline", at = @At("HEAD"), cancellable = true, require = 1)
    private void umbrella$renderHandleOutline(PoseStack matrices, VertexConsumer lines, Entity viewer,
                                                double cameraX, double cameraY, double cameraZ,
                                                BlockPos pos, BlockState state, CallbackInfo ci) {
        if (PlacedUmbrellaModels.renderHandleOutline(state, pos, matrices, lines, cameraX, cameraY, cameraZ)) {
            ci.cancel();
        }
    }
}
