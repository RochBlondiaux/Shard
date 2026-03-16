package me.rochblondiaux.shard.server;

import org.jspecify.annotations.NonNull;

import com.github.retrooper.packetevents.manager.server.ServerVersion;

import io.github.retrooper.packetevents.impl.netty.manager.server.ServerManagerAbstract;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerManager extends ServerManagerAbstract {

    private final ServerVersion version;

    @Override
    public @NonNull ServerVersion getVersion() {
        return version;
    }
}
