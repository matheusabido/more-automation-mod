package dev.abidux.moreautomation.wrapper;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

public class HopperWrapper implements IItemHandler {

    private final IItemHandler handler;
    private final BiPredicate<Integer, ItemStack> insert;
    private final BiPredicate<Integer, Integer> extract;
    public HopperWrapper(IItemHandler handler, BiPredicate<Integer, ItemStack> insert, BiPredicate<Integer, Integer> extract) {
        this.handler = handler;
        this.insert = insert;
        this.extract = extract;
    }

    @Override
    public int getSlots() {
        return handler.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return handler.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return insert.test(slot, stack) ? handler.insertItem(slot, stack, simulate) : stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return extract.test(slot, amount) ? handler.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return handler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return handler.isItemValid(slot, stack);
    }
}