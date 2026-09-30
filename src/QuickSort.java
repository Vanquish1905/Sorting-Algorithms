public class QuickSort extends ArrayTools implements Sorter{
    public int[] sort(int[] array) {
        sort(array, 0, array.length - 1);
        return array;
    }
    private static void sort(int[] array, int start, int end) {
        // Base case: sub-array has 0 or 1 element
        if (end <= start) return;
        // Partition the array around a pivot element
        int pivot = partition(array, start, end);
        // Recursively sort elements before and after the pivot
        sort(array, start, pivot - 1);
        sort(array, pivot + 1, end);
    }

    private static int partition(int[] array, int start, int end) {
        int pivot = array[end];
        int i = start - 1;

        for (int j = start; j <= end - 1; j++) {
            if (array[j] < pivot) {
                i++;
                swap(array, i, j);
            }
        }
        i++;
        swap(array, i, end);
        return i;
    }
}
