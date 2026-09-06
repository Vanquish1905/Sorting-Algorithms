public class BubbleSort implements Sorter {

    public static String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public static boolean runOneCycle(int[] array, int lengthToScan) {
        boolean swapped = false;
        for (int i = 0; i < lengthToScan - 1; i++) {
            if (array[i] > array[i + 1]) {
                swap(array, i, i + 1);
                swapped = true;
            }
        }
        return swapped;
    }

    public int[] sort(int[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }
        int unsortedLength = array.length;
        boolean swapped = true;

        while (swapped) {
            swapped = runOneCycle(array, unsortedLength);
            unsortedLength--;
        }

        return array;
    }
}