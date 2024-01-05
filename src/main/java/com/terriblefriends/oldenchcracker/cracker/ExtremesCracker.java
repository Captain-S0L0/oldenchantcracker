package com.terriblefriends.oldenchcracker.cracker;

import com.seedfinding.latticg.RandomReverser;
import com.terriblefriends.oldenchcracker.EnchantCracker;
import com.terriblefriends.oldenchcracker.EnchantCrackerI18n;
import com.terriblefriends.oldenchcracker.version.Version;

import java.util.Collections;

public class ExtremesCracker {
    private final int[] advances;
    private final boolean[] isLow;
    private final Version version;
    private boolean failed = false;
    private long result = EnchantCracker.SEED_RESULT_UNSET;


    public ExtremesCracker(int[] advances, boolean[] isLow, Version version) {
        this.advances = advances;
        this.isLow = isLow;
        this.version = version;
    }

    public void run() {
        RandomReverser reverser = new RandomReverser(Collections.emptyList());

        for (int i = 0; i < advances.length; i++) {
            this.version.reverseExtremes(reverser, advances[i], isLow[i]);
        }

        long[] seeds = reverser.findAllValidSeeds().toArray();

        if (seeds.length > 1) {
            System.err.println(EnchantCrackerI18n.translate("cracker.error.multipleseeds"));
            for (long l : seeds) {
                System.err.println(l + "L");
            }
            this.failed = true;
            return;
        } else if (seeds.length == 0) {
            this.failed = true;
            return;
        }

        long resultSeed = seeds[0];

        ReadableRandom advanceToPresent = new ReadableRandom(resultSeed);

        //need this to be -1 to cancel out the (0+1) of the hardcoded first slot of advances
        int total = -1;

        for (int i : advances) {
            total += (i + 1);
        }

        for (int i = 0; i < (total * 3)+2; i++) {
            advanceToPresent.nextLong();
            this.version.getEnchantLevels(advanceToPresent, 30);
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
