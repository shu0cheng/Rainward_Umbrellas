package com.shu0cheng.umbrellamod.block;

import java.util.function.Supplier;
import com.shu0cheng.umbrellamod.platform.Services;

import com.shu0cheng.umbrellamod.item.UmbrellaType;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ModBlocks {
    private ModBlocks() {
    }

    private static BlockBehaviour.Properties props() {
        return BlockBehaviour.Properties.of()
                .noCollission()
                .noOcclusion()
                .strength(0.25F)
                .sound(SoundType.WOOD);
    }

    public static final Supplier<Block> TELESCOPIC_UMBRELLA = Services.PLATFORM.register(Registries.BLOCK, "telescopic_umbrella_block",
            () -> new PlacedUmbrellaBlock(props(), UmbrellaType.TELESCOPIC));
    public static final Supplier<Block> STRAIGHT_UMBRELLA = Services.PLATFORM.register(Registries.BLOCK, "straight_umbrella_block",
            () -> new PlacedUmbrellaBlock(props(), UmbrellaType.STRAIGHT));
    public static final Supplier<Block> OIL_PAPER_UMBRELLA = Services.PLATFORM.register(Registries.BLOCK, "oil_paper_umbrella_block",
            () -> new PlacedUmbrellaBlock(props(), UmbrellaType.OIL_PAPER));

    public static Block forType(UmbrellaType type) {
        return switch (type) {
            case TELESCOPIC -> TELESCOPIC_UMBRELLA.get();
            case STRAIGHT -> STRAIGHT_UMBRELLA.get();
            case OIL_PAPER -> OIL_PAPER_UMBRELLA.get();
        };
    }

    public static void init() {}
}
