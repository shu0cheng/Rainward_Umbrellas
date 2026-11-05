package com.shu0cheng.umbrellamod;

import com.shu0cheng.umbrellamod.block.ModBlockEntities;
import com.shu0cheng.umbrellamod.block.ModBlocks;
import com.shu0cheng.umbrellamod.registry.ModComponents;
import com.shu0cheng.umbrellamod.registry.ModCreativeTabs;
import com.shu0cheng.umbrellamod.registry.ModItems;
import com.shu0cheng.umbrellamod.registry.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Umbrella mod entry point.
 *
 * <p>Three kinds of umbrellas (telescopic, straight, oil-paper) that are closed
 * while in the inventory and can be opened/closed with right click.  A keybind
 * moves the umbrella between the main hand and the off hand.</p>
 */
@Mod(UmbrellaMod.MODID)
public final class UmbrellaMod {
    public static final String MODID = "umbrella";
    public static final Logger LOGGER = LoggerFactory.getLogger("Umbrella");

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public UmbrellaMod(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModComponents.COMPONENTS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        modEventBus.addListener(com.shu0cheng.umbrellamod.network.ModNetwork::register);
    }
}
