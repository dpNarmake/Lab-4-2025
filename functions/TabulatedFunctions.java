package functions;

import java.io.*;

public class TabulatedFunctions {
    private static final double eps = 1e-10;

    private TabulatedFunctions() {}

    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        if (leftX < function.getLeftDomainBorder() - eps || rightX > function.getRightDomainBorder() + eps) {
            throw new IllegalArgumentException("границы для табулирования выходят за область определения функции");
        }

        FunctionPoint[] points = new FunctionPoint[pointsCount];
        double interval = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; ++i)points[i] = new FunctionPoint((leftX + i * interval), function.getFunctionValue(leftX + i * interval));

        return new ArrayTabulatedFunction(points);
    }

    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(out);

        dataOut.writeInt(function.getPointsCount());

        for (int i = 0; i < function.getPointsCount(); ++i) {
            dataOut.writeDouble(function.getPointX(i));
            dataOut.writeDouble(function.getPointY(i));
        }

        dataOut.flush();
    }

    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(in);

        int pointsCount = dataIn.readInt();

        FunctionPoint[] points = new FunctionPoint[pointsCount];

        for (int i = 0; i < pointsCount; ++i) points[i] = new FunctionPoint(dataIn.readDouble(), dataIn.readDouble());

        return new ArrayTabulatedFunction(points);
    }

    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) throws IOException {
        PrintWriter writer = new PrintWriter(new BufferedWriter(out));

        writer.print(function.getPointsCount());

        for (int i = 0; i < function.getPointsCount(); ++i) {
            writer.print(" " + function.getPointX(i));
            writer.print(" " + function.getPointY(i));
        }

        writer.flush();
    }

    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(in);

        tokenizer.nextToken();

        int pointsCount = (int)tokenizer.nval;

        double x, y;
        FunctionPoint points[] = new FunctionPoint[pointsCount];

        for(int i = 0; i < pointsCount; ++i){
            tokenizer.nextToken();
            x = tokenizer.nval;
            tokenizer.nextToken();
            y = tokenizer.nval;
            points[i] = new FunctionPoint(x, y);
        }

        return new ArrayTabulatedFunction(points);
    }
}