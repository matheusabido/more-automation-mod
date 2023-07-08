package dev.abidux.moreautomation.block.custom;

import dev.abidux.moreautomation.block.entities.HarvesterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class HarvesterBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public static final int TRIGGER_DURATION = 4;
    public HarvesterBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).sound(SoundType.METAL).strength(3.5f).requiresCorrectToolForDrops());
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, false));
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, TRIGGERED);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand pHand, BlockHitResult pHit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof HarvesterBlockEntity entity) {
            NetworkHooks.openScreen((ServerPlayer) player, entity, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pFromPos, boolean pIsMoving) {
        boolean hasSignal = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
        boolean isTriggered = state.getValue(TRIGGERED);
        if (hasSignal && !isTriggered) {
            level.scheduleTick(pos, this, TRIGGER_DURATION);
            level.setBlock(pos, state.setValue(TRIGGERED, true), 4);
        } else if (!hasSignal && isTriggered) {
            level.setBlock(pos, state.setValue(TRIGGERED, false), 4);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof HarvesterBlockEntity e) {
            e.harvest(state, level, pos);
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            HarvesterBlockEntity entity = (HarvesterBlockEntity) pLevel.getBlockEntity(pPos);
            entity.remove();
            pLevel.updateNeighbourForOutputSignal(pPos, this);
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        HarvesterBlockEntity entity = (HarvesterBlockEntity) pLevel.getBlockEntity(pPos);
        IItemHandler handler = entity.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().get();
        SimpleContainer container = new SimpleContainer(2);
        for (int i = 0; i < handler.getSlots(); i++) {
            container.setItem(i, handler.getStackInSlot(i));
        }
        return AbstractContainerMenu.getRedstoneSignalFromContainer(container);
    }

    /* BLOCK ENTITY */

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new HarvesterBlockEntity(pPos, pState);
    }
}