package com.terriblefriends.oldenchcracker.thingmanager;

public enum SeedResults {
    SEED_RESULT_UNSET(-1L),
    SEED_RESULT_NOTFOUND(-2L),
    SEED_RESULT_MANYFOUND(-3L);

    private final long value;

    SeedResults(long l) {
        this.value = l;
    }

    public long getValue() {
        return this.value;
    }
}
