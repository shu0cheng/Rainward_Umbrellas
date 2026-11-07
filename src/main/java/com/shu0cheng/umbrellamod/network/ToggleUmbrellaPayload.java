package com.shu0cheng.umbrellamod.network;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleUmbrellaPayload() implements CustomPacketPayload {
   public static final Type<ToggleUmbrellaPayload> TYPE = new Type(UmbrellaMod.id("toggle_umbrella"));
   public static final StreamCodec<RegistryFriendlyByteBuf, ToggleUmbrellaPayload> STREAM_CODEC = StreamCodec.unit(new ToggleUmbrellaPayload());

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(ToggleUmbrellaPayload payload, IPayloadContext context) {
      context.enqueueWork(() -> {
         if (context.player() instanceof ServerPlayer player) {
            for (InteractionHand hand : new InteractionHand[]{InteractionHand.MAIN_HAND, InteractionHand.OFF_HAND}) {
               ItemStack stack = player.getItemInHand(hand);
               if (UmbrellaItem.isUmbrella(stack)) {
                  ((UmbrellaItem)stack.getItem()).toggle(player, hand);
                  return;
               }
            }
         }
      });
   }
}
