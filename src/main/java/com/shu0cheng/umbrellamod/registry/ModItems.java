package com.shu0cheng.umbrellamod.registry;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public final class ModItems {
    private ModItems() {
    }

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(UmbrellaMod.MODID);

    public static final DeferredItem<UmbrellaItem> TELESCOPIC_UMBRELLA = ITEMS.register("telescopic_umbrella",
            () -> new UmbrellaItem(UmbrellaType.TELESCOPIC, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<UmbrellaItem> STRAIGHT_UMBRELLA = ITEMS.register("straight_umbrella",
            () -> new UmbrellaItem(UmbrellaType.STRAIGHT, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<UmbrellaItem> OIL_PAPER_UMBRELLA = ITEMS.register("oil_paper_umbrella",
            () -> new UmbrellaItem(UmbrellaType.OIL_PAPER, new Item.Properties().stacksTo(1)));

    public static Item itemFor(UmbrellaType type) {
        return switch (type) {
            case TELESCOPIC -> TELESCOPIC_UMBRELLA.get();
            case STRAIGHT -> STRAIGHT_UMBRELLA.get();
            case OIL_PAPER -> OIL_PAPER_UMBRELLA.get();
        };
    }

    public static final List<DeferredItem<UmbrellaItem>> ALL = List.of(
            TELESCOPIC_UMBRELLA, STRAIGHT_UMBRELLA, OIL_PAPER_UMBRELLA
    );
}
