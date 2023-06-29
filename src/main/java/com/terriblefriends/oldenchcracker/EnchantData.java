package com.terriblefriends.oldenchcracker;

public class EnchantData {

    private int level;
    private EnchantmentList.Enchant enchant;

    public EnchantData(EnchantmentList.Enchant enchant, int level) {
        this.enchant = enchant;
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }

    public EnchantmentList.Enchant getEnchant() {
        return this.enchant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof EnchantData && ((EnchantData)o).getEnchant() == this.getEnchant() && ((EnchantData)o).getLevel() == this.getLevel();
    }
}
