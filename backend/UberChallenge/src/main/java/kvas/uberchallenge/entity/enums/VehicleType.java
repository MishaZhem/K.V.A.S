package kvas.uberchallenge.entity.enums;

public enum VehicleType {
    CAR(0),
    SCOOTER(1),
    BIKE(2);

    private final int value;

    VehicleType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static VehicleType fromValue(int value) {
        for (VehicleType type : VehicleType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid VehicleType value: " + value);
    }
}
