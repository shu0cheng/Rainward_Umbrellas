package com.shu0cheng.umbrellamod.network;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SwitchHandPayload() implements CustomPacketPayload {
   public static final Type<SwitchHandPayload> TYPE = new Type(UmbrellaMod.id("switch_hand"));
   public static final StreamCodec<RegistryFriendlyByteBuf, SwitchHandPayload> STREAM_CODEC = StreamCodec.unit(new SwitchHandPayload());

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(SwitchHandPayload payload, IPayloadContext context) {
      context.enqueueWork(() -> {
         if (context.player() instanceof ServerPlayer player) {
            ItemStack var4 = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();
            if (UmbrellaItem.isUmbrella(var4) || UmbrellaItem.isUmbrella(off)) {
               player.setItemInHand(InteractionHand.MAIN_HAND, off);
               player.setItemInHand(InteractionHand.OFF_HAND, var4);
               player.inventoryMenu.broadcastChanges();
               player.playSound(SoundEvents.ITEM_PICKUP, 0.4F, 1.35F);
            }
         }
      });
   }
}
