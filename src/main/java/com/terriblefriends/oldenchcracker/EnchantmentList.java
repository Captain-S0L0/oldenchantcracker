package com.terriblefriends.oldenchcracker;

import java.util.function.Function;

public class EnchantmentList {

    private final Enchant[] enchants = new Enchant[256];

    public Enchant[] getEnchants() {
        return this.enchants;
    }

    public void register(String name, int id, int weight, int maxLevel, Function<Integer, Integer> getMinEnchantability, Function<Integer, Integer> getMaxEnchantability, int[] incompatibleEnchants) {
        if (enchants[id] == null) {
            enchants[id] = new Enchant(name, id, weight, maxLevel, getMinEnchantability, getMaxEnchantability, incompatibleEnchants);
        }
        else {
            System.out.println("ERROR! Duplicate enchant id!");
        }
    }

    public class Enchant {
        private final String name;
        private final int id;
        private final int maxLevel;
        private final int weight;
        private final Function<Integer, Integer> getMinEnchantability;
        private final Function<Integer, Integer> getMaxEnchantability;
        private final int[] incompatibleEnchants;

        public Enchant(String name, int id, int weight, int maxLevel, Function<Integer, Integer> getMinEnchantability, Function<Integer, Integer> getMaxEnchantability, int[] incompatibleEnchants) {
            this.id = id;
            this.name = name;
            this.weight = weight;
            this.maxLevel = maxLevel;
            this.getMinEnchantability = getMinEnchantability;
            this.getMaxEnchantability = getMaxEnchantability;
            this.incompatibleEnchants = incompatibleEnchants;
        }

        public int getWeight() {
            return this.weight;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public int getMaxLevel() {
            return this.maxLevel;
        }

        public int getMinEnchantability(int l) {
            return this.getMinEnchantability.apply(l);
        }

        public int getMaxEnchantability(int l) {
            return this.getMaxEnchantability.apply(l);
        }

        public boolean isCompatibleEnchant(int id) {
            if (id == this.id) {
                return false;
            }
            if (this.incompatibleEnchants == null) {
                return true;
            }
            for (int i : this.incompatibleEnchants) {
                if (i == id) {
                    return false;
                }
            }
            return true;
        }
    }
}
