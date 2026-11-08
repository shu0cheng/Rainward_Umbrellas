package com.shu0cheng.umbrellamod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PlacedUmbrellaBlockEntity extends BlockEntity {
    public PlacedUmbrellaBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLACED_UMBRELLA.get(), pos, state);
    }

    public BlockState state() {
        return getBlockState();
    }
}
