package com.shu0cheng.umbrellamod.client;

import com.mojang.blaze3d.platform.InputConstants.Type;
import com.shu0cheng.umbrellamod.registry.ModItems;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = "umbrella", bus = Bus.MOD, value = Dist.CLIENT)
@SuppressWarnings("removal")
public final class UmbrellaClient {
   public static final KeyMapping OPEN_CONFIG = new KeyMapping("key.umbrella.config", Type.KEYSYM, 79, "key.categories.umbrella");
   public static final KeyMapping DEV_TUNER = new KeyMapping("key.umbrella.devtuner", Type.KEYSYM, 293, "key.categories.umbrella");
   private static final Map<String, UmbrellaGeoRenderer> RENDERERS = new HashMap<>();

   public static void refreshPlacedModels() {
      com.shu0cheng.umbrellamod.block.UmbrellaGeometry.reload();
      StaticUmbrellaModel.reload();
      var mc = net.minecraft.client.Minecraft.getInstance();
      if (mc.levelRenderer != null) mc.levelRenderer.allChanged();
   }

   public static UmbrellaGeoRenderer renderer(String id) {
      return RENDERERS.computeIfAbsent(id, UmbrellaGeoRenderer::new);
   }

   private UmbrellaClient() {
   }

   private static IClientItemExtensions geo(final String id) {
      return new IClientItemExtensions() {
         private UmbrellaGeoRenderer renderer;

         public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            if (this.renderer == null) {
               this.renderer = UmbrellaClient.renderer(id);
            }

            return this.renderer;
         }
      };
   }

   @SubscribeEvent
   public static void registerExtensions(RegisterClientExtensionsEvent event) {
      event.registerItem(geo("telescopic_umbrella"), new Item[]{(Item)ModItems.TELESCOPIC_UMBRELLA.get()});
      event.registerItem(geo("straight_umbrella"), new Item[]{(Item)ModItems.STRAIGHT_UMBRELLA.get()});
      event.registerItem(geo("oil_paper_umbrella"), new Item[]{(Item)ModItems.OIL_PAPER_UMBRELLA.get()});
      event.registerItem(geo("bamboo_oil_paper_umbrella"), new Item[]{(Item)ModItems.BAMBOO_OIL_PAPER_UMBRELLA.get()});
   }

   @SubscribeEvent
   public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
      event.register(StaticUmbrellaModel.LOADER,StaticUmbrellaModel.GEOMETRY_LOADER);
   }

   @SubscribeEvent
   public static void registerKeys(RegisterKeyMappingsEvent event) {
      event.register(OPEN_CONFIG);
      event.register(DEV_TUNER);
      UmbrellaBlockDevConfig.load();
   }
}
