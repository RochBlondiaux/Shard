package me.rochblondiaux.shard.entity;

import com.github.retrooper.packetevents.protocol.player.User;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Player {

    private final User user;
    private long latency;

    public Channel channel() {
        return (Channel) user.getChannel();
    }
}
