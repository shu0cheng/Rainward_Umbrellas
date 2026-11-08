package com.shu0cheng.umbrellamod;

import com.shu0cheng.umbrellamod.block.ModBlockEntities;
import com.shu0cheng.umbrellamod.block.ModBlocks;
import com.shu0cheng.umbrellamod.registry.ModComponents;
import com.shu0cheng.umbrellamod.registry.ModCreativeTabs;
import com.shu0cheng.umbrellamod.registry.ModItems;
import com.shu0cheng.umbrellamod.registry.ModSounds;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Loader-independent content bootstrap. Registry names are shared by both loaders. */
public final class UmbrellaMod {
    public static final String MODID = "umbrella";
    public static final Logger LOGGER = LoggerFactory.getLogger("Umbrella");

    private UmbrellaMod() {}

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static void init() {
        ModComponents.init();
        ModSounds.init();
        ModItems.init();
        ModBlocks.init();
        ModBlockEntities.init();
        ModCreativeTabs.init();
    }
}
