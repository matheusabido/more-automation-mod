package dev.abidux.moreautomation.block.entities;

import dev.abidux.moreautomation.block.ModBlockEntities;
import dev.abidux.moreautomation.block.custom.TransporterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

public class TransporterBlockEntity extends BlockEntity {
    public TransporterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.TRANSPORTER.get(), pPos, pBlockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TransporterBlockEntity entity) {
        if (level.isClientSide()) return;
        Direction direction = state.getValue(TransporterBlock.FACING);
        BlockPos front = pos.relative(direction);
        BlockPos back = pos.relative(direction.getOpposite());
        BlockEntity frontEntity = level.getBlockEntity(front);
        if (frontEntity == null) return;
        BlockEntity backEntity = level.getBlockEntity(back);
        if (backEntity == null) return;
        frontEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.DOWN).ifPresent(frontHandler -> {
            backEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, calculateSide(pos, back)).ifPresent(backHandler -> {
                tryTransferItem(frontHandler, backHandler);
            });
        });
    }

    private static void tryTransferItem(IItemHandler from, IItemHandler to) {
        for (int i = 0; i < from.getSlots(); i++) {
            ItemStack fromStack = from.getStackInSlot(i);
            if (!fromStack.isEmpty() && !from.extractItem(i, 1, true).isEmpty()) {
                ItemStack insertItem = fromStack.copy();
                insertItem.setCount(1);
                if (tryInserting(to, insertItem)) {
                    from.extractItem(i, 1, false);
                    break;
                }
            }
        }
    }

    private static boolean tryInserting(IItemHandler to, ItemStack fromStack) {
        for (int i = 0; i < to.getSlots(); i++) {
            if (to.insertItem(i, fromStack, false).isEmpty()) return true;
        }
        return false;
    }

    private static Direction calculateSide(BlockPos pos, BlockPos reference) {
        if (pos.getX() != reference.getX())
            return pos.getX() > reference.getX() ? Direction.EAST : Direction.WEST;
        if (pos.getZ() != reference.getZ())
            return pos.getZ() > reference.getZ() ? Direction.SOUTH : Direction.NORTH;
        return pos.getY() > reference.getY() ? Direction.UP : Direction.DOWN;
    }
}