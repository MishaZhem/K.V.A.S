package kvas.uberchallenge.entity.enums;

public enum PaymentType {
    CASH(0),
    CARD(1),
    WALLET(2);

    private final int value;

    PaymentType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PaymentType fromValue(int value) {
        for (PaymentType type : PaymentType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid PaymentType value: " + value);
    }
}
