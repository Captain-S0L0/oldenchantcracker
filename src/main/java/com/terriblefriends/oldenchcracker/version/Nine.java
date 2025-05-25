package com.terriblefriends.oldenchcracker.version;

import com.seedfinding.latticg.RandomReverser;
import com.terriblefriends.oldenchcracker.EnchantCrackerI18n;
import com.terriblefriends.oldenchcracker.thingmanager.EnchantData;
import com.terriblefriends.oldenchcracker.thingmanager.*;

import java.util.*;

class Nine extends Version {
    //Tools for Minecraft versions 13w37a (1.7) to 1.7.10

    protected Nine() {
        enchantmentManager.register("enchantment.protection", 0, 10, 4, l -> 1 + (l - 1) * 11, l -> (1 + (l - 1) * 11) + 20, new int[]{1, 3, 4});
        enchantmentManager.register("enchantment.fire_protection", 1, 5, 4, l -> 10 + (l - 1) * 8, l -> (10 + (l - 1) * 8) + 12, new int[]{0, 3, 4});
        enchantmentManager.register("enchantment.feather_falling", 2, 5, 4, l -> 5 + (l - 1) * 6, l -> (5 + (l - 1) * 6) + 10, null);
        enchantmentManager.register("enchantment.blast_protection", 3, 2, 4, l -> 5 + (l - 1) * 8, l -> (5 + (l - 1) * 8) + 12, new int[]{0, 1, 4});
        enchantmentManager.register("enchantment.projectile_protection", 4, 5, 4, l -> 3 + (l - 1) * 6, l -> (3 + (l - 1) * 6) + 15, new int[]{0, 1, 3});
        enchantmentManager.register("enchantment.respiration", 5, 2, 3, l -> 10 * l, l -> (10 * l) + 30, null);
        enchantmentManager.register("enchantment.aqua_affinity", 6, 2, 1, l -> 1, l -> 41, null);
        enchantmentManager.register("enchantment.thorns", 7, 1, 3, l -> 10 + 20 * (l - 1), l -> (1 + l * 10) + 50, null);
        enchantmentManager.register("enchantment.sharpness", 16, 10, 5, l -> 1 + (l - 1) * 11, l -> (1 + (l - 1) * 11) + 20, new int[]{17, 18});
        enchantmentManager.register("enchantment.smite", 17, 5, 5, l -> 5 + (l - 1) * 8, l -> (5 + (l - 1) * 8) + 20, new int[]{16, 18});
        enchantmentManager.register("enchantment.bane_of_arthropods", 18, 5, 5, l -> 5 + (l - 1) * 8, l -> (5 + (l - 1) * 8) + 20, new int[]{16, 17});
        enchantmentManager.register("enchantment.knockback", 19, 5, 2, l -> 5 + 20 * (l - 1), l -> (1 + l * 10) + 50, null);
        enchantmentManager.register("enchantment.fire_aspect", 20, 2, 2, l -> 10 + 20 * (l - 1), l -> (1 + l * 10) + 50, null);
        enchantmentManager.register("enchantment.looting", 21, 2, 3, l -> 15 + (l - 1) * 9, l -> (1 + l * 10) + 50, null);
        enchantmentManager.register("enchantment.efficiency", 32, 10, 5, l -> 1 + 10 * (l - 1), l -> (1 + l * 10) + 50, null);
        enchantmentManager.register("enchantment.silk_touch", 33, 1, 1, l -> 15, l -> (1 + l * 10) + 50, new int[]{35});
        enchantmentManager.register("enchantment.unbreaking", 34, 5, 3, l -> 5 + (l - 1) * 8, l -> (1 + l * 10) + 50, null);
        enchantmentManager.register("enchantment.fortune", 35, 2, 3, l -> 15 + (l - 1) * 9, l -> (1 + l * 10) + 50, new int[]{33});
        enchantmentManager.register("enchantment.power", 48, 10, 5, l -> 1 + (l - 1) * 10, l -> (1 + (l - 1) * 10) + 15, null);
        enchantmentManager.register("enchantment.punch", 49, 2, 2, l -> 12 + (l - 1) * 20, l -> (12 + (l - 1) * 20) + 25, null);
        enchantmentManager.register("enchantment.flame", 50, 2, 1, l -> 20, l -> 50, null);
        enchantmentManager.register("enchantment.infinity", 51, 1, 1, l -> 20, l -> 50, null);
        enchantmentManager.register("enchantment.luck_of_the_sea", 61, 2, 3, l -> 15 + (l - 1) * 9, l -> (1 + l * 10) + 50, new int[]{35});
        enchantmentManager.register("enchantment.lure", 62, 2, 3, l -> 15 + (l - 1) * 9, l -> (1 + l * 10) + 50, null);

        itemManager.register("item.helmet", new int[]{5, 6, 7, 8, 9}, new int[]{0, 1, 3, 4, 5, 6, 34});
        itemManager.register("item.chestplate", new int[]{5, 6, 7, 8, 9}, new int[]{0, 1, 3, 4, 7, 34});
        itemManager.register("item.leggings", new int[]{5, 6, 7, 8, 9}, new int[]{0, 1, 3, 4, 34});
        itemManager.register("item.boots", new int[]{5, 6, 7, 8, 9}, new int[]{0, 1, 2, 3, 4, 34});
        itemManager.register("item.sword", new int[]{0, 1, 2, 3, 4}, new int[]{16, 17, 18, 19, 20, 21, 34});
        itemManager.register("item.pickaxe", new int[]{0, 1, 2, 3, 4}, new int[]{32, 33, 34, 35});
        itemManager.register("item.axe", new int[]{0, 1, 2, 3, 4}, new int[]{32, 33, 34, 35});
        itemManager.register("item.shovel", new int[]{0, 1, 2, 3, 4}, new int[]{32, 33, 34, 35});
        itemManager.register("item.bow", new int[]{10}, new int[]{34, 48, 49, 50, 51});
        itemManager.register("item.book", new int[]{11}, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 16, 17, 18, 19, 20, 21, 32, 33, 34, 35, 48, 49, 50, 51, 61, 62});
        itemManager.register("item.fishing_rod", new int[]{12}, new int[]{34, 61, 62});

