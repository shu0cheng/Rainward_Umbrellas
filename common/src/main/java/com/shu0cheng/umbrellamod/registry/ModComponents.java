package com.shu0cheng.umbrellamod.registry;

import java.util.function.Supplier;
import com.shu0cheng.umbrellamod.platform.Services;

import com.shu0cheng.umbrellamod.item.UmbrellaState;
import com.shu0cheng.umbrellamod.item.UmbrellaTransform;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.core.registries.Registries;

public final class ModComponents {
    private ModComponents() {
    }

    public static final Supplier<DataComponentType<Long>> UMBRELLA_ANIM_ID =
            Services.PLATFORM.register(Registries.DATA_COMPONENT_TYPE, "umbrella_anim_id", () -> DataComponentType.<Long>builder()
                    .persistent(Codec.LONG)
                    .networkSynchronized(ByteBufCodecs.VAR_LONG)
                    .build());

    public static final Supplier<DataComponentType<UmbrellaTransform>> UMBRELLA_HELD_TRANSFORM =
            Services.PLATFORM.register(Registries.DATA_COMPONENT_TYPE, "umbrella_held_transform", () -> DataComponentType.<UmbrellaTransform>builder()
                    .persistent(UmbrellaTransform.CODEC)
                    .networkSynchronized(UmbrellaTransform.STREAM_CODEC)
                    .build());

    public static final Supplier<DataComponentType<UmbrellaTransform>> UMBRELLA_FIRST_TRANSFORM =
            Services.PLATFORM.register(Registries.DATA_COMPONENT_TYPE, "umbrella_first_transform", () -> DataComponentType.<UmbrellaTransform>builder()
                    .persistent(UmbrellaTransform.CODEC)
                    .networkSynchronized(UmbrellaTransform.STREAM_CODEC)
                    .build());

    public static final Supplier<DataComponentType<UmbrellaState>> UMBRELLA_STATE =
            Services.PLATFORM.register(Registries.DATA_COMPONENT_TYPE, "umbrella_state", () -> DataComponentType.<UmbrellaState>builder()
                    .persistent(UmbrellaState.CODEC)
                    .networkSynchronized(UmbrellaState.STREAM_CODEC)
                    .build());

    public static void init() {}
}
