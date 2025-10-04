package kvas.uberchallenge.entity.enums;

public enum WeatherType {
    CLEAR(0),
    RAIN(1),
    SNOW(2);

    private final int value;

    WeatherType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static WeatherType fromValue(int value) {
        for (WeatherType type : WeatherType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid WeatherType value: " + value);
    }
}
