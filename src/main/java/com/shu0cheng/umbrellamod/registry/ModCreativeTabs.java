package com.shu0cheng.umbrellamod.registry;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    private ModCreativeTabs() {
    }

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, UmbrellaMod.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> UMBRELLAS = TABS.register("umbrellas",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .title(Component.translatable("itemGroup.umbrella"))
                    .icon(() -> new ItemStack(ModItems.OIL_PAPER_UMBRELLA.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.TELESCOPIC_UMBRELLA.get());
                        output.accept(ModItems.STRAIGHT_UMBRELLA.get());
                        output.accept(ModItems.OIL_PAPER_UMBRELLA.get());
                    })
                    .build());
}
