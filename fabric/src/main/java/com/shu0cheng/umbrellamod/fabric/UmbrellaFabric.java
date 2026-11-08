package com.shu0cheng.umbrellamod.fabric;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.network.TransformPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class UmbrellaFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        UmbrellaMod.init();
        PayloadTypeRegistry.playC2S().register(TransformPayload.TYPE, TransformPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(TransformPayload.TYPE,
                (payload, context) -> context.server().execute(() -> TransformPayload.handle(payload, context.player())));
    }
}
