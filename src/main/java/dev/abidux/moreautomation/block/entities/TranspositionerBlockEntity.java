package dev.abidux.moreautomation.block.entities;

import dev.abidux.moreautomation.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class TranspositionerBlockEntity extends BlockEntity {
    private String linkedTranspositionerDimension;
    private BlockPos linkedTranspositionerPosition;
    public TranspositionerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.TRANSPOSITIONER.get(), pPos, pBlockState);
    }

    public void link(Level level, BlockPos position) {
        this.linkedTranspositionerDimension = level.dimension().location().toString();
        this.linkedTranspositionerPosition = position;
    }

    public Optional<BlockPos> getLinkedTranspositionerPosition() {
        return Optional.ofNullable(linkedTranspositionerPosition);
    }

    public String getLinkedTranspositionerDimension() {
        return linkedTranspositionerDimension;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("linked_x")) {
            this.linkedTranspositionerPosition = new BlockPos(pTag.getInt("linked_x"), pTag.getInt("linked_y"), pTag.getInt("linked_z"));
            this.linkedTranspositionerDimension = pTag.getString("linked_dimension");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        getLinkedTranspositionerPosition().ifPresent(pos -> {
            pTag.putInt("linked_x", pos.getX());
            pTag.putInt("linked_y", pos.getY());
            pTag.putInt("linked_z", pos.getZ());
            pTag.putString("linked_dimension", linkedTranspositionerDimension);
        });
    }
}