package me.rochblondiaux.shard.server;

import org.jspecify.annotations.NonNull;

import com.github.retrooper.packetevents.protocol.ProtocolVersion;

import io.github.retrooper.packetevents.impl.netty.manager.protocol.ProtocolManagerAbstract;

public class ProtocolManager extends ProtocolManagerAbstract {

    @Override
    public @NonNull ProtocolVersion getPlatformVersion() {
        return ProtocolVersion.UNKNOWN;
    }

}
