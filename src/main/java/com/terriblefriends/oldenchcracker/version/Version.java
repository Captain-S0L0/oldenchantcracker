package com.terriblefriends.oldenchcracker.version;

import com.seedfinding.latticg.RandomReverser;
import com.terriblefriends.oldenchcracker.thingmanager.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public abstract class Version {
    protected final EnchantmentManager enchantmentManager = new EnchantmentManager();
    protected final ItemManager itemManager = new ItemManager();
    protected final MaterialManager materialManager = new MaterialManager();

    public String[] getItemStrings() {
        return itemManager.getItemStrings();
    }

    public String[] getMaterialStrings(String item) {
        return materialManager.getValidMaterials(itemManager.getItem(item).getValidMaterials());
    }

    public Item getItem(String item) {
        return itemManager.getItem(item);
    }

    public Enchantment getEnchant(int id) {
        return enchantmentManager.getEnchants()[id];
    }

    public int getMaterialEnchantability(String materialName) {
        Material material = materialManager.getMaterial(materialName);
        if (material != null) {
            return material.getEnchantability();
        }
        else {
            return -1;
        }
    }

    public abstract int getMaxLevels();
    public abstract int getMaxEnchantability(int enchantability, int level);
    public abstract int getMaxBookShelves();
    public abstract int[] getEnchantLevels(Random random, int books);
    public abstract void reverseLevels(RandomReverser reverser, int[] cycle);
    public abstract void reverseExtremes(RandomReverser reverser, int advances, boolean isLow);
    public abstract int getExtremesNeeded();
    public abstract List<EnchantData> getItemEnchantments(Random random, int enchantability, String item, int level);
    public abstract CrackType getCrackType();

    public enum CrackType {
        GALACTIC,
        LEVELS,
        EXTREMES
    }

    protected static EnchantData getWeightedEnchantment(Random random, Collection<EnchantData> values) {
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
}
