package com.shu0cheng.umbrellamod.registry;

import java.util.function.Supplier;
import com.shu0cheng.umbrellamod.platform.Services;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeTabs {
    private ModCreativeTabs() {
    }

    public static final Supplier<CreativeModeTab> UMBRELLAS = Services.PLATFORM.register(Registries.CREATIVE_MODE_TAB, "umbrellas",
            () -> Services.PLATFORM.creativeTabBuilder()
                    .title(Component.translatable("itemGroup.umbrella"))
                    .icon(() -> new ItemStack(ModItems.OIL_PAPER_UMBRELLA.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.TELESCOPIC_UMBRELLA.get());
                        output.accept(ModItems.STRAIGHT_UMBRELLA.get());
                        output.accept(ModItems.OIL_PAPER_UMBRELLA.get());
                    })
                    .build());

    public static void init() {}
}
