package com.app.alltra.backend;

import java.util.Random;

public class RandomNumberGenerator {

    public static double generateRandomDouble(double startRange, double endRange) {
        Random random = new Random();
        return roundToTwoDecimalPlaces(startRange + (endRange - startRange) * random.nextDouble());
    }

    public static int generateRandomInt(int startRange, int endRange) {
        Random random = new Random();
        return random.nextInt(endRange - startRange + 1) + startRange;
    }

    public static double roundToTwoDecimalPlaces(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
