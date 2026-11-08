package com.shu0cheng.umbrellamod.registry;

import java.util.function.Supplier;
import com.shu0cheng.umbrellamod.platform.Services;

import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;

import java.util.List;

public final class ModItems {
    private ModItems() {
    }

    public static final Supplier<UmbrellaItem> TELESCOPIC_UMBRELLA = Services.PLATFORM.register(Registries.ITEM, "telescopic_umbrella",
            () -> new UmbrellaItem(UmbrellaType.TELESCOPIC, new Item.Properties().stacksTo(1)));

    public static final Supplier<UmbrellaItem> STRAIGHT_UMBRELLA = Services.PLATFORM.register(Registries.ITEM, "straight_umbrella",
            () -> new UmbrellaItem(UmbrellaType.STRAIGHT, new Item.Properties().stacksTo(1)));

    public static final Supplier<UmbrellaItem> OIL_PAPER_UMBRELLA = Services.PLATFORM.register(Registries.ITEM, "oil_paper_umbrella",
            () -> new UmbrellaItem(UmbrellaType.OIL_PAPER, new Item.Properties().stacksTo(1)));

    public static Item itemFor(UmbrellaType type) {
        return switch (type) {
            case TELESCOPIC -> TELESCOPIC_UMBRELLA.get();
            case STRAIGHT -> STRAIGHT_UMBRELLA.get();
            case OIL_PAPER -> OIL_PAPER_UMBRELLA.get();
        };
    }

    public static final List<Supplier<UmbrellaItem>> ALL = List.of(
            TELESCOPIC_UMBRELLA, STRAIGHT_UMBRELLA, OIL_PAPER_UMBRELLA
    );

    public static void init() {}
}
