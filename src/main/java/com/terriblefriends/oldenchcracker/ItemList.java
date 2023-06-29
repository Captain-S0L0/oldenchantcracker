package com.terriblefriends.oldenchcracker;

import java.util.HashMap;

public class ItemList {
    private final HashMap<String, Item> items = new HashMap<>();

    public void register(String name, int[] validEnchants) {
        items.put(name, new Item(validEnchants));
    }

    public Item getItem(String name) {
        return items.get(name);
    }

    public class Item {
        private final int[] validEnchants;

        public Item(int[] validEnchants) {
            this.validEnchants = validEnchants;
        }

        public int[] getValidEnchants() {
            return this.validEnchants;
        }

        public boolean validEnchantment(int id) {
            for (int validEnchant : validEnchants) {
                if (id == validEnchant) {
                    return true;
                }
            }
            return false;
        }
    }
}
