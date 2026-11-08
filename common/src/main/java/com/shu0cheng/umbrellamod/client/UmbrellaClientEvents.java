package com.shu0cheng.umbrellamod.client;

import com.shu0cheng.umbrellamod.item.UmbrellaType;
import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import com.shu0cheng.umbrellamod.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;

public final class UmbrellaClientEvents {
    private static ClientLevel animationLevel;

    private UmbrellaClientEvents() {
    }
    public static void onClientTick() {
        UmbrellaRainSounds.tick();
        Minecraft mc = Minecraft.getInstance();
        if (animationLevel != mc.level) {
            animationLevel = mc.level;
            ModItems.ALL.forEach(item -> item.get().clearClientAnimationCache());
        }
        if (mc.player != null && UmbrellaItem.isUmbrella(mc.player.getMainHandItem())) {
            mc.player.attackAnim = 0.0F;
            mc.player.oAttackAnim = 0.0F;
        }
        if (mc.screen == null) {
            while (UmbrellaClient.DEV_TUNER.consumeClick()) {
                mc.setScreen(new UmbrellaBlockDevScreen(UmbrellaType.TELESCOPIC, 0, 0));
            }
            while (UmbrellaClient.OPEN_CONFIG.consumeClick()) {
                if (mc.player == null) continue;
                ItemStack held = mc.player.getMainHandItem();
                if (!UmbrellaItem.isUmbrella(held)) held = mc.player.getOffhandItem();
                if (held.getItem() instanceof UmbrellaItem umbrella) {
                    mc.setScreen(new UmbrellaConfigScreen(umbrella.getUmbrellaType(), false));
                }
            }
        }
    }

}
