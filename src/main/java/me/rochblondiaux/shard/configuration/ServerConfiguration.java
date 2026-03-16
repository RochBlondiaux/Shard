package me.rochblondiaux.shard.configuration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import net.kyori.adventure.text.Component;

@ConfigSerializable
public record ServerConfiguration(String host, int port, Motd motd) {

    @ConfigSerializable
    public record Motd(Component text, int maxPlayers, long cacheTtl) {
    }
}
