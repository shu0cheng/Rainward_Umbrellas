package com.shu0cheng.umbrellamod.block;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public final class ModBlockEntities {
    private ModBlockEntities() {
    }

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, UmbrellaMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PlacedUmbrellaBlockEntity>> PLACED_UMBRELLA =
            BLOCK_ENTITY_TYPES.register("placed_umbrella", ModBlockEntities::createType);

    @SuppressWarnings("unchecked")
    private static BlockEntityType<PlacedUmbrellaBlockEntity> createType() {
        try {
            Class<?> supplierType = Class.forName("net.minecraft.world.level.block.entity.BlockEntityType$BlockEntitySupplier");
            Object supplier = Proxy.newProxyInstance(
                    supplierType.getClassLoader(),
                    new Class<?>[]{supplierType},
                    (proxy, method, args) -> new PlacedUmbrellaBlockEntity(
                            (BlockPos) args[0], (BlockState) args[1]));
            Method of = BlockEntityType.Builder.class.getMethod("of", supplierType, Block[].class);
            Object builder = of.invoke(null, supplier, new Block[]{
                    ModBlocks.TELESCOPIC_UMBRELLA.get(),
                    ModBlocks.STRAIGHT_UMBRELLA.get(),
                    ModBlocks.OIL_PAPER_UMBRELLA.get()
            });
            Method build = builder.getClass().getMethod("build", com.mojang.datafixers.types.Type.class);
            return (BlockEntityType<PlacedUmbrellaBlockEntity>) build.invoke(builder, new Object[]{null});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
