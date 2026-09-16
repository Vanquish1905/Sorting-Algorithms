public class ArrayTools {
    public static int head(int[] array) {
        return array[0];
    }

    public static int last(int[] array) {
        return array[array.length - 1];
    }

    public static String arrayToString(int[] array) {
        String arrays = "[";
        for (int number : array) {
            arrays = arrays + number + ", ";
        }
        arrays = arrays + "]";
        return arrays;
    }

    public static String arrayToString(char[] array) {
        String arrays = "[";
        for (char number : array) {
            arrays = arrays + number + ", ";
        }
        arrays = arrays + "]";
        return arrays;
    }

    public static String arrayToString(float[] array) {
        String arrays = "[";
        for (float number : array) {
            arrays = arrays + number + ", ";
        }
        arrays = arrays + "]";
        return arrays;
    }

    public static int[] copy(int[] array) {
        int[] copy = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            copy[i] = array[i];
        }
        return copy;
    }

    public static int[] copy(int[] array, int start, int end) {
        int length = end - start + 1;
        int[] copy = new int[length];

        for (int i = 0; i < length; i++) {
            copy[i] = array[start + i];
        }
        return copy;
    }

    public static int[] addSlot(int[] array) {
        int[] arrays = new int[array.length + 1];
        System.arraycopy(array, 0, arrays, 0, array.length);
        return arrays;

    }

    public static int[] addSlots(int[] array, int amount) {
        int[] arrays = new int[array.length + amount];
        System.arraycopy(array, 0, arrays, 0, array.length);
        return arrays;
    }

    public static int[] append(int[] array, int value) {
        int[] arrays = new int[array.length + 1];
        System.arraycopy(array, 0, arrays, 0, array.length);
        arrays[arrays.length - 1] = value;
        return arrays;
    }

    public static int[] addToAll(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i] + value;
        }
        return array;
    }

    public static float[] swap(float[] array, int i, int j) {
        float temp = array[i];
        array[i] = array[j];
        array[j] = temp;
        return array;
    }
    public static int[] swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
        return array;
    }

    public static char[] leftShift(char[] array) {
        if (array.length > 0) {
            char index0 = array[0];
            for (int i = 0; i < array.length - 1; i++) {
                char temp = array[i + 1];
                array[i] = temp;
            }
            array[array.length - 1] = index0;
        }
        return array;
    }

    public static int[] concat(int[] array1, int[] array2) {
        int[] array = new int[array1.length + array2.length];
        System.arraycopy(array1, 0, array, 0, array1.length);
        System.arraycopy(array2, 0, array, array1.length, array2.length);
        System.out.println(arrayToString(array));
        return array;
    }

    public static int[] remove(int[] array, int index){
        int[] left = (index > 0) ? copy(array, 0, index - 1) : new int[0];
        int[] right = (index < array.length - 1) ? copy(array, index + 1, array.length - 1) : new int[0];
        int[] result = concat(left, right);
        return result;
    }

    public static int indexOfMin(int[] array) {
        if (array == null || array.length == 0) return -1;
        int minIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[minIndex]) minIndex = i;
        }
        return minIndex;
    }

    public static int indexOfMin(int[] array, int startIndex) {
        if (array == null || array.length == 0 || startIndex >= array.length) return -1;

        int minIndex = startIndex;
        for (int i = startIndex + 1; i < array.length; i++) {
            if (array[i] < array[minIndex]) {
                minIndex = i;
            }
        }
        return minIndex;
    }

}
