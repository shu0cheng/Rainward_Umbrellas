package com.shu0cheng.umbrellamod.registry;

import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public final class ModItems {
   public static final Items ITEMS = DeferredRegister.createItems("umbrella");
   public static final DeferredItem<UmbrellaItem> TELESCOPIC_UMBRELLA = ITEMS.register(
      "telescopic_umbrella", () -> new UmbrellaItem(UmbrellaType.TELESCOPIC, new Properties().stacksTo(1))
   );
   public static final DeferredItem<UmbrellaItem> STRAIGHT_UMBRELLA = ITEMS.register(
      "straight_umbrella", () -> new UmbrellaItem(UmbrellaType.STRAIGHT, new Properties().stacksTo(1))
   );
   public static final DeferredItem<UmbrellaItem> OIL_PAPER_UMBRELLA = ITEMS.register(
      "oil_paper_umbrella", () -> new UmbrellaItem(UmbrellaType.OIL_PAPER, new Properties().stacksTo(1))
   );
   public static final DeferredItem<UmbrellaItem> BAMBOO_OIL_PAPER_UMBRELLA = ITEMS.register(
      "bamboo_oil_paper_umbrella", () -> new UmbrellaItem(UmbrellaType.BAMBOO_OIL_PAPER, new Properties().stacksTo(1))
   );
   public static final List<DeferredItem<UmbrellaItem>> ALL = List.of(TELESCOPIC_UMBRELLA, STRAIGHT_UMBRELLA, OIL_PAPER_UMBRELLA, BAMBOO_OIL_PAPER_UMBRELLA);

   private ModItems() {
   }

   public static Item itemFor(UmbrellaType type) {
      return switch (type) {
         case TELESCOPIC -> (UmbrellaItem)TELESCOPIC_UMBRELLA.get();
         case STRAIGHT -> (UmbrellaItem)STRAIGHT_UMBRELLA.get();
         case OIL_PAPER -> (UmbrellaItem)OIL_PAPER_UMBRELLA.get();
         case BAMBOO_OIL_PAPER -> (UmbrellaItem)BAMBOO_OIL_PAPER_UMBRELLA.get();
      };
   }
}
