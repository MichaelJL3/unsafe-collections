package net.ml.unsafe.collections.memory;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public final class FakeMemory implements Memory {
    private static final Map<Long, Byte> memory = new ConcurrentHashMap<>();
    private static final Map<Long, Integer> allocated = new ConcurrentHashMap<>();
    private final Random rand = new Random();
    private long currentAddress = newAddress();

    @Override
    public long malloc(int size) {
        long addr = nextAddress(size);
        log.info("Allocate @{}[{}]", addr, size);

        decomposeBytes(addr, new byte[size], false);
        allocated.put(addr, size);
        return addr;
    }

    @Override
    public long realloc(long address, int prevSize, int size) {
        if (!memory.containsKey(address))
            return malloc(size);

        long addr = nextAddress(size);
        log.info("Reallocate from @{}[{}] to @{}[{}]", address, prevSize, addr, size);

        byte[] bytes = decomposeAddress(address, prevSize);
        byte[] expanded = new byte[size];
        System.arraycopy(bytes, 0, expanded, 0, size);
        free(address);

        decomposeBytes(addr, expanded, false);
        allocated.put(addr, size);
        return addr;
    }

    @Override
    public void free(long address) {
        checkAddress(address);

        int numBytes = allocated.get(address);
        log.info("Free @{}[{}]", address, numBytes);

        for (int i = 0, byteOffset = 0; i < numBytes; ++i, byteOffset += 4) {
            log.info("Free byte @[{}]", address + byteOffset);
            memory.remove(address + byteOffset);
        }

        allocated.remove(address);
    }

    @Override
    public void put(long address, byte[] bytes) {
        log.info("Storing @{}[{}]", address, bytes.length);
        decomposeBytes(address, bytes, true);
    }

    @Override
    public void swap(long addressA, long addressB, int size) {
        log.info("Swapping @{} with @{}", addressA, addressB);
        byte[] bytesA = decomposeAddress(addressA, size);
        byte[] bytesB = decomposeAddress(addressB, size);

        decomposeBytes(addressB, bytesA, true);
        decomposeBytes(addressA, bytesB, true);
    }

    @Override
    public void copy(long addressA, long addressB, int size) {
        log.info("Copying @{} into @{}", addressA, addressB);
        decomposeBytes(addressB, decomposeAddress(addressA, size), true);
    }

    @Override
    public byte[] get(long address, int size) {
        log.info("Retrieving @{}[{}]", address, size);
        return decomposeAddress(address, size);
    }

    private long newAddress() {
        return Math.abs(rand.nextInt(1024));
    }

    private long nextAddress(int size) {
        return (currentAddress += (size * 4));
    }

    private void decomposeBytes(long address, byte[] bytes, boolean check) {
        if (check) checkAddress(address);

        byte b;
        for (int i = 0; i < bytes.length; ++i) {
            b = bytes[i];
            log.info("Storing byte @{}[{}] = {}", address, i, b);
            memory.put(address, b);
            address += 4;
        }
    }

    private byte[] decomposeAddress(long address, int size) {
        byte[] bytes = new byte[size];

        for (int i = 0; i < size; ++i, address += 4) {
            log.info("Retrieving byte @{}[{}]", address, i);
            checkAddress(address);
            bytes[i] = memory.get(address);
        }

        return bytes;
    }

    private void checkAddress(long address) {
        if (!memory.containsKey(address)) {
            log.error("{} not an address in the memory map", address);
            throw new IllegalArgumentException("not a mapped address " + address);
        }
    }
}
