package dev.abidux.moreautomation.gui.treecutter;

import dev.abidux.moreautomation.block.ModBlocks;
import dev.abidux.moreautomation.block.entities.TreeCutterBlockEntity;
import dev.abidux.moreautomation.gui.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class TreeCutterMenu extends AbstractContainerMenu {

    private final TreeCutterBlockEntity entity;
    private final ContainerData data;
    public TreeCutterMenu(int pContainerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(pContainerId, playerInventory, (TreeCutterBlockEntity)playerInventory.player.level().getBlockEntity(buf.readBlockPos()), new SimpleContainerData(4));
    }

    public TreeCutterMenu(int containerId, Inventory playerInventory, TreeCutterBlockEntity entity, ContainerData data) {
        super(ModMenuTypes.TREE_CUTTER.get(), containerId);
        this.entity = entity;
        this.data = data;

        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);
        addTreeCutterSlots();

        addDataSlots(data);
    }

    private void addTreeCutterSlots() {
        entity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
            addSlot(new SlotItemHandler(itemHandler, 0, 26, 28) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) { return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0; }
            });
            addSlot(new SlotItemHandler(itemHandler, 1, 116, 24) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) { return false; }
            });
            addSlot(new SlotItemHandler(itemHandler, 2, 134, 24) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) { return false; }
            });
            addSlot(new SlotItemHandler(itemHandler, 3, 116, 42) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) { return false; }
            });
            addSlot(new SlotItemHandler(itemHandler, 4, 134, 42) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) { return false; }
            });
        });
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

    public int getScaledProgress() {
        int burnTime = data.get(2);
        int maxBurnTime = data.get(3);
        if (burnTime == 0 || maxBurnTime == 0) return 0;
        return Math.max(13*burnTime/maxBurnTime, 1);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = getSlot(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        if (index < 36) {
            if (!moveItemStackTo(stack, 36, 37, false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, 36, false)) return ItemStack.EMPTY;
        if (stack.getCount() == 0) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return stack.copy();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(pPlayer.level(), entity.getBlockPos()), pPlayer, ModBlocks.TREE_CUTTER.get());
    }
}