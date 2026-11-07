package com.shu0cheng.umbrellamod.client;

import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import com.shu0cheng.umbrellamod.item.UmbrellaAnimationAdapter;
import com.shu0cheng.umbrellamod.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.event.level.LevelEvent;

@EventBusSubscriber(modid = "umbrella", bus = Bus.GAME, value = Dist.CLIENT)
@SuppressWarnings("removal")
public final class UmbrellaClientEvents {
   private UmbrellaClientEvents() {
   }

   @SubscribeEvent
   public static void onClientTick(Post event) {
      UmbrellaRainSounds.tick();
      Minecraft mc = Minecraft.getInstance();
      if (mc.screen == null) {
         while (UmbrellaClient.DEV_TUNER.consumeClick()) {
            mc.setScreen(new UmbrellaBlockDevScreen(com.shu0cheng.umbrellamod.item.UmbrellaType.TELESCOPIC, 0, 0));
         }
         while (UmbrellaClient.OPEN_CONFIG.consumeClick()) {
            if (mc.player != null) {
               ItemStack held = mc.player.getMainHandItem();
               if (held.getItem() instanceof UmbrellaItem umbrella) {
                  mc.setScreen(new UmbrellaConfigScreen(umbrella.getUmbrellaType(), false));
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLevelUnload(LevelEvent.Unload event) {
      if (event.getLevel().isClientSide()) {
         for (var item : ModItems.ALL) {
            item.get().clearAnimationCache();
         }
         UmbrellaAnimationAdapter.clear();
         UmbrellaRainSounds.stopNow();
      }
   }
}
