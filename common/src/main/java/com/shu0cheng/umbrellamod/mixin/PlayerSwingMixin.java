package com.shu0cheng.umbrellamod.mixin;

import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Suppresses the vanilla arm swing when right-clicking an umbrella. */
@Mixin(Player.class)
public abstract class PlayerSwingMixin {
    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;)V", at = @At("HEAD"), cancellable = true)
    private void umbrella$noSwing(InteractionHand hand, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (UmbrellaItem.isUmbrella(player.getItemInHand(hand))) {
            ci.cancel();
        }
    }

    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"), cancellable = true)
    private void umbrella$noSwing(InteractionHand hand, boolean updateSelf, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (UmbrellaItem.isUmbrella(player.getItemInHand(hand))) {
            ci.cancel();
        }
    }
}
