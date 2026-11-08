package com.shu0cheng.umbrellamod.item;

import com.shu0cheng.umbrellamod.registry.ModComponents;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animation.AnimatableManager;

import java.util.IdentityHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Per-ItemStack animation identity and one-shot trigger adapter.
 *
 * <p>GeckoLib reuses a single animatable instance for an Item.  That makes two
 * umbrellas of the same type share one animation manager unless every stack has
 * its own stable id.  The renderer and this adapter must use the same id.</p>
 */
public final class UmbrellaAnimationAdapter {
    private static final Map<ItemStack, Long> IDS = new IdentityHashMap<>();
    private static final Map<Long, Boolean> OPEN = new HashMap<>();
    private static final AtomicLong NEXT = new AtomicLong(1_000_000L);

    private UmbrellaAnimationAdapter() {
    }

    public static long id(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0L;
        return IDS.computeIfAbsent(stack, s -> NEXT.getAndIncrement());
    }

    public static boolean open(ItemStack stack) {
        if (stack == null) return false;
        return UmbrellaItem.isOpen(stack);
    }

    public static void setOpen(ItemStack stack, boolean open) {
        if (stack == null) return;
        OPEN.put(id(stack), open);
    }

    public static void trigger(UmbrellaItem item, ItemStack stack, boolean open) {
        try {
            long id = id(stack);
            AnimatableManager<?> manager = item.geoCache().getManagerForId(id);
            if (manager != null) {
                manager.tryTriggerAnimation("main", open ? "open" : "close");
            }
        } catch (Exception ignored) {
        }
    }
}
