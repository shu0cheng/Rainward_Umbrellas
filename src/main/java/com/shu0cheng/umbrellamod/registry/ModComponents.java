package com.shu0cheng.umbrellamod.registry;

import com.mojang.serialization.Codec;
import com.shu0cheng.umbrellamod.item.UmbrellaState;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModComponents {
   public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "umbrella");
   // Retained so existing saves can still decode the old component. Rendering no
   // longer reads it; new persistent animation IDs are assigned by GeckoLib.
   public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> UMBRELLA_ANIM_ID = COMPONENTS.register(
      "umbrella_anim_id", () -> DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build()
   );
   public static final DeferredHolder<DataComponentType<?>, DataComponentType<UmbrellaState>> UMBRELLA_STATE = COMPONENTS.register(
      "umbrella_state", () -> DataComponentType.<UmbrellaState>builder().persistent(UmbrellaState.CODEC).networkSynchronized(UmbrellaState.STREAM_CODEC).build()
   );

   private ModComponents() {
   }
}
