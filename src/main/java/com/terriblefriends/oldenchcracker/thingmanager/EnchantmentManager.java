package com.terriblefriends.oldenchcracker.thingmanager;

import com.terriblefriends.oldenchcracker.EnchantCrackerI18n;

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
            throw new RuntimeException(String.format(EnchantCrackerI18n.translate("enchantment.error.duplicate"), id, name));
        }
    }
}
