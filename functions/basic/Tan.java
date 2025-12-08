package functions.basic;

public class Tan extends TrigonometricFunction {
    private static final double eps = 1e-10;

    public double getFunctionValue(double x) {
        double cosValue = Math.cos(x);
        if (Math.abs(cosValue) < eps) return Double.NaN;
        return Math.tan(x);
    }
}