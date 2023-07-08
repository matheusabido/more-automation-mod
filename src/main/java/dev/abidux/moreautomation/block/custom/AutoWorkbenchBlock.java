package dev.abidux.moreautomation.block.custom;

import dev.abidux.moreautomation.block.entities.AutoWorkbenchBlockEntity;
import dev.abidux.moreautomation.block.entities.ModBlockEntities;
import dev.abidux.moreautomation.block.entities.PlacerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class AutoWorkbenchBlock extends BaseEntityBlock {
    public AutoWorkbenchBlock() {
        super(BlockBehaviour.Properties.of().ignitedByLava().strength(2.5f).sound(SoundType.WOOD).mapColor(MapColor.WOOD));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof AutoWorkbenchBlockEntity entity) {
            NetworkHooks.openScreen((ServerPlayer) player, entity, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void onRemove(BlockState state, Level pLevel, BlockPos pos, BlockState nState, boolean isMoving) {
        AutoWorkbenchBlockEntity entity = (AutoWorkbenchBlockEntity) pLevel.getBlockEntity(pos);
        entity.remove();
        pLevel.updateNeighbourForOutputSignal(pos, this);
        super.onRemove(state, pLevel, pos, nState, isMoving);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        AutoWorkbenchBlockEntity entity = (AutoWorkbenchBlockEntity) pLevel.getBlockEntity(pPos);
        IItemHandler handler = entity.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().get();
        SimpleContainer container = new SimpleContainer(9);
        for (int i = 9; i < 18; i++) {
            container.setItem(i-9, handler.getStackInSlot(i));
        }
        return AbstractContainerMenu.getRedstoneSignalFromContainer(container);
    }

    /* BLOCK ENTITY */

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.AUTO_WORKBENCH.get(), AutoWorkbenchBlockEntity::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new AutoWorkbenchBlockEntity(pPos, pState);
    }
}