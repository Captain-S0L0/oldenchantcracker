package com.terriblefriends.oldenchcracker;

import com.terriblefriends.oldenchcracker.versions.Version;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

public class EnchantFinder extends Thread {
    private static final long RANDOM_MULTIPLIER = 0x5DEECE66DL;
    private final int materialEnchantability;
    private final String item;
    private final Random random;
    private final int books;
    private final Version version;
    private final HashMap<Integer, EnchantData> desiredEnchants;
    private final boolean exactly;
    private final int maxAdvances;
    private final boolean advancedAdvancements;
    private int resultAdvances = -1;
    private int resultSlot = -1;
    private long resultSeed = -1;
    private int[] resultLevels = new int[0];
    private boolean failed = false;

    public EnchantFinder(String item, String material, long randomSeed, Version version, int books, int maxAdvances, HashMap<Integer, EnchantData> desiredEnchants, boolean exactly, boolean advancedAdvancements) {
        this.materialEnchantability = version.getMaterialEnchantability(material);
        if (this.materialEnchantability == -1) {
            throw new RuntimeException("ERROR! Invalid material!");
        }
        this.item = item;
        this.random = new Random(randomSeed^ RANDOM_MULTIPLIER);
        this.random.nextLong();
        this.version = version;
        this.desiredEnchants = desiredEnchants;
        this.exactly = exactly;
        this.books = books;
        this.maxAdvances = advancedAdvancements ? maxAdvances * 3 : maxAdvances;
        this.advancedAdvancements = advancedAdvancements;
    }

    @Override
    public void run() {

        //setup levels once first in case enchants are available on current selection
        int[] levels = this.version.getEnchantLevels(this.random, this.books);

        //System.out.println(levels[0]+","+levels[1]+","+levels[2]);

        for (int advances = 0; advances < this.maxAdvances; advances++) {
            long slotResetSeed = getSeed(this.random);

            //System.out.println();
            //System.out.println("advance "+advances);
            //System.out.println("stored seed "+slotResetSeed);

            for (int slot = 0; slot < 3; slot++) {
                //System.out.println("slot "+slot);
                //this.random.setSeed(slotResetSeed ^ randomMultiplier);
                List<EnchantData> enchantList = this.version.getItemEnchantments(this.random, this.materialEnchantability, this.item, levels[slot]);

                if (enchantList != null) {
                    boolean valid = false;

                    if (this.exactly) {
                        if (enchantList.size() == this.desiredEnchants.size()) {
                            valid = true;
                            for (EnchantData testFor : this.desiredEnchants.values()) {
                                boolean contains = false;
                                for (EnchantData data : enchantList) {
                                    if (data.getEnchant() == testFor.getEnchant() && data.getLevel() == testFor.getLevel()) {
                                        contains = true;
                                        break;
                                    }
                                }

                                if (!contains) {
                                    valid = false;
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        valid = true;
                        for (EnchantData testFor : this.desiredEnchants.values()) {
                            boolean contains = false;
                            for (EnchantData data : enchantList) {
                                if (data.getEnchant() == testFor.getEnchant() && data.getLevel() >= testFor.getLevel()) {
                                    contains = true;
                                    break;
                                }
                            }

                            if (!contains) {
                                valid = false;
                                break;
                            }
                        }
                    }

                    if (valid) {
                        for (EnchantData data : enchantList) {
                            System.out.println("Found enchantment "+data.getEnchant().getName()+" @ level "+data.getLevel());
                        }

                        this.resultLevels = levels;
                        this.resultAdvances = advances;
                        this.resultSlot = slot;

                        if (this.version.getCrackType() == Version.CrackType.LEVELS) {
                            this.random.nextLong();//Do this twice because 1.3+ changed advances slightly
                            this.version.getEnchantLevels(this.random, this.books);
                        }
                        this.random.nextLong();//DON'T DO THIS TWICE BECAUSE WE DO IT AGAIN TO START THE NEXT SEARCH
                        this.version.getEnchantLevels(this.random, this.books);

                        this.resultSeed = getSeed(this.random);
                        return;
                    }
                }
                this.random.setSeed(slotResetSeed ^ RANDOM_MULTIPLIER);
                //System.out.println("set seed "+(slotResetSeed ^ randomMultiplier));
            }

            for (int swap = 0; swap < (this.advancedAdvancements ? 1 : 3); swap++) { //calculate levels 3 times because Minecraft
                this.random.nextLong();
                levels = this.version.getEnchantLevels(this.random, this.books);
            }
            //System.out.println(levels[0]+","+levels[1]+","+levels[2]);
        }
        System.out.println("Failed to find enchant in "+this.maxAdvances+" advances!");
        this.resultAdvances = -9001;
        this.failed = true;
    }

    public boolean getFailed() {
        return this.failed;
    }

    public int getResultAdvances() {
        return this.resultAdvances;
    }

    public int getResultSlot() {
        return this.resultSlot;
    }

    public long getResultSeed() {
        return this.resultSeed;
    }

    public int[] getResultLevels() {
        return this.resultLevels;
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
