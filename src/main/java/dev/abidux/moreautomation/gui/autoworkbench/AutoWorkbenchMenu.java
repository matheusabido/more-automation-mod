package dev.abidux.moreautomation.gui.autoworkbench;

import dev.abidux.moreautomation.block.ModBlocks;
import dev.abidux.moreautomation.block.entities.AutoWorkbenchBlockEntity;
import dev.abidux.moreautomation.gui.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class AutoWorkbenchMenu extends AbstractContainerMenu {
    public final Inventory playerInventory;
    public final AutoWorkbenchBlockEntity blockEntity;
    public final ContainerData data;
    public AutoWorkbenchMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(buf.readBlockPos()), new SimpleContainerData(2));
    }

    public AutoWorkbenchMenu(int containerId, Inventory playerInventory, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.AUTO_WORKBENCH.get(), containerId);
        this.playerInventory = playerInventory;
        this.blockEntity = (AutoWorkbenchBlockEntity) entity;
        this.data = data;

        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);
        addAutoWorkbenchSlots();

        addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = getSlot(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (index < 36) {
            if (!moveItemStackTo(stack, 45, 54, false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, 36, false)) return ItemStack.EMPTY;
        if (stack.getCount() == 0) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), blockEntity.getBlockPos()), player, ModBlocks.AUTO_WORKBENCH.get());
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

    private void addAutoWorkbenchSlots() {
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            for (int j = 0; j < 3; j++) {
                for (int i = 0; i < 3; i++) {
                    addSlot(new SlotItemHandler(handler, i+j*3, 8+i*18, 17+j*18));
                }
            }
            for (int j = 0; j < 3; j++) {
                for (int i = 0; i < 3; i++) {
                    addSlot(new SlotItemHandler(handler, 9+i+j*3, 116+i*18, 17+j*18));
                }
            }
            addSlot(new SlotItemHandler(handler, 18, 80, 35));
        });
    }

    public int getScaledProgress() {
        int progress = data.get(0);
        int maxProgress = data.get(1);
        return progress * 14 / Math.max(1, maxProgress);
    }
}