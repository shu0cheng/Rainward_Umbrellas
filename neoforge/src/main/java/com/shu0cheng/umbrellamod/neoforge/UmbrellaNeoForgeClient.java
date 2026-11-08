package com.shu0cheng.umbrellamod.neoforge;

import com.shu0cheng.umbrellamod.block.ModBlocks;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import com.shu0cheng.umbrellamod.client.UmbrellaClient;
import com.shu0cheng.umbrellamod.client.UmbrellaClientEvents;
import com.shu0cheng.umbrellamod.network.ModNetwork;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/** Only referenced from the client-side bootstrap branch. */
public final class UmbrellaNeoForgeClient {
    private UmbrellaNeoForgeClient() {}

    public static void register(IEventBus bus) {
        bus.addListener(UmbrellaNeoForgeClient::setup);
        bus.addListener(UmbrellaNeoForgeClient::registerKeys);
        NeoForge.EVENT_BUS.addListener(UmbrellaNeoForgeClient::tick);
    }

    private static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            UmbrellaClient.init();
            ModNetwork.setClientSender(PacketDistributor::sendToServer);
            for (UmbrellaType type : UmbrellaType.values()) {
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.forType(type), RenderType.cutout());
            }
        });
    }

    private static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(UmbrellaClient.OPEN_CONFIG);
        event.register(UmbrellaClient.DEV_TUNER);
    }

    private static void tick(ClientTickEvent.Post event) {
        UmbrellaClientEvents.onClientTick();
    }
}
