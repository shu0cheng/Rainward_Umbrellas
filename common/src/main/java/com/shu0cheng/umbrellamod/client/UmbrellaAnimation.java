package com.shu0cheng.umbrellamod.client;

import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import com.shu0cheng.umbrellamod.item.UmbrellaState;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animation.RawAnimation;

/** Stacks toggled on the same tick share a transition. Static poses use reserved IDs. */
public final class UmbrellaAnimation {
    private static final int CLOCK_TOLERANCE_TICKS = 3;
    private static final long DISPLAY = -1L;
    private static final long OPEN = -2L;
    private static final long CLOSED = -3L;
    private static final RawAnimation DISPLAY_POSE = RawAnimation.begin().thenLoop("display_open");
    private static final RawAnimation OPEN_POSE = RawAnimation.begin().thenLoop("pose_open");
    private static final RawAnimation CLOSED_POSE = RawAnimation.begin().thenLoop("pose_closed");
    private static final RawAnimation OPENING = RawAnimation.begin().thenPlayAndHold("open");
    private static final RawAnimation CLOSING = RawAnimation.begin().thenPlayAndHold("close");

    private UmbrellaAnimation() {}

    public static long instanceId(UmbrellaType type, ItemStack stack, ItemDisplayContext context) {
        if (context == ItemDisplayContext.GUI || context == ItemDisplayContext.FIXED) return DISPLAY;

        UmbrellaState state = stack == null ? UmbrellaState.CLOSED : UmbrellaItem.getState(stack);
        ClientLevel level = Minecraft.getInstance().level;
        if (context != ItemDisplayContext.NONE && level != null && state.changedAt() >= 0) {
            long elapsed = level.getGameTime() - state.changedAt();
            if (elapsed >= -CLOCK_TOLERANCE_TICKS && elapsed < type.animationTicks()) {
                return state.changedAt() * 2 + (state.open() ? 1 : 0);
            }
        }
        return state.open() ? OPEN : CLOSED;
    }

    public static RawAnimation animation(UmbrellaType type, ItemStack stack, ItemDisplayContext context) {
        long id = instanceId(type, stack, context);
        if (id == DISPLAY) return DISPLAY_POSE;
        if (id == OPEN) return OPEN_POSE;
        if (id == CLOSED) return CLOSED_POSE;
        return (id & 1) != 0 ? OPENING : CLOSING;
    }
}
