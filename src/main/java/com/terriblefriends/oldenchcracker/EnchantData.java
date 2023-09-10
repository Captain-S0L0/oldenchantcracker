package com.terriblefriends.oldenchcracker;

import com.terriblefriends.oldenchcracker.thingmanagers.Enchantment;

public class EnchantData {

    private int level;
    private Enchantment enchant;

    public EnchantData(Enchantment enchant, int level) {
        this.enchant = enchant;
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }

    public Enchantment getEnchant() {
        return this.enchant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof EnchantData && ((EnchantData)o).getEnchant() == this.getEnchant() && ((EnchantData)o).getLevel() == this.getLevel();
    }
}
