import functions.*;
import functions.basic.*;
import functions.meta.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {

        Function sinFunction = new Sin();
        Function cosFunction = new Cos();

        System.out.println("вывод Sin и Cos на отрезке от 0 до π с шагом 0.1:");
        printFunctionValues(sinFunction, 0, Math.PI, 0.1);
        printFunctionValues(cosFunction, 0, Math.PI, 0.1);

        TabulatedFunction sinTabulated = TabulatedFunctions.tabulate(sinFunction, 0, Math.PI, 10);
        TabulatedFunction cosTabulated = TabulatedFunctions.tabulate(cosFunction, 0, Math.PI, 10);

        System.out.println("\nвывод табулированных Sin и Cos на отрезке от 0 до π с шагом 0.1:");
        printTabulatedFunctionValues(sinTabulated, 0, Math.PI, 0.1);
        printTabulatedFunctionValues(cosTabulated, 0, Math.PI, 0.1);

        System.out.println("\nвывод суммы квадратов на отрезке от 0 до π с шагом 0.1:");
        printFunctionValues(Functions.sum(Functions.power(sinTabulated, 2), Functions.power(cosTabulated, 2)), 0, Math.PI, 0.1);

        int[] pointCounts = {3, 5, 10, 20, 50, 100};
        for (int points : pointCounts) {
            System.out.println("\nколичество точек табуляции: " + points);

            TabulatedFunction sinTabulated1 = TabulatedFunctions.tabulate(sinFunction, 0, Math.PI, points);
            TabulatedFunction cosTabulated1 = TabulatedFunctions.tabulate(cosFunction, 0, Math.PI, points);

            System.out.println("значения суммы квадратов:");
            printFunctionValues(Functions.sum(Functions.power(sinTabulated1, 2), Functions.power(cosTabulated1, 2)), 0, Math.PI, 0.1);
        }

        Function expFunction = new Exp();

        TabulatedFunction expTabulated = TabulatedFunctions.tabulate(expFunction, 0, 10, 11);

        try {
            FileWriter fileWriter = new FileWriter("tabulated_exp.txt");
            TabulatedFunctions.writeTabulatedFunction(expTabulated, fileWriter);
            fileWriter.close();
        } catch (IOException err) {
            err.printStackTrace();
        }

        try {
            FileReader fileReader = new FileReader("tabulated_exp.txt");
            TabulatedFunction readExpTabulated = TabulatedFunctions.readTabulatedFunction(fileReader);
            fileReader.close();

            System.out.println("\nзначения исходной и считанной функций Exp на отрезке от 0 до 10 с шагом 1:");
            System.out.println("исходная функция:");
            printFunctionValues(expTabulated, 0, 10, 1);
            System.out.println("\nсчитанная функция:");
            printTabulatedFunctionValues(readExpTabulated, 0, 10, 1);

        } catch (IOException err) {
            err.printStackTrace();
        }

        Function logFunction = new Log(Math.E);

        TabulatedFunction logTabulated = TabulatedFunctions.tabulate(logFunction, 0, 10, 11);

        try {
            FileOutputStream fileOut = new FileOutputStream("tabulated_log.txt");
            TabulatedFunctions.outputTabulatedFunction(logTabulated, fileOut);
            fileOut.close();
        } catch (IOException err) {
            err.printStackTrace();
        }

        try {
            FileInputStream fileIn = new FileInputStream("tabulated_log.txt");
            TabulatedFunction readLogTabulated = TabulatedFunctions.inputTabulatedFunction(fileIn);
            fileIn.close();

            System.out.println("\nзначения исходной и считанной функций Log на отрезке от 0 до 10 с шагом 1:");
            System.out.println("исходная функция:");
            printFunctionValues(logTabulated, 0, 10, 1);
            System.out.println("\nсчитанная функция:");
            printTabulatedFunctionValues(readLogTabulated, 0, 10, 1);

        } catch (IOException err) {
            err.printStackTrace();
        }


        // сериализация
        TabulatedFunction logTabulatedSer = TabulatedFunctions.tabulate(new Log(Math.E), 0, 10, 11);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("serializable_tabulated_log.txt"))) {
            oos.writeObject(logTabulatedSer);
        } catch (IOException err) {
            err.printStackTrace();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("serializable_tabulated_log.txt"))) {
            TabulatedFunction readLogTabulated = (TabulatedFunction) ois.readObject();

            System.out.println("\nserializable: значения исходной и считанной функций Log на отрезке от 0 до 10 с шагом 1:");
            System.out.println("исходная функция:");
            printTabulatedFunctionValues(logTabulatedSer, 0, 10, 1);
            System.out.println("\nсчитанная функция:");
            printTabulatedFunctionValues(readLogTabulated, 0, 10, 1);

        } catch (IOException | ClassNotFoundException err) {
            err.printStackTrace();
        }

        TabulatedFunction logTabulatedEx = TabulatedFunctions.tabulate(new Log(Math.E), 0, 10, 11);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("externalizable_tabulated_log.txt"))) {
            oos.writeObject(logTabulatedEx);
        } catch (IOException err) {
            err.printStackTrace();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("externalizable_tabulated_log.txt"))) {
            TabulatedFunction readLogTabulated = (TabulatedFunction) ois.readObject();

            System.out.println("\nexternalizable: значения исходной и считанной функций Log на отрезке от 0 до 10 с шагом 1:");
            System.out.println("исходная функция:");
            printTabulatedFunctionValues(logTabulatedEx, 0, 10, 1);
            System.out.println("\nсчитанная функция:");
            printTabulatedFunctionValues(readLogTabulated, 0, 10, 1);

        } catch (IOException | ClassNotFoundException err) {
            err.printStackTrace();
        }
    }

    private static void printFunctionValues(Function function, double start, double end, double step) throws Exception {
        for (double x = start; x <= end + 1e-10; x += step) {
            System.out.println("F(" + x + ")" + " = " +function.getFunctionValue(x));
        }
    }

    private static void printTabulatedFunctionValues(TabulatedFunction tabulatedFunction, double start, double end, double step) throws Exception {
        for (double x = start; x <= end + 1e-10; x += step) {
            System.out.println("Tabulated F(" + x + ")" + " = " + tabulatedFunction.getFunctionValue(x));
        }
    }
}