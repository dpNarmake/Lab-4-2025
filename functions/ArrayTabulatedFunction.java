package functions;

import java.io.*;

public class ArrayTabulatedFunction implements TabulatedFunction, Externalizable {

    private FunctionPoint[] points;
    private int size;
    private static final double eps = 1e-10;

    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("левая граница области определения должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("количество точек должно быть не менее двух");
        }

        points = new FunctionPoint[pointsCount];
        size = pointsCount;
        double interval = (rightX - leftX) / (size - 1);

        for (int i = 0; i < size; ++i) points[i] = new FunctionPoint((leftX + i * interval), 0);
    }

    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("левая граница области определения должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("количество точек должно быть не менее двух");
        }

        points = new FunctionPoint[values.length];
        size = values.length;
        double interval = (rightX - leftX) / (values.length - 1);

        for (int i = 0; i < values.length; ++i) points[i] = new FunctionPoint((leftX + i * interval), values[i]);
    }

    public ArrayTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("количество точек должно быть не менее двух");
        }

        for (int i = 0; i < points.length - 1; i++) {
            if (points[i].getX() >= points[i + 1].getX()) {
                throw new IllegalArgumentException("точки должны быть упорядочены по возрастанию абсцисс");
            }
        }

        this.points = new FunctionPoint[points.length];
        for (int i = 0; i < points.length; i++) {
            this.points[i] = new FunctionPoint(points[i]);
        }
        this.size = points.length;
    }

    public ArrayTabulatedFunction() {}

    public double getLeftDomainBorder() {
        if (size == 0) return Double.NaN;
        return points[0].getX();
    }

    public double getRightDomainBorder() {
        if (size == 0) return Double.NaN;
        return points[size - 1].getX();
    }

    public double getFunctionValue(double x) {
        if ((size == 0) || (x < getLeftDomainBorder()) || x > getRightDomainBorder()) return Double.NaN;

        for (int i = 0; i < size; i++) {
            if (Math.abs(x - points[i].getX()) < eps) return points[i].getY();
        }

        for (int i = 0; i < size - 1; i++) {
            if (x >= points[i].getX() && x <= points[i + 1].getX()) {
                double x1 = points[i].getX();
                double y1 = points[i].getY();
                double x2 = points[i + 1].getX();
                double y2 = points[i + 1].getY();

                return (y1 + (x - x1) * (y2 - y1) / (x2 - x1));
            }
        }
        return Double.NaN;
    }

    public int getPointsCount() {return size;}

    public FunctionPoint getPoint(int index) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException();}
        return new FunctionPoint(points[index]);
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        FunctionPoint newPoint = new FunctionPoint(point);

        if (size > 1) {
            if (index == 0) {
                if (newPoint.getX() > points[1].getX()) {
                    throw new InappropriateFunctionPointException();
                }
                points[0] = newPoint;
            } else if (index == size - 1) {
                if (newPoint.getX() < points[size - 2].getX()) {
                    throw new InappropriateFunctionPointException();
                }
                points[size - 1] = newPoint;
            } else {
                if (newPoint.getX() < points[index - 1].getX() || newPoint.getX() > points[index + 1].getX()) {
                    throw new InappropriateFunctionPointException();
                }
                points[index] = newPoint;
            }
        } else points[index] = newPoint;
    }

    public double getPointX(int index) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        return points[index].getX();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        if (size > 1) {
            if (index == 0) {
                if (x > points[1].getX()) {
                    throw new InappropriateFunctionPointException();
                }
                points[0].setX(x);
            } else if (index == size - 1) {
                if (x < points[size - 2].getX()) {
                    throw new InappropriateFunctionPointException();
                }
                points[size - 1].setX(x);
            } else {
                if (x < points[index - 1].getX() || x > points[index + 1].getX()) {
                    throw new InappropriateFunctionPointException();
                }
                points[index].setX(x);
            }
        } else points[index].setX(x);
    }

    public double getPointY(int index) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        return points[index].getY();
    }

    public void setPointY(int index, double y) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        points[index].setY(y);
    }

    public void deletePoint(int index) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        if (size < 3) {
            throw new IllegalStateException("количество точек должно быть не менее трех");
        }

        --size;
        System.arraycopy(points, index + 1, points, index, size - index);
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionPoint newPoint = new FunctionPoint(point);
        double x = newPoint.getX();

        for (int i = 0; i < size; i++) {
            if (Math.abs(x - points[i].getX()) < eps) {
                throw new InappropriateFunctionPointException();}
        }

        int insert_index = size;
        for (int i = 0; i < size; i++) {
            if (x < points[i].getX()) {
                insert_index = i;
                break;
            }
        }

        if (size >= points.length) {
            FunctionPoint[] newPoints = new FunctionPoint[size + 10];
            System.arraycopy(points, 0, newPoints, 0, size);
            points = newPoints;
        }

        if (insert_index < size)
            System.arraycopy(points, insert_index, points, insert_index + 1, size - insert_index);

        points[insert_index] = newPoint;
        size++;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(size);

        for (int i = 0; i < size; i++) {
            out.writeDouble(points[i].getX());
            out.writeDouble(points[i].getY());
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        size = in.readInt();

        points = new FunctionPoint[size];

        for (int i = 0; i < size; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            points[i] = new FunctionPoint(x, y);
        }
    }

    public FunctionPoint[] getAllPoints() {
        FunctionPoint[] result = new FunctionPoint[size];
        for (int i = 0; i < size; i++) {
            result[i] = new FunctionPoint(points[i]);
        }
        return result;
    }
}