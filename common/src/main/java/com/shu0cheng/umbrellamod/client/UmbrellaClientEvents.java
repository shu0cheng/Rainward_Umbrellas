package com.shu0cheng.umbrellamod.client;

import com.shu0cheng.umbrellamod.item.UmbrellaType;
import net.minecraft.client.Minecraft;

public final class UmbrellaClientEvents {
    private UmbrellaClientEvents() {
    }
    public static void onClientTick() {
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
