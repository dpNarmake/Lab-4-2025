package functions.basic;

import functions.Function;

public class Log implements Function {
    private double base;
    private static final double eps = 1e-10;

    public Log(double base) {
        if (Math.abs(base - 1.0) <= eps || base <= 0) {
            throw new IllegalArgumentException("основание логарифма должно быть положительным и не равным 1");
        }
        this.base = base;
    }

    public double getLeftDomainBorder() {return 0;}

    public double getRightDomainBorder() {return Double.POSITIVE_INFINITY;}

    public double getFunctionValue(double x) {
        if (x <= 0) return Double.NaN;
        return Math.log(x) / Math.log(base);
    }
}