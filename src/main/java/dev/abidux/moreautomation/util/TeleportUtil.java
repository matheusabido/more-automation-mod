package dev.abidux.moreautomation.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Set;

public class TeleportUtil {

    public static void teleport(LivingEntity entity, ServerLevel level, BlockPos pos, int yOffset) {
        spawnParticles(level, entity.blockPosition());
        entity.teleportTo(level, pos.getX()+.5f, pos.getY() + yOffset, pos.getZ()+.5f, Set.of(), entity.getYRot(), entity.getXRot());
        spawnParticles(level, pos);
    }

    public static void teleport(LivingEntity entity, ServerLevel level, BlockPos pos) {
        teleport(entity, level, pos, 0);
    }

    public static void teleport(List<LivingEntity> entities, ServerLevel level, BlockPos pos, int yOffset) {
        if (entities.size() == 0) return;
        spawnParticles(level, entities.get(0).blockPosition());
        entities.forEach(entity -> entity.teleportTo(level, pos.getX()+.5f, pos.getY() + yOffset, pos.getZ()+.5f, Set.of(), entity.getYRot(), entity.getXRot()));
        spawnParticles(level, pos);
    }

    public static void teleport(List<LivingEntity> entities, ServerLevel level, BlockPos pos) {
        teleport(entities, level, pos, 0);
    }

    public static void spawnParticles(ServerLevel level, BlockPos position) {
        level.sendParticles(ParticleTypes.PORTAL, position.getX()+.5f, position.getY() + 1, position.getZ()+.5f, 15, .25, .25, .25, .25);
        level.playSound(null, position, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS);
    }

}