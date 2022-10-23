package AdditionalClasses;

import java.util.Arrays;

/**
 * Класс для хранения и обработки результатов расчетов агентов.
 * Fields:
 * - size - размер матрицы результатов расчетов (матрица является квадратной
 * - matrix - матрица для хранения результатов расчетов
 * - curColumn - заполняемая колонна (нужно для инкапсуляции поведения при добавлении нового столбца)
 */
public class ValuesContainer{
    private int size;
    private double[][] matrix;
    private int curColumn = 0;

    public ValuesContainer(int sideLength) {
        this.size = sideLength;
        matrix = new double[sideLength][sideLength];
    }

    /**
     * Заполняет столбец matrix значениями из values
     * @param values
     */
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

    /**
     * Возвращает сумму элементов строки
     * @param row - номер строки
     * @return sum
     */
    public double getRowSum(int row) {
        if (row > size) {
            throw new IllegalArgumentException("Номер строки выходит за размеры контэйнера!");
        }
        return Arrays.stream(matrix[row]).sum();
    }
    public boolean isFull() {
        return curColumn < size;
    }

    /**
     * Определяет строку с наибольшей суммой элементов
     * @return номер строки с лучшим результатом
     */
    public int getBestResultNumber() {
        double maxSum = getRowSum(0);
        int bestRow = 0;
        for (int i = 1; i < size; i++) {
            if (maxSum <= getRowSum(i)) {
                maxSum = getRowSum(i);
                bestRow = i;
            }
        }
        return bestRow;
    }
}
