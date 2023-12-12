package com.terriblefriends.oldenchcracker.versions;

import com.seedfinding.latticg.RandomReverser;
import com.terriblefriends.oldenchcracker.EnchantData;
import com.terriblefriends.oldenchcracker.thingmanagers.Enchantment;
import com.terriblefriends.oldenchcracker.thingmanagers.Item;

import java.util.List;
import java.util.Random;

public interface Version {
    default int getMaxBookShelves() {
        return 0;
    }
    default String[] getItemStrings() {
        return null;
    }
    default String[] getMaterialStrings(String item) {
        return null;
    }
    default Item getItem(String item) {
        return null;
    }
    default Enchantment getEnchant(int id) {
        return null;
    }
    default int getMaxLevels() {
        return 0;
    }
    default int getMaxEnchantability(int enchantability, int level) {
        return 0;
    }
    default int getMaterialEnchantability(String material) {
        return 0;
    }
    default int[] getEnchantLevels(Random random, int books) {
        return null;
    }
    default void reverseLevels(RandomReverser reverser, int[] cycle) {}
    default void reverseExtremes(RandomReverser reverser, int advances, boolean isLow) {}
    default int getExtremesNeeded() {
        return 0;
    }
    default List<EnchantData> getItemEnchantments(Random random, int enchantability, String item, int level) {
        return null;
    }
    default CrackType getCrackType() {
        return null;
    }

    enum CrackType {
        GALACTIC,
        LEVELS,
        EXTREMES
    }
}
