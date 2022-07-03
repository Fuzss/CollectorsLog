package at.petrak.collectorslog.fabric;

import at.petrak.collectorslog.CollectorsLogConfig;
import at.petrak.collectorslog.api.CollectorsLogAPI;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;

import java.io.*;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

public class FabricConfig implements CollectorsLogConfig {
    private static final PropertyMirror<Set<String>> denylist = PropertyMirror.create(
        ConfigTypes.makeSet(ConfigTypes.STRING));

    @Override
    public boolean isItemAllowed(Item item) {
        return !denylist.getValue().contains(Registry.ITEM.getKey(item).toString());
    }

    private static void writeDefaultConfig(ConfigTree config, Path path, JanksonValueSerializer serializer) {
        try (OutputStream s = new BufferedOutputStream(
            Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW))) {
            FiberSerialization.serialize(config, s, serializer);
        } catch (FileAlreadyExistsException ignored) {
        } catch (IOException e) {
            CollectorsLogAPI.LOGGER.error("Error writing default config", e);
        }
    }

    private static void setupConfig(ConfigTree config, Path p, JanksonValueSerializer serializer) {
        writeDefaultConfig(config, p, serializer);

        try (InputStream s = new BufferedInputStream(
            Files.newInputStream(p, StandardOpenOption.READ, StandardOpenOption.CREATE))) {
            FiberSerialization.deserialize(config, s, serializer);
        } catch (IOException | ValueDeserializationException e) {
            CollectorsLogAPI.LOGGER.error("Error loading config from {}", p, e);
        }
    }

    public static void setup() {
        try {
            Files.createDirectory(Paths.get("config"));
        } catch (FileAlreadyExistsException ignored) {
        } catch (IOException e) {
            CollectorsLogAPI.LOGGER.warn("Failed to make config dir", e);
        }

        var serializer = new JanksonValueSerializer(false);
        var builder = ConfigTree.builder();
        var client = builder.beginValue("denylist", ConfigTypes.makeSet(ConfigTypes.STRING),
                new HashSet<String>(CollectorsLogConfig.DEFAULT_DENYLIST))
            .withComment("Items that won't show up in the collector's log")
            .finishValue(denylist::mirror);
        setupConfig(client.build(), Paths.get("config", CollectorsLogAPI.MOD_ID + "-client.json5"), serializer);
    }
}
