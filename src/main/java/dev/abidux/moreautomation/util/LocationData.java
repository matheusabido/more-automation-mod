package dev.abidux.moreautomation.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

public class LocationData {

    public final String dimension;
    public final int x, y, z;
    public final BlockPos pos;
    public LocationData(String dimension, int x, int y, int z) {
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pos = new BlockPos(x, y, z);
    }

    public static LocationData from(ServerLevel level, BlockPos pos) {
        return from(level.dimension().location().toString(), pos);
    }

    public static LocationData from(String dimension, BlockPos pos) {
        return new LocationData(dimension, pos.getX(), pos.getY(), pos.getZ());
    }

    public static LocationData from(CompoundTag tag) {
        return new LocationData(tag.getString("dimension"), tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
    }

    public void writeTo(CompoundTag tag) {
        tag.putString("dimension", dimension);
        tag.putInt("x", x);
        tag.putInt("y", y);
        tag.putInt("z", z);
    }
}