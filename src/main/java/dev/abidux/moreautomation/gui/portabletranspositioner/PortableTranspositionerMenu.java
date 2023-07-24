package dev.abidux.moreautomation.gui.portabletranspositioner;

import dev.abidux.moreautomation.gui.ModMenuTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class PortableTranspositionerMenu extends AbstractContainerMenu {

    private ItemStackHandler handler;

    public PortableTranspositionerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readItem());
    }

    public PortableTranspositionerMenu(int containerId, Inventory playerInventory, ItemStack stack) {
        super(ModMenuTypes.PORTABLE_TRANSPOSITIONER.get(), containerId);
        this.handler = createHandler(stack);

        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);
        addSlots();
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

    private void addSlots() {
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 3; i++) {
                addSlot(new SlotItemHandler(handler, i+j*3, 62+18*i, 17+18*j) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return stack.is(Items.ENDER_PEARL);
                    }
                });
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        Slot slot = getSlot(pIndex);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        if (pIndex < 36) {
            if (!moveItemStackTo(stack, 36, 45, false)) return ItemStack.EMPTY;
        } else if (pIndex < 45) {
            if (!moveItemStackTo(stack, 0, 36, false)) return ItemStack.EMPTY;
        }
        if (stack.getCount() == 0) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(pPlayer, stack);
        return stack.copy();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    private ItemStackHandler createHandler(ItemStack stack) {
        ItemStackHandler handler = new ItemStackHandler(9) {
            @Override
            protected void onContentsChanged(int slot) {
                updateNBT(stack);
            }
        };
        CompoundTag tag = stack.getOrCreateTag();
        for (int i = 0; i < handler.getSlots(); i++) {
            handler.setStackInSlot(i, ItemStack.of(tag.getCompound(String.valueOf(i))));
        }
        return handler;
    }

    private void updateNBT(ItemStack stack) {
        if (handler == null) return;
        CompoundTag tag = stack.getOrCreateTag();
        for (int i = 0; i < handler.getSlots(); i++) {
            tag.put(String.valueOf(i), handler.getStackInSlot(i).serializeNBT());
        }
        stack.setTag(tag);
    }
}