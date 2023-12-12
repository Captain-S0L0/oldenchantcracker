package com.terriblefriends.oldenchcracker;

import com.seedfinding.latticg.RandomReverser;
import com.terriblefriends.oldenchcracker.versions.Version;

import java.util.ArrayList;

public class LevelCracker {
    private final int[][] levelData;
    private final Version version;
    private boolean failed = false;
    private long result = -1;


    public LevelCracker(int[][] levelData, Version version) {
        this.levelData = levelData;
        this.version = version;
    }

    public void run() {
        RandomReverser reverser = new RandomReverser(new ArrayList<>(0));

        for (int[] cycle : levelData) {
            this.version.reverseLevels(reverser, cycle);
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

        ReadableRandom advanceToPresent = new ReadableRandom(seeds[0]);

        for (int cycles = 0; cycles < 32; cycles++) {
            advanceToPresent.nextLong();
            version.getEnchantLevels(advanceToPresent, 0);
        }

        this.result = advanceToPresent.getSeed();
    }

    public boolean getFailed() {
        return this.failed;
    }

    public long getResult() {
        return this.result;
    }
}
