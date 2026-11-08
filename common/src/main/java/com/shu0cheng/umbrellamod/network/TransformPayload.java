package com.shu0cheng.umbrellamod.network;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import com.shu0cheng.umbrellamod.item.UmbrellaTransform;
import com.shu0cheng.umbrellamod.registry.ModComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/** Client -> server sync of the custom display transforms stored on the held umbrella stack. */
public record TransformPayload(InteractionHand hand, ResourceLocation itemId,
                               UmbrellaTransform held, UmbrellaTransform first) implements CustomPacketPayload {
    public static final Type<TransformPayload> TYPE = new Type<>(UmbrellaMod.id("umbrella_transform_v2"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TransformPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL.map(offHand -> offHand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND,
                    hand -> hand == InteractionHand.OFF_HAND), TransformPayload::hand,
            ResourceLocation.STREAM_CODEC, TransformPayload::itemId,
            UmbrellaTransform.STREAM_CODEC, TransformPayload::held,
            UmbrellaTransform.STREAM_CODEC, TransformPayload::first,
            TransformPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TransformPayload payload, ServerPlayer player) {
        if (player.isSpectator() || !payload.held().isValid(1.0F) || !payload.first().isValid(1.0F)) return;

        ItemStack stack = player.getItemInHand(payload.hand());
        if (!UmbrellaItem.isUmbrella(stack) || !BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(payload.itemId())) return;
        if (payload.held().equals(stack.get(ModComponents.UMBRELLA_HELD_TRANSFORM.get()))
                && payload.first().equals(stack.get(ModComponents.UMBRELLA_FIRST_TRANSFORM.get()))) return;

        stack.set(ModComponents.UMBRELLA_HELD_TRANSFORM.get(), payload.held());
        stack.set(ModComponents.UMBRELLA_FIRST_TRANSFORM.get(), payload.first());
        player.containerMenu.broadcastChanges();
    }
}
