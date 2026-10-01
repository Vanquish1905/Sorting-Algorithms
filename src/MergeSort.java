public class MergeSort extends ArrayTools implements Sorter {

    public static void sort(int[] array, int start, int end) {
        // Base case: sub-array has 1 or 0 elements
        if (start >= end) {
            return;
        }

        // Find middle point
        int mid = (start + end) / 2;

        // Recursively sort left and right halves
        sort(array, start, mid);
        sort(array, mid + 1, end);

        // Merge the two sorted halves back together
        merge(array, start, mid, end);
    }

    // Helper method to merge two sorted sub-arrays
    private static void merge(int[] array, int start, int mid, int end) {
        // Copy left sub-array from start to mid
        int[] left = copy(array, start, mid);

        // Copy right sub-array from mid + 1 to end
        int[] right = copy(array, mid + 1, end);

        int i = 0; // Index for left array
        int j = 0; // Index for right array
        int k = start; // Index for main array

        // Merge elements in sorted order
        while (i < left.length && j < right.length) {
            if (left[i] <= right[j]) {
                array[k] = left[i];
                i++;
            } else {
                array[k] = right[j];
                j++;
            }
            k++;
        }

        // Copy remaining elements from left array if any
        while (i < left.length) {
            array[k] = left[i];
            i++;
            k++;
        }

        // Copy remaining elements from right array if any
        while (j < right.length) {
            array[k] = right[j];
            j++;
            k++;
        }
    }

    @Override
    public int[] sort(int[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }
        sort(array, 0, array.length - 1);
        return array;
    }
}