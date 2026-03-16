package me.rochblondiaux.shard.network;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.player.UserProfile;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import me.rochblondiaux.shard.entity.Player;
import me.rochblondiaux.shard.network.handler.PacketDecoder;
import me.rochblondiaux.shard.network.handler.PacketEncoder;
import me.rochblondiaux.shard.network.handler.PacketFormatter;
import me.rochblondiaux.shard.network.handler.PacketSplitter;
import me.rochblondiaux.shard.server.ProtocolManager;

public class PlayerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        // Create & register user
        User user = new User(channel, ConnectionState.HANDSHAKING, ClientVersion.UNKNOWN, new UserProfile(null, null));
        ProtocolManager.USERS.put(channel, user);

        // Create player
        Player player = new Player(user);

        // Setup pipeline
        PacketDecoder decoder = new PacketDecoder(user, player);
        PacketEncoder encoder = new PacketEncoder(user, player);

        channel.pipeline()
                .addLast("decryption_handler", new ChannelHandlerAdapter() {
                })
                .addLast("packet_splitter", new PacketSplitter())
                .addLast(PacketEvents.DECODER_NAME, decoder)
                .addLast("encryption_handler", new ChannelHandlerAdapter() {
                })
                .addLast("packet_formatter", new PacketFormatter())
                .addLast(PacketEvents.ENCODER_NAME, encoder);
    }

}
