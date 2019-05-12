package net.ml.unsafe.collections.memory.blocks.models;

import lombok.Getter;

/**
 * Reference holds address and length in bytes of object
 *
 * @author micha
 */
@Getter
public class Reference {
    public static final int WORD_SIZE = Long.BYTES;
    public static final int LEN_SIZE = Integer.BYTES;

    /**
     * Constructor
     *
     * @param addr address in memory
     * @param length length in bytes
     */
    public Reference(long addr, int length) {
        this.addr = addr;
        this.length = length;
    }

    /**
     * Size of a reference
     *
     * @return references size
     */
    public static int size() {
        return WORD_SIZE + LEN_SIZE;
    }

    private final long addr;
    private final int length;
}
