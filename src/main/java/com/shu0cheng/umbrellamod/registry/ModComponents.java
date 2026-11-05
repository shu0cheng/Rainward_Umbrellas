package com.shu0cheng.umbrellamod.registry;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.item.UmbrellaState;
import com.shu0cheng.umbrellamod.item.UmbrellaTransform;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModComponents {
    private ModComponents() {
    }

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, UmbrellaMod.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> UMBRELLA_ANIM_ID =
            COMPONENTS.register("umbrella_anim_id", () -> DataComponentType.<Long>builder()
                    .persistent(Codec.LONG)
                    .networkSynchronized(ByteBufCodecs.VAR_LONG)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UmbrellaTransform>> UMBRELLA_HELD_TRANSFORM =
            COMPONENTS.register("umbrella_held_transform", () -> DataComponentType.<UmbrellaTransform>builder()
                    .persistent(UmbrellaTransform.CODEC)
                    .networkSynchronized(UmbrellaTransform.STREAM_CODEC)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UmbrellaTransform>> UMBRELLA_FIRST_TRANSFORM =
            COMPONENTS.register("umbrella_first_transform", () -> DataComponentType.<UmbrellaTransform>builder()
                    .persistent(UmbrellaTransform.CODEC)
                    .networkSynchronized(UmbrellaTransform.STREAM_CODEC)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UmbrellaState>> UMBRELLA_STATE =
            COMPONENTS.register("umbrella_state", () -> DataComponentType.<UmbrellaState>builder()
                    .persistent(UmbrellaState.CODEC)
                    .networkSynchronized(UmbrellaState.STREAM_CODEC)
                    .build());
}
