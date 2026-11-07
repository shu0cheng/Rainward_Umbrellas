package com.shu0cheng.umbrellamod.client;

import com.shu0cheng.umbrellamod.item.UmbrellaState;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import com.shu0cheng.umbrellamod.registry.ModComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class UmbrellaAnimation {
   private UmbrellaAnimation() {
   }

   public static boolean isRecent(UmbrellaState state, float durationTicks) {
      Level level = Minecraft.getInstance().level;
      if (level == null || state.changedAt() <= 0) {
         return false;
      }
      long elapsed = level.getGameTime() - state.changedAt();
      return elapsed >= 0 && elapsed < durationTicks;
   }

   public static float progress(ItemStack stack, float partialTick, UmbrellaType type) {
      UmbrellaState state = (UmbrellaState)stack.get((DataComponentType)ModComponents.UMBRELLA_STATE.get());
      if (state == null) {
         return 0.0F;
      } else {
         Level level = Minecraft.getInstance().level;
         if (level == null) {
            return state.open() ? 1.0F : 0.0F;
         } else {
            float ticks = type.animationTicks();
            float elapsed = (float)(level.getGameTime() - state.changedAt()) + partialTick;
            float t = Mth.clamp(elapsed / ticks, 0.0F, 1.0F);
            float eased = 1.0F - (1.0F - t) * (1.0F - t) * (1.0F - t);
            return state.open() ? eased : 1.0F - eased;
         }
      }
   }
}
