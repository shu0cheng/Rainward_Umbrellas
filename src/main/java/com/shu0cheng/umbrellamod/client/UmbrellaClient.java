package com.shu0cheng.umbrellamod.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import com.shu0cheng.umbrellamod.registry.ModItems;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.shu0cheng.umbrellamod.block.ModBlockEntities;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.lwjgl.glfw.GLFW;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = UmbrellaMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class UmbrellaClient {
    public static final KeyMapping OPEN_CONFIG = new KeyMapping(
            "key.umbrella.config",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "key.categories.umbrella"
    );

    public static final KeyMapping DEV_TUNER = new KeyMapping("key.umbrella.devtuner",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F9, "key.categories.umbrella");
    private static final Map<String, UmbrellaGeoRenderer> RENDERERS = new HashMap<>();

    public static UmbrellaGeoRenderer renderer(String id) {
        return RENDERERS.computeIfAbsent(id, UmbrellaGeoRenderer::new);
    }

    private UmbrellaClient() {
    }

    private static IClientItemExtensions geo(String id) {
        return new IClientItemExtensions() {
            private UmbrellaGeoRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = UmbrellaClient.renderer(id);
                }
                return renderer;
            }
        };
    }

    @SubscribeEvent
    public static void registerExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(geo("telescopic_umbrella"), ModItems.TELESCOPIC_UMBRELLA.get());
        event.registerItem(geo("straight_umbrella"), ModItems.STRAIGHT_UMBRELLA.get());
        event.registerItem(geo("oil_paper_umbrella"), ModItems.OIL_PAPER_UMBRELLA.get());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PLACED_UMBRELLA.get(), PlacedUmbrellaRenderer::new);
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(DEV_TUNER);
        UmbrellaBlockDevConfig.load();
    }
}
