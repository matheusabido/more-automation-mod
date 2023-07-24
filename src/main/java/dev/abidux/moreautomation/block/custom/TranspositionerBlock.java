package dev.abidux.moreautomation.block.custom;

import dev.abidux.moreautomation.block.ModBlocks;
import dev.abidux.moreautomation.block.entities.TranspositionerBlockEntity;
import dev.abidux.moreautomation.item.ModItems;
import dev.abidux.moreautomation.util.LocationData;
import dev.abidux.moreautomation.util.ServerUtil;
import dev.abidux.moreautomation.util.TeleportUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class TranspositionerBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public static final int TRIGGER_DURATION = 4;

    public TranspositionerBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(10f).requiresCorrectToolForDrops());
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult pHit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            if (stack.is(ModItems.TRANSPOSITIONER_REMOTE.get())) {
                ServerLevel serverLevel = (ServerLevel) level;
                stack.hurtAndBreak(1, player, event -> event.broadcastBreakEvent(hand));
                CompoundTag tag = stack.getOrCreateTag();
                if (!tag.contains("x")) {
                    LocationData.from(serverLevel, pos).writeTo(tag);
                    stack.setTag(tag);
                    player.sendSystemMessage(Component.translatable("messages.transpositioner.saved").withStyle(ChatFormatting.YELLOW));
                } else {
                    linkTranspositionersUsingRemote(stack, serverLevel, pos, player);
                }
            } else if (stack.is(ModItems.PORTABLE_TRANSPOSITIONER.get())) {
                CompoundTag tag = stack.getOrCreateTag();
                LocationData data = LocationData.from(level.dimension().location().toString(), pos);
                data.writeTo(tag);
                stack.setTag(tag);
                player.sendSystemMessage(Component.translatable("messages.portable_transpositioner.linked").withStyle(ChatFormatting.GREEN));
            }
        }
        if (stack.is(ModItems.TRANSPOSITIONER_REMOTE.get()) || stack.is(ModItems.PORTABLE_TRANSPOSITIONER.get())) return InteractionResult.sidedSuccess(level.isClientSide());
        return super.use(state, level, pos, player, hand, pHit);
    }

    private void linkTranspositionersUsingRemote(ItemStack stack, ServerLevel serverLevel, BlockPos clickedPosition, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        LocationData locationData = LocationData.from(tag);
        Optional<ServerLevel> optionalTargetLevel = ServerUtil.getLevel(serverLevel, locationData.dimension);
        if (optionalTargetLevel.isEmpty()) return;

        ServerLevel targetLevel = optionalTargetLevel.get();
        BlockState targetBlockState = targetLevel.getBlockState(locationData.pos);
        if (targetBlockState.is(ModBlocks.TRANSPOSITIONER.get()) && targetLevel.getBlockEntity(locationData.pos) instanceof TranspositionerBlockEntity entity && serverLevel.getBlockEntity(clickedPosition) instanceof TranspositionerBlockEntity thisEntity) {
            entity.link(serverLevel, clickedPosition);
            thisEntity.link(targetLevel, locationData.pos);
        }
        player.sendSystemMessage(Component.translatable("messages.transpositioner.linked").withStyle(ChatFormatting.GREEN));

        CompoundTag newTag = new CompoundTag();
        newTag.putInt("Damage", tag.getInt("Damage"));
        stack.setTag(newTag);
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
        if (level.getBlockEntity(pos) instanceof TranspositionerBlockEntity entity) {
            if (entity.getLinkedTranspositionerPosition().isEmpty()) return;
            Optional<ServerLevel> optionalTargetLevel = ServerUtil.getLevel(level, entity.getLinkedTranspositionerDimension());

            if (optionalTargetLevel.isEmpty()) return;
            BlockPos frontPosition = pos.relative(state.getValue(FACING));
            ServerLevel targetLevel = optionalTargetLevel.get();
            BlockPos targetPosition = entity.getLinkedTranspositionerPosition().get();
            BlockState targetState = targetLevel.getBlockState(targetPosition);

            if (!targetState.is(ModBlocks.TRANSPOSITIONER.get())) return;
            Direction targetFacingDirection = targetState.getValue(TranspositionerBlock.FACING);
            BlockPos teleportPosition = targetPosition.relative(targetFacingDirection);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(new Vec3(frontPosition.getX() + .5f, frontPosition.getY(), frontPosition.getZ() + .5f), 1, 1, 1));
            TeleportUtil.teleport(entities, targetLevel, teleportPosition, targetFacingDirection.getStepY());
        }
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
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TranspositionerBlockEntity(pPos, pState);
    }
}