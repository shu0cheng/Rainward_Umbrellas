package com.shu0cheng.umbrellamod.client;

import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Keeps one rain-on-umbrella loop per nearby open umbrella. */
public final class UmbrellaRainSounds {
    private static final Map<UUID, UmbrellaRainSound> ACTIVE = new HashMap<>();

    private UmbrellaRainSounds() {
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer listener = mc.player;
        if (listener == null || mc.level == null || !UmbrellaClientConfig.rainSoundEnabled()) {
            stopAll(mc);
            return;
        }

        Set<UUID> desired = new HashSet<>();
        {
            for (Player player : mc.level.players()) {
                if (player.isRemoved() || !isExposedOpenUmbrella(player)
                        || !mc.level.isRainingAt(player.blockPosition().above())) {
                    continue;
                }
                boolean self = player == listener;
                double distance = listener.distanceTo(player);
                if (!self && distance >= 4.0) {
                    continue;
                }
                float target = self ? 0.95F : 0.90F * (1.0F - (float) (distance / 4.0));
                if (target <= 0.005F) {
                    continue;
                }
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

        // Anything no longer desired fades out instead of stopping instantly.
        ACTIVE.entrySet().removeIf(entry -> {
            if (desired.contains(entry.getKey())) {
                return false;
            }
            UmbrellaRainSound sound = entry.getValue();
            if (sound.owner().level() != mc.level) {
                sound.setTargetVolume(0.0F);
                mc.getSoundManager().stop(sound);
                return true;
            }
            sound.setTargetVolume(0.0F);
            if (sound.isStopped() || sound.isFadedOut()) {
                mc.getSoundManager().stop(sound);
                return true;
            }
            return false;
        });
    }

    private static boolean isExposedOpenUmbrella(Player player) {
        if (!UmbrellaItem.isOpen(player.getMainHandItem())
                && !UmbrellaItem.isOpen(player.getOffhandItem())) {
            return false;
        }
        // Use the same precipitation check as vanilla rain particles: biome
        // precipitation + heightmap + dimension weather, not just isRaining().
        return player.level().isRainingAt(player.blockPosition().above());
    }

    private static void stopAll(Minecraft mc) {
        for (UmbrellaRainSound sound : ACTIVE.values()) {
            mc.getSoundManager().stop(sound);
        }
        ACTIVE.clear();
    }

    /** Used by the config toggle to stop immediately without waiting for a tick. */
    public static void stopNow() {
        stopAll(Minecraft.getInstance());
    }
}
