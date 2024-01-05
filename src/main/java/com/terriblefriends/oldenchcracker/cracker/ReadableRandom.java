package com.terriblefriends.oldenchcracker.cracker;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ReadableRandom extends Random {
    private static final long RANDOM_MULTIPLIER = 0x5DEECE66DL;
    private static final long RANDOM_MASK = (1L << 48) - 1;
    private static final long RANDOM_ADDEND = 0xBL;

    private final AtomicLong readableSeed;

    public ReadableRandom(long seed) {
        this.readableSeed = new AtomicLong(seed);
    }

    @Override
    protected int next(int bits) {
        long oldseed, nextseed;
        AtomicLong seed = this.readableSeed;
        do {
            oldseed = seed.get();
            nextseed = (oldseed * RANDOM_MULTIPLIER + RANDOM_ADDEND) & RANDOM_MASK;
        } while (!seed.compareAndSet(oldseed, nextseed));
        return (int)(nextseed >>> (48 - bits));
    }

    public long getSeed() {
        return this.readableSeed.get();
    }

    synchronized public void setRSeed(long seed) {
        this.readableSeed.set(seed);
    }
}