        materialManager.register(0, "material.tool.wood", 15);
        materialManager.register(1, "material.tool.stone", 5);
        materialManager.register(2, "material.tool.iron", 14);
        materialManager.register(3, "material.tool.diamond", 10);
        materialManager.register(4, "material.tool.gold", 22);
        materialManager.register(5, "material.armor.leather", 15);
        materialManager.register(6, "material.armor.chainmail", 12);
        materialManager.register(7, "material.armor.iron", 9);
        materialManager.register(8, "material.armor.diamond", 10);
        materialManager.register(9, "material.armor.gold", 25);
        materialManager.register(10, "material.bow", 1);
        materialManager.register(11, "material.book", 1);
        materialManager.register(12, "material.fishing_rod", 1);
    }

    @Override
    public int getMaxLevels() {
        return 30;
    }

    @Override
    public int getMaxBookShelves() {
        return 15;
    }

    @Override
    public int[] getEnchantLevels(Random random, int books) {
        int[] levels = new int[3];
        int intermediate = random.nextInt(8) + 1 + (books >> 1) + random.nextInt(books + 1);
        levels[0] = Math.max(intermediate / 3, 1);
        intermediate = random.nextInt(8) + 1 + (books >> 1) + random.nextInt(books + 1);
        levels[1] = (intermediate * 2 / 3 + 1);
        intermediate = random.nextInt(8) + 1 + (books >> 1) + random.nextInt(books + 1);
        levels[2] = Math.max(intermediate, books * 2);
        return levels;
    }

    @Override
    public void reverseLevels(RandomReverser reverser, int[] cycle) {
        for (int calls = 0; calls < 2; calls++) {
            reverser.consumeNextLongCalls(1);
            reverser.consumeNextIntCalls(1, 8);
            reverser.consumeNextIntCalls(1, 1);
            reverser.consumeNextIntCalls(1, 8);
            reverser.consumeNextIntCalls(1, 1);
            reverser.consumeNextIntCalls(1, 8);
            reverser.consumeNextIntCalls(1, 1);
        }

        reverser.consumeNextLongCalls(1);

        reverser.addNextIntCall(8, cycle[0] == 2 ? 5 : 0, cycle[0] == 2 ? 7 : 4);
        reverser.consumeNextIntCalls(1, 1);

        int secondMin = 0;
        int secondMax = 0;

        switch (cycle[1]) {
            case 1:
                break;
            case 2:
                secondMin = 1;
                secondMax = 1;
                break;
            case 3:
                secondMin = 2;
                secondMax = 3;
                break;
            case 4:
                secondMin = 4;
                secondMax = 4;
                break;
            case 5:
                secondMin = 5;
                secondMax = 6;
                break;
            case 6:
                secondMin = 7;
                secondMax = 7;
                break;
        }

        reverser.addNextIntCall(8, secondMin, secondMax);
        reverser.consumeNextIntCalls(1, 1);

        reverser.addNextIntCall(8, cycle[2]-1, cycle[2]-1);
        reverser.consumeNextIntCalls(1, 1);
    }

    @Override
    public void reverseExtremes(RandomReverser reverser, int advances, boolean isLow) {
        throw new UnsupportedOperationException(EnchantCrackerI18n.translate("version.error.unsupported"));
    }

    @Override
    public int getExtremesNeeded() {
        throw new UnsupportedOperationException(EnchantCrackerI18n.translate("version.error.unsupported"));
    }

    @Override
    public int getMaxEnchantability(int enchantability, int level) {
        enchantability /= 2;
        return Math.max((int)((float)(1 + ((enchantability >> 1) * 2) + level) * 1.15F + 0.5F), 1);
    }

    @Override
    public List<EnchantData> getItemEnchantments(Random random, int enchantability, String item, int level) {
        enchantability/=2;
        enchantability = 1 + random.nextInt((enchantability >> 1)+1) + random.nextInt((enchantability >> 1)+1);
        int enchantStep1 = enchantability + level;
        float enchantStep2 = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
        int enchantStep3 = (int)((float)enchantStep1 * (1.0F + enchantStep2) + 0.5F);

        if (enchantStep3 < 1) {
            enchantStep3 = 1;
        }

        List<EnchantData> enchantmentsFinal = null;

        HashMap<Integer, EnchantData> enchantmentCandidates = null;

        for (int id = 0; id < 256; id++) {
            Enchantment enchant = enchantmentManager.getEnchants()[id];
            if (enchant != null && itemManager.getItem(item).validEnchantment(enchant.getId())) {
                for (int enchantLevel = 1; enchantLevel <= enchant.getMaxLevel(); enchantLevel++) {
                    if (enchantStep3 >= enchant.getMinEnchantability(enchantLevel) && enchantStep3 <= enchant.getMaxEnchantability(enchantLevel)) {
                        if (enchantmentCandidates == null) {
                            enchantmentCandidates = new HashMap<>();
                        }
                        enchantmentCandidates.put(enchant.getId(), new EnchantData(enchant, enchantLevel));
                    }
                }
            }
        }

        if (enchantmentCandidates != null && !enchantmentCandidates.isEmpty()) {
            EnchantData selectedEnchant = getWeightedEnchantment(random, enchantmentCandidates.values());
            if (selectedEnchant != null) {
                //System.out.println("1st enchantment "+selectedEnchant.getEnchant().getName()+" @ level "+selectedEnchant.getLevel());
                //System.out.println("data size "+enchantmentCandidates.size());

                //for (EnchantData data : enchantmentCandidates.values()) {
                //    System.out.println(data.getEnchant().getName()+" @ level "+data.getLevel()+" weight "+data.getEnchant().getWeight());
                //}

                enchantmentsFinal = new ArrayList<>();
                enchantmentsFinal.add(selectedEnchant);
                for (int multipleChance = enchantStep3; random.nextInt(50) <= multipleChance; multipleChance >>= 1) {
                    Iterator<Integer> multipleIterator = enchantmentCandidates.keySet().iterator();
                    while (multipleIterator.hasNext()) {
                        int multipleId = multipleIterator.next();
                        for (EnchantData data : enchantmentsFinal) {
                            if (!data.getEnchant().isCompatibleEnchant(multipleId)) {
                                multipleIterator.remove();
                            }
                        }
                    }

                    if (!enchantmentCandidates.isEmpty()) {
                        EnchantData enchant = getWeightedEnchantment(random, enchantmentCandidates.values());
                        enchantmentsFinal.add(enchant);
                        //System.out.println("added extra enchant "+enchant.getEnchant().getName()+" @ level "+enchant.getLevel());
                    }
                }
            }
        }

        if (item.equals("item.book") && enchantmentsFinal != null) {
            int special = random.nextInt(enchantmentsFinal.size());
            List<EnchantData> copyEverythingButSpecial = new ArrayList<>();
            for (int i = 0; i < enchantmentsFinal.size(); i++) {
                if (i != special) {
                    copyEverythingButSpecial.add(enchantmentsFinal.get(i));
                }
            }
            enchantmentsFinal.clear();
            enchantmentsFinal.addAll(copyEverythingButSpecial);
        }


        return enchantmentsFinal;
    }

    @Override
    public CrackType getCrackType() {
        return CrackType.LEVELS;
    }
}
