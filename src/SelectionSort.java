import java.util.List;

public class SelectionSort extends ArrayTools implements Sorter{
    public static List<Integer> swap(List<Integer> list, int i, int j) {
        int temp = list.get(i);
        list.set(i,list.get(j));
        list.set(j,temp);
        return list;
    }

    public static int indexOfMin(List<Integer> list){
        if (list == null || list.isEmpty()) return -1;
        int minIndex = 0;
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) < list.get(minIndex)) minIndex = i;
        }
        return minIndex;
    }

    public static int indexOfMin(List<Integer> list, int startIndex) {
        if (list == null || list.isEmpty() || startIndex >= list.size()) return -1;

        int minIndex = startIndex;
        for (int i = startIndex + 1; i < list.size(); i++) {
            if (list.get(i) < list.get(minIndex)) {
                minIndex = i;
            }
        }
        return minIndex;
    }

    public static List<Integer> sort(List<Integer> list){
        if (list == null|| list.isEmpty()) return null;
        for(int i =0; i<list.size(); i++){
            swap(list,i,indexOfMin(list,i));
        }
        return list;
    }

    public int[] sort(int[] array){
        if (array ==null||array.length==0)return null;
        for(int i =0; i<array.length;i++){
            swap(array,i,indexOfMin(array,i));
        }
        return array;
    }
    public int[] sortSlow(int[] array){
        int[] arr = new int[0];
        if (array ==null||array.length==0)return null;
        for (int i = 0; i<array.length; i++){
            append(arr, array[indexOfMin(array)]);
            remove(array,indexOfMin(array));
        }
        return arr;
    }

}
