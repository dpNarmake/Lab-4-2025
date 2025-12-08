package functions;

import java.io.*;

public class LinkedListTabulatedFunction implements TabulatedFunction, Serializable {

    private static class FunctionNode implements Serializable {
        private FunctionPoint point;
        private FunctionNode prev;
        private FunctionNode next;

        public FunctionNode() {
            point = null;
            prev = next = null;
        }

        public FunctionNode(FunctionPoint point) {
            this.point = point;
            prev = next = null;
        }

        public FunctionNode(FunctionPoint point, FunctionNode prev, FunctionNode next) {
            this.point = point;
            this.prev = prev;
            this.next = next;
        }
    }

    private FunctionNode head;
    private int size;
    private FunctionNode lastAccessedNode;
    private int lastAccessedIndex;
    private static final double eps = 1e-10;

    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {throw new IllegalArgumentException("левая граница области определения должна быть меньше правой");
        }
        if (pointsCount < 2) {throw new IllegalArgumentException("количество точек должно быть не менее двух");
        }

        head = new FunctionNode();
        head.prev = head;
        head.next = head;
        size = 0;
        lastAccessedNode = head;
        lastAccessedIndex = -1;

        double interval = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; ++i) {
            addNodeToTail().point = new FunctionPoint(leftX + i * interval, 0);
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {throw new IllegalArgumentException("левая граница области определения должна быть меньше правой");
        }
        if (values.length < 2) {throw new IllegalArgumentException("количество точек должно быть не менее двух");
        }

        head = new FunctionNode();
        head.prev = head;
        head.next = head;
        size = 0;
        lastAccessedNode = head;
        lastAccessedIndex = -1;

        double interval = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; ++i) {
            addNodeToTail().point = new FunctionPoint(leftX + i * interval, values[i]);
        }
    }

    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("количество точек должно быть не менее двух");
        }

        for (int i = 0; i < points.length - 1; i++) {
            if (points[i].getX() >= points[i + 1].getX()) {
                throw new IllegalArgumentException("точки должны быть упорядочены по возрастанию абсцисс");
            }
        }

        head = new FunctionNode();
        head.prev = head;
        head.next = head;
        size = 0;
        lastAccessedNode = head;
        lastAccessedIndex = -1;

        for (FunctionPoint point : points) {
            addNodeToTail().point = new FunctionPoint(point);
        }
    }

    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        if (lastAccessedIndex != -1) {
            int dif = index - lastAccessedIndex;
            if (Math.abs(dif) <= Math.min(index, size - 1 - index)) {
                if (dif > 0) {
                    for (int i = 0; i < dif; i++) lastAccessedNode = lastAccessedNode.next;
                } else {
                    for (int i = 0; i < -dif; i++) lastAccessedNode = lastAccessedNode.prev;
                }
                lastAccessedIndex = index;
                return lastAccessedNode;
            }
        }

        FunctionNode current;
        if (index < size / 2) {
            current = head.next;
            for (int i = 0; i < index; i++) current = current.next;
        } else {
            current = head.prev;
            for (int i = size - 1; i > index; i--) current = current.prev;
        }

        lastAccessedNode = current;
        lastAccessedIndex = index;
        return current;
    }

    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode();
        newNode.next = head;
        newNode.prev = head.prev;
        head.prev.next = newNode;
        head.prev = newNode;
        size++;

        lastAccessedNode = newNode;
        lastAccessedIndex = size - 1;
        return newNode;
    }

    private FunctionNode addNodeByIndex(int index) {
        if (index < 0 || index > size) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        if (index == size) return addNodeToTail();

        FunctionNode nodeAtIndex = getNodeByIndex(index);
        FunctionNode newNode = new FunctionNode();
        newNode.prev = nodeAtIndex.prev;
        newNode.next = nodeAtIndex;
        nodeAtIndex.prev.next = newNode;
        nodeAtIndex.prev = newNode;
        size++;

        lastAccessedNode = newNode;
        lastAccessedIndex = index;
        return newNode;
    }

    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        FunctionNode nodeToDelete = getNodeByIndex(index);
        nodeToDelete.prev.next = nodeToDelete.next;
        nodeToDelete.next.prev = nodeToDelete.prev;
        size--;

        if (lastAccessedIndex == index) {
            lastAccessedNode = head;
            lastAccessedIndex = -1;
        } else if (lastAccessedIndex > index) {
            lastAccessedIndex--;
        }

        return nodeToDelete;
    }

    public double getLeftDomainBorder() {
        if (size == 0) return Double.NaN;
        return head.next.point.getX();
    }

    public double getRightDomainBorder() {
        if (size == 0) return Double.NaN;
        return head.prev.point.getX();
    }

    public double getFunctionValue(double x) {
        if (size == 0 || x < getLeftDomainBorder() || x > getRightDomainBorder()) return Double.NaN;

        FunctionNode current = head.next;
        while (current != head) {
            if (Math.abs(x - current.point.getX()) < eps) return current.point.getY();
            current = current.next;
        }

        current = head.next;
        while (current.next != head) {
            if (x >= current.point.getX() && x <= current.next.point.getX()) {
                double x1 = current.point.getX();
                double y1 = current.point.getY();
                double x2 = current.next.point.getX();
                double y2 = current.next.point.getY();
                return y1 + (x - x1) * (y2 - y1) / (x2 - x1);
            }
            current = current.next;
        }

        return Double.NaN;
    }

    public int getPointsCount() {return size;}

    public FunctionPoint getPoint(int index) {
        FunctionNode node = getNodeByIndex(index);
        return new FunctionPoint(node.point);
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException{
        FunctionNode node = getNodeByIndex(index);
        FunctionPoint newPoint = new FunctionPoint(point);

        if (size > 1) {
            if (index == 0) {
                if (newPoint.getX() > getNodeByIndex(1).point.getX()) {
                    throw new InappropriateFunctionPointException();
                }
            } else if (index == size - 1) {
                if (newPoint.getX() < getNodeByIndex(size - 2).point.getX()) {
                    throw new InappropriateFunctionPointException();
                }
            } else {
                FunctionNode prevNode = getNodeByIndex(index - 1);
                FunctionNode nextNode = getNodeByIndex(index + 1);
                if (newPoint.getX() < prevNode.point.getX() || newPoint.getX() > nextNode.point.getX()) {
                    throw new InappropriateFunctionPointException();
                }
            }
        }

        node.point = newPoint;
    }

    public double getPointX(int index) {return getNodeByIndex(index).point.getX();}

    public void setPointX(int index, double x) throws InappropriateFunctionPointException{
        FunctionNode node = getNodeByIndex(index);

        if (size > 1) {
            if (index == 0) {
                if (x > getNodeByIndex(1).point.getX()) {
                    throw new InappropriateFunctionPointException();
                }
            } else if (index == size - 1) {
                if (x < getNodeByIndex(size - 2).point.getX()) {
                    throw new InappropriateFunctionPointException();
                }
            } else {
                FunctionNode prevNode = getNodeByIndex(index - 1);
                FunctionNode nextNode = getNodeByIndex(index + 1);
                if (x < prevNode.point.getX() || x > nextNode.point.getX()) {
                    throw new InappropriateFunctionPointException();
                }
            }
        }

        node.point.setX(x);
    }

    public double getPointY(int index) {return getNodeByIndex(index).point.getY();}

    public void setPointY(int index, double y) {getNodeByIndex(index).point.setY(y);}

    public void deletePoint(int index) {
        if (size < 3) {
            throw new IllegalStateException("количество точек должно быть не менее трех");
        }
        deleteNodeByIndex(index);
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException{
        FunctionPoint newPoint = new FunctionPoint(point);
        double x = newPoint.getX();

        FunctionNode current = head.next;
        while (current != head) {
            if (Math.abs(x - current.point.getX()) < eps) {
                throw new InappropriateFunctionPointException();
            }
            current = current.next;
        }

        int insertIndex = 0;
        current = head.next;
        while (current != head && x > current.point.getX()) {
            insertIndex++;
            current = current.next;
        }

        FunctionNode newNode = addNodeByIndex(insertIndex);
        newNode.point = newPoint;
    }

    public FunctionPoint[] getAllPoints() {
        FunctionPoint[] result = new FunctionPoint[getPointsCount()];
        for (int i = 0; i < getPointsCount(); i++) {
            result[i] = getPoint(i);
        }
        return result;
    }
}