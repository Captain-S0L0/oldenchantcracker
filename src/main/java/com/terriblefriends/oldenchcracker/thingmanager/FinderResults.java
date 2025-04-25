package com.terriblefriends.oldenchcracker.thingmanager;

public enum FinderResults {
    FINDER_RESULT_UNSET(-1),
    FINDER_RESULT_NOTFOUND(-2);

    private final int value;

    FinderResults(int l) {
        this.value = l;
    }

    public int getValue() {
        return this.value;
    }
}
