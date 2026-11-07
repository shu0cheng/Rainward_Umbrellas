package com.shu0cheng.umbrellamod.block;

import com.shu0cheng.umbrellamod.item.UmbrellaType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

public final class ModBlocks {
   public static final Blocks BLOCKS = DeferredRegister.createBlocks("umbrella");
   public static final DeferredBlock<Block> TELESCOPIC_UMBRELLA = BLOCKS.register(
      "telescopic_umbrella_block", () -> new PlacedUmbrellaBlock(props(), UmbrellaType.TELESCOPIC)
   );
   public static final DeferredBlock<Block> STRAIGHT_UMBRELLA = BLOCKS.register(
      "straight_umbrella_block", () -> new PlacedUmbrellaBlock(props(), UmbrellaType.STRAIGHT)
   );
   public static final DeferredBlock<Block> OIL_PAPER_UMBRELLA = BLOCKS.register(
      "oil_paper_umbrella_block", () -> new PlacedUmbrellaBlock(props(), UmbrellaType.OIL_PAPER)
   );
   public static final DeferredBlock<Block> BAMBOO_OIL_PAPER_UMBRELLA = BLOCKS.register(
      "bamboo_oil_paper_umbrella_block", () -> new PlacedUmbrellaBlock(props(), UmbrellaType.BAMBOO_OIL_PAPER)
   );
   public static final DeferredBlock<UmbrellaPartBlock> UMBRELLA_PART = BLOCKS.register(
      "umbrella_part", () -> new UmbrellaPartBlock(props().randomTicks().noLootTable()));

   private ModBlocks() {
   }

   private static Properties props() {
      return Properties.of().noCollission().noOcclusion().dynamicShape().strength(0.2F).sound(SoundType.WOOD).pushReaction(PushReaction.BLOCK);
   }

   public static Block forType(UmbrellaType type) {
      return switch (type) {
         case TELESCOPIC -> (Block)TELESCOPIC_UMBRELLA.get();
         case STRAIGHT -> (Block)STRAIGHT_UMBRELLA.get();
         case OIL_PAPER -> (Block)OIL_PAPER_UMBRELLA.get();
         case BAMBOO_OIL_PAPER -> (Block)BAMBOO_OIL_PAPER_UMBRELLA.get();
      };
   }
}
