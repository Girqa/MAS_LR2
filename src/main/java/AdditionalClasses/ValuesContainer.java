package AdditionalClasses;

import java.util.Arrays;

public class ValuesContainer{
    private int size;
    private double[][] matrix;
    private int curColumn = 0;

    public ValuesContainer(int sideLength) {
        this.size = sideLength;
        matrix = new double[sideLength][sideLength];
    }

    public void addColumn(double[] values) {
        if (values.length != size) {
            throw new IllegalArgumentException("Колличество элементов не соответствует размеру контэйнера!");
        } else if (!isFull()) {
            throw new UnsupportedOperationException("Контэйнер переполнен!");
        }
        for (int i = 0; i < size; i++) {
            matrix[i][curColumn] = values[i];
        }
        curColumn++;
    }

    public double getRowSum(int row) {
        if (row > size) {
            throw new IllegalArgumentException("Номер строки выходит за размеры контэйнера!");
        }
        return Arrays.stream(matrix[row]).sum();
    }
    public boolean isFull() {
        return curColumn < size;
    }
}
