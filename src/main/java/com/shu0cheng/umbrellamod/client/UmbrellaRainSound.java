package com.shu0cheng.umbrellamod.client;

import com.shu0cheng.umbrellamod.registry.ModSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public final class UmbrellaRainSound extends AbstractTickableSoundInstance {
   private final Player owner;
   private float targetVolume = 0.95F;

   public UmbrellaRainSound(Player owner, boolean self) {
      super((SoundEvent)ModSounds.RAIN_ON_UMBRELLA.get(), SoundSource.AMBIENT, SoundInstance.createUnseededRandom());
      this.owner = owner;
      this.looping = true;
      this.attenuation = Attenuation.NONE;
      this.relative = false;
      this.volume = 0.01F;
      this.pitch = 1.0F;
      this.updatePosition(owner);
   }

   public void updatePosition(Player owner) {
      this.x = owner.getX();
      this.y = owner.getEyeY() + 1.45;
      this.z = owner.getZ();
   }

   public void setTargetVolume(float target) {
      this.targetVolume = Math.max(0.0F, Math.min(1.0F, target));
   }

   public boolean isFadedOut() {
      return this.targetVolume <= 0.001F && this.volume <= 0.01F;
   }

   public Player owner() {
      return this.owner;
   }

   public void tick() {
      float diff = this.targetVolume - this.volume;
      if (Math.abs(diff) < 0.004F) {
         this.volume = this.targetVolume;
      } else {
         this.volume += diff * 0.06F;
      }
   }
}
