package com.terriblefriends.oldenchcracker.cracker;

import com.seedfinding.latticg.RandomReverser;
import com.terriblefriends.oldenchcracker.EnchantCracker;
import com.terriblefriends.oldenchcracker.EnchantCrackerI18n;
import com.terriblefriends.oldenchcracker.thingmanager.SeedResults;
import com.terriblefriends.oldenchcracker.version.Version;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GalacticCracker {
    private static final int WORD_LIST_LENGTH = 55;
    private static final long RANDOM_MULTIPLIER = 0x5DEECE66DL;
    private static final long RANDOM_MULTIPLIER_INVERSE = 0xdfe05bcb1365L;
    private static final long RANDOM_DELTA = 0xBL;
    private static final long RANDOM_MASK = ((1L << 48)-1);
    private final int[][] words;
    private final int[] levels;
    private final int books;
    private final Version version;
    private boolean failed = false;
    private long result = SeedResults.SEED_RESULT_UNSET.getValue();


    public GalacticCracker(int[][] words, int[] levels, int books, Version version) {
        this.words = words;
        this.levels = levels;
        this.books = books;
        this.version = version;
    }

    public void run() {
        RandomReverser reverser = new RandomReverser(Collections.emptyList());

        for (int slot = 0; slot < 3; slot++) {
            boolean four = words[slot][3] != -1;
            reverser.addNextIntCall(2, four ? 1 : 0, four ? 1 : 0);
            reverser.addNextIntCall(WORD_LIST_LENGTH, words[slot][0], words[slot][0]);
            reverser.addNextIntCall(WORD_LIST_LENGTH, words[slot][1], words[slot][1]);
            reverser.addNextIntCall(WORD_LIST_LENGTH, words[slot][2], words[slot][2]);
            if (four) {
                reverser.addNextIntCall(WORD_LIST_LENGTH, words[slot][3], words[slot][3]);
            }
        }

        long[] seeds = reverser.findAllValidSeeds().toArray();
        if (seeds.length > 1) {
            System.err.println(EnchantCrackerI18n.translate("cracker.error.multipleseeds"));
            for (long l : seeds) {
                System.err.println(l+"L");
            }
            this.failed = true;
            this.result = SeedResults.SEED_RESULT_MANYFOUND.getValue();
            return;
        }
        else if (seeds.length == 0) {
            this.failed = true;
            this.result = SeedResults.SEED_RESULT_NOTFOUND.getValue();
            return;
        }

        // XOR by RANDOM_MULTIPLIER to undo setSeed() scramble
        long lower48 = seeds[0] ^ RANDOM_MULTIPLIER;

        List<Long> longSeeds = new ArrayList<>();

        // normally, you can crack the seed of a random from the result of a nextLong() call really easily, but we don't have the top 16 bits of that call so we brute force those
        for (long topbits = 0; topbits < 65536; topbits++) {
            long longToTest = (topbits << 48) + lower48;

            //THX https://stackoverflow.com/a/15237585
            long a = longToTest >>> 32;
            long b = longToTest & ((1L<<32)-1);
            if((b & 0x80000000) != 0)
                a++;
            long q = ((b << 16) - RANDOM_DELTA - (a << 16)* RANDOM_MULTIPLIER) & RANDOM_MASK;
            for(long k=0; k<=5; k++) {
                long rem = (RANDOM_MULTIPLIER - (q + (k<<48))) % RANDOM_MULTIPLIER;
                long d = (rem + RANDOM_MULTIPLIER)% RANDOM_MULTIPLIER;
                if(d < 65536) {
                    long c = ((q + d) * RANDOM_MULTIPLIER_INVERSE) & RANDOM_MASK;
                    if(c < 65536) {
                        long longSeed = ((((a << 16) + c) - RANDOM_DELTA) * RANDOM_MULTIPLIER_INVERSE) & RANDOM_MASK;

                        //we don't need the levels to crack the RNG directly, but as 2 64 bit longs could be valid for a 48 bit seed, need this extra check
                        Random sixteenCrack = new Random(longSeed ^ RANDOM_MULTIPLIER);
                        sixteenCrack.nextLong();

                        int[] levels = version.getEnchantLevels(sixteenCrack, books);
                        if (levels[0] == this.levels[0] && levels[1] == this.levels[1] && levels[2] == this.levels[2]) {
                            longSeeds.add(longSeed);
                        }
                    }
                }
            }
            //END "BORROWED" CODE
        }

        if (longSeeds.size() > 1) {
            System.err.println(EnchantCrackerI18n.translate("cracker.error.multipleseeds"));
            for (long l : longSeeds) {
                System.err.println(l+"L");
            }
            this.failed = true;
            this.result = SeedResults.SEED_RESULT_MANYFOUND.getValue();
        }
        else if (longSeeds.size() == 0) {
            this.failed = true;
            this.result = SeedResults.SEED_RESULT_NOTFOUND.getValue();
        }
        else {
            this.result = longSeeds.get(0);
        }
    }

    public boolean getFailed() {
        return this.failed;
    }

    public long getResult() {
        return this.result;
    }
}
