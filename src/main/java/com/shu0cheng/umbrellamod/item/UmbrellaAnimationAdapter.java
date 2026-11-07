package com.shu0cheng.umbrellamod.item;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;

public final class UmbrellaAnimationAdapter {
   // Display identities are weak and client-only. They never override item state.
   private static final Map<ItemStack, long[]> LOCAL_IDS = new WeakHashMap<>();
   private static final AtomicLong NEXT_LOCAL_ID = new AtomicLong(-1L);

   private UmbrellaAnimationAdapter() {
   }

   public static long id(ItemStack stack, boolean display) {
      if (stack == null || stack.isEmpty()) {
         return Long.MIN_VALUE;
      }

      // GeckoLib persists its server counter; restarting cannot reuse old IDs.
      long serverId = GeoItem.getId(stack);
      if (!display && serverId != Long.MAX_VALUE) {
         return serverId;
      }

      // Server IDs are non-negative. GUI and unassigned stacks use negative IDs.
      long[] ids = LOCAL_IDS.computeIfAbsent(stack,
         ignored -> new long[]{NEXT_LOCAL_ID.getAndDecrement(), NEXT_LOCAL_ID.getAndDecrement()});
      return ids[display ? 1 : 0];
   }

   public static void clear() {
      LOCAL_IDS.clear();
      NEXT_LOCAL_ID.set(-1L);
   }
}
