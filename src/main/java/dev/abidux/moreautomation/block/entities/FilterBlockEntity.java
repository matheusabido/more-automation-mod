package dev.abidux.moreautomation.block.entities;

import dev.abidux.moreautomation.block.ModBlockEntities;
import dev.abidux.moreautomation.gui.filter.FilterMenu;
import dev.abidux.moreautomation.wrapper.HopperWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FilterBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final HopperWrapper hopperWrapper = new HopperWrapper(itemHandler,
            (slot, stack) -> slot == 1 && itemHandler.getStackInSlot(0).getItem() == stack.getItem(),
            (slot, amount) -> slot == 1);
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyHopperWrapper = LazyOptional.empty();

    public FilterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FILTER.get(), pPos, pBlockState);
    }

    public void remove() {
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) container.setItem(i, itemHandler.getStackInSlot(i));
        Containers.dropContents(level, getBlockPos(), container);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return side == null ? lazyItemHandler.cast() : lazyHopperWrapper.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyHopperWrapper = LazyOptional.of(() -> hopperWrapper);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inventory", itemHandler.serializeNBT());
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyHopperWrapper.invalidate();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.moreautomationmod.filter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new FilterMenu(pContainerId, pPlayerInventory, this);
    }
}