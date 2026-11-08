package com.shu0cheng.umbrellamod.fabric;

import com.shu0cheng.umbrellamod.block.ModBlockEntities;
import com.shu0cheng.umbrellamod.client.PlacedUmbrellaRenderer;
import com.shu0cheng.umbrellamod.client.UmbrellaClient;
import com.shu0cheng.umbrellamod.client.UmbrellaClientEvents;
import com.shu0cheng.umbrellamod.network.ModNetwork;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public final class UmbrellaFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        UmbrellaClient.init();
        ModNetwork.setClientSender(payload -> {
            if (ClientPlayNetworking.canSend(payload.type())) ClientPlayNetworking.send(payload);
        });
        KeyBindingHelper.registerKeyBinding(UmbrellaClient.OPEN_CONFIG);
        KeyBindingHelper.registerKeyBinding(UmbrellaClient.DEV_TUNER);
        BlockEntityRendererRegistry.register(ModBlockEntities.PLACED_UMBRELLA.get(), PlacedUmbrellaRenderer::new);
        ClientTickEvents.END_CLIENT_TICK.register(client -> UmbrellaClientEvents.onClientTick());
    }
}
