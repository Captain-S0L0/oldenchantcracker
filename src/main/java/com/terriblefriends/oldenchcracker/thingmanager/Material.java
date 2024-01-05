package com.terriblefriends.oldenchcracker.thingmanager;

public class Material {
    private final int enchantability;
    private final String name;

    public Material(String name, int enchantability) {
        this.enchantability = enchantability;
        this.name = name;
    }

    public int getEnchantability() {
        return this.enchantability;
    }

    public String getName() {
        return name;
    }
}
