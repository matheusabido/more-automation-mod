package dev.abidux.moreautomation.block.entities;

import dev.abidux.moreautomation.gui.autoworkbench.AutoWorkbenchMenu;
import dev.abidux.moreautomation.wrapper.HopperWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class AutoWorkbenchBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(19) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final HopperWrapper hopperWrapper = new HopperWrapper(itemHandler,
            (slot, item) -> slot >= 9 && slot <= 17,
            (slot, amount) -> slot == 18);

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<HopperWrapper> lazyHopperWrapper = LazyOptional.empty();
    public ItemStack craftingItem = ItemStack.EMPTY;
    private NonNullList<Ingredient> craftingIngredients;
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 80;
    public AutoWorkbenchBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.AUTO_WORKBENCH.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0 -> AutoWorkbenchBlockEntity.this.progress;
                    case 1 -> AutoWorkbenchBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0 -> AutoWorkbenchBlockEntity.this.progress = value;
                    case 1 -> AutoWorkbenchBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return side == null ? lazyItemHandler.cast() : lazyHopperWrapper.cast();
        }
        return super.getCapability(cap, side);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, AutoWorkbenchBlockEntity e) {
        e.updateRecipe();
        if (level.isClientSide()) return;
        if (!e.craftingItem.isEmpty() && e.canCraft() && e.hasMaterial()) {
            e.progress++;
            if (e.progress >= e.maxProgress) {
                e.progress = 0;
                e.craft();
            }
        } else e.progress = 0;
        e.setChanged();
    }

    public boolean canCraft() {
        ItemStack stack = itemHandler.getStackInSlot(18);
        int count = stack.getCount();
        boolean same = stack.getItem() == craftingItem.getItem();
        boolean fits = count + craftingItem.getCount() <= craftingItem.getMaxStackSize();
        return stack.isEmpty() || (same && fits);
    }

    public void updateRecipe() {
        CraftingContainer container = new CraftingContainer() {
            @Override
            public int getWidth() {return 3;}
            @Override
            public int getHeight() {return 3;}
            @Override
            public List<ItemStack> getItems() {
                List<ItemStack> list = new ArrayList<>();
                for (int i = 0; i < 9; i++) list.add(Optional.of(itemHandler.getStackInSlot(i)).orElse(ItemStack.EMPTY));
                return list;
            }
            @Override
            public int getContainerSize() {return getWidth() * getHeight();}
            @Override
            public boolean isEmpty() {return false;}
            @Override
            public ItemStack getItem(int pSlot) {
                return getItems().get(pSlot);
            }
            @Override
            public ItemStack removeItem(int pSlot, int pAmount) {return ItemStack.EMPTY;}
            @Override
            public ItemStack removeItemNoUpdate(int pSlot) {return ItemStack.EMPTY;}
            @Override
            public void setItem(int pSlot, ItemStack pStack) {}
            @Override
            public void setChanged() {}
            @Override
            public boolean stillValid(Player pPlayer) {return true;}
            @Override
            public void clearContent() {}
            @Override
            public void fillStackedContents(StackedContents pHelper) {}
        };
        Optional<CraftingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, container, level);
        if (recipe.isPresent()) {
            CraftingRecipe r = recipe.get();
            craftingItem = r.getResultItem(RegistryAccess.EMPTY).copy();
            craftingIngredients = r.getIngredients();
        } else {
            craftingItem = ItemStack.EMPTY.copy();
        }
    }

    public void craft() {
        extractIngredients();
        itemHandler.insertItem(18, craftingItem.copy(), false);
    }

    public void extractIngredients() {
        HashMap<Item, Integer> itemCount = countIngredients(craftingIngredients);
        for (int i = 9; i < 18 && itemCount.size() > 0; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!itemCount.containsKey(stack.getItem())) continue;
            int needs = itemCount.get(stack.getItem());
            int amount = Math.min(stack.getCount(), needs);
            itemHandler.extractItem(i, amount, false);
            int remaining = needs - amount;
            if (remaining > 0) {
                itemCount.put(stack.getItem(), remaining);
            } else itemCount.remove(stack.getItem());
        }
    }

    public boolean hasMaterial() {
        HashMap<Item, Integer> itemCount = countIngredients(craftingIngredients);
        return checkContents(itemCount);
    }

    private boolean checkContents(HashMap<Item, Integer> itemCount) {
        for (int i = 9; i < 18 && itemCount.size() > 0; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!itemCount.containsKey(stack.getItem())) continue;
            int remaining = itemCount.get(stack.getItem()) - stack.getCount();
            if (remaining > 0) {
                itemCount.put(stack.getItem(), remaining);
            } else itemCount.remove(stack.getItem());
        }
        return itemCount.size() == 0;
    }

    private HashMap<Item, Integer> countIngredients(NonNullList<Ingredient> ingredients) {
        HashMap<Item, Integer> map = new HashMap<>();
        for (Ingredient ingredient : ingredients) {
            ItemStack stack = ingredient.getItems()[0];
            map.put(stack.getItem(), map.getOrDefault(stack.getItem(), 0) + stack.getCount());
        }
        return map;
    }

    public void remove() {
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) container.setItem(i, itemHandler.getStackInSlot(i));
        Containers.dropContents(level, getBlockPos(), container);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyHopperWrapper = LazyOptional.of(() -> hopperWrapper);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("progress", progress);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        progress = tag.getInt("progress");
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyHopperWrapper.invalidate();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.moreautomationmod.auto_workbench");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AutoWorkbenchMenu(containerId, playerInventory, this, this.data);
    }
}