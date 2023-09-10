package com.terriblefriends.oldenchcracker.thingmanagers;

import java.util.HashMap;

public class ItemManager {
    private final HashMap<String, Item> items = new HashMap<>();

    public void register(String name, int[] validMaterials, int[] validEnchants) {
        if (!items.containsKey(name)) {
            items.put(name, new Item(validMaterials, validEnchants));
        }
        else {
            throw new RuntimeException("Duplicate Item ID: "+name+"!");
        }
    }

    public String[] getItemStrings() {
        return items.keySet().toArray(new String[0]);
    }

    public Item getItem(String name) {
        return items.get(name);
    }
}
