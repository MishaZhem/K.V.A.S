package kvas.uberchallenge.entity.enums;

public enum ProductType {
    UBER_X(0),
    UBER_POOL(1),
    UBER_GREEN(2),
    UBER_BLACK(3),
    UBER_EATS(4);

    private final int value;

    ProductType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ProductType fromValue(int value) {
        for (ProductType type : ProductType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid ProductType value: " + value);
    }
}
