package com.terriblefriends.oldenchcracker.version;

public class Versions {
    public static final Version ZERO = new Zero();
    public static final Version ONE = new One();
    public static final Version TWO = new Two();
    public static final Version THREE = new Three();
    public static final Version FOUR = new Four();
    public static final Version FIVE = new Five();
    public static final Version SIX = new Six();
    public static final Version SEVEN = new Seven();
    public static final Version EIGHT = new Eight();
    public static final Version NINE = new Nine();

    public static final Version[] VERSIONS = new Version[] {ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE};

    public static int versionToInt(Version version) {
        for (int i = 0; i < VERSIONS.length; i++) {
            if (version == VERSIONS[i]) {
                return i;
            }
        }
        throw new IllegalArgumentException("Version argument is not present in version list!!! Make sure to add any new versions to the Versions.VERSIONS array!");
    }

    public static Version intToVersion(int i) {
        if (i < 0 || i >= VERSIONS.length) {
            throw new IllegalArgumentException("Requested index is invalid!");
        }
        return VERSIONS[i];
    }
}
