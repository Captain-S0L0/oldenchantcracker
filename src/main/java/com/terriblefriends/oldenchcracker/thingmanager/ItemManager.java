package com.terriblefriends.oldenchcracker.thingmanager;

import com.terriblefriends.oldenchcracker.EnchantCrackerI18n;

import java.util.LinkedHashMap;

public class ItemManager {
    private final LinkedHashMap<String, Item> items = new LinkedHashMap<>();


    public void register(String name, int[] validMaterials, int[] validEnchants) {
        if (!items.containsKey(name)) {
            items.put(name, new Item(validMaterials, validEnchants));
        }
        else {
            throw new RuntimeException(String.format(EnchantCrackerI18n.translate("item.error.duplicate"), name));
        }
    }

    public String[] getItemStrings() {
        return items.keySet().toArray(new String[0]);
    }

    public Item getItem(String name) {
        return items.get(name);
    }
}
