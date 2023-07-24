package dev.abidux.moreautomation.util;

import net.minecraft.server.level.ServerLevel;

import java.util.Optional;

public class ServerUtil {

    public static Optional<ServerLevel> getLevel(ServerLevel level, String id) {
        if (level.dimension().location().toString().equals(id)) return Optional.of(level);
        for (ServerLevel current : level.getServer().getAllLevels()) {
            if (current.dimension().location().toString().equals(id)) return Optional.of(current);
        }
        return Optional.empty();
    }

}