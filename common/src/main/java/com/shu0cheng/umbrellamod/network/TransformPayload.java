package com.shu0cheng.umbrellamod.network;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import com.shu0cheng.umbrellamod.item.UmbrellaTransform;
import com.shu0cheng.umbrellamod.registry.ModComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/** Client -> server sync of the custom display transforms stored on the held umbrella stack. */
public record TransformPayload(UmbrellaTransform held, UmbrellaTransform first) implements CustomPacketPayload {
    public static final Type<TransformPayload> TYPE = new Type<>(UmbrellaMod.id("umbrella_transform"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TransformPayload> STREAM_CODEC = StreamCodec.composite(
            UmbrellaTransform.STREAM_CODEC, TransformPayload::held,
            UmbrellaTransform.STREAM_CODEC, TransformPayload::first,
            TransformPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TransformPayload payload, ServerPlayer player) {
        for (InteractionHand hand : new InteractionHand[]{InteractionHand.MAIN_HAND, InteractionHand.OFF_HAND}) {
            ItemStack stack = player.getItemInHand(hand);
            if (!(stack.getItem() instanceof UmbrellaItem)) continue;
            stack.set(ModComponents.UMBRELLA_HELD_TRANSFORM.get(), payload.held());
            stack.set(ModComponents.UMBRELLA_FIRST_TRANSFORM.get(), payload.first());
            player.inventoryMenu.broadcastChanges();
            return;
        }
    }
}
