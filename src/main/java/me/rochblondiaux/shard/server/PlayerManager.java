package me.rochblondiaux.shard.server;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import io.github.retrooper.packetevents.impl.netty.manager.player.PlayerManagerAbstract;
import me.rochblondiaux.shard.entity.Player;

public class PlayerManager extends PlayerManagerAbstract {

    @Override
    public int getPing(@NotNull Object rawPlayer) {
        if (!(rawPlayer instanceof Player player))
            return 0;

        return (int) player.latency();
    }

    @Override
    public @NonNull Object getChannel(@NotNull Object rawPlayer) {
        if (!(rawPlayer instanceof Player player))
            throw new IllegalArgumentException("Expected a Player instance");
        return player.channel();
    }
}
