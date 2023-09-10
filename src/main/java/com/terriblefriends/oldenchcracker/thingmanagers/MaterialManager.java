package com.terriblefriends.oldenchcracker.thingmanagers;

import java.util.HashMap;

public class MaterialManager {
    private final HashMap<Integer, Material> materials = new HashMap<>();

    public void register(int id, String string, int enchantability) {
        if (!materials.containsKey(id)) {
            materials.put(id, new Material(string, enchantability));
        }
        else {
            throw new RuntimeException("Duplicate Material ID: "+string+"!");
        }
    }

    public String[] getValidMaterials(int[] ids) {
        String[] value = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            value[i] = materials.get(ids[i]).getName();
        }
        return value;
    }

    public Material getMaterial(String name) {
        for (Material m : materials.values()) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }

}
