package me.rochblondiaux.shard.configuration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public record ServerConfiguration(String host, int port) {
}
