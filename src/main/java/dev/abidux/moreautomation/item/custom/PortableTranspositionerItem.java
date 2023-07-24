package dev.abidux.moreautomation.item.custom;

import dev.abidux.moreautomation.block.ModBlocks;
import dev.abidux.moreautomation.block.custom.TranspositionerBlock;
import dev.abidux.moreautomation.gui.portabletranspositioner.PortableTranspositionerMenu;
import dev.abidux.moreautomation.item.ModItems;
import dev.abidux.moreautomation.util.LocationData;
import dev.abidux.moreautomation.util.ServerUtil;
import dev.abidux.moreautomation.util.TeleportUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PortableTranspositionerItem extends Item implements MenuProvider {
    public PortableTranspositionerItem() {
        super(new Item.Properties().stacksTo(1).setNoRepair());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (!pLevel.isClientSide()) {
            ServerPlayer player = (ServerPlayer) pPlayer;
            if (pPlayer.isCrouching()) {
                NetworkHooks.openScreen(player, this, data -> data.writeItemStack(stack, false));
            } else teleport(player, (ServerLevel)pLevel, stack);
        }
        if (pLevel.isClientSide() && pPlayer.isCrouching()) return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
        return InteractionResultHolder.consume(stack);
    }

    private void teleport(ServerPlayer player, ServerLevel level, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("x")) return;

        LocationData locationData = LocationData.from(tag);
        Optional<ServerLevel> optionalTargetLevel = ServerUtil.getLevel(level, locationData.dimension);
        if (optionalTargetLevel.isEmpty()) return;

        ServerLevel targetLevel = optionalTargetLevel.get();
        BlockState targetState = targetLevel.getBlockState(locationData.pos);
        if (!targetState.is(ModBlocks.TRANSPOSITIONER.get())) return;

        Direction transpositionerFacingDirection = targetState.getValue(TranspositionerBlock.FACING);
        BlockPos targetPos = locationData.pos.relative(transpositionerFacingDirection);

        if (!removeEnderPearl(stack)) return;
        TeleportUtil.teleport(player, targetLevel, targetPos, transpositionerFacingDirection.getStepY());
        player.getCooldowns().addCooldown(this, 20);
    }

    private boolean removeEnderPearl(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("0")) return false;
        for (int i = 0; i < 9; i++) {
            ItemStack current = ItemStack.of(tag.getCompound(String.valueOf(i)));
            if (current.is(Items.ENDER_PEARL)) {
                current.shrink(1);
                tag.put(String.valueOf(i), (current.getCount() == 0 ? ItemStack.EMPTY : current).serializeNBT());
                return true;
            }
        }
        return false;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("item.moreautomationmod.portable_transpositioner");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new PortableTranspositionerMenu(pContainerId, pPlayerInventory, getPortableTranspositioner(pPlayer));
    }

    private ItemStack getPortableTranspositioner(Player player) {
        if (player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.PORTABLE_TRANSPOSITIONER.get())) return player.getItemInHand(InteractionHand.MAIN_HAND);
        if (player.getItemInHand(InteractionHand.OFF_HAND).is(ModItems.PORTABLE_TRANSPOSITIONER.get())) return player.getItemInHand(InteractionHand.OFF_HAND);
        return null;
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        CompoundTag tag = pStack.getOrCreateTag();
        if (!tag.contains("0")) return 0;
        int enderPearls = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = ItemStack.of(tag.getCompound(String.valueOf(i)));
            enderPearls += stack.getCount();
        }
        return Math.round(enderPearls * 13.0F / 144);
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return true;
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return 0xc7000d;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
        tooltip.add(Screen.hasShiftDown() ? Component.translatable("tooltip.portable_transpositioner_guide") : Component.translatable("tooltip.press_shift_for_more").withStyle(ChatFormatting.YELLOW));
    }
}