package com.terriblefriends.oldenchcracker.thingmanager;

import com.terriblefriends.oldenchcracker.EnchantCrackerI18n;

import java.util.HashMap;

public class MaterialManager {
    private final HashMap<Integer, Material> materials = new HashMap<>();

    public void register(int id, String name, int enchantability) {
        if (!materials.containsKey(id)) {
            materials.put(id, new Material(name, enchantability));
        }
        else {
            throw new RuntimeException(String.format(EnchantCrackerI18n.translate("material.error.duplicate"), id, name));
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
