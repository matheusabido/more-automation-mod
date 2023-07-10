package dev.abidux.moreautomation.gui.filter;

import dev.abidux.moreautomation.block.ModBlocks;
import dev.abidux.moreautomation.block.entities.FilterBlockEntity;
import dev.abidux.moreautomation.gui.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class FilterMenu extends AbstractContainerMenu {
    public final FilterBlockEntity blockEntity;
    public FilterMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public FilterMenu(int containerId, Inventory playerInventory, BlockEntity entity) {
        super(ModMenuTypes.FILTER.get(), containerId);
        this.blockEntity = (FilterBlockEntity) entity;

        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);
        addFilterSlots();
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inventory, i, 8 + i*18, 142));
        }
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 9; i++) {
                addSlot(new Slot(inventory, j*9+i+9, 8+i*18, 84+j*18));
            }
        }
    }

    private void addFilterSlots() {
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            addSlot(new SlotItemHandler(handler, 0, 80, 26));
            addSlot(new SlotItemHandler(handler, 1, 80, 44) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    Slot filterSlot = getSlot(36);
                    return filterSlot.getItem().getItem() == stack.getItem();
                }
            });
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = getSlot(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (index < 36) {
            Slot filterSlot = getSlot(36);
            boolean passFilter = filterSlot.getItem().getItem() == stack.getItem();
            if (!passFilter || !moveItemStackTo(stack, 37, 38, false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, 36, false)) return ItemStack.EMPTY;
        if (stack.getCount() == 0) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return copy;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(pPlayer.level(), blockEntity.getBlockPos()), pPlayer, ModBlocks.FILTER.get());
    }
}