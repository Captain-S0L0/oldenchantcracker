package com.terriblefriends.oldenchcracker;

import com.terriblefriends.oldenchcracker.versions.Version;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

public class EnchantFinder extends Thread {
    private static final long randomMultiplier = 0x5DEECE66DL;
    private final int materialEnchantability;
    private final String item;
    private final Random random;
    private final int books;
    private final Version version;
    private final HashMap<Integer, EnchantData> desiredEnchants;
    private final boolean exactly;
    private int resultAdvances = -1;
    private int resultSlot = -1;
    private long resultSeed = -1;
    private int[] resultLevels = new int[0];
    private boolean failed = false;

    public EnchantFinder(String item, String material, long randomSeed, Version version, int books, HashMap<Integer, EnchantData> desiredEnchants, boolean exactly) {
        this.materialEnchantability = version.getMaterialEnchantability(material);
        if (this.materialEnchantability == -1) {
            System.out.println("ERROR! Invalid material!");
            this.failed = true;
        }
        this.item = item;
        this.random = new Random(randomSeed^randomMultiplier);
        this.random.nextLong();
        this.version = version;
        this.desiredEnchants = desiredEnchants;
        this.exactly = exactly;
        this.books = books;
    }

    @Override
    public void run() {

        //setup levels once first in case enchants are available on current selection
        int[] levels = version.getEnchantLevels(this.random, this.books);

        //System.out.println(levels[0]+","+levels[1]+","+levels[2]);

        for (int advances = 0; advances < Main.getMaxAdvances(); advances++) {
            long slotResetSeed = getSeed(this.random);

            //System.out.println();
            //System.out.println("advance "+advances);
            //System.out.println("stored seed "+slotResetSeed);

            for (int slot = 0; slot < 3; slot++) {
                //System.out.println("slot "+slot);
                //this.random.setSeed(slotResetSeed ^ randomMultiplier);
                int enchantability = this.materialEnchantability;
                enchantability = 1 + this.random.nextInt((enchantability >> 1)+1) + this.random.nextInt((enchantability >> 1)+1);
                int enchantStep1 = enchantability + levels[slot];
                float enchantStep2 = (this.random.nextFloat() + this.random.nextFloat() - 1.0F) * 0.25F;
                int enchantStep3 = (int)((float)enchantStep1 * (1.0F + enchantStep2) + 0.5F);
                HashMap<Integer, EnchantData> enchantList = null;
                HashMap<Integer, EnchantData> enchantmentData = version.mapEnchantmentData(enchantStep3, item);

                if (enchantmentData != null && !enchantmentData.isEmpty()) {
                    EnchantData selectedEnchant = getWeightedEnchantment(this.random, enchantmentData.values());
                    if (selectedEnchant != null) {
                        //System.out.println("1st enchantment "+selectedEnchant.getEnchant().getName()+" @ level "+selectedEnchant.getLevel());
                        //System.out.println("data size "+enchantmentData.size());

                        //for (EnchantData data : enchantmentData.values()) {
                        //    System.out.println(data.getEnchant().getName()+" @ level "+data.getLevel()+" weight "+data.getEnchant().getWeight());
                        //}

                        enchantList = new HashMap<>();
                        enchantList.put(selectedEnchant.getEnchant().getId(), selectedEnchant);
                        for (int multipleChance = enchantStep3 >> 1; this.random.nextInt(50) < multipleChance; multipleChance >>= 1) {
                            Iterator<Integer> multipleIterator = enchantmentData.keySet().iterator();
                            while (multipleIterator.hasNext()) {
                                int multipleId = multipleIterator.next();
                                for (EnchantData data : enchantList.values()) {
                                    if (!data.getEnchant().isCompatibleEnchant(multipleId)) {
                                        multipleIterator.remove();
                                    }
                                }
                            }

                            if (!enchantmentData.isEmpty()) {
                                EnchantData enchant = getWeightedEnchantment(this.random, enchantmentData.values());
                                enchantList.put(enchant.getEnchant().getId(), enchant);
                                //System.out.println("added extra enchant "+enchant.getEnchant().getName()+" @ level "+enchant.getLevel());
                            }
                        }
                    }
                }

                if (enchantList != null) {
                    boolean valid = false;

                    if (exactly) {
                        if (enchantList.size() == desiredEnchants.size()) {
                            valid = true;
                            for (EnchantData testFor : desiredEnchants.values()) {
                                if (!Objects.equals(enchantList.get(testFor.getEnchant().getId()), testFor)) {
                                    valid = false;
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        valid = true;
                        for (EnchantData testFor : desiredEnchants.values()) {
                            int testForId = testFor.getEnchant().getId();
                            if (!enchantList.containsKey(testForId) || enchantList.get(testForId).getLevel() < testFor.getLevel()) {
                                valid = false;
                                break;
                            }
                        }
                    }

                    if (valid) {
                        resultLevels = levels;
                        this.resultAdvances = advances;
                        this.resultSlot = slot;

                        this.random.nextLong();//DON'T DO THIS TWICE BECAUSE WE DO IT AGAIN TO START THE NEXT SEARCH
                        levels = version.getEnchantLevels(this.random, this.books);

                        this.resultSeed = getSeed(this.random);
                        return;
                    }
                }
                this.random.setSeed(slotResetSeed ^ randomMultiplier);
                //System.out.println("set seed "+(slotResetSeed ^ randomMultiplier));
            }

            for (int swap = 0; swap < 3; swap++) { //calculate levels 3 times because Minecraft
                this.random.nextLong();
                levels = version.getEnchantLevels(this.random, this.books);
            }
            //System.out.println(levels[0]+","+levels[1]+","+levels[2]);
        }
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

    private static EnchantData getWeightedEnchantment(Random random, Collection<EnchantData> values) {
        int totalWeight = 0;
        for (EnchantData enchant : values) {
            totalWeight += enchant.getEnchant().getWeight();
        }
        int selectedWeight = random.nextInt(totalWeight);

        Iterator<EnchantData> weightIterator = values.iterator();
        EnchantData selectedEnchant;
        do {
            if (!weightIterator.hasNext()) {
                return null;
            }

            selectedEnchant = weightIterator.next();
            selectedWeight -= selectedEnchant.getEnchant().getWeight();
        }
        while (selectedWeight >= 0);

        return selectedEnchant;
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
