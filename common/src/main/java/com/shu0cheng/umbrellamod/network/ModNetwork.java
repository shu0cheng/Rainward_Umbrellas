package com.shu0cheng.umbrellamod.network;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Loader clients install their sender; payload data and server logic remain shared. */
public final class ModNetwork {
    private static Consumer<CustomPacketPayload> sender;

    private ModNetwork() {}

    public static void setClientSender(Consumer<CustomPacketPayload> clientSender) {
        sender = Objects.requireNonNull(clientSender);
    }

    public static void sendToServer(CustomPacketPayload payload) {
        if (sender == null) throw new IllegalStateException("Umbrella client networking is not initialized");
        sender.accept(payload);
    }
}
