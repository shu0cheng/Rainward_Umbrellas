package com.shu0cheng.umbrellamod.client;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = UmbrellaMod.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class UmbrellaClientEvents {
    private UmbrellaClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        UmbrellaRainSounds.tick();
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && com.shu0cheng.umbrellamod.item.UmbrellaItem.isUmbrella(mc.player.getMainHandItem())) {
            mc.player.attackAnim = 0.0F;
            mc.player.oAttackAnim = 0.0F;
        }
        if (mc.screen == null) {
            while (UmbrellaClient.DEV_TUNER.consumeClick()) {
                mc.setScreen(new UmbrellaBlockDevScreen(UmbrellaType.TELESCOPIC, 0, 0));
            }
            while (UmbrellaClient.OPEN_CONFIG.consumeClick()) {
                if (mc.player == null) continue;
                net.minecraft.world.item.ItemStack held = mc.player.getMainHandItem();
                if (held.getItem() instanceof com.shu0cheng.umbrellamod.item.UmbrellaItem umbrella) {
                    mc.setScreen(new UmbrellaConfigScreen(umbrella.getUmbrellaType(), false));
                }
            }
        }
    }

}
