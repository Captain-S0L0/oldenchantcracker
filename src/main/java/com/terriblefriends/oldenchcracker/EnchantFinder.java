package com.terriblefriends.oldenchcracker;

import com.terriblefriends.oldenchcracker.versions.Version;

import java.util.*;

public class EnchantFinder {
    private final int materialEnchantability;
    private final String item;
    private final ReadableRandom random;
    private final int books;
    private final Version version;
    private final List<EnchantData> desiredEnchants;
    private final boolean exactly;
    private final int maxAdvances;
    private final boolean advancedAdvancements;
    private int resultAdvances = -1;
    private int resultSlot = -1;
    private long resultSeed = -1;
    private int[] resultLevels = new int[0];
    private boolean failed = false;

    public EnchantFinder(String item, String material, long randomSeed, Version version, int books, int maxAdvances, List<EnchantData> desiredEnchants, boolean exactly, boolean advancedAdvancements) {
        this.materialEnchantability = version.getMaterialEnchantability(material);
        if (this.materialEnchantability == -1) {
            throw new RuntimeException("ERROR! Invalid material!");
        }
        this.item = item;
        this.random = new ReadableRandom(randomSeed);
        this.random.nextLong();
        this.version = version;
        this.desiredEnchants = desiredEnchants;
        this.exactly = exactly;
        this.books = books;
        this.maxAdvances = maxAdvances;
        this.advancedAdvancements = advancedAdvancements;
    }

    public void run() {

        //setup levels once first in case enchants are available on current selection
        int[] levels = this.version.getEnchantLevels(this.random, this.books);

        //System.out.println(levels[0]+","+levels[1]+","+levels[2]);

        //precalculate max enchantabilities and required enchantability for performance
        int[] maxEnchantabilities = new int[this.version.getMaxLevels()+1];
        for (int level = 1; level <= this.version.getMaxLevels(); level++) {
            maxEnchantabilities[level] = this.version.getMaxEnchantability(this.materialEnchantability, level);
        }

        int requiredEnchantability = 0;
        for (EnchantData data : this.desiredEnchants) {
            int enchantability = data.getEnchant().getMinEnchantability(data.getLevel());
            if (enchantability > requiredEnchantability) {
                requiredEnchantability = enchantability;
            }
        }

        for (int advances = 0; advances < this.maxAdvances; advances++) {
            long slotResetSeed = this.random.getSeed();

            //System.out.println();
            //System.out.println("advance "+advances);
            //System.out.println("stored seed "+slotResetSeed);

            for (int slot = 0; slot < 3; slot++) {

                //if our enchantments aren't possible at the given level, skip looking
                if (maxEnchantabilities[levels[slot]] >= requiredEnchantability) {

                    //System.out.println("slot "+slot);
                    List<EnchantData> enchantList = this.version.getItemEnchantments(this.random, this.materialEnchantability, this.item, levels[slot]);

                    if (enchantList != null) {
                        boolean valid = false;

                        if (this.exactly) {
                            if (enchantList.size() == this.desiredEnchants.size()) {
                                valid = true;
                                for (EnchantData testFor : this.desiredEnchants) {
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
                            for (EnchantData testFor : this.desiredEnchants) {
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

                            this.resultSeed = this.random.getSeed();
                            return;
                        }
                    }
                }
                this.random.setRSeed(slotResetSeed);
                //System.out.println("set seed "+(slotResetSeed ^ randomMultiplier));
            }

            // the default advancement mode actually advances 3 times per, advanced checks everything
            for (int swap = 0; swap < (this.advancedAdvancements ? 1 : 3); swap++) {
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
}
