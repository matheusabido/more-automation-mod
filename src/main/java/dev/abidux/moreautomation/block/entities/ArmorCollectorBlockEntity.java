package dev.abidux.moreautomation.block.entities;

import dev.abidux.moreautomation.block.ModBlockEntities;
import dev.abidux.moreautomation.block.ModBlocks;
import dev.abidux.moreautomation.block.custom.ArmorCollectorBlock;
import dev.abidux.moreautomation.gui.armor.ArmorMenu;
import dev.abidux.moreautomation.wrapper.HopperWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class ArmorCollectorBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<HopperWrapper> lazyHopperWrapper = LazyOptional.empty();

    private final HopperWrapper hopperWrapper = new HopperWrapper(itemHandler,
            (slot, item) -> {
                if (!(item.getItem() instanceof ArmorItem armor)) return false;
                switch (armor.getType()) {
                    case BOOTS: return slot == 3;
                    case LEGGINGS: return slot == 2;
                    case CHESTPLATE: return slot == 1;
                    case HELMET: return slot == 0;
                }
                return false;
            },
            (slot, item) -> true);
    public ArmorCollectorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ARMOR_COLLECTOR.get(), pPos, pBlockState);
    }

    public void collect(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos frontPos = pos.relative(state.getValue(ArmorCollectorBlock.FACING));
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(frontPos.getCenter(), .5, .5, .5));
        if (entities.size() == 0) return;

        LivingEntity entity = entities.get(random.nextInt(entities.size()));
        EquipmentSlot[] slots = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
        for (int i = 0; i < slots.length; i++) {
            ItemStack armorCollectorSlot = itemHandler.getStackInSlot(i);
            if (!armorCollectorSlot.isEmpty()) continue;

            EquipmentSlot slot = slots[i];
            ItemStack itemInSlot = entity.getItemBySlot(slot);
            if (itemInSlot.isEmpty() || !(itemInSlot.getItem() instanceof ArmorItem)) continue;

            itemHandler.setStackInSlot(i, itemInSlot);
            entity.setItemSlot(slot, ItemStack.EMPTY);
        }
        level.playSound(null, pos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS);
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
        this.lazyItemHandler = LazyOptional.of(() -> itemHandler);
        this.lazyHopperWrapper = LazyOptional.of(() -> hopperWrapper);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(pTag);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyHopperWrapper.invalidate();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.moreautomationmod.armor_collector");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ArmorMenu(pContainerId, pPlayerInventory, this);
    }
}