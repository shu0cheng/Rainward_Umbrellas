package com.shu0cheng.umbrellamod.block;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    private ModBlocks() {
    }

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(UmbrellaMod.MODID);

    private static BlockBehaviour.Properties props() {
        return BlockBehaviour.Properties.of()
                .noCollission()
                .noOcclusion()
                .strength(0.25F)
                .sound(SoundType.WOOD);
    }

    public static final DeferredBlock<Block> TELESCOPIC_UMBRELLA = BLOCKS.register("telescopic_umbrella_block",
            () -> new PlacedUmbrellaBlock(props(), UmbrellaType.TELESCOPIC));
    public static final DeferredBlock<Block> STRAIGHT_UMBRELLA = BLOCKS.register("straight_umbrella_block",
            () -> new PlacedUmbrellaBlock(props(), UmbrellaType.STRAIGHT));
    public static final DeferredBlock<Block> OIL_PAPER_UMBRELLA = BLOCKS.register("oil_paper_umbrella_block",
            () -> new PlacedUmbrellaBlock(props(), UmbrellaType.OIL_PAPER));

    public static Block forType(UmbrellaType type) {
        return switch (type) {
            case TELESCOPIC -> TELESCOPIC_UMBRELLA.get();
            case STRAIGHT -> STRAIGHT_UMBRELLA.get();
            case OIL_PAPER -> OIL_PAPER_UMBRELLA.get();
        };
    }
}
