package net.ml.unsafe.collections.memory;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public final class FakeMemory implements Memory {
    private static final Map<Long, Byte> memory = new ConcurrentHashMap<>();
    private static final Map<Long, Integer> allocated = new ConcurrentHashMap<>();

    private static AtomicLong currentAddress = new AtomicLong(newAddress());

    @Override
    public long malloc(int size) {
        long addr = nextAddress(size);
        log.info("Allocate @{}[{}]", addr, size);

        checkAddressUnique(addr, size);

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

        checkAddressUnique(addr, size);

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

        long addr = address;
        for (int i = 0; i < numBytes; ++i, ++addr) {
            log.trace("Free byte @[{}]", addr);
            memory.remove(addr);
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

    private static long newAddress() {
        return 1; //Math.abs(rand.nextInt(1024));
    }

    private long nextAddress(int size) {
        return currentAddress.getAndAdd(size);
    }

    private void decomposeBytes(long address, byte[] bytes, boolean check) {
        if (check) checkAddress(address);

        byte b;
        for (int i = 0; i < bytes.length; ++i, ++address) {
            b = bytes[i];
            log.trace("Storing byte @{}[{}] = {}", address, i, b);
            memory.put(address, b);
        }
    }

    private byte[] decomposeAddress(long address, int size) {
        byte[] bytes = new byte[size];

        for (int i = 0; i < size; ++i, ++address) {
            log.trace("Retrieving byte @{}[{}]", address, i);
            checkAddress(address);
            bytes[i] = memory.get(address);
        }

        return bytes;
    }

    private void checkAddressUnique(long address, int size) {
        for (int i = 0; i < size; ++i) {
            checkAddressUnique(address + i);
        }
    }

    private void checkAddressUnique(long address) {
        if (memory.containsKey(address)) {
            log.error("Address in use @{}", address);
            throw new RuntimeException("Address already in use " + address);
        }
    }

    private void checkAddress(long address) {
        if (!memory.containsKey(address)) {
            log.error("{} not an address in the memory map", address);
            throw new IllegalArgumentException("not a mapped address " + address);
        }
    }
}
