package com.terriblefriends.oldenchcracker;

import com.seedfinding.latticg.RandomReverser;
import com.terriblefriends.oldenchcracker.versions.Version;

import java.util.ArrayList;

public class ExtremesCracker {
    private final int[] advances;
    private final boolean[] isLow;
    private final Version version;
    private boolean failed = false;
    private long result = -1;


    public ExtremesCracker(int[] advances, boolean[] isLow, Version version) {
        this.advances = advances;
        this.isLow = isLow;
        this.version = version;
    }

    public void run() {
        RandomReverser reverser = new RandomReverser(new ArrayList<>(0));

        for (int i = 0; i < advances.length; i++) {
            this.version.reverseExtremes(reverser, advances[i], isLow[i]);
        }

        long[] seeds = reverser.findAllValidSeeds().toArray();

        if (seeds.length > 1) {
            System.out.println("ERROR! Found more than 1 valid seed!");
            for (long l : seeds) {
                System.out.println(l + "L");
            }
            System.out.println("Using the first and hoping everything's alright...");
        } else if (seeds.length == 0) {
            System.out.println("ERROR! Failed to find valid seed!");
            failed = true;
            return;
        }

        long resultSeed = seeds[0];

        ReadableRandom advanceToPresent = new ReadableRandom(resultSeed);

        int total = -1;//need this to be -1 to cancel out the (0+1) of the hardcoded first slot of advances

        for (int i : advances) {
            total += (i + 1);
        }

        for (int i = 0; i < total; i++) {
            advanceToPresent.nextLong();
            this.version.getEnchantLevels(advanceToPresent, 30);
            advanceToPresent.nextLong();
            this.version.getEnchantLevels(advanceToPresent, 30);
            advanceToPresent.nextLong();
            this.version.getEnchantLevels(advanceToPresent, 30);
        }
        advanceToPresent.nextLong();
        this.version.getEnchantLevels(advanceToPresent, 30);
        advanceToPresent.nextLong();
        this.version.getEnchantLevels(advanceToPresent, 30);

        this.result = advanceToPresent.getSeed();
    }

    public boolean getFailed() {
        return this.failed;
    }

    public long getResult() {
        return this.result;
    }
}
