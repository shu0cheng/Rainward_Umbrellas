package com.shu0cheng.umbrellamod.client;

import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public final class UmbrellaRainSounds {
   private static final Map<UUID, UmbrellaRainSound> ACTIVE = new HashMap<>();

   private UmbrellaRainSounds() {
   }

   public static void tick() {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer listener = mc.player;
      if (listener != null && mc.level != null && UmbrellaClientConfig.rainSoundEnabled()) {
         Set<UUID> desired = new HashSet<>();
         if (mc.level.isRaining()) {
            for (Player player : mc.level.players()) {
               if (!player.isRemoved() && isExposedOpenUmbrella(player)) {
                  boolean self = player == listener;
                  double distance = listener.distanceTo(player);
                  if (self || !(distance >= 4.0)) {
                     float target = self ? 0.95F : 0.9F * (1.0F - (float)(distance / 4.0));
                     if (!(target <= 0.005F)) {
                        UUID id = player.getUUID();
                        desired.add(id);
                        UmbrellaRainSound sound = ACTIVE.get(id);
                        if (sound == null || sound.isStopped()) {
                           sound = new UmbrellaRainSound(player, self);
                           ACTIVE.put(id, sound);
                           mc.getSoundManager().play(sound);
                        }

                        sound.updatePosition(player);
                        sound.setTargetVolume(target);
                     }
                  }
               }
            }
         }

         ACTIVE.entrySet().removeIf(entry -> {
            if (desired.contains(entry.getKey())) {
               return false;
            } else {
               UmbrellaRainSound soundx = entry.getValue();
               soundx.setTargetVolume(0.0F);
               if (!soundx.isStopped() && !soundx.isFadedOut()) {
                  return false;
               } else {
                  mc.getSoundManager().stop(soundx);
                  return true;
               }
            }
         });
      } else {
         stopAll(mc);
      }
   }

   private static boolean isExposedOpenUmbrella(Player player) {
      return !UmbrellaItem.isOpen(player.getMainHandItem()) && !UmbrellaItem.isOpen(player.getOffhandItem())
         ? false
         : player.level().canSeeSky(player.blockPosition().above());
   }

   private static void stopAll(Minecraft mc) {
      for (UmbrellaRainSound sound : ACTIVE.values()) {
         mc.getSoundManager().stop(sound);
      }

      ACTIVE.clear();
   }

   public static void stopNow() {
      stopAll(Minecraft.getInstance());
   }
}
