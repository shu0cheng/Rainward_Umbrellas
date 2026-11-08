package com.shu0cheng.umbrellamod.item;

import com.shu0cheng.umbrellamod.block.PlacedUmbrellaBlockEntity;
import com.shu0cheng.umbrellamod.client.UmbrellaAnimation;
import com.shu0cheng.umbrellamod.registry.ModComponents;
import com.shu0cheng.umbrellamod.registry.ModSounds;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import com.shu0cheng.umbrellamod.client.UmbrellaClient;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animation.PlayState;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import com.shu0cheng.umbrellamod.block.ModBlocks;
import com.shu0cheng.umbrellamod.block.PlacedUmbrellaBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

/**
 * An umbrella.  Right click toggles between the folded (closed) and deployed
 * (open) state.  While closed the item is rendered folded, matching the
 * inventory icon.
 */
public class UmbrellaItem extends Item implements GeoItem {
    private final UmbrellaAnimationCache geoCache = new UmbrellaAnimationCache(this);
    private final UmbrellaType type;

    public UmbrellaItem(UmbrellaType type, Properties properties) {
        super(properties);
        this.type = type;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<UmbrellaItem> controller = new AnimationController<>(this, "main", 0, this::animationState);
        controllers.add(controller);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    public void clearClientAnimationCache() {
        geoCache.clear();
    }

    public UmbrellaType getUmbrellaType() {
        return type;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return UmbrellaClient.renderer(type.id() + "_umbrella");
            }
        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        ItemStack stack = player.getItemInHand(hand);
        boolean open = isOpen(stack);
        if (!level.isClientSide) {
            stack.set(ModComponents.UMBRELLA_STATE.get(), new UmbrellaState(!open, level.getGameTime()));
            if (open) {
                player.playSound(SoundEvents.WOOL_BREAK, 0.85F, 1.15F);
            } else {
                level.playSound(null, player.getX(), player.getEyeY(), player.getZ(),
                        ModSounds.UMBRELLA_OPEN.get(), SoundSource.PLAYERS, 0.9F, 1.0F);
            }
        }
        player.getCooldowns().addCooldown(this, 24);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        Direction face = context.getClickedFace();
        BlockPos placePos = context.getClickedPos().relative(face);
        if (level.isOutsideBuildHeight(placePos)
                || !level.getWorldBorder().isWithinBounds(placePos)
                || !level.mayInteract(player, placePos)
                || !player.mayUseItemAt(placePos, face, stack)
                || !level.getBlockState(placePos).canBeReplaced()) {
            return InteractionResult.FAIL;
        }
        boolean open = isOpen(stack);
        boolean inverted = open && player.getXRot() < -45.0F;
        if (!inverted && !level.getBlockState(placePos.below()).isSolid()) {
            return InteractionResult.FAIL;
        }
        boolean leaning = !open && face.getAxis().isHorizontal();
        boolean half = inverted && face.getAxis().isHorizontal()
                && (context.getClickLocation().y - context.getClickedPos().getY()) < 0.5D;
        int rotation = Mth.floor((player.getYRot() + 180.0F) * 16.0F / 360.0F + 0.5F) & 15;
        BlockState state = ModBlocks.forType(type).defaultBlockState()
                .setValue(PlacedUmbrellaBlock.ROTATION, rotation)
                .setValue(PlacedUmbrellaBlock.OPEN, open)
                .setValue(PlacedUmbrellaBlock.INVERTED, inverted)
                .setValue(PlacedUmbrellaBlock.HALF, half)
                .setValue(PlacedUmbrellaBlock.LEANING, leaning);
        if (!level.isClientSide) {
            if (!level.setBlock(placePos, state, Block.UPDATE_ALL)) {
                return InteractionResult.FAIL;
            }
            if (level.getBlockEntity(placePos) instanceof PlacedUmbrellaBlockEntity placed) {
                placed.storeStack(stack);
            }
            level.playSound(null, placePos, state.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        stack.set(ModComponents.UMBRELLA_STATE.get(), UmbrellaState.CLOSED);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!stack.has(ModComponents.UMBRELLA_STATE.get()) && !level.isClientSide) {
            stack.set(ModComponents.UMBRELLA_STATE.get(), UmbrellaState.CLOSED);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        boolean open = isOpen(stack);
        tooltip.add(Component.translatable(open ? "tooltip.umbrella.open" : "tooltip.umbrella.closed")
                .withStyle(open ? ChatFormatting.AQUA : ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.umbrella.hint").withStyle(ChatFormatting.DARK_GRAY));
    }

    public static boolean isOpen(ItemStack stack) {
        UmbrellaState state = stack.get(ModComponents.UMBRELLA_STATE.get());
        return state != null && state.open();
    }

    public static UmbrellaState getState(ItemStack stack) {
        return stack.getOrDefault(ModComponents.UMBRELLA_STATE.get(), UmbrellaState.CLOSED);
    }

    private PlayState animationState(AnimationState<UmbrellaItem> state) {
        return state.setAndContinue(UmbrellaAnimation.animation(type, state.getData(DataTickets.ITEMSTACK),
                state.getData(DataTickets.ITEM_RENDER_PERSPECTIVE)));
    }

    public static boolean isUmbrella(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof UmbrellaItem;
    }
}
