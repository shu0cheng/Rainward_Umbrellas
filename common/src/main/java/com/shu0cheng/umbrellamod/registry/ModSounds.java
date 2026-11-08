package com.shu0cheng.umbrellamod.registry;

import java.util.function.Supplier;
import com.shu0cheng.umbrellamod.platform.Services;
import com.shu0cheng.umbrellamod.UmbrellaMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;

public final class ModSounds {
    private ModSounds() {
    }

    public static final Supplier<SoundEvent> RAIN_ON_UMBRELLA =
            Services.PLATFORM.register(Registries.SOUND_EVENT, "rain_on_umbrella",
                    () -> SoundEvent.createVariableRangeEvent(UmbrellaMod.id("rain_on_umbrella")));

    public static final Supplier<SoundEvent> UMBRELLA_OPEN =
            Services.PLATFORM.register(Registries.SOUND_EVENT, "umbrella_open",
                    () -> SoundEvent.createVariableRangeEvent(UmbrellaMod.id("umbrella_open")));

    public static void init() {}
}
