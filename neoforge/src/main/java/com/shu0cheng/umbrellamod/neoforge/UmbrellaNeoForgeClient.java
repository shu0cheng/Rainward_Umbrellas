package com.shu0cheng.umbrellamod.neoforge;

import com.shu0cheng.umbrellamod.block.ModBlockEntities;
import com.shu0cheng.umbrellamod.client.PlacedUmbrellaRenderer;
import com.shu0cheng.umbrellamod.client.UmbrellaClient;
import com.shu0cheng.umbrellamod.client.UmbrellaClientEvents;
import com.shu0cheng.umbrellamod.network.ModNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/** Only referenced from the client-side bootstrap branch. */
public final class UmbrellaNeoForgeClient {
    private UmbrellaNeoForgeClient() {}

    public static void register(IEventBus bus) {
        bus.addListener(UmbrellaNeoForgeClient::setup);
        bus.addListener(UmbrellaNeoForgeClient::registerKeys);
        bus.addListener(UmbrellaNeoForgeClient::registerRenderers);
        NeoForge.EVENT_BUS.addListener(UmbrellaNeoForgeClient::tick);
    }

    private static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            UmbrellaClient.init();
            ModNetwork.setClientSender(PacketDistributor::sendToServer);
        });
    }

    private static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(UmbrellaClient.OPEN_CONFIG);
        event.register(UmbrellaClient.DEV_TUNER);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PLACED_UMBRELLA.get(), PlacedUmbrellaRenderer::new);
    }

    private static void tick(ClientTickEvent.Post event) {
        UmbrellaClientEvents.onClientTick();
    }
}
