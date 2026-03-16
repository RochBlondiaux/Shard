package me.rochblondiaux.shard.listener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.status.client.WrapperStatusClientPing;
import com.github.retrooper.packetevents.wrapper.status.server.WrapperStatusServerPong;
import com.github.retrooper.packetevents.wrapper.status.server.WrapperStatusServerResponse;
import com.google.gson.JsonObject;

import me.rochblondiaux.shard.Shard;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class ServerListPingListener implements PacketListener {

    private static final GsonComponentSerializer GSON_COMPONENT_SERIALIZER = GsonComponentSerializer.gson();

    private final Shard app;
    private final String versionName;
    private final int versionProtocol;

    private String cachedMotd;
    private long lastUpdate;

    public ServerListPingListener(Shard app) {
        this.app = app;
        versionName = PacketEvents.getAPI().getServerManager().getVersion().getReleaseName();
        versionProtocol = PacketEvents.getAPI().getServerManager().getVersion().getProtocolVersion();

        this.updateResponse();
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        User user = event.getUser();

        if (event.getPacketType().equals(PacketType.Status.Client.REQUEST)) {
            WrapperStatusServerResponse response = new WrapperStatusServerResponse(cachedMotd);
            user.sendPacket(response);

            this.updateResponse();
        } else if (event.getPacketType().equals(PacketType.Status.Client.PING)) {
            WrapperStatusClientPing ping = new WrapperStatusClientPing(event);
            long time = ping.getTime();
            System.out.println("Time: " + time);

            WrapperStatusServerPong pong = new WrapperStatusServerPong(time);
            user.sendPacket(pong);

            System.out.println("Successfully sent our pong!");
            user.closeConnection();
        }
    }

    private void updateResponse() {
        if (cachedMotd != null && System.currentTimeMillis() - lastUpdate < this.app.configuration().motd().cacheTtl())
            return; // Cache is still valid

        //This is invoked when the client opens up or refreshes the multiplayer server list menu.
        JsonObject responseComponent = new JsonObject();

        JsonObject versionComponent = new JsonObject();
        versionComponent.addProperty("name", versionName);
        versionComponent.addProperty("protocol", versionProtocol);
        //Add sub component
        responseComponent.add("version", versionComponent);

        JsonObject playersComponent = new JsonObject();
        playersComponent.addProperty("max", this.app.configuration().motd().maxPlayers());
        playersComponent.addProperty("online", 0);
        //Add sub component
        responseComponent.add("players", playersComponent);

        //Add sub component
        responseComponent.add("description", GSON_COMPONENT_SERIALIZER.serializeToTree(this.app.configuration().motd().text()).getAsJsonObject());

        cachedMotd = responseComponent.toString();
        lastUpdate = System.currentTimeMillis();
    }

}
