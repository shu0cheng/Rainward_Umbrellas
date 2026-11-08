package com.shu0cheng.umbrellamod.network;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import com.shu0cheng.umbrellamod.item.UmbrellaState;
import com.shu0cheng.umbrellamod.registry.ModComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/** Client -> server request to toggle the held umbrella open/closed. */
public record ToggleUmbrellaPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ToggleUmbrellaPayload> TYPE =
            new CustomPacketPayload.Type<>(UmbrellaMod.id("toggle_umbrella"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleUmbrellaPayload> STREAM_CODEC =
            StreamCodec.unit(new ToggleUmbrellaPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ToggleUmbrellaPayload payload, ServerPlayer player) {
        for (InteractionHand hand : new InteractionHand[]{InteractionHand.MAIN_HAND, InteractionHand.OFF_HAND}) {
            ItemStack stack = player.getItemInHand(hand);
            if (!UmbrellaItem.isUmbrella(stack)) {
                continue;
            }
            boolean open = UmbrellaItem.isOpen(stack);
            stack.set(ModComponents.UMBRELLA_STATE.get(), new UmbrellaState(!open, player.level().getGameTime()));
            player.playSound(open ? SoundEvents.WOOL_BREAK : SoundEvents.WOOL_PLACE,
                    0.85F, open ? 1.15F : 0.85F);
            player.inventoryMenu.broadcastChanges();
            return;
        }
    }
}
