package com.shu0cheng.umbrellamod.platform;

import java.nio.file.Path;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** The small set of loader services needed by shared code. */
public interface UmbrellaPlatform {
    <T, R extends T> Supplier<R> register(ResourceKey<? extends Registry<T>> registry,
                                         String name, Supplier<R> factory);

    <T extends BlockEntity> BlockEntityType<T> blockEntityType(BlockEntityFactory<T> factory, Block... blocks);

    CreativeModeTab.Builder creativeTabBuilder();

    Path configDirectory();

    @FunctionalInterface
    interface BlockEntityFactory<T extends BlockEntity> {
        T create(BlockPos pos, BlockState state);
    }
}
