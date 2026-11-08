package com.shu0cheng.umbrellamod.fabric;

import com.shu0cheng.umbrellamod.block.ModBlocks;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import com.shu0cheng.umbrellamod.client.UmbrellaClient;
import com.shu0cheng.umbrellamod.client.UmbrellaClientEvents;
import com.shu0cheng.umbrellamod.network.ModNetwork;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.renderer.RenderType;

public final class UmbrellaFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        UmbrellaClient.init();
        ModNetwork.setClientSender(payload -> {
            if (ClientPlayNetworking.canSend(payload.type())) ClientPlayNetworking.send(payload);
        });
        KeyBindingHelper.registerKeyBinding(UmbrellaClient.OPEN_CONFIG);
        KeyBindingHelper.registerKeyBinding(UmbrellaClient.DEV_TUNER);
        for (UmbrellaType type : UmbrellaType.values()) {
            BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.forType(type), RenderType.cutout());
        }
        ClientTickEvents.END_CLIENT_TICK.register(client -> UmbrellaClientEvents.onClientTick());
    }
}
