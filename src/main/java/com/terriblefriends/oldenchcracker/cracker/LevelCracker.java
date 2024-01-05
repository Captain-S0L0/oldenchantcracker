package com.terriblefriends.oldenchcracker.cracker;

import com.seedfinding.latticg.RandomReverser;
import com.terriblefriends.oldenchcracker.EnchantCracker;
import com.terriblefriends.oldenchcracker.EnchantCrackerI18n;
import com.terriblefriends.oldenchcracker.version.Version;

import java.util.ArrayList;

public class LevelCracker {
    private final int[][] levelData;
    private final Version version;
    private boolean failed = false;
    private long result = EnchantCracker.SEED_RESULT_UNSET;


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
            System.err.println(EnchantCrackerI18n.translate("cracker.error.multipleseeds"));
            for (long l : seeds) {
                System.err.println(l + "L");
            }
            this.failed = true;
            this.result = EnchantCracker.SEED_RESULT_MANYFOUND;
            return;
        } else if (seeds.length == 0) {
            this.failed = true;
            this.result = EnchantCracker.SEED_RESULT_NOTFOUND;
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
