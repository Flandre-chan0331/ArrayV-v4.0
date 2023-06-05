package sorts.quick;
import main.ArrayVisualizer;
import sorts.templates.Sort;
public final class SemiMedianQuickSort extends Sort {
    public static final double DELAY = 1;
    private int[] arr;
    public SemiMedianQuickSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Semi-Median Quick");
        this.setRunAllSortsName("Semi-Median Quick Sort");
        this.setRunSortName("Semi-Median Quick Sort");
        this.setCategory("Quick Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }

    private void sort(int start, int stop) {
        int len = stop - start;
        if (len >= 2) {
            int cmp = 1;
            for (int i = 1; i < len; i *= 2, cmp = -cmp)
                for (int j = start; j < stop - i; j += i*2)
                    if (Reads.compareIndices(arr, j, j+i, DELAY, true) == cmp)
                        Writes.swap(arr, j, j+i, DELAY, true, false);
            int left = start + 1;
            for (int right = left; right < stop; right++)
                if (Reads.compareIndices(arr, start, right, DELAY, true) == 1) {
                    if (left != right)
                        Writes.swap(arr, left, right, DELAY, true, false);
                    left++;
                }
            if (start != left - 1)
                Writes.swap(arr, start, left - 1, DELAY, true, false);
            sort(start, left - 1);
            sort(left, stop);
        }
    }

    public void runSort(int[] array, int length, int buckets) {
        arr = array;
        sort(0, length);
    }
}