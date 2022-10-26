package AdditionalClasses;

import java.util.Arrays;

/**
 * Класс для хранения и обработки результатов расчетов агентов.
 * Fields:
 * - size - количество столбцов матрицы (агентов, отправляющих сообщения)
 * - matrix - матрица для хранения результатов расчетов
 * - curColumn - заполняемая колонна (нужно для инкапсуляции поведения при добавлении нового столбца)
 */
public class ValuesContainer{
    private int rows;
    private int columns;
    private double[][] matrix;
    private int curColumn = 0;

    public ValuesContainer(int sideLength) {
        this.columns = sideLength;
        rows = 3;
        matrix = new double[rows][sideLength];
    }

    /**
     * Заполняет столбец matrix значениями из values
     * @param values
     */
    public void addColumn(double[] values) {
        if (values.length != rows) {
            throw new IllegalArgumentException("Колличество элементов не соответствует размеру контэйнера!");
        } else if (isFull()) {
            throw new UnsupportedOperationException("Контэйнер переполнен!");
        }
        for (int i = 0; i < rows; i++) {
            matrix[i][curColumn] = values[i];
        }
        curColumn++;
    }

    /**
     * Возвращает сумму элементов строки
     * @param row - номер строки
     * @return sum
     */
    public double getRowSum(int row) {
        if (row > rows) {
            throw new IllegalArgumentException("Номер строки выходит за размеры контэйнера!");
        }
        return Arrays.stream(matrix[row]).sum();
    }

    public boolean isFull() {
        return curColumn >= columns;
    }

    /**
     * Определяет строку с наибольшей суммой элементов
     * @return номер строки с лучшим результатом
     */
    public int getBestResultNumber() {
        double maxSum = getRowSum(0);
        int bestRow = 0;
        for (int i = 1; i < rows; i++) {
            if (maxSum <= getRowSum(i)) {
                maxSum = getRowSum(i);
                bestRow = i;
            }
        }
        return bestRow;
    }
}
