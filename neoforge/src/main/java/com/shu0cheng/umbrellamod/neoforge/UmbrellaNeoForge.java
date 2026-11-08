package com.shu0cheng.umbrellamod.neoforge;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.network.SwitchHandPayload;
import com.shu0cheng.umbrellamod.network.ToggleUmbrellaPayload;
import com.shu0cheng.umbrellamod.network.TransformPayload;
import com.shu0cheng.umbrellamod.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(UmbrellaMod.MODID)
public final class UmbrellaNeoForge {
    public UmbrellaNeoForge(IEventBus bus) {
        UmbrellaMod.init();
        ((NeoForgePlatform) Services.PLATFORM).attach(bus);
        bus.addListener(UmbrellaNeoForge::registerNetwork);
        if (FMLEnvironment.dist == Dist.CLIENT) UmbrellaNeoForgeClient.register(bus);
    }

    private static void registerNetwork(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(SwitchHandPayload.TYPE, SwitchHandPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player) SwitchHandPayload.handle(payload, player);
                }));
        registrar.playToServer(ToggleUmbrellaPayload.TYPE, ToggleUmbrellaPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player) ToggleUmbrellaPayload.handle(payload, player);
                }));
        registrar.playToServer(TransformPayload.TYPE, TransformPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player) TransformPayload.handle(payload, player);
                }));
    }
}
