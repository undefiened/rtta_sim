package other;

public class QuadraticEquationSolver {
    protected final double x1, x2;

    public QuadraticEquationSolver(double a, double b, double c){
        double d = Math.sqrt(b * b - 4 * a * c);
        x1 = (-b + d) / (2 * a);
        x2 = (-b - d) / (2 * a);
    }

    public double getX1() {
        return x1;
    }

    public double getX2() {
        return x2;
    }

    public double getMax() {
        return Math.max(x1, x2);
    }

    public double getMin() {
        return Math.min(x1, x2);
    }
}
