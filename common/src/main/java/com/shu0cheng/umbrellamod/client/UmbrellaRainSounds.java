package com.shu0cheng.umbrellamod.client;

import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Keeps one rain-on-umbrella loop per nearby open umbrella. */
public final class UmbrellaRainSounds {
    private static final double HEARING_DISTANCE = 4.0;
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

        ACTIVE.values().forEach(sound -> sound.setTargetVolume(0.0F));
        if (mc.level.isRaining()) {
            for (Player player : mc.level.players()) {
                boolean self = player == listener;
                double distanceSquared = listener.distanceToSqr(player);
                if (player.isRemoved() || distanceSquared >= HEARING_DISTANCE * HEARING_DISTANCE
                        || (!UmbrellaItem.isOpen(player.getMainHandItem()) && !UmbrellaItem.isOpen(player.getOffhandItem()))
                        || !mc.level.isRainingAt(player.blockPosition().above())) {
                    continue;
                }
                float target = self ? 0.95F : 0.90F * (1.0F - (float) (Math.sqrt(distanceSquared) / HEARING_DISTANCE));
                if (target <= 0.005F) {
                    continue;
                }
                UUID id = player.getUUID();
                UmbrellaRainSound sound = ACTIVE.get(id);
                if (sound == null || sound.isStopped() || sound.owner() != player) {
                    if (sound != null) mc.getSoundManager().stop(sound);
                    sound = new UmbrellaRainSound(player);
                    ACTIVE.put(id, sound);
                    mc.getSoundManager().play(sound);
                }
                sound.updatePosition();
                sound.setTargetVolume(target);
            }
        }

        // Anything no longer desired fades out instead of stopping instantly.
        ACTIVE.entrySet().removeIf(entry -> {
            UmbrellaRainSound sound = entry.getValue();
            if (sound.owner().level() != mc.level || sound.isStopped() || sound.isFadedOut()) {
                mc.getSoundManager().stop(sound);
                return true;
            }
            return false;
        });
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
