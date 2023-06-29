package com.terriblefriends.oldenchcracker.versions;

import com.terriblefriends.oldenchcracker.EnchantData;
import com.terriblefriends.oldenchcracker.EnchantmentList;
import com.terriblefriends.oldenchcracker.ItemList;
import com.terriblefriends.oldenchcracker.Materials;

import java.util.HashMap;
import java.util.Random;

public class One implements Version {

    //Tools for Minecraft versions Beta 1.9 Prerelease 5 to 1.0.0

    private static final EnchantmentList enchantmentList = new EnchantmentList();
    private static final ItemList itemList = new ItemList();
    private static final Materials materials = new Materials();
    
    static {
        enchantmentList.register("Protection", 0, 10, 4, l -> 1 + (l - 1) * 16, l -> (1 + (l - 1) * 16) + 20, new int[]{1, 3, 4});
        enchantmentList.register("Fire Protection", 1, 5, 4, l -> 10 + (l - 1) * 8, l -> (10 + (l - 1) * 8) + 12, new int[]{0, 3, 4});
        enchantmentList.register("Feather Falling", 2, 5, 4, l -> 5 + (l - 1) * 6, l -> (5 + (l - 1) * 6) + 10, null);
        enchantmentList.register("Blast Protection", 3, 2, 4, l -> 5 + (l - 1) * 8, l -> (5 + (l - 1) * 8) + 12, new int[]{0, 1, 4});
        enchantmentList.register("Projectile Protection", 4, 5, 4, l -> 3 + (l - 1) * 6, l -> (3 + (l - 1) * 6) + 15, new int[]{0, 1, 3});
        enchantmentList.register("Respiration", 5, 2, 3, l -> 10 * l, l -> (10 * l) + 30, null);
        enchantmentList.register("Aqua Affinity", 6, 2, 1, l -> 1, l -> 41, null);
        enchantmentList.register("Sharpness", 16, 10, 5, l -> 1 + (l - 1) * 16, l -> (1 + (l - 1) * 16) + 20, new int[]{17, 18});
        enchantmentList.register("Smite", 17, 5, 5, l -> 5 + (l - 1) * 8, l -> (5 + (l - 1) * 8) + 20, new int[]{16, 18});
        enchantmentList.register("Bane of Arthropods", 18, 5, 5, l -> 5 + (l - 1) * 8, l -> (5 + (l - 1) * 8) + 20, new int[]{16, 17});
        enchantmentList.register("Knockback", 19, 5, 2, l -> 5 + 20 * (l - 1), l -> (1 + l * 10) + 50, null);
        enchantmentList.register("Fire Aspect", 20, 2, 2, l -> 10 + 20 * (l - 1), l -> (1 + l * 10) + 50, null);
        enchantmentList.register("Looting", 21, 2, 3, l -> 20 + (l - 1) * 12, l -> (1 + l * 10) + 50, null);
        enchantmentList.register("Efficiency", 32, 10, 5, l -> 1 + 15 * (l - 1), l -> (1 + l * 10) + 50, null);
        enchantmentList.register("Silk Touch", 33, 1, 1, l -> 25, l -> (1 + l * 10) + 50, new int[]{35});
        enchantmentList.register("Unbreaking", 34, 5, 3, l -> 5 + (l - 1) * 10, l -> (1 + l * 10) + 50, null);
        enchantmentList.register("Fortune", 35, 2, 3, l -> 20 + (l - 1) * 12, l -> (1 + l * 10) + 50, new int[]{33});

        itemList.register("Helmet", new int[]{0, 1, 3, 4, 5, 6});
        itemList.register("Chestplate", new int[]{0, 1, 3, 4});
        itemList.register("Leggings", new int[]{0, 1, 3, 4});
        itemList.register("Boots", new int[]{0, 1, 2, 3, 4});
        itemList.register("Sword", new int[]{16, 17, 18, 19, 20, 21});
        itemList.register("Pickaxe", new int[]{32, 33, 34, 35});
        itemList.register("Axe", new int[]{32, 33, 34, 35});
        itemList.register("Shovel", new int[]{32, 33, 34, 35});

        materials.register("ToolWood", 15);
        materials.register("ToolStone", 5);
        materials.register("ToolIron", 14);
        materials.register("ToolDiamond", 10);
        materials.register("ToolGold", 22);
        materials.register("ArmorLeather", 15);
        materials.register("ArmorChain", 12);
        materials.register("ArmorIron", 9);
        materials.register("ArmorDiamond", 10);
        materials.register("ArmorGold", 25);
    }

    @Override
    public ItemList.Item getItem(String item) {
        return itemList.getItem(item);
    }

    @Override
    public EnchantmentList.Enchant getEnchant(int id) {
        return enchantmentList.getEnchants()[id];
    }

    @Override
    public int getMaxLevels() {
        return 50;
    }

    @Override
    public int getMaterialEnchantability(String s) {
        Materials.Material material = materials.getMaterials().get(s);
        if (material != null) {
            return material.getEnchantiability();
        }
        else {
            return -1;
        }
    }

    @Override
    public HashMap<Integer, EnchantData> mapEnchantmentData(int enchantability, String item) {
        HashMap<Integer, EnchantData> results = null;

        for (int id = 0; id < 256; id++) {
            EnchantmentList.Enchant enchant = enchantmentList.getEnchants()[id];
            if (enchant != null && itemList.getItem(item).validEnchantment(enchant.getId())) {
                for (int enchantLevel = 1; enchantLevel <= enchant.getMaxLevel(); enchantLevel++) {
                    if (enchantability >= enchant.getMinEnchantability(enchantLevel) && enchantability <= enchant.getMaxEnchantability(enchantLevel)) {
                        if (results == null) {
                            results = new HashMap<>();
                        }
                        results.put(enchant.getId(), new EnchantData(enchant, enchantLevel));
                    }
                }
            }
        }

        return results;
    }

    @Override
    public int getMaxBookShelves() {
        return 30;
    }

    @Override
    public String[] getItems() {
        return new String[] {"Helmet", "Chestplate", "Leggings", "Boots", "Sword", "Pickaxe", "Axe", "Shovel"};
    }

    @Override
    public String[] getMaterials() {
        return new String[] {"ToolWood", "ToolStone", "ToolIron", "ToolDiamond", "ToolGold", "ArmorLeather", "ArmorChain", "ArmorIron", "ArmorDiamond", "ArmorGold"};
    }

    @Override
    public int[] getEnchantLevels(Random random, int books) {
        int[] levels = new int[3];
        int booksStep1 = 1 + random.nextInt((books >> 1)+1) + random.nextInt(books + 1);
        int booksStep2 = random.nextInt(5) + booksStep1;
        levels[0] = ((booksStep2 >> 1) + 1);
        booksStep1 = 1 + random.nextInt((books >> 1)+1) + random.nextInt(books + 1);
        booksStep2 = random.nextInt(5) + booksStep1;
        levels[1] = (booksStep2 * 2 / 3 + 1);
        booksStep1 = 1 + random.nextInt((books >> 1)+1) + random.nextInt(books + 1);
        booksStep2 = random.nextInt(5) + booksStep1;
        levels[2] = booksStep2;
        return levels;
    }
}
