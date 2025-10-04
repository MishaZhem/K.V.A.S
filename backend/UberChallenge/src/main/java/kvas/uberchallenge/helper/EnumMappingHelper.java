package kvas.uberchallenge.helper;

import kvas.uberchallenge.entity.*;
import kvas.uberchallenge.entity.enums.*;

/**
 * Utility class for converting between enum names and their integer values
 * Based on the mappings:
 * - mapProduct = {'UberX':0, 'UberPool':1, 'UberGreen':2, 'UberBlack':3, 'Uber Eats':4}
 * - mapVehicleType = {'car':0, 'scooter':1, 'bike':2}
 * - mapPaymentType = {'cash':0, 'card':1, 'wallet':2}
 * - mapEarnerType = {'driver':0, 'courier':1}
 * - mapFuelType = {'hybrid':0, 'EV':1, 'gas':2}
 * - mapWeatherType = {'clear':0, 'rain':1, 'snow':2}
 */
public class EnumMappingHelper {

    // Product Type mappings
    public static ProductType getProductTypeByName(String name) {
        return switch (name.toLowerCase()) {
            case "uberx" -> ProductType.UBER_X;
            case "uberpool" -> ProductType.UBER_POOL;
            case "ubergreen" -> ProductType.UBER_GREEN;
            case "uberblack" -> ProductType.UBER_BLACK;
            case "uber_eats" -> ProductType.UBER_EATS;
            default -> throw new IllegalArgumentException("Invalid product type: " + name);
        };
    }

    // Vehicle Type mappings
    public static VehicleType getVehicleTypeByName(String name) {
        return switch (name.toLowerCase()) {
            case "car" -> VehicleType.CAR;
            case "scooter" -> VehicleType.SCOOTER;
            case "bike" -> VehicleType.BIKE;
            default -> throw new IllegalArgumentException("Invalid vehicle type: " + name);
        };
    }

    // Payment Type mappings
    public static PaymentType getPaymentTypeByName(String name) {
        return switch (name.toLowerCase()) {
            case "cash" -> PaymentType.CASH;
            case "card" -> PaymentType.CARD;
            case "wallet" -> PaymentType.WALLET;
            default -> throw new IllegalArgumentException("Invalid payment type: " + name);
        };
    }

    // Earner Type mappings
    public static EarnerType getEarnerTypeByName(String name) {
        return switch (name.toLowerCase()) {
            case "driver" -> EarnerType.DRIVER;
            case "courier" -> EarnerType.COURIER;
            default -> throw new IllegalArgumentException("Invalid earner type: " + name);
        };
    }

    // Fuel Type mappings
    public static FuelType getFuelTypeByName(String name) {
        return switch (name.toLowerCase()) {
            case "hybrid" -> FuelType.HYBRID;
            case "ev" -> FuelType.EV;
            case "gas" -> FuelType.GAS;
            default -> throw new IllegalArgumentException("Invalid fuel type: " + name);
        };
    }

    // Weather Type mappings
    public static WeatherType getWeatherTypeByName(String name) {
        return switch (name.toLowerCase()) {
            case "clear" -> WeatherType.CLEAR;
            case "rain" -> WeatherType.RAIN;
            case "snow" -> WeatherType.SNOW;
            default -> throw new IllegalArgumentException("Invalid weather type: " + name);
        };
    }

    // Utility methods to get integer values directly
    public static int getProductTypeValue(String name) {
        return getProductTypeByName(name).getValue();
    }

    public static int getVehicleTypeValue(String name) {
        return getVehicleTypeByName(name).getValue();
    }

    public static int getPaymentTypeValue(String name) {
        return getPaymentTypeByName(name).getValue();
    }

    public static int getEarnerTypeValue(String name) {
        return getEarnerTypeByName(name).getValue();
    }

    public static int getFuelTypeValue(String name) {
        return getFuelTypeByName(name).getValue();
    }

    public static int getWeatherTypeValue(String name) {
        return getWeatherTypeByName(name).getValue();
    }
}
