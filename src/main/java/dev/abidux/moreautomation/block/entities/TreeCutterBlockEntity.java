package dev.abidux.moreautomation.block.entities;

import dev.abidux.moreautomation.block.ModBlockEntities;
import dev.abidux.moreautomation.block.custom.TreeCutterBlock;
import dev.abidux.moreautomation.gui.treecutter.TreeCutterMenu;
import dev.abidux.moreautomation.wrapper.HopperWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;

public class TreeCutterBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final HopperWrapper hopperWrapper = new HopperWrapper(itemHandler,
            (slot, item) -> slot == 0 && ForgeHooks.getBurnTime(item, RecipeType.SMELTING) > 0,
            (slot, item) -> slot > 0);
    private LazyOptional<IItemHandler> lazyItemHandler;
    private LazyOptional<HopperWrapper> lazyHopperWrapper;
    private final ContainerData data;

    private int progress, maxProgress = 20, burnTime, maxBurnTime;

    public TreeCutterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.TREE_CUTTER.get(), pPos, pBlockState);

        this.data = new SimpleContainerData(4) {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    case 2 -> burnTime;
                    case 3 -> maxBurnTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> TreeCutterBlockEntity.this.progress = pValue;
                    case 1 -> TreeCutterBlockEntity.this.maxProgress = pValue;
                    case 2 -> TreeCutterBlockEntity.this.burnTime = pValue;
                    case 3 -> TreeCutterBlockEntity.this.maxBurnTime = pValue;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TreeCutterBlockEntity entity) {
        BlockPos frontPos = pos.relative(state.getValue(TreeCutterBlock.FACING));
        boolean isFrontLog = level.getBlockState(frontPos).is(BlockTags.LOGS);
        if (entity.burnTime <= 0) {
            if (isFrontLog) entity.updateFuel();
            else entity.progress = 0;
            return;
        }
        // has fuel
        entity.burnTime--;
        entity.progress++;
        if (entity.progress >= entity.maxProgress) {
            entity.progress = 0;

            if (!isFrontLog || level.isClientSide() || entity.isFull()) return;
            BlockPos lastLogPosition = entity.getLastLog(frontPos, new HashSet<>());
            BlockState lastLogState = level.getBlockState(lastLogPosition);

            List<ItemStack> drops = Block.getDrops(lastLogState, (ServerLevel)level, lastLogPosition, null);
            level.setBlock(lastLogPosition, Blocks.AIR.defaultBlockState(), 2);
            if (drops.size() > 0) {
                ItemStack drop = drops.get(0);
                for (int i = 1; i < 5; i++) {
                    if (entity.itemHandler.insertItem(i, drop, false).isEmpty()) break;
                }
            }
        }
    }

    private void updateFuel() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        int stackBurnTime = ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
        if (stackBurnTime <= 0 || isFull()) {
            progress = 0;
            return;
        }
        ItemStack remaining = stack.getCraftingRemainingItem();
        stack.shrink(1);
        if (stack.isEmpty() && !remaining.isEmpty()) {
            itemHandler.setStackInSlot(0, remaining);
        }
        burnTime = Math.max(stackBurnTime/10, 1);
        maxBurnTime = Math.max(stackBurnTime/10, 1);
    }

    private boolean isFull() {
        for (int i = 1; i < 5; i++) {
            ItemStack item = itemHandler.getStackInSlot(i);
            if (item.isEmpty() || item.getCount() < item.getMaxStackSize()) return false;
        }
        return true;
    }

    private BlockPos getLastLog(BlockPos pos, HashSet<BlockPos> visited) {
        BlockPos startPosition = pos;
        int[] stop = {pos.getX()+1, pos.getY()+1, pos.getZ()+1};
        for (int x = pos.getX() - 1; x <= stop[0]; x++) {
            for (int y = pos.getY(); y <= stop[1]; y++) {
                for (int z = pos.getZ() - 1; z <= stop[2]; z++) {
                    boolean isSameBlock = x == pos.getX() && y == pos.getY() && z == pos.getZ();
                    boolean isTooFar = Math.abs(startPosition.getX() - x) > 5 || Math.abs(startPosition.getZ() - z) > 5;
                    if (isSameBlock || isTooFar) continue;
                    BlockPos iterationPos = new BlockPos(x,y,z);
                    if (visited.contains(iterationPos)) continue;
                    BlockState state = level.getBlockState(iterationPos);
                    if (state.is(BlockTags.LOGS)) {
                        x = iterationPos.getX() - 1;
                        y = iterationPos.getY();
                        z = iterationPos.getZ() - 1;
                        stop = new int[]{iterationPos.getX()+1,iterationPos.getY()+1,iterationPos.getZ()+1};
                        visited.add(pos);
                        pos = iterationPos;
                    }
                }
            }
        }
        return pos;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return side == null ? lazyItemHandler.cast() : lazyHopperWrapper.cast();
        }
        return super.getCapability(cap, side);
    }

    public void remove() {
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) container.setItem(i, itemHandler.getStackInSlot(i));
        Containers.dropContents(level, getBlockPos(), container);
    }

    /* LOAD/SAVE */

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        this.progress = pTag.getInt("progress");
        this.burnTime = pTag.getInt("burnTime");
        this.maxBurnTime = pTag.getInt("maxBurnTime");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("progress", progress);
        pTag.putInt("burnTime", burnTime);
        pTag.putInt("maxBurnTime", maxBurnTime);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyHopperWrapper = LazyOptional.of(() -> hopperWrapper);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyHopperWrapper.invalidate();
    }

    /* MENU */

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.moreautomationmod.tree_cutter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new TreeCutterMenu(pContainerId, pPlayerInventory, this, data);
    }
}