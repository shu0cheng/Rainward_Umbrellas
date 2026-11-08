package com.shu0cheng.umbrellamod.neoforge;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.platform.UmbrellaPlatform;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class NeoForgePlatform implements UmbrellaPlatform {
    private final Map<ResourceKey<?>, DeferredRegister<?>> registers = new LinkedHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T, R extends T> Supplier<R> register(ResourceKey<? extends Registry<T>> registry,
                                                String name, Supplier<R> factory) {
        DeferredRegister<T> deferred = (DeferredRegister<T>) registers.computeIfAbsent(registry,
                key -> DeferredRegister.create(registry, UmbrellaMod.MODID));
        return deferred.register(name, factory);
    }

    public void attach(IEventBus bus) {
        registers.values().forEach(register -> register.register(bus));
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> blockEntityType(BlockEntityFactory<T> factory, Block... blocks) {
        return BlockEntityType.Builder.<T>of(factory::create, blocks).build(null);
    }

    @Override
    public CreativeModeTab.Builder creativeTabBuilder() {
        return CreativeModeTab.builder();
    }

    @Override
    public Path configDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
