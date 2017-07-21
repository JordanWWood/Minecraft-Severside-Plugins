package network.marble.moderationslave.utils;

import org.bukkit.util.Vector;

public class Schematic {

    private short width;  // X
    private short height; // Y
    private short length; // Z

    private Vector offset;
    private byte[] blocks;
    private byte[] data;

    public Schematic(byte[] blocks, byte[] data, short width, short height, short length, Vector offset) {
        this.width = width;
        this.height = height;
        this.length = length;
        this.blocks = blocks;
        this.data = data;
        this.offset = offset;
    }

    public short getWidth() {
        return width;
    }

    public short getHeight() {
        return height;
    }

    public short getLength() {
        return length;
    }

    public byte[] getBlocks() {
        return blocks;
    }

    public byte[] getData() {
        return data;
    }

    public Vector getOffset() { return offset; }

    public int blockcount() {
        int count = blocks.length;
        for (byte block : blocks) {
            if (block <= 0) count--;
        }
        return count;
    }
}