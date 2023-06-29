package com.terriblefriends.oldenchcracker;

import java.util.HashMap;

public class Materials {
    private final HashMap<String, Material> materials = new HashMap<>();

    public void register(String string, int enchantability) {
        materials.put(string, new Material(enchantability));
    }

    public HashMap<String, Material> getMaterials() {
        return materials;
    }

    static {


    }

    public static class Material {
        private final int enchantiability;

        public Material(int enchantiability) {
            this.enchantiability = enchantiability;
        }

        public int getEnchantiability() {
            return this.enchantiability;
        }
    }

}
