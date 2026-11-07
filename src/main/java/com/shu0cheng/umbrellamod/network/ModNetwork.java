package com.shu0cheng.umbrellamod.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetwork {
   private ModNetwork() {
   }

   public static void register(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("1");
      registrar.playToServer(SwitchHandPayload.TYPE, SwitchHandPayload.STREAM_CODEC, SwitchHandPayload::handle);
      registrar.playToServer(ToggleUmbrellaPayload.TYPE, ToggleUmbrellaPayload.STREAM_CODEC, ToggleUmbrellaPayload::handle);
   }
}
