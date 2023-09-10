package com.terriblefriends.oldenchcracker;

import com.seedfinding.latticg.RandomReverser;
import com.terriblefriends.oldenchcracker.versions.Version;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Random;

public class LevelCracker extends Thread {
    private static final long randomMultiplier = 0x5DEECE66DL;
    private static final long randomMultiplierInverse = 0xdfe05bcb1365L;
    private static final long randomDelta = 0xbL;
    private static final long randomMask = ((1L << 48)-1);
    private final int[][] levelData;
    private final Version version;
    private boolean failed = false;
    private long result = -1;


    public LevelCracker(int[][] levelData, Version version) {
        this.levelData = levelData;
        this.version = version;
    }

    @Override
    public void run() {
        RandomReverser reverser = new RandomReverser(Collections.EMPTY_LIST);

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
        Random advanceToPresent = new Random(seeds[0] ^ randomMultiplier);

        for (int cycles = 0; cycles < 32; cycles++) {
            advanceToPresent.nextLong();
            version.getEnchantLevels(advanceToPresent, 0);
        }

        this.result = getSeed(advanceToPresent);

        //System.out.println(advanceToPresent.nextLong());
    }

    public boolean getFailed() {
        return this.failed;
    }

    public long getResult() {
        return this.result;
    }

    private static long getSeed(Random random) {
        //THX https://stackoverflow.com/a/6001407
        byte[] ba0, ba1, bar;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(128);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(new Random(0));
            ba0 = baos.toByteArray();
            baos = new ByteArrayOutputStream(128);
            oos = new ObjectOutputStream(baos);
            oos.writeObject(new Random(-1));
            ba1 = baos.toByteArray();
            baos = new ByteArrayOutputStream(128);
            oos = new ObjectOutputStream(baos);
            oos.writeObject(random);
            bar = baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("IOException: " + e);
        }
        if (ba0.length != ba1.length || ba0.length != bar.length)
            throw new RuntimeException("bad serialized length");
        int i = 0;
        while (i < ba0.length && ba0[i] == ba1[i]) {
            i++;
        }
        int j = ba0.length;
        while (j > 0 && ba0[j - 1] == ba1[j - 1]) {
            j--;
        }
        if (j - i != 6)
            throw new RuntimeException("6 differing bytes not found");
        // The constant 0x5DEECE66DL is from
        // http://download.oracle.com/javase/6/docs/api/java/util/Random.html .
        return ((bar[i] & 255L) << 40 | (bar[i + 1] & 255L) << 32 |
                (bar[i + 2] & 255L) << 24 | (bar[i + 3] & 255L) << 16 |
                (bar[i + 4] & 255L) << 8 | (bar[i + 5] & 255L));
        //END "BORROWED" CODE
    }
}
