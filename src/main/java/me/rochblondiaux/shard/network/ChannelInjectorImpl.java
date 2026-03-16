package me.rochblondiaux.shard.network;

import org.jspecify.annotations.NonNull;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.injector.ChannelInjector;
import com.github.retrooper.packetevents.protocol.player.User;

import io.netty.channel.Channel;
import me.rochblondiaux.shard.network.handler.PacketDecoder;
import me.rochblondiaux.shard.network.handler.PacketEncoder;

public class ChannelInjectorImpl implements ChannelInjector {

    @Override
    public void inject() {

    }

    @Override
    public void uninject() {

    }

    @Override
    public void updateUser(@NonNull Object rawChannel, @NonNull User user) {
        if (!(rawChannel instanceof Channel channel))
            return;

        ((PacketDecoder) channel.pipeline().get(PacketEvents.DECODER_NAME)).user = user;
        ((PacketEncoder) channel.pipeline().get(PacketEvents.ENCODER_NAME)).user = user;
    }

    @Override
    public void setPlayer(@NonNull Object channel, @NonNull Object player) {

    }

    @Override
    public boolean isPlayerSet(@NonNull Object channel) {
        return false;
    }

    @Override
    public boolean isServerBound() {
        return true;
    }

    @Override
    public boolean isProxy() {
        return false;
    }
}
