public class InsertionSort extends ArrayTools implements Sorter{


    public int[] sort(int[] array) {
        // Start from index 1 because array[0] is considered a sorted sub-array of size 1
        for (int i = 1; i < array.length; i++) {
            // 1. Save the target value to insert
            int key = array[i];
            // 2. Start comparing with the element immediately to the left
            int j = i - 1;
            // 3. Shift elements to the right as long as they are GREATER than key
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j]; // Shift element right
                j--;                      // Move pointer left
            }
            // 4. Place key into its correct sorted position
            array[j + 1] = key;
        }
        return array;
    }
}
