package com.terriblefriends.oldenchcracker;

import com.terriblefriends.oldenchcracker.version.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Main {

    /*
    ZERO b1.9pre4 - fortune + silk
    ONE b1.9pre5 - wooden tool enchantability increased
    TWO 12w01a - level formula change
    THREE 1.1 - bows can be enchanted
    FOUR 12w18a - internal server
    FIVE 12w22a (1.3) - 50 -> 30 level change
    SIX 12w49a (1.4.6) - enchanted books added
    SEVEN 12w50a (1.4.6) - chestplates can receive thorns, shears can receive silk
    EIGHT 13w36a (1.7) - fishing rods can be enchanted, armor + bows can receive unbreaking
    NINE 13w37a (1.7) - changes to how enchantments are selected for books
    14w02a (1.8) - end of this tool
     */

    public static void main(String[] args) {
        Iterator<String> arguments = Arrays.stream(args).iterator();

        EnchantCracker cracker = new EnchantCracker();

        while (arguments.hasNext()) {
            String nextArg = arguments.next();
            switch (nextArg) {
                case "--help":
                    System.out.println(EnchantCrackerI18n.translate("program.arg.help"));
                    return;
                case "--seed":
                    try {
                        long seed = Long.parseLong(arguments.next());
                        cracker.setRngSeed(seed);
                        System.out.printf((EnchantCrackerI18n.translate("program.arg.seed")) + "%n", seed);
                    } catch (NoSuchElementException | NumberFormatException e) {
                        System.err.printf((EnchantCrackerI18n.translate("program.arg.error.parse")) + "%n", "--seed");
                        System.err.println(EnchantCrackerI18n.translate("program.arg.help"));
                    }
                    break;
                case "--advances":
                    try {
                        int advances = Integer.parseInt(arguments.next());
                        cracker.setMaxAdvances(advances);
                        System.out.printf((EnchantCrackerI18n.translate("program.arg.advances")) + "%n", advances);
                    } catch (NoSuchElementException | NumberFormatException e) {
                        System.err.printf((EnchantCrackerI18n.translate("program.arg.error.parse")) + "%n", "--advances");
                        System.err.println(EnchantCrackerI18n.translate("program.arg.help"));
                    }
                    break;
                case "--version":
                    try {
                        int versionI = Integer.parseInt(arguments.next());
                        Version version;
                        switch (versionI) {
                            case 0:
                                version = new Zero();
                                break;
                            case 1:
                                version = new One();
                                break;
                            case 2:
                                version = new Two();
                                break;
                            case 3:
                                version = new Three();
                                break;
                            case 4:
                                version = new Four();
                                break;
                            case 5:
                                version = new Five();
                                break;
                            case 6:
                                version = new Six();
                                break;
                            case 7:
                                version = new Seven();
                                break;
                            case 8:
                                version = new Eight();
                                break;
                            case 9:
                                version = new Nine();
                                break;
                            default:
                                throw new IllegalArgumentException();
                        }
                        cracker.setVersion(version);
                        System.out.printf((EnchantCrackerI18n.translate("program.arg.version")) + "%n", EnchantCrackerI18n.translate("version." + versionI));
                    } catch (IllegalArgumentException e) {
                        System.err.printf((EnchantCrackerI18n.translate("program.arg.error.parse")) + "%n", "--version");
                        System.err.println(EnchantCrackerI18n.translate("program.arg.help"));
                    }
                    break;
                case "--bookshelves":
                    try {
                        int bookshelves = Integer.parseInt(arguments.next());
                        cracker.setBookshelves(bookshelves);
                        System.out.printf((EnchantCrackerI18n.translate("program.arg.bookshelves")) + "%n", bookshelves);
                    } catch (NoSuchElementException | NumberFormatException e) {
                        System.err.printf((EnchantCrackerI18n.translate("program.arg.error.parse")) + "%n", "--bookshelves");
                        System.err.println(EnchantCrackerI18n.translate("program.arg.help"));
                    }
                    break;
                default:
                    System.err.printf((EnchantCrackerI18n.translate("program.arg.error.unknownarg")) + "%n", nextArg);
                    return;
            }
        }

        cracker.init();
    }
}