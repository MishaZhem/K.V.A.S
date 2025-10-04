package kvas.uberchallenge.entity.enums;

public enum EarnerType {
    DRIVER(0),
    COURIER(1);

    private final int value;

    EarnerType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static EarnerType fromValue(int value) {
        for (EarnerType type : EarnerType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid EarnerType value: " + value);
    }
}
