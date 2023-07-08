package dev.abidux.moreautomation.gui.harvester;

import dev.abidux.moreautomation.block.ModBlocks;
import dev.abidux.moreautomation.block.entities.HarvesterBlockEntity;
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

public class HarvesterMenu extends AbstractContainerMenu {
    public final HarvesterBlockEntity blockEntity;
    public HarvesterMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public HarvesterMenu(int containerId, Inventory playerInventory, BlockEntity entity) {
        super(ModMenuTypes.HARVESTER.get(), containerId);
        this.blockEntity = (HarvesterBlockEntity) entity;

        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);
        addHarvesterSlots();
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

    private void addHarvesterSlots() {
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            addSlot(new SlotItemHandler(handler, 0, 80, 26));
            addSlot(new SlotItemHandler(handler, 1, 80, 44));
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = getSlot(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (index < 36) {
            if (!moveItemStackTo(stack, 36, 38, false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, 36, false)) return ItemStack.EMPTY;
        if (stack.getCount() == 0) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return copy;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(pPlayer.level(), blockEntity.getBlockPos()), pPlayer, ModBlocks.HARVESTER.get());
    }
}