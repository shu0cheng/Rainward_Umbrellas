package com.shu0cheng.umbrellamod.block;

import com.shu0cheng.umbrellamod.platform.Services;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
    private ModBlockEntities() {}

    public static final Supplier<BlockEntityType<PlacedUmbrellaBlockEntity>> PLACED_UMBRELLA =
            Services.PLATFORM.register(Registries.BLOCK_ENTITY_TYPE, "placed_umbrella",
                    () -> Services.PLATFORM.blockEntityType(PlacedUmbrellaBlockEntity::new,
                            ModBlocks.TELESCOPIC_UMBRELLA.get(),
                            ModBlocks.STRAIGHT_UMBRELLA.get(),
                            ModBlocks.OIL_PAPER_UMBRELLA.get()));

    public static void init() {}
}
