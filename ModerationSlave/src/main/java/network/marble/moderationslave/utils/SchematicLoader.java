package network.marble.moderationslave.utils;

import org.bukkit.util.Vector;
import org.jnbt.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class SchematicLoader {
    public static Schematic loadSchematic(String path) throws IOException {
        NBTInputStream nbtStream = new NBTInputStream(new FileInputStream(path));
        CompoundTag schematicTag = (CompoundTag) nbtStream.readTag();

        if (!schematicTag.getName().equals("Schematic")) {
            nbtStream.close();
            throw new IllegalArgumentException("Tag \"Schematic\" does not exist or is not first");
        }

        Map<String, Tag> schematic = schematicTag.getValue();
        if (!schematic.containsKey("Blocks")) {
            nbtStream.close();
            throw new IllegalArgumentException("Schematic file is missing a \"Blocks\" tag");
        }

        short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
        short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
        short height = getChildTag(schematic, "Height", ShortTag.class).getValue();


        Vector offset = new Vector();
        try {
            int offsetX = getChildTag(schematic, "WEOffsetX", IntTag.class).getValue();
            int offsetY = getChildTag(schematic, "WEOffsetY", IntTag.class).getValue();
            int offsetZ = getChildTag(schematic, "WEOffsetZ", IntTag.class).getValue();
            offset = new Vector(offsetX, offsetY, offsetZ);
        } catch (Exception e) {
            throw new IllegalArgumentException("Schematic file is missing a \"Offset\" tag");
        }

        String materials = getChildTag(schematic, "Materials", StringTag.class).getValue();
        if (!"Alpha".equals(materials)) {
            nbtStream.close();
            throw new IllegalArgumentException("Schematic file is not an Alpha schematic");
        }

        byte[] blocks = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
        byte[] blockData = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();
        nbtStream.close();
        return new Schematic(blocks, blockData, width, height, length, offset);
    }

    /**
     * Get child tag of a NBT structure.
     *
     * @param items    The parent tag map
     * @param key      The name of the tag to get
     * @param expected The expected type of the tag
     * @return child tag casted to the expected type
     * @throws IllegalArgumentException if the tag does not exist or the tag is not of the expected type
     */
    private static <T extends Tag> T getChildTag(Map<String, Tag> items, String key, Class<T> expected) throws IllegalArgumentException {
        if (!items.containsKey(key)) {
            throw new IllegalArgumentException("Schematic file is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new IllegalArgumentException(key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }
}