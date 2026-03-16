package me.rochblondiaux.shard.server;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollIoHandler;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.shard.Shard;
import me.rochblondiaux.shard.network.ChannelInjectorImpl;
import me.rochblondiaux.shard.network.PlayerChannelInitializer;

@RequiredArgsConstructor
@Slf4j(topic = "ShardServer")
public class ShardServer {

    public static final Set<Channel> SERVER_CHANNELS = new HashSet<>();

    private final Shard app;
    private final ChannelInjectorImpl injector;

    public void start() {
        IoHandlerFactory factory = Epoll.isAvailable()
                ? EpollIoHandler.newFactory()
                : NioIoHandler.newFactory();

        EventLoopGroup bossGroup = new MultiThreadIoEventLoopGroup(1, factory);
        EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(factory);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channelFactory(Epoll.isAvailable() ? EpollServerSocketChannel::new : NioServerSocketChannel::new)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new PlayerChannelInitializer());

            ChannelFutureListener listener = (channelFuture) -> SERVER_CHANNELS.add(channelFuture.channel());
            ChannelFuture future = bootstrap.bind(new InetSocketAddress(app.configuration().host(), app.configuration().port()))
                    .addListener(listener);

            log.info("Server started on {}:{}", app.configuration().host(), app.configuration().port());
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            throw new RuntimeException("Failed to start shard server!", e);
        }
    }

    public void stop() {
        for (Channel channel : SERVER_CHANNELS) {
            try {
                channel.close();
            } catch (Exception e) {
                log.warn("Failed to close channel {}!", channel, e);
            }
        }
    }
}
