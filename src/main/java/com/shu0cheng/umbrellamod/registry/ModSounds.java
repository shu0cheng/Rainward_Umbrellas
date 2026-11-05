package com.shu0cheng.umbrellamod.registry;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSounds {
    private ModSounds() {
    }

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, UmbrellaMod.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> RAIN_ON_UMBRELLA =
            SOUNDS.register("rain_on_umbrella",
                    () -> SoundEvent.createVariableRangeEvent(UmbrellaMod.id("rain_on_umbrella")));

    public static final DeferredHolder<SoundEvent, SoundEvent> UMBRELLA_OPEN =
            SOUNDS.register("umbrella_open",
                    () -> SoundEvent.createVariableRangeEvent(UmbrellaMod.id("umbrella_open")));
}
