package me.rochblondiaux.shard;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.manager.server.ServerVersion;

import io.github.retrooper.packetevents.impl.netty.BuildData;
import io.github.retrooper.packetevents.impl.netty.factory.NettyPacketEventsBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.shard.configuration.ServerConfiguration;
import me.rochblondiaux.shard.network.ChannelInjectorImpl;
import me.rochblondiaux.shard.server.PlayerManager;
import me.rochblondiaux.shard.server.ProtocolManager;
import me.rochblondiaux.shard.server.ServerManager;
import me.rochblondiaux.shard.server.ShardServer;

@Slf4j(topic = "Shard")
@Getter
public class Shard {

    public static final ExecutorService WORKER_THREADS = Executors.newFixedThreadPool(2);


    private final Path dataFolder;
    private ServerConfiguration configuration;
    private ShardServer server;

    public Shard() {
        this.dataFolder = Paths.get("").toAbsolutePath();
    }

    public void start() {
        log.info("Starting shard v1.0...");
        long start = System.currentTimeMillis();

        // Configuration
        this.loadConfiguration();

        // Start server
        this.initializeServer();

        log.info("Shard started in {}ms", System.currentTimeMillis() - start);
    }

    public void stop() {
        log.info("Stopping shard...");
        long start = System.currentTimeMillis();

        // Stop server
        this.server.stop();

        log.info("Shard stopped in {}ms", System.currentTimeMillis() - start);
    }

    private void loadConfiguration() {
        log.info("Loading configuration...");

        Path path = dataFolder.resolve("config.yml");

        // Copy default
        if (!Files.exists(path)) {
            try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config.yml")) {
                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Failed to copy default configuration!", e);
            }
        }

        // Load configuration
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(path)
                .build();

        CommentedConfigurationNode root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration!", e);
        }

        try {
            this.configuration = root.get(ServerConfiguration.class);
        } catch (SerializationException e) {
            throw new RuntimeException("Failed to deserialize configuration!", e);
        }
    }

    private void initializeServer() {
        log.info("Initializing server...");
        long start = System.currentTimeMillis();

        BuildData buildData = new BuildData("shard");
        ChannelInjectorImpl channelInjector = new ChannelInjectorImpl();
        ServerManager serverManager = new ServerManager(ServerVersion.V_1_21_11);
        PlayerManager playerManager = new PlayerManager();
        ProtocolManager protocolManager = new ProtocolManager();

        PacketEvents.setAPI(NettyPacketEventsBuilder.build(
                buildData,
                channelInjector,
                protocolManager,
                serverManager,
                playerManager
        ));
        PacketEventsAPI<?> api = PacketEvents.getAPI();
        api.getSettings().debug(true);
        api.load();
        // TODO: Register listeners
        api.init();

        this.server = new ShardServer(this, channelInjector);
        this.server.start();

        log.info("Server initialized in {}ms", System.currentTimeMillis() - start);
    }
}
