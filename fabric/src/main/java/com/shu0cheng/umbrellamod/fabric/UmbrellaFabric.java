package com.shu0cheng.umbrellamod.fabric;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.network.SwitchHandPayload;
import com.shu0cheng.umbrellamod.network.ToggleUmbrellaPayload;
import com.shu0cheng.umbrellamod.network.TransformPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class UmbrellaFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        UmbrellaMod.init();
        PayloadTypeRegistry.playC2S().register(SwitchHandPayload.TYPE, SwitchHandPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ToggleUmbrellaPayload.TYPE, ToggleUmbrellaPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(TransformPayload.TYPE, TransformPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SwitchHandPayload.TYPE,
                (payload, context) -> context.server().execute(() -> SwitchHandPayload.handle(payload, context.player())));
        ServerPlayNetworking.registerGlobalReceiver(ToggleUmbrellaPayload.TYPE,
                (payload, context) -> context.server().execute(() -> ToggleUmbrellaPayload.handle(payload, context.player())));
        ServerPlayNetworking.registerGlobalReceiver(TransformPayload.TYPE,
                (payload, context) -> context.server().execute(() -> TransformPayload.handle(payload, context.player())));
    }
}
