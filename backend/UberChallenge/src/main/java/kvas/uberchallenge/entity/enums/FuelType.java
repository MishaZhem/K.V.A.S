package kvas.uberchallenge.entity.enums;

public enum FuelType {
    HYBRID(0),
    EV(1),
    GAS(2);

    private final int value;

    FuelType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static FuelType fromValue(int value) {
        for (FuelType type : FuelType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid FuelType value: " + value);
    }
}
