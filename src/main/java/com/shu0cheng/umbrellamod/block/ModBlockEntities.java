package com.shu0cheng.umbrellamod.block;

import com.mojang.datafixers.types.Type;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
   public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "umbrella");
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PlacedUmbrellaBlockEntity>> PLACED_UMBRELLA = BLOCK_ENTITY_TYPES.register(
      "placed_umbrella", ModBlockEntities::createType
   );

   private ModBlockEntities() {
   }

   private static BlockEntityType<PlacedUmbrellaBlockEntity> createType() {
      try {
         Class<?> supplierType = Class.forName("net.minecraft.world.level.block.entity.BlockEntityType$BlockEntitySupplier");
         Object supplier = Proxy.newProxyInstance(
            supplierType.getClassLoader(),
            new Class[]{supplierType},
            (proxy, method, args) -> new PlacedUmbrellaBlockEntity((BlockPos)args[0], (BlockState)args[1])
         );
         Method of = Builder.class.getMethod("of", supplierType, Block[].class);
         Object builder = of.invoke(
            null,
            supplier,
            new Block[]{(Block)ModBlocks.TELESCOPIC_UMBRELLA.get(), (Block)ModBlocks.STRAIGHT_UMBRELLA.get(), (Block)ModBlocks.OIL_PAPER_UMBRELLA.get(), (Block)ModBlocks.BAMBOO_OIL_PAPER_UMBRELLA.get()}
         );
         Method build = builder.getClass().getMethod("build", Type.class);
         return (BlockEntityType<PlacedUmbrellaBlockEntity>)build.invoke(builder, (Object)null);
      } catch (Exception var5) {
         throw new RuntimeException(var5);
      }
   }
}
