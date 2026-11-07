package com.shu0cheng.umbrellamod.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.Row;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
   public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "umbrella");
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> UMBRELLAS = TABS.register(
      "umbrellas",
      () -> CreativeModeTab.builder(Row.TOP, 0)
         .title(Component.translatable("itemGroup.umbrella"))
         .icon(() -> new ItemStack((ItemLike)ModItems.OIL_PAPER_UMBRELLA.get()))
         .displayItems((parameters, output) -> {
            output.accept((ItemLike)ModItems.TELESCOPIC_UMBRELLA.get());
            output.accept((ItemLike)ModItems.STRAIGHT_UMBRELLA.get());
            output.accept((ItemLike)ModItems.OIL_PAPER_UMBRELLA.get());
            output.accept((ItemLike)ModItems.BAMBOO_OIL_PAPER_UMBRELLA.get());
         })
         .build()
   );

   private ModCreativeTabs() {
   }
}
