package com.shu0cheng.umbrellamod.client;

import com.shu0cheng.umbrellamod.registry.ModSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/**
 * A looping rain-on-canopy sound attached to one umbrella owner.
 *
 * <p>The sound is positioned above the owner's head so that with headphones it
 * is audibly overhead.  Attenuation is set to NONE and a custom 4 block linear
 * volume falloff is applied by the manager.  The volume is faded in/out instead
 * of starting or stopping abruptly.</p>
 */
public final class UmbrellaRainSound extends AbstractTickableSoundInstance {
    private final Player owner;
    private float targetVolume = 0.95F;

    public UmbrellaRainSound(Player owner) {
        super(ModSounds.RAIN_ON_UMBRELLA.get(), SoundSource.AMBIENT,
                SoundInstance.createUnseededRandom());
        this.owner = owner;
        this.looping = true;
        this.attenuation = SoundInstance.Attenuation.NONE;
        this.relative = false;
        this.volume = 0.01F;
        this.pitch = 1.0F;
        updatePosition();
    }

    public void updatePosition() {
        this.x = owner.getX();
        this.y = owner.getEyeY() + 1.45;
        this.z = owner.getZ();
    }

    public void setTargetVolume(float target) {
        this.targetVolume = Math.max(0.0F, Math.min(1.0F, target));
    }

    public boolean isFadedOut() {
        return targetVolume <= 0.001F && volume <= 0.01F;
    }

    public Player owner() {
        return owner;
    }

    @Override
    public void tick() {
        float diff = targetVolume - volume;
        if (Math.abs(diff) < 0.004F) {
            volume = targetVolume;
        } else {
            // ~1.5 s fade at 20 ticks per second.
            volume += diff * 0.06F;
        }
    }
}
