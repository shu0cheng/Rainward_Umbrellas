package com.shu0cheng.umbrellamod.mixin;

import com.shu0cheng.umbrellamod.client.PlacedUmbrellaModels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;

@Mixin(BlockModelShaper.class)
abstract class BlockModelShaperMixin {
    @ModifyVariable(method = "replaceCache", at = @At("HEAD"), argsOnly = true, require = 1)
    private Map<BlockState, BakedModel> umbrella$bakePlacedModels(Map<BlockState, BakedModel> models) {
        return PlacedUmbrellaModels.replaceModels(models, Minecraft.getInstance().getResourceManager());
    }
}
