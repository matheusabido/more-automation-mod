package dev.abidux.moreautomation.gui.placer;

import dev.abidux.moreautomation.block.ModBlocks;
import dev.abidux.moreautomation.block.entities.PlacerBlockEntity;
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

public class PlacerMenu extends AbstractContainerMenu {

    public final PlacerBlockEntity blockEntity;
    public PlacerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public PlacerMenu(int containerId, Inventory playerInventory, BlockEntity entity) {
        super(ModMenuTypes.PLACER.get(), containerId);
        this.blockEntity = (PlacerBlockEntity) entity;
        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);
        addPlacerSlots();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = getSlot(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        if (index < 36) {
            if (!moveItemStackTo(stack, 36, 45, false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, 36, false)) return ItemStack.EMPTY;
        if (stack.getCount() == 0) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return stack.copy();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(pPlayer.level(), blockEntity.getBlockPos()), pPlayer, ModBlocks.PLACER.get());
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

    private void addPlacerSlots() {
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            for (int j = 0; j < 3; j++) {
                for (int i = 0; i < 3; i++) {
                    addSlot(new SlotItemHandler(handler, i+j*3, 62+18*i, 17+18*j));
                }
            }
        });
    }
}