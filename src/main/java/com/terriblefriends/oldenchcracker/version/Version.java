package com.terriblefriends.oldenchcracker.version;

import com.seedfinding.latticg.RandomReverser;
import com.terriblefriends.oldenchcracker.thingmanager.EnchantData;
import com.terriblefriends.oldenchcracker.thingmanager.Enchantment;
import com.terriblefriends.oldenchcracker.thingmanager.Item;

import java.util.List;
import java.util.Random;

public interface Version {
    int getMaxBookShelves();
    String[] getItemStrings();
    String[] getMaterialStrings(String item);
    Item getItem(String item);
    Enchantment getEnchant(int id);
    int getMaxLevels();
    int getMaxEnchantability(int enchantability, int level);
    int getMaterialEnchantability(String material);
    int[] getEnchantLevels(Random random, int books);
    void reverseLevels(RandomReverser reverser, int[] cycle);
    void reverseExtremes(RandomReverser reverser, int advances, boolean isLow);
    int getExtremesNeeded();
    List<EnchantData> getItemEnchantments(Random random, int enchantability, String item, int level);
    CrackType getCrackType();

    enum CrackType {
        GALACTIC,
        LEVELS,
        EXTREMES
    }
}
