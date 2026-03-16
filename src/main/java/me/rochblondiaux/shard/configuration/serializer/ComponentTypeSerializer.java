package me.rochblondiaux.shard.configuration.serializer;

import java.lang.reflect.Type;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ComponentTypeSerializer implements TypeSerializer<Component> {

    public static final ComponentTypeSerializer INSTANCE = new ComponentTypeSerializer();
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    @Override
    public Component deserialize(@NonNull Type type, @NonNull ConfigurationNode node) {
        if (node.isNull())
            return Component.empty();

        String value = node.getString("");
        return MINI_MESSAGE.deserialize(value);
    }

    @Override
    public void serialize(@NonNull Type type, @Nullable Component obj, @NonNull ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }
        String value = MINI_MESSAGE.serialize(obj);
        node.set(value);
    }
}
