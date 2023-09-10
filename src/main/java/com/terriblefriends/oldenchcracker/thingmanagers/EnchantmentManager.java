package com.terriblefriends.oldenchcracker.thingmanagers;

import java.util.function.Function;

public class EnchantmentManager {

    private final Enchantment[] enchants = new Enchantment[256];

    public Enchantment[] getEnchants() {
        return this.enchants;
    }

    public void register(String name, int id, int weight, int maxLevel, Function<Integer, Integer> getMinEnchantability, Function<Integer, Integer> getMaxEnchantability, int[] incompatibleEnchants) {
        if (enchants[id] == null) {
            enchants[id] = new Enchantment(name, id, weight, maxLevel, getMinEnchantability, getMaxEnchantability, incompatibleEnchants);
        }
        else {
            System.out.println("ERROR! Duplicate enchant id!");
        }
    }
}
