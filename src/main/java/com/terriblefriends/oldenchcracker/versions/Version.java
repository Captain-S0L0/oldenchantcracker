package com.terriblefriends.oldenchcracker.versions;

import com.terriblefriends.oldenchcracker.EnchantData;
import com.terriblefriends.oldenchcracker.EnchantmentList;
import com.terriblefriends.oldenchcracker.ItemList;

import java.util.HashMap;
import java.util.Random;

public interface Version {
    int getMaxBookShelves();
    String[] getItems();
    String[] getMaterials();
    ItemList.Item getItem(String item);
    EnchantmentList.Enchant getEnchant(int id);
    int getMaxLevels();
    int getMaterialEnchantability(String material);
    HashMap<Integer, EnchantData> mapEnchantmentData(int enchantability, String item);
    int[] getEnchantLevels(Random random, int books);
}
