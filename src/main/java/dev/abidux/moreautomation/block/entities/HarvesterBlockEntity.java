package dev.abidux.moreautomation.block.entities;

import dev.abidux.moreautomation.block.custom.HarvesterBlock;
import dev.abidux.moreautomation.gui.harvester.HarvesterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class HarvesterBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public HarvesterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.HARVESTER.get(), pPos, pBlockState);
    }

    public void harvest(BlockState state, ServerLevel level, BlockPos pos) {
        BlockPos forward = pos.relative(state.getValue(HarvesterBlock.FACING));
        BlockState forwardState = level.getBlockState(forward);
        List<ItemStack> items = null;
        if (forwardState.getBlock() instanceof CropBlock) {
            items = Block.getDrops(forwardState, level, pos, null);
            BlockState newState = getReplant(items);
            level.setBlock(forward, newState, 2);
        } else if (forwardState.is(Blocks.MELON) || forwardState.is(Blocks.PUMPKIN) || forwardState.is(Blocks.SWEET_BERRY_BUSH)) {
            items = Block.getDrops(forwardState, level, pos, null);
            BlockState newState = forwardState.is(Blocks.SWEET_BERRY_BUSH) ? forwardState.setValue(SweetBerryBushBlock.AGE, Math.min(forwardState.getValue(SweetBerryBushBlock.AGE), 1)) : Blocks.AIR.defaultBlockState();
            level.setBlock(forward, newState, 2);
        }
        if (items != null) {
            storeItems(level, forward, items);
            level.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS);
        }
    }

    private void storeItems(ServerLevel level, BlockPos pos, List<ItemStack> items) {
        for (ItemStack item : items) {
            if (!storeItem(item)) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), item);
            }
        }
    }

    private boolean storeItem(ItemStack stack) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack current = itemHandler.getStackInSlot(i);
            if ((current.isEmpty() || current.getItem() == stack.getItem()) && current.getCount() + stack.getCount() < current.getMaxStackSize()) {
                itemHandler.insertItem(i, stack, false);
                return true;
            }
        }
        return false;
    }

    private BlockState getReplant(List<ItemStack> items) {
        Iterator<ItemStack> iterator = items.iterator();
        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();
            if (stack.getItem() instanceof BlockItem b) {
                if (stack.getCount() == 0) {
                    iterator.remove();
                } else stack.shrink(1);
                return b.getBlock().defaultBlockState();
            }
        }
        return Blocks.AIR.defaultBlockState();
    }

    public void remove() {
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) container.setItem(i, itemHandler.getStackInSlot(i));
        Containers.dropContents(level, getBlockPos(), container);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inventory", itemHandler.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.moreautomationmod.harvester");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new HarvesterMenu(pContainerId, pPlayerInventory, this);
    }
}