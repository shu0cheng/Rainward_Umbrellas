package com.shu0cheng.umbrellamod.client;

import com.shu0cheng.umbrellamod.item.UmbrellaState;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import com.shu0cheng.umbrellamod.registry.ModComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Client-side interpolation of the open/close animation.
 */
public final class UmbrellaAnimation {
    private UmbrellaAnimation() {
    }

    public static float progress(ItemStack stack, float partialTick, UmbrellaType type) {
        UmbrellaState state = stack.get(ModComponents.UMBRELLA_STATE.get());
        if (state == null) {
            return 0.0F;
        }
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return state.open() ? 1.0F : 0.0F;
        }
        float ticks = switch (type) {
            case TELESCOPIC -> 12.0F;
            case OIL_PAPER -> 16.0F;
            case STRAIGHT -> 8.0F;
        };
        float elapsed = (level.getGameTime() - state.changedAt()) + partialTick;
        float t = Mth.clamp(elapsed / ticks, 0.0F, 1.0F);
        float eased = 1.0F - (1.0F - t) * (1.0F - t) * (1.0F - t);
        return state.open() ? eased : 1.0F - eased;
    }
}
