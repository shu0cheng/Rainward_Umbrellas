package com.shu0cheng.umbrellamod.item;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

/** Retains recent transition managers and the small set of static poses per umbrella type. */
final class UmbrellaAnimationCache extends AnimatableInstanceCache {
    private static final int MAX_MANAGERS = 64;
    private final Long2ObjectLinkedOpenHashMap<AnimatableManager<?>> managers = new Long2ObjectLinkedOpenHashMap<>();

    UmbrellaAnimationCache(GeoAnimatable animatable) {
        super(animatable);
    }

    void clear() {
        managers.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends GeoAnimatable> AnimatableManager<T> getManagerForId(long id) {
        AnimatableManager<?> manager = managers.getAndMoveToLast(id);
        if (manager == null) {
            if (managers.size() >= MAX_MANAGERS) {
                managers.removeFirst();
            }
            manager = new AnimatableManager<>(animatable);
            managers.put(id, manager);
        }
        return (AnimatableManager<T>) manager;
    }
}
