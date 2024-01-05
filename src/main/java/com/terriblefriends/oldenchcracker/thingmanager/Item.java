package com.terriblefriends.oldenchcracker.thingmanager;

public class Item {
    private final int[] validEnchants;
    private final int[] validMaterials;

    public Item(int[] validMaterials, int[] validEnchants) {
        this.validEnchants = validEnchants;
        this.validMaterials = validMaterials;
    }

    public int[] getValidEnchants() {
        return this.validEnchants;
    }

    public int[] getValidMaterials() {
        return this.validMaterials;
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
